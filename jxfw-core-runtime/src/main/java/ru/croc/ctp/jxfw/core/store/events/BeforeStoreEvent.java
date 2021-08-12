package ru.croc.ctp.jxfw.core.store.events;

import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Событие "До сохранения UoW".
 * 
 */
@SuppressWarnings("serial")
public final class BeforeStoreEvent extends StoreEvent {
	
	/**
	 * 
	 * @param source
	 * @param storeContext
	 */
	public BeforeStoreEvent(Object source, StoreContext storeContext) {
        super(source, storeContext);
    }
}
