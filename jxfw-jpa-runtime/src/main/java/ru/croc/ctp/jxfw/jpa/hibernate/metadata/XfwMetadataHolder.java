package ru.croc.ctp.jxfw.jpa.hibernate.metadata;

import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.hibernate.persister.walking.spi.AssociationAttributeDefinition.AssociationNature.COLLECTION;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
import org.hibernate.tuple.NonIdentifierAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionParser;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;


/**
 * Сервис держит метаданные, собранные с Hibernate о связях имен таблиц и констрейнтов
 * с метамоделью.
 *
 * @author OKrutova
 * @since 1.6
 */
@Service
public class XfwMetadataHolder {

    /**
     * Логгер.
     */
    static Logger logger = LoggerFactory.getLogger(XfwMetadataHolder.class);
    private final XfwModel xfwModel;
    private final JpaContext jpaContext;
    /**
     * Для тестов. количество соединений с БД на этапе инициализации метаданных.
     */
    int totalConnections = 0;


    /*
      Имена всех сущностей(таблиц, колонок и констрейнтов) в этих мапах хранятся в lowerCase
      для того, чтобы не надо было разбирать, как эти имена выглядт в реальной БД. JXFW-1522.
     */
    private Map<String, XfwTable> tableToMetadataMap = new HashMap<>();
    private Map<String, XfwTable.XfwConstraint> fkConstraintToMetadataMap = new HashMap<>();
    private Map<String, XfwCrossTable> crossTableMap = new HashMap<>();


    /**
     * Конструктор.
     *
     * @param xfwModel   метамодель
     * @param jpaContext jpaContext
     */
    @Autowired
    public XfwMetadataHolder(XfwModel xfwModel, JpaContext jpaContext) {
        this.xfwModel = xfwModel;
        this.jpaContext = jpaContext;
    }

    /**
     * Инициализация. ДЛя всех доменных объектов из модели
     * ищет имя таблицы в БД, сопоставляет колонки БД с именами полей домена,
     * анализирует констренйты на таблице.
     */
    @PostConstruct
    private void init() {
        Map<SessionFactory, Connection> sessionFactoryConnectionMap = new HashMap<>();

        StreamSupport.stream(xfwModel.getAll(XfwClass.class))
                .forEach(xfwClass -> {

                    // здесь нас интересуют только JPA-сущности
                    if (!xfwClass.getPersistenceModule().contains("JPA")) {
                        return;
                    }

                    // для комплексных и транзиентных типов таблиц нет и не должно быть.
                    if (xfwClass.isComplexType() || !xfwClass.isPersistentType()) {
                        return;
                    }


                    // Определяем имя таблицы
                    String tableName = null;
                    SessionFactoryImplementor sessionFactory = null;
                    AbstractEntityPersister persister = null;
                    try {
                        Class entityClass = xfwClass.getInstanceClass();
                        if (entityClass != null) {
                            sessionFactory = jpaContext
                                    .getEntityManagerByManagedType(entityClass).getEntityManagerFactory()
                                    .unwrap(SessionFactoryImplementor.class);

                            ClassMetadata classMetadata = sessionFactory
                                    .getClassMetadata(xfwClass.getInstanceClassName());
                            if (classMetadata instanceof AbstractEntityPersister) {
                                persister = (AbstractEntityPersister) classMetadata;
                            }
                            tableName = persister.getSubclassTableName(0);
                        }

                    } catch (Exception ex) {
                        logger.info("Failed to get table name for domain object {}", xfwClass.getName());
                    }


                    if (tableName == null) {
                        logger.info("Undefined table name for domain object {}", xfwClass.getName());
                        return;
                    }
                    final JdbcServices jdbcService = sessionFactory.getServiceRegistry().getService(JdbcServices.class);
                    final IdentifierHelper identifierHelper = jdbcService.getJdbcEnvironment().getIdentifierHelper();


                    logger.debug("Table {} = domain object {}", tableName, xfwClass.getName());


                    Map<String, XfwStructuralFeature> columnsToFields = buildColumnsToFieldsMap(persister, xfwClass,
                            "");

                    Map<String, String> joinTableToColumn = buildJoinTableToColumnMap(persister);


                    XfwTable xfwTableMetadata = tableToMetadataMap.get(lowerCase(tableName));
                    if (xfwTableMetadata != null) {
                        // К одной таблице может относится много сущностей, если стратегия наследования SINGLE_TABLE.
                        // из двух возможных xfwClass кладем в xfwTableMetadata того, который родитель.
                        if( xfwTableMetadata.getXfwClass().getEAllSuperTypes().contains(xfwClass)){
                            xfwTableMetadata.setXfwClass(xfwClass);
                        }
                        // информацию о столбцах накапливаем всю
                        xfwTableMetadata.getColumnsToFields().putAll(columnsToFields);
                        // информацию о связях с кросс-таблицами накапливаем всю
                        xfwTableMetadata.getJoinTableToColumn().putAll(joinTableToColumn);
                    } else {
                        xfwTableMetadata = new XfwTable(xfwClass, tableName,
                                identifierHelper.toMetaDataObjectName(identifierHelper.toIdentifier(tableName)),
                                columnsToFields, joinTableToColumn);
                    }

                    tableToMetadataMap.put(lowerCase(tableName), xfwTableMetadata);

                    try {
                        Connection connection = sessionFactoryConnectionMap.get(sessionFactory);
                        if (connection == null) {
                            connection = SessionFactoryUtils.getDataSource(sessionFactory).getConnection();
                            sessionFactoryConnectionMap.put(sessionFactory, connection);
                        }
                        buildIndexConstraintData(connection, xfwTableMetadata);
                    } catch (SQLException ex) {
                        logger.info("Failed to get JDBC Connection {}", xfwClass.getName());
                    }


                });


        for (XfwTable xfwTable : tableToMetadataMap.values()) {
            for (Map.Entry<String, XfwTable.XfwConstraint> entry : xfwTable.getConstraints().entrySet()) {
                String key = entry.getKey();
                XfwTable.XfwConstraint value = entry.getValue();
                // импортированные fk сработают на той таблице, про которую надо написать сообщение.
                if (!value.isExported()) {
                    fkConstraintToMetadataMap.put(lowerCase(key), value);
                } else if (tableToMetadataMap.get(value.getOppositeTableName()) != null) {
                    /*
                    Экпортированный из одной таблицы fk уже учтен как импортированный в другой таблице доменной модели.
                    На таблице импорта он и сработает. Как экспортированный fk учитывать не надо.
                     */
                    if (tableToMetadataMap.get(value.getOppositeTableName()).getConstraints().get(key) == null
                            || tableToMetadataMap.get(
                            value.getOppositeTableName()).getConstraints().get(key).isExported()) {
                        logger.info(
                                "Экспортированный fk не найден в соответствующей таблице как импортируемый fk={} "
                                        + "table1={} table2={}",
                                key, xfwTable.getTableName(), value.getOppositeTableName());
                    }
                } else {
                    // связь через кросс-таблицу
                    String crossTableName = lowerCase(value.getOppositeTableName());
                    crossTableMap.putIfAbsent(crossTableName, new XfwCrossTable(crossTableName));
                    XfwCrossTable crossTable = crossTableMap.get(crossTableName);
                    crossTable.addConstraint(value);
                }
            }
        }

        /*
         Констрейнт привязывается к таблице того ДО, про который надо сообщить пользователю
         в случае нарушения.
         */
        for (XfwCrossTable xfwCrossTable : crossTableMap.values()) {
            XfwTable.XfwConstraint constraint1 = xfwCrossTable.getConstraint(0);
            XfwTable.XfwConstraint constraint2 = xfwCrossTable.getConstraint(1);
            if (constraint1 != null && constraint2 != null) {
                // переносим   constraint через кросс таблицу
                fkConstraintToMetadataMap.put(lowerCase(constraint1.getName()),
                        constraint2.getTable().importCrossTableConstraint(constraint1, xfwCrossTable.getName()));
                fkConstraintToMetadataMap.put(lowerCase(constraint2.getName()),
                        constraint1.getTable().importCrossTableConstraint(constraint2, xfwCrossTable.getName()));

            }
        }

        totalConnections = sessionFactoryConnectionMap.values().size();
        StreamSupport.stream(sessionFactoryConnectionMap.values()).forEach(connection -> {
            try {
                connection.close();
            } catch (SQLException ex) {
                logger.error("Failed to close connection {}", ex);
            }
        });


    }


    /*
     * Строит соответствие между колонками БД в таблице, соответствующей ДО, и полями ДО.
     */
    private Map<String, XfwStructuralFeature> buildColumnsToFieldsMap(AbstractEntityPersister persister,
                                                                      XfwClass xfwClass, String prefix) {
        // определяем имена столбцов в БД
        Map<String, XfwStructuralFeature> columnsToFields = new HashMap<>();
        for (XfwStructuralFeature xfwStructuralFeature : xfwClass.getEAllStructuralFeatures()) {

            //для транзиентных полей колонок в БД нет
            if (xfwStructuralFeature.getEAnnotation(XFWConstants.getUri("Transient")) != null) {
                continue;
            }
            //для коллекций колонок в таблице ДО нет
            if (xfwStructuralFeature.isMany()) {
                continue;
            }

            // пассивная сторона ассоциации, управляется другой стороной связи.
            if (StreamSupport.stream(xfwStructuralFeature.getEAnnotations()).filter(xfwAnnotation -> {
                String value = xfwAnnotation.getDetails().get("mappedBy");
                return value != null && !value.isEmpty();
            }).count() > 0) {
                continue;
            }

            if (xfwStructuralFeature instanceof XfwReference
                    && ((XfwReference) xfwStructuralFeature).getEReferenceType().isComplexType()) {
                // комплексное свойство
                columnsToFields.putAll(buildColumnsToFieldsMap(persister,
                        ((XfwReference) xfwStructuralFeature).getEReferenceType(),
                        prefix + xfwStructuralFeature.getName() + "."));
            } else {
                try {
                    String[] columnNames = persister.getPropertyColumnNames(
                            prefix + xfwStructuralFeature.getName());
                    if (columnNames.length > 0) {
                        String columnName = columnNames[0];
                        if (columnsToFields.get(columnName) != null) {
                            logger.info("Attempt to map two features({}, {}) to one column{}",
                                    columnsToFields.get(columnName).getName(),
                                    xfwStructuralFeature.getName(),
                                    columnName);
                        } else {
                            columnsToFields.put(lowerCase(columnName), xfwStructuralFeature);
                        }

                    } else {
                        logger.info("Undefined column name for domain object {} field {}",
                                xfwClass.getName(), xfwStructuralFeature.getName());
                    }
                } catch (Exception ex) {
                    logger.info("Undefined column name for domain object {} field {}",
                            xfwClass.getName(), xfwStructuralFeature.getName());
                }
            }
        }
        return columnsToFields;
    }


    /*
     Строит соответствие между кросс-таблицей и колонкой БД в таблице, соответствующей ДО,
     на которую из кросс таблицы есть FK.
     */
    private Map<String, String> buildJoinTableToColumnMap(AbstractEntityPersister persister) {

        Map<String, String> result = new HashMap<>();
        for (NonIdentifierAttribute attribute : persister.getEntityMetamodel().getProperties()) {
            if (attribute instanceof AssociationAttributeDefinition
                    && ((AssociationAttributeDefinition) attribute).getAssociationNature() == COLLECTION
                    && ((AssociationAttributeDefinition) attribute).toCollectionDefinition()
                    .getCollectionPersister() instanceof Joinable) {
                result.put(lowerCase(((Joinable) ((AssociationAttributeDefinition) attribute).toCollectionDefinition()
                                .getCollectionPersister()).getTableName()),
                        lowerCase(attribute.getName()));
            }
        }
        return result;
    }

    /*
      Чтение метаданны об индексах и FK средствами jdbc.
     */
    private void buildIndexConstraintData(Connection connection, XfwTable xfwTableMetadata) throws SQLException {

        // индексы
        ResultSet indexInfo = connection.getMetaData().getIndexInfo(null, null,
                xfwTableMetadata.getDbTableName(), false, false);
        while (indexInfo.next()) {
            String constraint = lowerCase(indexInfo.getString("INDEX_NAME"));

            xfwTableMetadata.getConstraints().putIfAbsent(constraint,
                    xfwTableMetadata.new XfwConstraint(false, null, constraint));
            XfwTable.XfwConstraint xfwConstraintMetadata
                    = xfwTableMetadata.getConstraints().get(constraint);
            xfwConstraintMetadata.getColumns().add(indexInfo.getString("COLUMN_NAME"));
        }

        // импортируемые FK: ограничение в текущей таблилце, ссылающееся на какую-то другую таблицу.
        ResultSet fkInfo = connection.getMetaData().getImportedKeys(null, null, xfwTableMetadata.getDbTableName());
        while (fkInfo.next()) {
            String constraint = lowerCase(fkInfo.getString("FK_NAME"));
            xfwTableMetadata.getConstraints().putIfAbsent(constraint, xfwTableMetadata.new XfwConstraint(false,
                    fkInfo.getString("PKTABLE_NAME"), constraint));
            XfwTable.XfwConstraint xfwConstraintMetadata
                    = xfwTableMetadata.getConstraints().get(constraint);
            xfwConstraintMetadata.getColumns().add(fkInfo.getString("FKCOLUMN_NAME"));
        }

        // экспортируемые FK: ограничение наложенное в какой-то другой таблице, ссылающееся на текущую таблицу.
        ResultSet fkInfoExp = connection.getMetaData().getExportedKeys(null, null, xfwTableMetadata.getDbTableName());
        while (fkInfoExp.next()) {
            String constraint = lowerCase(fkInfoExp.getString("FK_NAME"));
            xfwTableMetadata.getConstraints().putIfAbsent(constraint, xfwTableMetadata.new XfwConstraint(true,
                    fkInfoExp.getString("FKTABLE_NAME"), constraint));
        }

    }

    /**
     * Получить метаданные по имени таблицы.
     * Поиск проводится по имени таблицы в LowerCase
     * для того, чтобы не надо было разбирать, как эти имена выглядт в реальной БД. JXFW-1522.
     *
     * @param tableName имя таблицы.
     * @return метаданные, если нашли
     */
    public Optional<XfwClass> getTableMetadata(String tableName) {
        return Optional.ofNullable(tableToMetadataMap.get(lowerCase(tableName)))
                .map(XfwTable::getXfwClass);
    }

    /**
     * Получить метаданные доменного типа для FK - констрейнта.
     * Поиск проводится по имени констрейнта в LowerCase
     * для того, чтобы не надо было разбирать, как эти имена выглядт в реальной БД. JXFW-1522.
     *
     * @param constraint имя констрейнта
     * @return метаданные, если нашли
     */
    public Optional<XfwClass> getFkTableMetadata(String constraint) {
        return Optional.ofNullable(fkConstraintToMetadataMap.get(lowerCase(constraint)))
                .map(xfwConstraint -> xfwConstraint.getTable().getXfwClass());
    }


    /**
     * Получить метаданные по имени таблицы и имени столбца в БД.
     * Поиск проводится по имени таблицы и имени столбца в LowerCase
     * для того, чтобы не надо было разбирать, как эти имена выглядт в реальной БД. JXFW-1522.
     *
     * @param tableName  имя таблица
     * @param columnName имя столбца
     * @return метаданные, если нашли
     */
    public Optional<XfwStructuralFeature> getColumnMetadata(String tableName, String columnName) {
        XfwTable xfwTable = tableToMetadataMap.get(lowerCase(tableName));

        XfwStructuralFeature structuralFeature = xfwTable.getColumnsToFields().get(lowerCase(columnName));
        if (structuralFeature == null) {
            structuralFeature = xfwTable.getXfwClass().getEStructuralFeature(columnName);
        }

        return Optional.ofNullable(structuralFeature);


    }

    /**
     * Получить набор метаданных для всех столбцов, на которые распространяется констрейнт.
     * Поиск проводится по имени констрейнт в LowerCase
     * для того, чтобы не надо было разбирать, как эти имена выглядт в реальной БД. JXFW-1522.
     *
     * @param constraint имя констрейнта
     * @return набор метаданных, если нашли
     */
    public Map<String, Optional<XfwStructuralFeature>> getColumnsMetadata(String constraint) {

        XfwTable.XfwConstraint xfwConstraint = fkConstraintToMetadataMap.get(lowerCase(constraint));
        if (xfwConstraint != null) {
            return StreamSupport.stream(xfwConstraint.getColumns())
                    .collect(Collectors.toMap(column -> column,
                            column -> getColumnMetadata(xfwConstraint.getTable().getTableName(), column)));
        }
        return newHashMap();


    }
}
