package ru.croc.ctp.jxfw.jpa.exception.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolation;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolationItem;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.jpa.exception.DbExceptionDescriptor;
import ru.croc.ctp.jxfw.jpa.exception.ExceptionDescriptor;
import ru.croc.ctp.jxfw.jpa.exception.XfwViolationType;
import ru.croc.ctp.jxfw.jpa.hibernate.metadata.XfwMetadataHolder;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwNamedElement;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Дефолтная реализация ExceptionDescriptor.
 *
 * @author OKrutova
 * @since 1.6
 */
public class ExceptionDescriptorImpl implements ExceptionDescriptor {


    private final DbExceptionDescriptor dbExceptionDescriptor;
    private StoreContext storeContext;
    private Optional<XfwClass> xfwClassOptional;
    private Map<String, Optional<XfwStructuralFeature>> columns;
    private Set<DomainViolation> domainViolations = newHashSet();


    /**
     * Конструктор.
     *
     * @deprecated since 1.7.2
     */
    @Deprecated
    public ExceptionDescriptorImpl() {
        dbExceptionDescriptor = new DbExceptionDescriptorImpl();
        storeContext = null;
    }

    /**
     * Конструктор.
     *
     * @param xfwMetadataHolder     метаданные, собранные с Hibernate о связях имен таблиц и констрейнтов с
     *                              метамоделью.
     * @param dbExceptionDescriptor описание исключения
     * @param storeContext          контекст сохранения
     */
    public ExceptionDescriptorImpl(XfwMetadataHolder xfwMetadataHolder, DbExceptionDescriptor dbExceptionDescriptor,
                                   StoreContext storeContext) {

        this.dbExceptionDescriptor = dbExceptionDescriptor;
        this.storeContext = storeContext;

        // метаданные сущности определяем по имени таблицы, на которой произошло исключение
        xfwClassOptional = xfwMetadataHolder.getTableMetadata(getTableName());
        // метаданные о полях определяем по имени констрейнта, на котором произошло исключение
        columns = xfwMetadataHolder.getColumnsMetadata(getConstraint());

        if (getViolationType() == XfwViolationType.ReferenceViolation) {
            // для нарушения ссылки метаданные сущности определяем по имени констрейнта
            xfwClassOptional = xfwMetadataHolder.getFkTableMetadata(getConstraint());
        }
        // если заданы имена колонок, на которых произошли исключения,
        // то они более приоритетны, чем определенные по констрейнту.
        if (getColumnNames().size() > 0) {
            columns = getColumnNames().stream()
                    .collect(Collectors.toMap(columnName -> columnName,
                        columnName -> xfwMetadataHolder.getColumnMetadata(getTableName(), columnName)));

        }


        updateTableMetadataFromColumnMetadata();
        updateTableMetadataFromStoreContext();

    }

    /**
     * Метод обновляет метаданные о классе по метаданным о полях. Это нужно для случая, когда
     * сущности из общей иерархии хранятся в одной таблице в БД. Если  исключение произошло на поле,
     * которое появляется не у корневой сущности иерархии, а только у какого-то из наследников, то
     * можно сделать вывод, что сохранялся как минимум этот наследник.
     */
    private void updateTableMetadataFromColumnMetadata() {

        columns.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(xfwStructuralFeature -> {
                    XfwClass xfwClass = xfwStructuralFeature.getEContainingClass();
                    if (extendCurrent(xfwClass)) {
                        this.xfwClassOptional = Optional.of(xfwClass);
                    }
                });


    }

    /**
     * Метод обновляет метаданные о классе по информации о том, какие конкретно объекты
     * сохранялись.
     * Также формирует набор идентификаторов доменных объектов, на которых потенциально
     * могло произойти исключение
     */
    private void updateTableMetadataFromStoreContext() {


        /*
        Именя полей класса, на которых случилось нарушение.
         */
        List<String> featureNames = columns.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(XfwNamedElement::getName)
                .collect(Collectors.toList());


        XfwModel xfwModel = XfwModelFactory.getInstance();
        storeContext.getOriginalsObjects().forEach(domainTo -> {
            if (!domainTo.isRemoved()) {
                XfwClass xfwClass = xfwModel.find(domainTo.getType(), XfwClass.class);

                if (extendCurrent(xfwClass)) {
                    /*
                        Если нарушена уникальность, то в измененном объекте должно быть
                        хотя бы одно поле из тех, которые вызвали нарушение.
                     */
                    DomainViolation violation = new DomainViolation();
                    if (dbExceptionDescriptor.getViolationType() == XfwViolationType.Unique) {
                        List<String> propNames = newArrayList();
                        domainTo.forEachProperty((propName, value) -> propNames.add(propName));
                        propNames.retainAll(featureNames);
                        propNames.forEach(propName -> {
                            DomainViolationItem violationItem =
                                    new DomainViolationItem(new DomainObjectIdentity(domainTo.getId(),
                                            domainTo.getType()), propName);
                            violation.getItems().add(violationItem);
                        });

                    }

                    /*
                        Если нарушена NOT NULL, то в измененном объекте не должно быть
                        ненулевых значений для полей, на которых сработало ограничение .
                     */
                    if (dbExceptionDescriptor.getViolationType() == XfwViolationType.NotNull) {
                        List<String> propNames = newArrayList();
                        domainTo.forEachProperty((propName, value) -> {
                            if (value != null) {
                                propNames.add(propName);
                            }
                        });

                        if (Collections.disjoint(propNames, featureNames)) {
                            featureNames.forEach(propName -> {
                                DomainViolationItem violationItem =
                                        new DomainViolationItem(new DomainObjectIdentity(domainTo.getId(),
                                                domainTo.getType()), propName);
                                violation.getItems().add(violationItem);
                            });
                        }
                    }

                    if (violation.getItems().size() > 0) {
                        domainViolations.add(violation);
                    }

                }
            }
        });

        domainViolations.stream().flatMap(violation -> violation.getItems().stream())
                .forEach(violationItem -> {
                    XfwClass xfwClass = xfwModel.find(violationItem.getIdentity().getTypeName(), XfwClass.class);
                    if (extendCurrent(xfwClass)) {
                        this.xfwClassOptional = Optional.of(xfwClass);
                    }
                });
    }

    /**
     * Проверяет, что данный класс явлвяется наследником того, что
     * лежит в xfwClassOptional.
     *
     * @param xfwClass метаданные класса
     * @return да\нет
     */
    private boolean extendCurrent(XfwClass xfwClass) {
        return (xfwClass != null && (!this.xfwClassOptional.isPresent()
                || xfwClass.getEAllSuperTypes().contains(this.xfwClassOptional.get())
                || xfwClass == this.xfwClassOptional.get()));
    }


    @Override
    @Nullable
    public String getConstraint() {
        return dbExceptionDescriptor.getConstraint();
    }

    /**
     * Установить constraint.
     *
     * @param constraint constraint
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setConstraint(String constraint) {
        if (dbExceptionDescriptor instanceof DbExceptionDescriptorImpl) {
            ((DbExceptionDescriptorImpl) dbExceptionDescriptor).setConstraint(constraint);
        }
    }

    @Override
    @Nullable
    public String getTableName() {
        return dbExceptionDescriptor.getTableName();
    }

    /**
     * Установить tableName.
     *
     * @param tableName tableName
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setTableName(String tableName) {
        if (dbExceptionDescriptor instanceof DbExceptionDescriptorImpl) {
            ((DbExceptionDescriptorImpl) dbExceptionDescriptor).setTableName(tableName);
        }
    }

    @Override
    public List<String> getColumnNames() {
        return dbExceptionDescriptor.getColumnNames();
    }

    /**
     * Установить columnNames.
     *
     * @param columnNames columnNames
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setColumnNames(List<String> columnNames) {
        if (dbExceptionDescriptor instanceof DbExceptionDescriptorImpl) {
            ((DbExceptionDescriptorImpl) dbExceptionDescriptor).setColumnNames(columnNames);
        }
    }

    @Override
    public String getDetails() {
        return dbExceptionDescriptor.getDetails();
    }

    /**
     * Установить details.
     *
     * @param details details
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setDetails(String details) {
        if (dbExceptionDescriptor instanceof DbExceptionDescriptorImpl) {
            ((DbExceptionDescriptorImpl) dbExceptionDescriptor).setDetails(details);
        }
    }

    @Override
    public Throwable getCause() {
        return dbExceptionDescriptor.getCause();
    }

    /**
     * Установить cause.
     *
     * @param cause cause
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setCause(Throwable cause) {
        if (dbExceptionDescriptor instanceof DbExceptionDescriptorImpl) {
            ((DbExceptionDescriptorImpl) dbExceptionDescriptor).setCause(cause);
        }
    }

    @Override
    public XfwViolationType getViolationType() {
        return dbExceptionDescriptor.getViolationType();
    }

    @Override
    public Optional<XfwClass> getXfwClassOptional() {
        return xfwClassOptional;
    }

    @Override
    public Map<String, Optional<XfwStructuralFeature>> getColumns() {
        return columns;
    }

    @Nonnull
    @Override
    public StoreContext getStoreContext() {
        return storeContext;
    }

    @Override
    @Nonnull
    public Set<DomainViolation> getDomainViolations() {
        return domainViolations;
    }

    /**
     * Установить xfwViolationType.
     *
     * @param xfwViolationType xfwViolationType
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setViolationType(XfwViolationType xfwViolationType) {
        if (dbExceptionDescriptor instanceof DbExceptionDescriptorImpl) {
            ((DbExceptionDescriptorImpl) dbExceptionDescriptor).setXfwViolationType(xfwViolationType);
        }
    }


    /**
     * Установить xfwClassOptional.
     *
     * @param xfwClassOptional xfwClassOptional
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setXfwClassOptional(Optional<XfwClass> xfwClassOptional) {
        this.xfwClassOptional = xfwClassOptional;
    }

    /**
     * Установить columns.
     *
     * @param columns columns
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setColumns(Map<String, Optional<XfwStructuralFeature>> columns) {
        this.columns = columns;
    }

    /**
     * Установить storeContext.
     *
     * @param storeContext storeContext
     * @deprecated since 1.7.2
     */
    @Deprecated
    public void setStoreContext(StoreContext storeContext) {
        this.storeContext = storeContext;
    }
}
