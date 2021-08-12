package ru.croc.ctp.jxfw.wc.web.config;

import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Объект конфигурации.
 *
 * @author Nosov Alexander
 * @see ConfigModule
 * @see BaseConfigModule
 * @since 1.2
 */
public class XConfig extends HashMap<String, Object> {

    private static final long serialVersionUID = 7640093877483380054L;

    private static final String ROOT = "root";

    private static final String CLIENT_BASE = "clientBase";

    private static final String SECURITY = "security";

    private static final String SUPPORT_OFFLINE = "supportOffline";

    private static final String DEFAULT_LANGUAGE = "defaultLanguage";

    private static final String IS_DEBUG = "isDebug";

    private static final String MODULES = "modules";

    private static final String FILES = "files";

    private static final String API_VERSION = "apiVersion";

    @JsonIgnore
    private static final String LOADER = "loader";

    /**
     * Версии приложений.
     */
    private SoftwareInfo software;

    /**
     * Json-объект конфигурации (мерж XConfig и main.config.json), отправляемый
     * на клиент.
     */
    @JsonIgnore
    private Map<String, Object> clientJson;

    /**
     * Json-объект конфигурации RequireJS, отправляемый на клиент.
     */
    @JsonIgnore
    private Map<String, Object> requireJson;

    /**
     * @return Признак поддержки Offline-режима (HTML5 AppCache & other features).
     */
    public boolean isSupportOffline() {
        final Object isSupportOffline = get(SUPPORT_OFFLINE);
        return isSupportOffline != null && (boolean) isSupportOffline;
    }

    /**
     * @param supportOffline - признак поддержки Offline-режима (HTML5 AppCache & other features).
     */
    public void setSupportOffline(boolean supportOffline) {
        put(SUPPORT_OFFLINE, supportOffline);
    }

    public String getDefaultLanguage() {
        return (String) get(DEFAULT_LANGUAGE);
    }

    /**
     * @param defaultLanguage - язык по-умолчанию.
     */
    public void setDefaultLanguage(String defaultLanguage) {
        put(DEFAULT_LANGUAGE, defaultLanguage);
    }

    public Boolean isDebug() {
        return get(IS_DEBUG) != null && (Boolean) get(IS_DEBUG);
    }

    /**
     * @param debug - признак отладочного режима. Инициализиуется значением настройки
     *              "croc.debug" из web.config/appSettings
     */
    public void setDebug(Boolean debug) {
        put(IS_DEBUG, debug);
    }

    public Integer getApiVersion() {
        return (Integer) get(API_VERSION);
    }

    /**
     * @param version - версия api приложения.
     */
    public void setApiVersion(Integer version) {
        put(API_VERSION, version);
    }

    public String getRoot() {
        return (String) get(ROOT);
    }

    /**
     * @param root - Url корень веб-приложения: это корень сайта ("/"), либо адрес веб-приложения ("/myapp/").
     *             Всегда заканчивается на "/".
     */
    public void setRoot(String root) {
        if (StringUtils.isEmpty(root) || root.equals("/")) {
            root = null;
        } else if (!root.endsWith("/")) {
            root = root + "/";
        }
        put(ROOT, root);
    }

    public String getClientBase() {
        return (String) get(CLIENT_BASE);
    }

    /**
     * @param clientBase - Относительное имя серверной папки клиентских скриптов (по-умолчанию "client").
     */
    public void setClientBase(String clientBase) {
        put(CLIENT_BASE, clientBase);
    }

    public XSecurityConfig getSecurity() {

        //ObjectMapper может распарсить json в иную реализацию мапы
        Map map = (Map) get(SECURITY);
        if (map instanceof XSecurityConfig) {
            return (XSecurityConfig) map;
        } else {
            XSecurityConfig xSecurityConfig = new XSecurityConfig();
            xSecurityConfig.putAll(map);
            return xSecurityConfig;
        }
    }

    /**
     * @param security - конфигурация безопасности.
     */
    public void setSecurity(XSecurityConfig security) {
        put(SECURITY, security);
    }

    public Map<String, Object> getClientJson() {
        return clientJson;
    }

    /**
     * @return конфигурацию модуля по работе с бинарными данными.
     */
    public XFilesModuleConfig getFilesModuleConfig() {
        final Map<String, Object> modules = getModules();
        if (modules == null || modules.get(FILES) == null) {
            return new XFilesModuleConfig();
        }
        final Map<String, Object> map = (Map) modules.get(FILES);
        //изза ClassCast проблемы
        final XFilesModuleConfig xFilesModuleConfig = new XFilesModuleConfig();
        for (Entry<String, Object> entry : map.entrySet()) {
            xFilesModuleConfig.put(entry.getKey(), entry.getValue());
        }

        return xFilesModuleConfig;
    }

    /**
     * @param filesModuleConfig конфигурацию модуля по работе с бинарными данными.
     */
    public void setFilesModuleConfig(XFilesModuleConfig filesModuleConfig) {
        Map<String, Object> modules = getModules();
        if (modules == null) {
            setModules(new HashMap<>());
            modules = getModules();
        }
        modules.put(FILES, filesModuleConfig);
    }

    /**
     * @param clientJson - JSON-MAP с конфигурацией xconfig.
     */
    public void setClientJson(Map<String, Object> clientJson) {
        this.clientJson = clientJson;
    }

    public Map<String, Object> getRequireJson() {
        return requireJson;
    }

    /**
     * @param requireJson - JSON-MAP с конфигурацией для RequireJS.
     */
    public void setRequireJson(Map<String, Object> requireJson) {
        this.requireJson = requireJson;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getLoader() {
        return get(LOADER) == null ? newHashMap() : (Map<String, Object>) get(LOADER);
    }

    /**
     * @param loader - конфигурация Bootloader.
     */
    public void setLoader(Map<String, Object> loader) {
        put(LOADER, loader);
    }

    public SoftwareInfo getSoftware() {
        return software;
    }

    public void setSoftware(SoftwareInfo software) {
        this.software = software;
    }

    public Map<String, Object> getModules() {
        return (Map<String, Object>) get(MODULES);
    }

    /**
     * @param modules - раздел modules из конфига main.config.json.
     */
    public void setModules(Map<String, Object> modules) {
        put(MODULES, modules);
    }
}
