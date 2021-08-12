package ru.croc.ctp.jxfw.solr.load.events;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;
import ru.croc.ctp.jxfw.solr.predicate.SolrQueryContext;

import javax.annotation.Nonnull;


/**
 * Событие для применения прикладной логики к условиям загрузки JPA-сущностей
 * Публикуется в конвейере чтения после {@link SolrPreCheckSecurityEvent}, до загрузки данных из хранилища.
 * Отличается от базового тем, что тип возвращаемого предиката - SolrQueryContext.
 *
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 * @author OKrutova
 * @since 1.6
 */
public class SolrBeforeLoadEvent<T extends DomainObject<?>> extends BeforeLoadEvent<T> {

    /**
     * Создание нового события.
     *
     * @param queryParams параметры запроса.
     * @param loadContext контекст read конвейера.
     */
    public SolrBeforeLoadEvent(QueryParams<T, ?> queryParams, LoadContext<T> loadContext) {
        super(queryParams, loadContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public SolrQueryContext<T> getPredicate() {
        if (!(super.getPredicate() instanceof SolrQueryContext)) {
            throw new IllegalStateException(
                    "Predicate is not a SolrQueryContext instance for " + getDomainObjectTypeName());
        }
        return (SolrQueryContext<T>) super.getPredicate();
    }
}
