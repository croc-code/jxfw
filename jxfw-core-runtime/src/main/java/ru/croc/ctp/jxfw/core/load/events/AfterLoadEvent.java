package ru.croc.ctp.jxfw.core.load.events;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.QueryParams;


/**
 * Событие для применения прикладной логики к результатам загрузки.
 * Публикуется в конвейере чтения после {@link CheckSecurityEvent}, после загрузки данных из хранилища.
 *
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 * @author Nosov Alexander
 * @since 1.2
 */
public final class AfterLoadEvent<T extends DomainObject<?>> extends LoadEventWithResult<T> {

    /**
     * Конструктор.
     *
     * @param loadResult  результаты загрузки
     * @param queryParams параметры запросов
     * @param loadContext контекст загрузки
     */
    public AfterLoadEvent(LoadResult<T> loadResult, QueryParams<T, ?> queryParams, LoadContext<T> loadContext) {
        super(loadResult, queryParams, loadContext);
    }
}
