package ru.croc.ctp.jxfw.wc.web.mvc;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import ru.croc.ctp.jxfw.wc.web.bootloader.IBootloader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Модель данных для главной страницы веб-приложения.
 *
 * @since 1.0
 */
public class MainPageModel {

    /**
     * Клиентская конфигурация (слияние XConfig и json'a из main.config.json),
     * сериализованная в json-строку
     */
    private String clientConfigJsonString;

    /**
     * Конфигурация для RequireJS, сериализованная в json-строку.
     */
    private String requireConfigJsonString;

    /**
     * Путь до файла, задаваемого в атрибуте data-main тега script для
     * require.js. Путь относительно папки приложения, например "client/main.js"
     */
    private String mainScriptPath;
    /**
     * Относительный путь для веб-сервера до main.js. В отличии от
     * MainScriptPath уже учитывает имя директории приложения. Всегда начинается
     * с "/". Пример: "/myapp/client/main.js"
     */
    private String mainScriptServerPath;

    /**
     * Информация о текущем браузере клента.
     */
    private BrowserInfo browser;

    private IBootloader bootloader;
    private List<Object> styles;


    private String locale;


    public String getMainScriptPath() {
        return mainScriptPath;
    }

    public void setMainScriptPath(String mainScriptPath) {
        this.mainScriptPath = mainScriptPath;
    }

    public String getMainScriptServerPath() {
        return mainScriptServerPath;
    }

    public void setMainScriptServerPath(String mainScriptServerPath) {
        this.mainScriptServerPath = mainScriptServerPath;
    }

    public BrowserInfo getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserInfo browser) {
        this.browser = browser;
    }

    public String getClientConfigJsonString() {
        return clientConfigJsonString;
    }

    public void setClientConfigJsonString(String clientConfigJsonString) {
        this.clientConfigJsonString = clientConfigJsonString;
    }

    public String getRequireConfigJsonString() {
        return requireConfigJsonString;
    }

    public void setRequireConfigJsonString(String requireConfigJsonString) {
        this.requireConfigJsonString = requireConfigJsonString;
    }

    /**
     * Возвращает содержимое js-скриптов, которые надо поместить на страницу при
     * первоначальной загрузке.
     *
     * @return Список содержимого файлов или null
     */
    public List<String> getBootScripts() {
        List<String> list = new ArrayList<>();
        Resource[] scriptFiles = getBootloader().getBootScripts();
        if (scriptFiles != null) {
            for (Resource res : scriptFiles) {
                InputStream is = null;
                try {
                    is = res.getInputStream();
                    list.add(IOUtils.toString(is, StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }
        }

        return list;
    }

    public IBootloader getBootloader() {
        return bootloader;
    }

    public void setBootloader(IBootloader bootloader) {
        this.bootloader = bootloader;
    }

    public List<Object> getStyles() {
        return styles;
    }

    public void setStyles(List<Object> styles) {
        this.styles = styles;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }
}
