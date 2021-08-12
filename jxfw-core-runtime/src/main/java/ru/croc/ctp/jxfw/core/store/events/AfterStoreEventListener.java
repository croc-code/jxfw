package ru.croc.ctp.jxfw.core.store.events;

import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;

/**
 * Интерфейс для слушателя события {@link AfterStoreEvent}.
 * 
 * @since 1.0
 */
public interface AfterStoreEventListener extends Ordered, ApplicationListener<AfterStoreEvent> {

    @Override
    default int getOrder() {
        return OrderUtils.getOrder(this.getClass(), 0);
    }
}
