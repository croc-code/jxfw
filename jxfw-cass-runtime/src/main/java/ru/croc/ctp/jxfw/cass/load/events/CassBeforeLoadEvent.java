package ru.croc.ctp.jxfw.cass.load.events;

import ru.croc.ctp.jxfw.cass.predicate.CassandraQueryContext;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;

import javax.annotation.Nonnull;


/**
 * Событие для применения прикладной логики к условиям загрузки CASS-сущностей
 * Публикуется в конвейере чтения после {@link CassPreCheckSecurityEvent}, до загрузки данных из хранилища.
 * Отличается от базового тем, что тип возвращаемого предиката - CassandraQueryContext.
 *
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 * @author OKrutova
 * @since 1.6
 */
public class CassBeforeLoadEvent<T extends DomainObject<?>> extends BeforeLoadEvent<T> {

    /**
     * Создание нового события.
     *
     * @param queryParams параметры запроса.
     * @param loadContext контекст read конвейера.
     */
    public CassBeforeLoadEvent(QueryParams<T, ?> queryParams, LoadContext<T> loadContext) {
        super(queryParams, loadContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public CassandraQueryContext<T> getPredicate() {
        if (!(super.getPredicate() instanceof CassandraQueryContext)) {
            throw new IllegalStateException(
                    "Predicate is not a CassandraQueryContext instance for " + getDomainObjectTypeName());
        }
        return (CassandraQueryContext<T>) super.getPredicate();
    }
}
