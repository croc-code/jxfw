package ru.croc.ctp.jxfw.core.load;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Билдер для {@link QueryParams} из готовых spring-data объектов, имеет 3 вида конструктора, для поиска по Id,
 * поиска по предикату и поиску без них (если просто нужен поиск с пагинацией и сортировкой).
 * Удобнее получать через фабрику {@link QueryParamsBuilderFactory}
 *
 * @param <T>  тип доменного объекта
 * @param <ID> идентификатор доменного объекта
 * @author smufazzalov
 * @since jxfw 1.6
 */
public class QueryParamsBuilder<T extends DomainObject<ID>, ID extends Serializable> {

    private SimpleQueryParams simpleQueryParams;

    /**
     * Конструктор.
     *
     * @param typeName          имя доменного объекта
     * @param predicateProvider поставщик предикатов
     */
    public QueryParamsBuilder(
            String typeName,
            PredicateProvider predicateProvider) {
        simpleQueryParams = new SimpleQueryParams();
        simpleQueryParams.typeName = typeName;
        //QueryParams обязан содержать экземпляр предиката,
        // соответствующий типу хранилища
        simpleQueryParams.predicate = predicateProvider.buildPredicate(
                simpleQueryParams.getDomainObjectTypeName(),
                null,
                null,
                null
        );

    }

    /**
     * Конструктор.
     *
     * @param typeName          имя доменного объекта
     * @param predicateProvider поставщик предикатов
     * @param predicate         predicate
     */
    public QueryParamsBuilder(
            String typeName,
            PredicateProvider predicateProvider,
            Predicate predicate) {
        this(typeName, predicateProvider);
        simpleQueryParams.predicate = predicate;
    }

    /**
     * Конструктор.
     *
     * @param id                id
     * @param predicateProvider поставщик предикатов
     * @param typeName          имя доменного объекта (допустимо simple либо canonical)
     */
    public QueryParamsBuilder(
            String typeName,
            PredicateProvider predicateProvider,
            ID id) {
        this(typeName, predicateProvider);
        simpleQueryParams.id = id;
    }

    /**
     * Пагинация.
     *
     * @param pageable pageable
     * @return {@link QueryParamsBuilder}
     */
    public QueryParamsBuilder<T, ID> withPageable(Pageable pageable) {
        simpleQueryParams.pageable = pageable;
        return this;
    }

    /**
     * Сортировка.
     *
     * @param sort sort
     * @return {@link QueryParamsBuilder}
     */
    public QueryParamsBuilder<T, ID> withSort(Sort sort) {
        simpleQueryParams.sort = sort;
        return this;
    }


    /**
     * Список навигируемых свойств.
     *
     * @param preloadsList preloadsList
     * @return {@link QueryParamsBuilder}
     */
    public QueryParamsBuilder<T, ID> withPreloadsList(Set<String> preloadsList) {
        simpleQueryParams.preloads = preloadsList;
        return this;
    }

    /**
     * Собрать результат работы билдера.
     *
     * @return {@link SimpleQueryParams}
     */
    public QueryParams<T, ID> build() {
        return simpleQueryParams;
    }

    private class SimpleQueryParams implements QueryParams<T, ID> {
        private ID id;
        private Pageable pageable;
        private Sort sort;
        private Predicate predicate;
        private Set<String> preloads = new HashSet<>();
        private String typeName;

        private SimpleQueryParams() {
        }

        @Override
        public ID getId() {
            return id;
        }

        @Override
        public Pageable getPageable() {
            return pageable;
        }

        @Override
        public Sort getSort() {
            return sort;
        }

        @Nonnull
        @Override
        public Predicate getPredicate() {
            return predicate;
        }

        @Override
        public Set<String> getPreloads() {
            return preloads;
        }

        @Nonnull
        @Override
        public String getDomainObjectTypeName() {
            return typeName;
        }
    }
}
