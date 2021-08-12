package ru.croc.ctp.jxfw.core.store.events;

import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.OrderUtils;

/**
 * Слушатель для события {@link BeforeStoreEvent}.
 */
public interface BeforeStoreEventListener extends Ordered, ApplicationListener<BeforeStoreEvent> {

    @Override
    default int getOrder() {
        return OrderUtils.getOrder(this.getClass(), 0);
    }

}
