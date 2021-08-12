package ru.croc.ctp.jxfw.core.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Настройки модуля core.
 *
 * @author Sergey Verkhushin
 * @since 1.8.8
 */
@Service
public class CorePreferences {

    @Value("${ru.croc.ctp.jxfw.core.store.events.domainObjectStoreEvent.enable:true}")
    private boolean enableDomainObjectStoreEvent;

    /**
     * Получает значение настройки включения публикации события EnableDomainObjectStoreEvent.
     *
     * @return {@code true} если настройка включена.
     */
    public boolean isEnableDomainObjectStoreEvent() {
        return enableDomainObjectStoreEvent;
    }
}
