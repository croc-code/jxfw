package ru.croc.ctp.jxfw.wc.web.bootloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Элемент конфигурации условной загрузки скрипта/файла для Bootloader.
 */
class BootloaderScriptConfig {

    /**
     * Список скриптов.
     */
    public final List<String> scripts;

    /**
     * Спецификация скриптов/файлов если условие совпадает.
     */
    final ScriptMatchSpecification ifMatch;

    /**
     * Спецификация скриптов/файлов если условие не совпадает.
     */
    final ScriptMatchSpecification ifNotMatch;

    /**
     * @param script - скрипт.
     */
    BootloaderScriptConfig(String script) {
        scripts = new ArrayList<>();
        scripts.add(script);
        ifMatch = null;
        ifNotMatch = null;
    }

    /**
     * @param scripts        - список скриптов для загрузки на страницу.
     * @param specIfMatch    - спецификация если условие совпадает с требованием.
     * @param specIfNotMatch - спецификация если условие НЕ совпадает с требованием.
     */
    BootloaderScriptConfig(List<String> scripts, ScriptMatchSpecification specIfMatch,
                           ScriptMatchSpecification specIfNotMatch) {
        this.scripts = scripts;
        ifMatch = specIfMatch;
        ifNotMatch = specIfNotMatch;
    }
}
