package ru.croc.ctp.jxfw.jpa.datasource;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPQLQuery;
import ru.croc.ctp.jxfw.core.datasource.DataSourceLoader;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * DataSourceLoader c пагинацией, реализованной с использованием JPQLQuery.
 * От прикладного кола требуется сконструировать JPQLQuery.
 * Данная имплементация выполняет запрос данных по JPQLQuery с учетом пагинации, которая была запрошена с клиента
 * и формирует хинты с информацией о пагинации.
 *
 * @param <T>  доменный тип
 * @param <ID> идентификатор доменного типа
 * @author OKrutova
 * @since 1.6.0
 */
public abstract class PagingJpaDataSourceLoader<T extends DomainObject<ID>, ID extends Serializable>
        extends DataSourceLoader<T, ID> {

    @Nonnull
    @Override
    public Predicate createPredicate() {
        return new BooleanBuilder();
    }

    /**
     * Построение JPQLQuery. При построении обязательно должен быть учтен предикат
     * whereClause, т.к. он может содержать дополнительные условия, установленные в событиях до загрузки.
     * @param loadContext контекст загрузки
     * @param queryParams параметры загрузки
     * @param whereClause предустановленный предикат, тот же, что и в составе queryParams, но приведенный к типу
     *                    BooleanBuilder
     *
     * @return JPQLQuery
     */
    @Nonnull
    public abstract JPQLQuery<T> query(@Nonnull LoadContext<T> loadContext, @Nonnull BooleanBuilder whereClause,
                                       @Nonnull QueryParams<T, ID> queryParams);

    @Nonnull
    @Override
    public LoadResult<T> load(@Nonnull LoadContext<T> loadContext, @Nonnull QueryParams<T, ID> queryParams) {
        LoadResult<T> loadResult = new LoadResult<>();
        JPQLQuery<T> query = query(loadContext, (BooleanBuilder) queryParams.getPredicate(),
                queryParams);
        if (queryParams.getPageable() != null) {
            loadResult.getData().addAll(getPagingResult(query,
                    queryParams.getPageable().getOffset(),
                    queryParams.getPageable().getPageSize(),
                    loadResult.getHints()));

        } else {
            loadResult.getData().addAll(query.fetch());
        }
        return loadResult;

    }


    private List<T> getPagingResult(JPQLQuery<T> query, long skip, long top,
                                    Map<String, Object> hints) {
        hints.put("paging", true);

        List<T> data = query
                .offset(skip)
                .limit(top + 1) // запрашиваем на 1 запись больше, чтобы выяснить, есть ли еще записи
                .fetch();

        if (data.size() > top) {
            // Запрос может вернуть на 1 запись больше, чтобы определить, есть ли еще записи.
            // Лишнюю запись удаляем и проставляем хинт.
            data.remove(data.size() - 1);
            hints.put("hasNext", true);
        }

        if (getFetchTotal() != null && getFetchTotal()) {
            long total = query.fetchCount();
            hints.put("total", total);
        }
        return data;

    }

    @Override
    public boolean supportsPaging() {
        return true;
    }

}
