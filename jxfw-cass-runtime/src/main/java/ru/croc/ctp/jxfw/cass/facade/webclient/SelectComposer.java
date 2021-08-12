package ru.croc.ctp.jxfw.cass.facade.webclient;

import static ru.croc.ctp.jxfw.core.facade.webclient.Filter.EQ;

import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.mapping.BasicCassandraPersistentProperty;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentEntity;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentProperty;
import org.springframework.data.cassandra.core.mapping.CassandraPersistentPropertyComparator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PropertyHandler;
import org.springframework.stereotype.Service;

import ru.croc.ctp.jxfw.cass.predicate.CassandraQueryContext;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.facade.webclient.FilterHelper;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;


/**
 * Преобразует выражение фильтра, полученное от клиента, в {@link Select}.
 *
 * @author SMufazzalov
 * @since 1.4
 */
@Service
public class SelectComposer implements PredicateProvider {

    /**
     * Err msg.
     */
    public static final String NOT_WHOLE_SET_OF_PARTION_KEYS_PASSED_FOR = "Not whole set of partion keys passed for: ";
    /**
     * Err msg.
     */
    public static final String MIXED_KEY_AND_INDEX_COLUMNS_IN_QUERY_FOUND_FOR
            = "Mixed key and index columns in query found for: ";
    /**
     * Err msg.
     */
    public static final String INVALID_ORDER_OF_CLUSTERING_KEYS_PASSED_FOR
            = "Invalid order of clustering keys passed for : ";
    /**
     * Err msg.
     */
    public static final String NOT_SUPPORTED_PREDICATE = "Not supported predicate : ";

    @Autowired
    private CassandraOperations cassandraOperations;

    @Autowired
    private DomainServicesResolver servicesResolver;

    @Autowired
    private DomainToServicesResolverWebClient domainToServicesResolver;

    /**
     * Получить выражение {@link Select}.
     * @param filter мапа фильтр
     * @param entityClass доменный объект, класс
     * @return {@link Select}
     */
    public Select toStatement(ObjectFilter filter, Class entityClass) {
        //проверка валидности фильтра
        checkFilter(filter, entityClass);

        CqlIdentifier tableName = persistentEntity(entityClass).getTableName();

        Select select = QueryBuilder.select().from(tableName.toString());
        Select.Where where = select.where();

        FilterMaps filterMaps = new FilterMaps(filter, entityClass);
        Set<Map.Entry<CassandraPersistentProperty, Object>> entries = filterMaps.propValueMap.entrySet();
        for (Map.Entry<CassandraPersistentProperty, Object> entry : entries) {
            String colName = entry.getKey().getColumnName().toString();
            Clause eq = QueryBuilder.eq(colName, entry.getValue());
            where.and(eq);
        }

        return select;
    }

    /**
     * Получить выражение {@link Select}.
     * @param pageable {@link Pageable}
     * @return {@link Select}
     */
    public Select toStatement(Pageable pageable) {

        throw new NotImplementedException();
    }

    /**
     * Получить выражение {@link Select}.
     * @param sort {@link Sort}
     * @return {@link Select}
     */
    public Select toStatement(Sort sort) {

        throw new NotImplementedException();
    }

    /**
     * Получить выражение {@link Select}.
     * @param filter мапа фильтр
     * @param pageable {@link Pageable}
     * @return {@link Select}
     */
    public Select toStatement(ObjectFilter filter, Pageable pageable) {

        throw new NotImplementedException();
    }

    /**
     * Получить выражение {@link Select}.
     * @param filter мапа фильтр
     * @param sort {@link Sort}
     * @return {@link Select}
     */
    public Select toStatement(ObjectFilter filter, Sort sort) {

        throw new NotImplementedException();
    }

    /**
     * Получить выражение {@link Select}.
     * @param filter мапа фильтр
     * @param orderBy строка сортировака (можно получить объект {@link Sort}
     *                через {@link ru.croc.ctp.jxfw.util.SortUtil#parse(String)})
     * @return {@link Select}
     */
    public Select toStatement(ObjectFilter filter, String orderBy) {

        throw new NotImplementedException();
    }

    /**
     * Можно проверить фильтр, на предмет получится ли данным класом подготовить выражение которое "съест" С*.
     *
     * @param filter мапа фильтр
     * @param entityClass доменный объект, класс
     */
    public void checkFilter(ObjectFilter filter, Class entityClass) {
        FilterMaps filterMaps = new FilterMaps(filter, entityClass);

        operatorsSupported(filter);

        //только по колонкам Secondary Idx
        if (queryByIdxCols(filter, filterMaps)) {
            return;
        }
        //нельзя мешать в запросе ключи и колонки индекса
        notMixedCompositeKeysAndIndexedCols(filterMaps, entityClass);

        //проверка что передан полный набор партиционных ключей
        partitionKeysPassed(filterMaps, entityClass);

        //проверка что кластерные ключи переданы в порядке друг за другом без пропусков предыдущего
        clusteringKeysHaveRightOrder(filterMaps, entityClass);
    }

    @Override
    public Predicate buildPredicate(
            @Nonnull String domainTypeName,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable
    ) {
        return new CassandraQueryContext(
                DomainObjectUtil.getDomainObjectType(domainTypeName),
                this,
                objectFilter == null ? new ObjectFilter() : objectFilter,
                sort,
                pageable
        );
    }

    @Override
    public Predicate buildPredicateById(@Nonnull String domainTypeName, @Nonnull Serializable id) {
        //FIXME: predicateByID
        return buildPredicate(domainTypeName, null, null, null);
    }

    @Override
    public boolean accepts(String domainTypeName) {
        XfwClass xfwClass = XfwModelFactory.getInstance().find(domainTypeName, XfwClass.class);
        return xfwClass != null && xfwClass.getPersistenceModule().contains("CASS");
    }

    private void clusteringKeysHaveRightOrder(FilterMaps filterMaps, Class entityClass) {
        Collection<CassandraPersistentProperty> props = filterMaps.properties;
        ArrayList<CassandraPersistentProperty> clustKeys = new ArrayList<>();
        for (CassandraPersistentProperty prop : props) {
            if (prop.isClusterKeyColumn()) {
                clustKeys.add(prop);
            }
        }
        Collections.sort(clustKeys, CassandraPersistentPropertyComparator.INSTANCE);

        Set<String> nameOfColsForQuery = filterMaps.fieldNamePropMap.keySet();
        boolean ok = true;
        for (CassandraPersistentProperty clustKey : clustKeys) {
            String ckName = clustKey.getName();
            if (nameOfColsForQuery.contains(ckName)) {
                if (!ok) {
                    //значит предыдущий ключ пропущен
                    throw new RuntimeException(INVALID_ORDER_OF_CLUSTERING_KEYS_PASSED_FOR
                    + entityClass.getCanonicalName());
                }
            } else {
                ok = false;
            }
        }    
    }

    private void partitionKeysPassed(FilterMaps filterMaps, Class entityClass) {
        List<String> partionKeyNameList = new ArrayList<>();
        for (CassandraPersistentProperty property : filterMaps.properties) {
            if (property.isPartitionKeyColumn()) {
                partionKeyNameList.add(property.getName());
            }
        }

        if (!filterMaps.fieldNamePropMap.keySet().containsAll(partionKeyNameList)) {
            throw new RuntimeException(NOT_WHOLE_SET_OF_PARTION_KEYS_PASSED_FOR + entityClass.getCanonicalName());
        }
    }

    private void notMixedCompositeKeysAndIndexedCols(FilterMaps filterMaps, Class entityClass) {
        CassandraPersistentProperty idxColPresent = null;
        CassandraPersistentProperty keyColPresent = null;
        Collection<CassandraPersistentProperty> values = filterMaps.fieldNamePropMap.values();
        for (CassandraPersistentProperty prop : values) {
            //if (prop.isIndexed()) {
            //    idxColPresent = prop;
            //}
            if (prop.isClusterKeyColumn() || prop.isPartitionKeyColumn()) {
                keyColPresent = prop;
            }
            if (idxColPresent != null && keyColPresent != null) {
                throw new RuntimeException(MIXED_KEY_AND_INDEX_COLUMNS_IN_QUERY_FOUND_FOR
                        + entityClass.getCanonicalName());
            }
        }
    }

    /**
     * Т.е. запрос по колонкам индекса (Secondary Index), такие колонки не входят в композитный ключ
     */
    private boolean queryByIdxCols(ObjectFilter filter, FilterMaps filterMaps) {
        Collection<CassandraPersistentProperty> props = filterMaps.fieldNamePropMap.values();
        for (CassandraPersistentProperty prop : props) {
            //if (!prop.isIndexed()) {
            //    return false;
            //}
        }
        return true;
    }

    private void operatorsSupported(ObjectFilter filter) {
        for (Map.Entry<String, Object> e : filter.entrySet()) {
            if (e.getValue() instanceof Map<?, ?>) {
                Map<String, Object> map = (Map<String, Object>) e.getValue();
                for (Map.Entry<String, Object> operator : map.entrySet()) {
                    final String opName = operator.getKey();
                    if (!opName.toLowerCase().equals(EQ)) {
                        throw new RuntimeException(NOT_SUPPORTED_PREDICATE + opName);
                    }
                }
            }
        }
    }

    private CassandraPersistentEntity persistentEntity(Class entityClass) {
        CassandraPersistentEntity<?> entity = cassandraOperations
                .getConverter().getMappingContext().getPersistentEntity(entityClass);
        if (entity == null) {
            throw new IllegalArgumentException(String.format("unknown entity class [%s]", entityClass.getName()));
        }
        return entity;
    }


    private class FilterMaps {
        public FilterMaps(ObjectFilter filter, Class entityClass) {
            this.properties = listProperties(entityClass);
            this.propValueMap = propValueMap(filter, entityClass);
            this.fieldNamePropMap = fieldNamePropMap(filter, entityClass);
        }

        private Map<String, CassandraPersistentProperty> fieldNamePropMap(ObjectFilter filter, Class entityClass) {
            Map<String, CassandraPersistentProperty> resultMap = new HashMap<>();
            Set<Map.Entry<String, Object>> filterEntries = filter.entrySet();
            for (Map.Entry<String, Object> filterEntry : filterEntries) {
                //имя поля доменного объекта DomainObject#someField
                String name = filterEntry.getKey();
                for (CassandraPersistentProperty property : properties) {
                    if (name.equals(property.getName())) {
                        resultMap.put(name, property);
                        break;
                    }
                }
            }
            return resultMap;
        }

        Map<CassandraPersistentProperty, Object> propValueMap;
        Map<String, CassandraPersistentProperty> fieldNamePropMap;
        //получить информацию о всех пропертях связанных с моделью
        List<CassandraPersistentProperty> properties;

        @SuppressWarnings("unchecked")
        private Map<CassandraPersistentProperty, Object> propValueMap(ObjectFilter filter, Class entityClass) {
            FilterHelper<DomainObject<?>> domainToService = (FilterHelper<DomainObject<?>>) domainToServicesResolver
                    .resolveToService(entityClass.getTypeName());

            Map<CassandraPersistentProperty, Object> resultMap = new HashMap<>();

            Set<Map.Entry<String, Object>> filterEntries = filter.entrySet();
            for (Map.Entry<String, Object> filterEntry : filterEntries) {
                //имя поля доменного объекта DomainObject#someField
                String name = filterEntry.getKey();

                for (CassandraPersistentProperty property : properties) {
                    if (name.equals(property.getName())) {
                        Object value = filterEntry.getValue();
                        //мапа CassandraPersistentProperty
                        // - объект приведенный к тому же типу что и поле в доменном объекте
                        resultMap.put(property, domainToService.parsePropValue(name, value));
                        break;
                    }
                }
            }
            return resultMap;
        }
    }

    private List<CassandraPersistentProperty> listProperties(Class entityClass) {
        //список всех пропертей (CassandraPersistentProperty) класса
        List<CassandraPersistentProperty> result = new ArrayList<>();
        CassandraPersistentEntity entity = persistentEntity(entityClass);
        //получаем через хендлер, т.к. проперти композитного ключа нужно достать отдельно.
        entity.doWithProperties(new PropertyHandler() {
            @Override
            public void doWithPersistentProperty(PersistentProperty persistentProperty) {
                BasicCassandraPersistentProperty property = (BasicCassandraPersistentProperty)persistentProperty;
                /*if (property.isCompositePrimaryKey()) {
                    List<CassandraPersistentProperty> compositePrimaryKeyProperties
                            = property.getCompositePrimaryKeyProperties();
                    for (CassandraPersistentProperty compositePrimaryKeyProperty : compositePrimaryKeyProperties) {
                        result.add(compositePrimaryKeyProperty);
                    }
                } else {*/
                    result.add(property);
                //}
            }
        });

        return result;
    }

}
