package ru.croc.ctp.jxfw.wc.web.bootloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Конфигурация Bootloader'a.
 */
class BootloaderConfig {

    /**
     * Список js-скриптов с условиями.
     */
    public final List<BootloaderScriptConfig> scripts = new ArrayList<>();

    /**
     * Список css-скриптов с условиями.
     */
    public final List<BootloaderScriptConfig> styles = new ArrayList<>();

    /**
     * "boot" каталог (путь относительно <see cref="XConfig.ClientBase"/>)
     */
    public String bootDir;

    /**
     * Конфигурация загрузки скрипта require.js
     */
    public RequirejsConfig requirejsConfig;

    /**
     * Конфигурация AppCache.
     */
    public AppCacheConfig appCache;

    /**
     * Тип загрузчика: requirejs или webpack.
     */
    public LoaderProvider loaderProvider = LoaderProvider.REQUIREJS;

}
