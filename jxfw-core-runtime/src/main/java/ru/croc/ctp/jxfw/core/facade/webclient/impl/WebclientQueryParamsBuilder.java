package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import com.querydsl.core.types.Predicate;
import java8.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.impl.PaginationUtil;
import ru.croc.ctp.jxfw.core.load.impl.SortUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Билдер QueryParams из клиентского запроса.
 *
 * @param <T>  тип
 * @param <ID> идентификатор.
 *
 * @since 1.6
 * @author OKrutova
 */
@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class WebclientQueryParamsBuilder<T extends DomainObject<ID>, ID extends Serializable> {

    private final RequestParams params = new RequestParams();

    private List<PredicateProvider> predicateProviders;

    @Autowired
    public void setPredicateProviders(List<PredicateProvider> predicateProviders) {
        this.predicateProviders = predicateProviders;
    }


    /**
     * Установить тип доменного объекта.
     *
     * @param domainTypeName тип доменного объекта
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withDomainType(@Nonnull String domainTypeName) {
        params.domainTypeName = domainTypeName;
        return this;
    }
    
    /**
     * Установить тип доменного объекта.
     *
     * @param domainType тип доменного объекта
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withDomainType(@Nonnull Class<T> domainType) {
        params.domainTypeName = DomainObjectUtil.getDomainObjectTypeName(domainType);
        return this;
    }
    
    /**
     * Установить property.
     *
     * @param property property
     * @return себя
     */
    /*public WebclientQueryParamsBuilder<T, ID> withProperty(String property) {
        params.property = property;
        return this;
    }*/

    /**
     * Установить id.
     *
     * @param id id
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withId(ID id) {
        params.id = id;
        return this;
    }

    /**
     * Установить filter.
     *
     * @param filter filter
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withFilter(ObjectFilter filter) {
        params.objectFilter = filter;
        return this;
    }

    /**
     * Установить orderBy.
     *
     * @param orderBy orderBy
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withOrderBy(String orderBy) {
        params.orderBy = orderBy;
        return this;
    }

    /**
     * Установить expand.
     *
     * @param expand expand
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withExpand(String expand) {
        params.expand = expand;
        return this;
    }

    /**
     * Установить top.
     *
     * @param top top
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withTop(Integer top) {
        params.top = top;
        return this;
    }

    /**
     * Установить skip.
     *
     * @param skip skip
     * @return себя
     */
    public WebclientQueryParamsBuilder<T, ID> withSkip(Integer skip) {
        params.skip = skip;
        return this;
    }


    /**
     * Выполнить построение.
     *
     * @return экземпляр QueryParams
     */
    public QueryParams<T, ID> build() {
        // FIXME JXFW-1215 что тут будет с комплексными ключами в CASS и SOLR?
        // падает ru.croc.ctp.jxfw.cass.tests.ReadConveyerTest#testEventsOfSpecifiedTypesFired

        if (params.predicateProvider == null) {
            for (PredicateProvider predicateProvider : predicateProviders) {
                if (predicateProvider.accepts(params.domainTypeName)) {
                    params.predicateProvider = predicateProvider;
                    break;
                }
            }
        }
        params.build();

        return params;
    }


    /**
     * Содержит все параметры запроса доменного контроллера.
     */
    private class RequestParams implements QueryParams<T, ID> {

        private ID id;
        private String domainTypeName;

        private String expand;
        private String orderBy;
        private ObjectFilter objectFilter = new ObjectFilter();
        private Integer top;
        private Integer skip;
        //private String property;
        private PredicateProvider predicateProvider;


        private Pageable pageable;
        private Sort sort;
        private Predicate predicate;
        private Set<String> preloads;

        private void build() {

            Objects.requireNonNull(domainTypeName, "DomainType must be not null");

            if (skip != null || top != null) {
                pageable = PaginationUtil.create(skip, top,
                        PaginationUtil.parseOrderByProp(orderBy));
            } else {
                pageable = null;
            }
            if (orderBy != null) {
                sort = SortUtil.parse(orderBy);
            } else {
                sort = Sort.unsorted();
            }

            predicate = predicateProvider.buildPredicate(domainTypeName,
                    objectFilter,
                    sort, pageable);

            if (expand == null) {
                preloads = Collections.emptySet();
            } else {
                preloads = new HashSet<>(Arrays.asList(expand.split(",")));
                preloads.removeIf(StringUtils::isBlank);
            }
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
            return domainTypeName;
        }
    }
}
