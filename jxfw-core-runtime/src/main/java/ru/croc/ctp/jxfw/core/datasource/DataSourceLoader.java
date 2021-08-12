package ru.croc.ctp.jxfw.core.datasource;

import com.querydsl.core.types.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.datasource.impl.LoadingQueryParams;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.impl.PaginationUtil;
import ru.croc.ctp.jxfw.core.load.impl.SortUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Базовая реализация загрузчика для доменных источников данных.
 *
 * @param <T>  тип доменного объекта
 * @param <ID> тип идентификатора доменного объекта
 */
public abstract class DataSourceLoader<T extends DomainObject<ID>, ID extends Serializable>
        extends BaseDataSourceLoader implements DomainDataLoader<T, ID> {

    private Predicate predicate;

    private LoadService loadService;

    /**
     * Создание предиката разного типа в зависимости от типа хранилища.
     *
     * @return предикат
     */
    @Nonnull
    public abstract Predicate createPredicate();

    @Nonnull
    @Override
    public QueryParams<T, ID> queryParams() {
        return new LoadingQueryParams<T, ID>(new QueryParams<T, ID>() {
            @Override
            public ID getId() {
                return null;
            }

            @Override
            public Pageable getPageable() {
                if (getSkip() != null || getTop() != null) {
                    return PaginationUtil.create(getSkip(), getTop(),
                            PaginationUtil.parseOrderByProp(getOrderby()));
                } else {
                    return null;
                }
            }

            @Override
            public Sort getSort() {
                if (getOrderby() != null) {
                    return SortUtil.parse(getOrderby());
                } else {
                    return null;
                }
            }

            @Override
            @Nonnull
            public Predicate getPredicate() {
                return predicate;
            }


            @Override
            public Set<String> getPreloads() {
                if (getExpand() == null) {
                    return Collections.emptySet();
                } else {

                    Set<String> preloads = new HashSet<>(Arrays.asList(getExpand().split(",")));
                    preloads.removeIf(StringUtils::isBlank);
                    return preloads;
                }
            }

            @Nonnull
            @Override
            public String getDomainObjectTypeName() {
                return DataSourceLoader.this.getDomainObjectTypeName();
            }

        }, this);
    }

    /**
     * Короткое имя типа доменного объекта.
     *
     * @return имя
     */
    public abstract String getDomainObjectTypeName();

    @Nonnull
    @Override
    public final LoadResult<T> load(@Nonnull LoadContext<T> loadContext) {
        loadService.load(queryParams(), loadContext);
        return loadContext.getLoadResult();
    }


    public LoadService getLoadService() {
        return loadService;
    }

    @Autowired
    public void setLoadService(LoadService loadService) {
        this.loadService = loadService;
    }


    @Override
    public boolean supportsPaging() {
        return false;
    }

    @Override
    public void setParams(@Nullable final String expand, @Nullable final Integer top, @Nullable final Integer skip,
                          @Nullable final String orderby,
                          @Nullable final Boolean fetchTotal, @Nonnull final Map<String, Object> filterValues) {

        super.setParams(expand, top, skip, orderby, fetchTotal, filterValues);
        predicate = createPredicate();

    }
}
