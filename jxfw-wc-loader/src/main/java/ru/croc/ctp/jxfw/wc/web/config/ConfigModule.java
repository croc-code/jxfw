package ru.croc.ctp.jxfw.wc.web.config;

import java.util.Map;

/**
 * Интерфейс для компонента конфигурации приложения.
 * Данный класс может дополнять JSON файл.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
public interface ConfigModule extends ConfigModuleModifier {
    /**
     * Дефолтный базовый путь до JS файлов WC.
     */
    String DEFAULT_CLIENT_BASE = "client";

    /**
     * Дефолтное название файла конфигурации WC.
     */
    String DEFAULT_MAIN_CONFIG_FILE = "main.config.json";

    /**
     * Дефолтное название файла зависимостей WC.
     */
    String DEFAULT_REQUIRE_CONFIG_FILE = "require.config.json";

    /**
     * @return Создание конфигурационного файла приложения.
     */
    XConfig createClientConfig();

    /**
     * Обновление конфигурации из JSON-файла.
     *
     * @param config     - конфигурация.
     * @param jsonObject - десериализованный объект main.config.json
     */
    void updateFromJson(XConfig config, Map<String, Object> jsonObject);

    /**
     * @param clientBase - базовый путь до JS файлов WC.
     * @param configFile - имя файла конфигурации.
     * @return объект с настройками для WC.
     */
    Map<String, Object> loadJsonConfig(String clientBase, String configFile);
}
