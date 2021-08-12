package ru.croc.ctp.jxfw.core.load.events;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;



/**
 * Событие для применения прикладной логики к условиям загрузки.
 * Публикуется в конвейере чтения после {@link PreCheckSecurityEvent}, до загрузки данных из хранилища.
 *
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 * @author Nosov Alexander
 * @since 1.2
 */
public class BeforeLoadEvent<T extends DomainObject<?>> extends LoadEvent<T> {
    /**
     * Конструктор.
     *
     * @param queryParams параметры запроса.
     * @param loadContext контекст read конвейера.
     */
    public BeforeLoadEvent(QueryParams<T, ?> queryParams, LoadContext<T> loadContext) {
        super(queryParams, loadContext);
    }

}
