package ru.croc.ctp.jxfw.solr.predicate;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.Query;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import ru.croc.ctp.jxfw.solr.facade.webclient.CriteriaComposer;

import javax.annotation.Nullable;

/**
 * Контекст запроса к Solr.
 *
 * @param <T> класс доменного объекта.
 * @author smufazzalov
 * @since jXFW 1.6.0
 */
public class SolrQueryContext<T extends DomainObject> implements Predicate {
    private Class<T> clazz;
    private CriteriaComposer criteriaComposer;
    private ObjectFilter objectFilter;
    private Sort sort;
    private Pageable pageable;
    private Query query;

    /**
     * Конструктор.
     *
     * @param clazz            класс доменного объекта
     * @param criteriaComposer criteriaComposer
     * @param objectFilter     фильтрация с WC
     * @param sort             сортировки
     * @param pageable         пагинация
     */
    public SolrQueryContext(
            Class<T> clazz,
            CriteriaComposer criteriaComposer,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable
    ) {
        this.clazz = clazz;
        this.criteriaComposer = criteriaComposer;
        this.objectFilter = objectFilter;
        this.sort = sort;
        this.pageable = pageable;
    }

    private SolrQueryContext(Query query) {
        this.query = query;
    }

    /**
     * Обертка над готовым запросом, который не нужно строить.
     * @param query запрос.
     * @return Обретка над запросом
     */
    public static SolrQueryContext of(Query query) {
        return new SolrQueryContext(query);
    }

    /**
     * Запрос.
     *
     * @return Собрать запрос
     */
    public Query getQuery() {
        Query result = null;
        if (query == null) {
            result = criteriaComposer.createQuery(objectFilter, clazz);
        } else {
            result = query;
        }
        if (result.getSort() == null || Sort.unsorted().equals(result.getSort())) {
            result.addSort(sort);
        }
        if (pageable != null) {
            result.setPageRequest(pageable);
        }
        return result;
    }

    @Override
    public Predicate not() {
        return null;
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C context) {
        return null;
    }

    @Override
    public Class<? extends Boolean> getType() {
        return null;
    }

/*    public Class<T> getClazz() {
        return clazz;
    }*/

    public CriteriaComposer getCriteriaComposer() {
        return criteriaComposer;
    }

    public void setCriteriaComposer(CriteriaComposer criteriaComposer) {
        this.criteriaComposer = criteriaComposer;
    }

    public ObjectFilter getObjectFilter() {
        return objectFilter;
    }

    public void setObjectFilter(ObjectFilter objectFilter) {
        this.objectFilter = objectFilter;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public Pageable getPageable() {
        return pageable;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
}
