package ru.croc.ctp.jxfw.wc.web.config;

import static com.google.common.collect.Maps.newHashMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Тип данных для хранения информации об security приложения.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
public class XSecurityConfig extends HashMap<String, Object> {

    private static final long serialVersionUID = -2686643728923898672L;

    /**
     * Имя проперти для 2F-аутентификации в файле main.config.json.
     */
    public static final String TWO_FACTOR_AUTH = "twoFactorAuth";
    /**
     * Включена.
     */
    public static final String ENABLED = "enabled";
    /**
     * Url конфирмации.
     */
    public static final String URL = "url";

    /**
     * Включена ли 2F-аутентификация.
     *
     * @return да/ нет
     */
    public boolean isTwoFactorAuthEnabled() {
        Map auth = (Map) get(TWO_FACTOR_AUTH);

        if (auth != null) {
            return Boolean.TRUE.equals(auth.get(ENABLED));
        }

        return false;
    }

    /**
     * Включить 2F-аутентификацию (Например способом конфигурации разработчиками через код).
     *
     * @param enabled true если нужно включить
     */
    public void setTwoFactorAuthEnabled(boolean enabled) {
        Map twoFactorAuth = (Map) get(TWO_FACTOR_AUTH);

        if (twoFactorAuth == null) {
            twoFactorAuth = newHashMap();
        }

        if (enabled) {
            twoFactorAuth.put(XSecurityConfig.ENABLED, true);
            put(XSecurityConfig.TWO_FACTOR_AUTH, twoFactorAuth);
        } else {
            remove(TWO_FACTOR_AUTH);
        }
    }

    /**
     * Добавить URL страницы конфирмации (Например способом конфигурации разработчиками через код).
     *
     * @param url адресс
     */
    public void setTwoFactorAuthUrl(String url) {
        Map twoFactorAuth = (Map) get(TWO_FACTOR_AUTH);

        if (twoFactorAuth == null) {
            twoFactorAuth = newHashMap();
            put(XSecurityConfig.TWO_FACTOR_AUTH, twoFactorAuth);

        }

        twoFactorAuth.put(URL, url);
    }

    /**
     * URL страницы конфирмации.
     *
     * @return адресс
     */
    public String getTwoFactorAuthUrl() {
        Map auth = (Map) get(TWO_FACTOR_AUTH);

        if (auth != null && auth.get(URL) != null) {
            return auth.get(URL).toString();
        }

        return null;
    }
}
