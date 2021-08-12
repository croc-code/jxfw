package ru.croc.ctp.jxfw.wc.web.bootloader;

/**
 * Компонент, сравнивающий два объекта <see cref="ScriptMatchSpecification"/>,
 * один из конфигурации, другой для текущего runtime клиента.
 *
 * @since 1.0
 */
class ScriptMatcher {
    /**
     * Проверка совпадения условия.
     *
     * @param specConfig  - конфиг спецификации.
     * @param specRuntime - спецификация среды исполнения
     * @return совпадение есть/отсуствует
     */
    static boolean isMatch(ScriptMatchSpecification specConfig,
                           ScriptMatchSpecification specRuntime) {
        // Browser: Условие на браузер
        if (specConfig.browser != null) {
            if (!specConfig.browser.equalsIgnoreCase(specRuntime.browser)) {
                return false;
            }
            if (specConfig.browserVersion != null
                    && specRuntime.browserVersion != null) {
                if (specConfig.browserVersion < specRuntime.browserVersion) {
                    return false;
                }
            }
        }

        // Device: условие на тип устройста
        if (specConfig.isMobileDevice != null) {
            if (!specConfig.isMobileDevice.equals(specRuntime.isMobileDevice)) {
                return false;
            }
        }

        // Screen: Условие на размер экрана
        if (specConfig.screen != null && specRuntime.screen != null) {
            if (!specConfig.screen.equals(specRuntime.screen)) {
                return false;
            }
        }

        return true;
    }
}
