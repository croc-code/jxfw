package ru.croc.ctp.jxfw.core.store.events;

import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Событие возникающее после сохранения UoW.
 *
 * @since 1.0
 */
@SuppressWarnings("serial")
public final class AfterStoreEvent extends StoreEvent {
    
    /**
     * Конструктор.
     *
     * @param source       - источник события.
     * @param storeContext - контекст сохранения с результатом операции сохранение.
     */
    public AfterStoreEvent(Object source, StoreContext storeContext) {
        super(source, storeContext);
    }
}
