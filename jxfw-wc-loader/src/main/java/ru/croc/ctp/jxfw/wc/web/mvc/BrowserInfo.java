package ru.croc.ctp.jxfw.wc.web.mvc;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import org.apache.commons.lang.StringUtils;


/**
 * Тип данных для хранения информации об браузере.
 *
 * @since 1.1
 */
public class BrowserInfo {
    /**
     * Имя браузера для PhantomJS.
     */
    private static final String PHANTOM_JS = "PHANTOMJS";

    /**
     * Признак браузера IE. Subject to change!.
     */
    private Boolean isIe;

    /**
     * Версия браузера. Subject to change!.
     */
    private Double version;

    /**
     * Наименование браузера.
     */
    private String browser;

    /**
     * Платформа.
     */
    private String platform;

    /**
     * Значение из свойства <see cref="HttpRequestBase.Browser" />.
     */
    private UserAgent capabilities;

    /**
     * @param cap - объект {@link UserAgent}.
     * @param userAgentString - хедер из запроса.
     * @return информация об браузере через объект {@link UserAgent}.
     */
    public static BrowserInfo create(UserAgent cap, String userAgentString) {
        BrowserInfo browser = null;
        if (cap != null) {
            browser = new BrowserInfo();
            browser.capabilities = cap;
            String browserName = cap.getBrowser().toString();
            browser.isIe = browserName.contains("IE");
            browser.version = getDoubleVersion(cap.getBrowserVersion());

            //PhantomJS определяется версией 1.20 UserAgentUtils как "BOT", добавить свою версию браузера
            //можно лишь склонировав проект и внеся изменения, т.к. браузеры задаются перечислениями
            //(описано в FAQ либы).
            if (browserName.equals(Browser.BOT.name())
                    && StringUtils.isNotEmpty(userAgentString)
                    && userAgentString.toLowerCase().contains(PHANTOM_JS.toLowerCase())) { //PhantomJS
                browser.browser = PHANTOM_JS;

                String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(userAgentString, null);
                for (String token : tokens) {
                    if (token.toLowerCase().contains(PHANTOM_JS.toLowerCase())) {
                        browser.version = getDoubleVersion(token.replaceAll("[^\\d.]", ""));
                        break;
                    }
                }
            } else {
                browser.browser = browserName;
            }
        }
        return browser;
    }

    /**
     * @param cap - объект {@link UserAgent}.
     * @return информация об браузере через объект {@link UserAgent}.
     * @deprecated
     */
    @Deprecated
    public static BrowserInfo create(UserAgent cap) {
        return create(cap, null);
    }

    /** Возвращает версию как double, если отсутсвует или некорректна, то null.
     * @param version информация о версии
     * @return  сокращеная версия в формате Double или null.
     */
    private static Double getDoubleVersion(Version version) {
        if (version == null) {
            return null;
        }

        try {
            return Double.parseDouble(version.getMajorVersion()) + Double.parseDouble(version.getMinorVersion());
        } catch (NumberFormatException e) {
            // return null
        }

        return null;
    }

    /** Возвращает версию как double, если отсутсвует или некорректна, то null.
     * @param version информация о версии
     * @return  сокращеная версия в формате Double или null.
     */
    private static Double getDoubleVersion(String version) {
        if (StringUtils.isEmpty(version)) {
            return null;
        }

        try {
            return Double.parseDouble(version);
        } catch (NumberFormatException e) {
            // return null
        }

        return null;
    }

    public Boolean isInternetExplorer() {
        return isIe;
    }

    public Double getVersion() {
        return version;
    }

    public String getBrowser() {
        return browser;
    }

    public String getPlatform() {
        return platform;
    }

    public UserAgent getCapabilities() {
        return capabilities;
    }
}
