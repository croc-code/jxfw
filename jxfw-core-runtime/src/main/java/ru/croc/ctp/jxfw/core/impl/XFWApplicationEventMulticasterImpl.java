package ru.croc.ctp.jxfw.core.impl;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

/**
 * Реализация {@link ApplicationEventMulticaster}, способная проверить существует ли обработчик события в приложении.
 *
 * @author Sergey Verkhushin
 * @since 1.8.8
 */
@Component(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME)
public class XFWApplicationEventMulticasterImpl extends SimpleApplicationEventMulticaster {

    /**
     * Проверяет, существует ли обработчик события.
     *
     * @param event
     *        экземпляр события.
     * @return {@code true} в случае, если обработчик события существует.
     */
    public boolean existsListener(Object event) {
        ApplicationEvent applicationEvent;
        ResolvableType eventType = null;
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent) event;
        } else {
            applicationEvent = new PayloadApplicationEvent<>(this, event);
            eventType = ((PayloadApplicationEvent) applicationEvent).getResolvableType();
        }

        return !getApplicationListeners(applicationEvent,
                (eventType == null ? ResolvableType.forInstance(event) : eventType)).isEmpty();
    }
}
