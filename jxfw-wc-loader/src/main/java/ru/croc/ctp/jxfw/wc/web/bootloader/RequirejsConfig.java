package ru.croc.ctp.jxfw.wc.web.bootloader;

/**
 * Конфигурация для RequireJS.
 *
 * @since 1.0
 */
public class RequirejsConfig {

    /**
     * Активность.
     */
    public Boolean enabled;

    /**
     * Относительный путь (от <see cref="XConfig.ClientBase"/>) до require.js
     */
    public String scriptPath;

    /**
     * Как загружать скрипт:
     * true - inline, т.е. вместе со всеми скриптами в Bootloader'e;
     * fasle - грузить require.js отдельно от всех скриптов (через динамическую вставку тега script)
     */
    public Boolean loadInline;
}
