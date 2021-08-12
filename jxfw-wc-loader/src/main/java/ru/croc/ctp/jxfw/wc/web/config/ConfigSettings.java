package ru.croc.ctp.jxfw.wc.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Параметры настройки приложения. Умолчания заданы литералами.
 * Могут переопределяться в properties-файлах или через переменные среды.
 */
@Component
@EnableConfigurationProperties(ConfigSettings.class)
@ConfigurationProperties(prefix = "croc")
public class ConfigSettings {
    private String clientBase = "client";
    /**
     * Наименование файла "main.config.json" (конфигурация для объединения с XConfig)
     */
    private String mainConfigFile = "main.config.json";
    /**
     * Наименование файла "require.config.json" (конфигация RequireJS)
     */
    private String requireConfigFile = "require.config.json";
    private Boolean debug = true;
    private String stage = "dev-debug";
    @Value("${ru.croc.ctp.wc.apiVersion:0}")
    private Integer apiVersion;

    public Integer getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(Integer apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getClientBase() {
        return clientBase;
    }

    public void setClientBase(String clientBase) {
        this.clientBase = clientBase;
    }

    public String getMainConfigFile() {
        return mainConfigFile;
    }

    public void setMainConfigFile(String mainConfigFile) {
        this.mainConfigFile = mainConfigFile;
    }

    public String getRequireConfigFile() {
        return requireConfigFile;
    }

    public void setRequireConfigFile(String requireConfigFile) {
        this.requireConfigFile = requireConfigFile;
    }

    public Boolean isDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    // TODO Получать версию билда
    public String getVersion() {
        return "";

    }
}
