package ru.croc.ctp.jxfw.core.export.impl;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.impl.PaginationUtil;

import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nonnull;


/**
 * Обертка вокруг QueryParams, создается на основе существующего экземпляра,
 * но подменяет ему настройки пагинации. Нужна для экспорта чанками.
 *
 * @param <T>  тип
 * @param <ID> идентификатор.
 * @author OKrutova
 * @since 1.6
 */
public class ExportChunkQueryParams<T extends DomainObject<ID>, ID extends Serializable> implements QueryParams<T, ID> {

    private final QueryParams<T, ID> queryParams;
    private final int top;
    private final int skip;


    /**
     * Конструкор.
     *
     * @param queryParams queryParams
     * @param top         top
     * @param skip        skip
     */
    public ExportChunkQueryParams(QueryParams<T, ID> queryParams, int top, int skip) {
        this.queryParams = queryParams;
        this.top = top;
        this.skip = skip;
    }


    @Override
    public Pageable getPageable() {
        return PaginationUtil.create(skip, top, getSort());
    }

    /*
     * delegation
     */
    @Override
    public ID getId() {
        return queryParams.getId();
    }


    @Override
    public Sort getSort() {
        return queryParams.getSort();
    }

    @Nonnull
    @Override
    public Predicate getPredicate() {
        return queryParams.getPredicate();
    }

/*    @Override
    public String getProperty() {
        return queryParams.getProperty();
    }*/

    @Override
    public Set<String> getPreloads() {
        return queryParams.getPreloads();
    }

    @Nonnull
    @Override
    public String getDomainObjectTypeName() {
        return queryParams.getDomainObjectTypeName();
    }
}
