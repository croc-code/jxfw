package ru.croc.ctp.jxfw.core.datasource.impl;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.datasource.DomainDataLoader;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Класс - контейнер совмещает  QueryParams и DomainDataLoader,
 * позволяет делегировать из LoadService выполнение load прикладному коду.
 * Используется в датасорсах.
 *
 * @param <T>  тип
 * @param <ID> идентификатор.
 */
public class LoadingQueryParams<T extends DomainObject<ID>, ID extends Serializable>
        implements QueryParams<T, ID>, DomainDataLoader<T, ID> {

    private final QueryParams<T, ID> queryParams;
    private final DomainDataLoader<T, ID> dataSourceLoader;

    /**
     * Конструктор.
     * @param queryParams параметры запроса
     * @param dataSourceLoader загрузчик
     */
    public LoadingQueryParams(@Nonnull QueryParams<T, ID> queryParams,
                              @Nonnull DomainDataLoader<T, ID> dataSourceLoader) {
        this.queryParams = queryParams;
        this.dataSourceLoader = dataSourceLoader;
    }

    @Override
    public ID getId() {
        return queryParams.getId();
    }

    @Override
    public Pageable getPageable() {
        return queryParams.getPageable();
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

    @Override
    public Set<String> getPreloads() {
        return queryParams.getPreloads();
    }

    @Nonnull
    @Override
    public String getDomainObjectTypeName() {
        return queryParams.getDomainObjectTypeName();
    }

    @Nonnull
    @Override
    public LoadResult<T> load(@Nonnull LoadContext<T> loadContext, @Nonnull QueryParams<T, ID> queryParams) {
        return dataSourceLoader.load(loadContext, queryParams);
    }

    @Nonnull
    @Override
    public LoadResult<T> load(@Nonnull LoadContext<T> loadContext) {
        return dataSourceLoader.load(loadContext);
    }

    @Override
    public boolean supportsPaging() {
        return dataSourceLoader.supportsPaging();
    }
}
