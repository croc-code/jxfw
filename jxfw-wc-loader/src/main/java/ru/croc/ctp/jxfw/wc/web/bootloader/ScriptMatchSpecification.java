package ru.croc.ctp.jxfw.wc.web.bootloader;

/**
 * Спецификация условия if-match/if-not-match, считываемая из конфигурации loader.scripts[].
 *
 * @since 1.0
 */
class ScriptMatchSpecification {
    /**
     * Условие на браузер. Может быть наименовнием ("ie", "firefox", "chrome", "opera", "safari"),
     * либо наименованием с версией через точку ("ie.7", "chrome.29")
     * Наименование браузера в конфигурации будет сравниваться с наименованием из свойства
     * <see cref="HttpBrowserCapabilitiesBase.Browser" />
     */
    public String browser;

    /**
     * Тип устройства:
     * <ul>
     * <li>mobile</li>
     * <li>desktop</li>
     * </ul>.
     */
    public Boolean isMobileDevice;

    /**
     * Размер экрана по классификации {@link DeviceScreenSizes}.
     */
    public DeviceScreenSizes screen;

    /**
     * Условие на версию браузера имея в виду "меньше или равно",
     * т.е. условие будет выполнено, если версия браузера клиента меньше или равно заданной в этом свойстве.
     */
    Integer browserVersion;

    /**
     * Проверка совпадения условия на основе параметров клиента..
     *
     * @param clientParams - параметры клиента.
     * @return совпадает/ не совпадает
     */
    public boolean isMatch(ScriptMatchSpecification clientParams) {
        return ScriptMatcher.isMatch(this, clientParams);
    }
}
