package ru.croc.ctp.jxfw.wc.web.config;

/**
 * Инофрмация об версиях приложений.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class SoftwareInfo {
    /**
     * Версия клинтских библиотек (javascript).
     */
    public String clientLibVersion;

    /**
     * Версия серверной библиотеки.
     */
    public String serverLibVersion;

    /**
     * Версия приложения.
     */
    public String appVersion;

    /**
     * Конструктор.
     *
     * @param clientLibVersion - версия WC
     * @param serverLibVersion - версия jXFW
     * @param appVersion       - версия приложения
     */
    public SoftwareInfo(String clientLibVersion, String serverLibVersion, String appVersion) {
        this.clientLibVersion = clientLibVersion;
        this.serverLibVersion = serverLibVersion;
        this.appVersion = appVersion;
    }
}
