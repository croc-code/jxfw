package ru.croc.ctp.jxfw.core.load.events;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;


/**
 * Событие для наложения ограничений подсистемы безопасности на условие загрузки.
 * Публикуется в конвейере чтения первым до загрузки данных из хранилища.
 *
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 * @author Nosov Alexander
 * @since 1.2
 */
public class PreCheckSecurityEvent<T extends DomainObject<?>> extends LoadEvent<T> {
    /**
     * Конструктор.
     *
     * @param queryParams параметры запроса.
     * @param loadContext контекст read конвейера.
     */
    public PreCheckSecurityEvent(QueryParams<T, ?> queryParams, LoadContext<T> loadContext) {
        super(queryParams, loadContext);
    }


}
