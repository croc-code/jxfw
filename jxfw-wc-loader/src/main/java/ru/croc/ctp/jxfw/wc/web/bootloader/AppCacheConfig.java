package ru.croc.ctp.jxfw.wc.web.bootloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация AppCache.
 * @since 1.0
 */
class AppCacheConfig {
    /**
     * Список скриптов для секции CACHE.
     */
    public final List<BootloaderScriptConfig> include = new ArrayList<>();

    /**
     * Признак включенной поддержки.
     */
    public Boolean enabled;
}
