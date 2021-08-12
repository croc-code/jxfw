package ru.croc.ctp.jxfw.core.store.context;

import ru.croc.ctp.jxfw.core.load.LoadContext;

/**
 * Ключи дополнительных атрибутов контекста сохранения.
 *
 * @author Sergey Verkhushin
 * @since 1.6.5
 */
public interface StoreContextKeys {

    /**
     * Common object для LoadContext, если в случае сохранения объекта создавался {@link LoadContext}.
     */
    String LOAD_CONTEXT_COMMON_OBJECT = "store_context.loadContextCommonObject";
}
