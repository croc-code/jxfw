package ru.croc.ctp.jxfw.core.store.events;

import org.springframework.context.ApplicationEvent;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.store.StoreContext;

import java.util.List;

/**
 * Абстрактный класс для событий сохранения.
 *
 * @see BeforeStoreEvent
 * @see AfterStoreEvent
 * @since 1.0
 */
@SuppressWarnings("serial")
public abstract class StoreEvent extends ApplicationEvent {

 	private final StoreContext storeContext;
    
	/**
	 * @param source
	 * @param storeContext
	 */
    public StoreEvent(Object source, StoreContext storeContext) {
        super(source);
        this.storeContext = storeContext;
    }

    @Deprecated 
    public List<? extends DomainObject<?>> getStoreObjects() {
        return storeContext.getDomainObjects();
    }
    
    /**
     * 
     * @return контекст сохранения
     */
    public StoreContext getStoreContext() {
        return storeContext;
    }
}