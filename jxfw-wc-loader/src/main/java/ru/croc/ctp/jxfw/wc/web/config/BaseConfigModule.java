package ru.croc.ctp.jxfw.wc.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Базовая реализация {@link ConfigModule}.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BaseConfigModule extends ApplicationObjectSupport implements ConfigModule {

    private static final Logger log = LoggerFactory.getLogger(BaseConfigModule.class);

    private ServletContext servletContext;
    private ConfigSettings configSettings;

    /**
     * Подкладываем если отсутствует файл main.config.json
     */
    private Map<String, Object> testMainConfigJson = Collections.unmodifiableMap(
            new HashMap<String, Object>() {
                {
                    put("loader", new LinkedHashMap<String, Object>() {
                        {
                            put("testConfig", true);
                            put("bootDir", "boot");
                            put("scripts", new ArrayList<>());
                            put("styles", new ArrayList<>());
                        }
                    });
                }
            }
    );

    @Autowired
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Autowired
    public void setConfigSettings(ConfigSettings configSettings) {
        this.configSettings = configSettings;
    }

    @Override
    public XConfig createClientConfig(XConfig config) {
        return config;
    }

    @Override
    @SuppressWarnings("unchecked")
    public XConfig createClientConfig() {
        final XConfig config = new XConfig();
        final String clientBase = configSettings.getClientBase();

        config.setApiVersion(configSettings.getApiVersion());

        if (isEmpty(clientBase)) {
            config.setClientBase(DEFAULT_CLIENT_BASE);
        } else {
            config.setClientBase(clientBase);
        }
        config.setDebug(configSettings.isDebug());

        final String contextPath = servletContext.getContextPath();
        if (!isEmpty(contextPath)) {
            config.setRoot(contextPath);
        }

        // 1. десериализуем main.config.json в Json-объект
        Map<String, Object> mainConfigJson = getMainConfigPropsMap(config);

        // 2. десериализуем require.config.json в Json-объект
        Map<String, Object> requireConfigJson = getRequireConfigPropsMap(config);

        // 3. мерджим секцию require из mainConfigJson (приоритет) в requireConfigJson
        requireConfigJson = updateRequireConfigJson(mainConfigJson, requireConfigJson);

        // 4. рекурсивно обновим XConfig данными из json-конфига (main.config.json)
        updateFromJson(config, mainConfigJson);

        config.setClientJson(mainConfigJson);
        config.setRequireJson(requireConfigJson);
        config.setModules((Map<String, Object>) mainConfigJson.get("modules"));

        return config;
    }

    private Map<String, Object> updateRequireConfigJson(
            Map<String, Object> mainConfigJson,
            Map<String, Object> requireConfigJson
    ) {
        Map<String, Object> requireFromMainConfig = (Map<String, Object>) mainConfigJson.get("require");

        if (requireFromMainConfig != null && requireConfigJson != null) {
            for (Map.Entry<String, Object> mentry : requireFromMainConfig.entrySet()) {
                String mentrykey = mentry.getKey();
                Object mvalue = mentry.getValue();

                if (requireConfigJson.containsKey(mentrykey)) { //нужно слить значения
                    //значения по ключу из require.config.json
                    Object rvalue = requireConfigJson.get(mentrykey);
                    if (rvalue instanceof Map && mvalue instanceof Map) { // если мапы то пробуем смержить
                        ((Map) rvalue).putAll((Map) mvalue);
                    } else { // замещаем из main.config.json
                        requireConfigJson.put(mentrykey, mvalue);
                    }
                } else { // достаточно установить отсутствующую запсиь
                    requireConfigJson.put(mentrykey, mvalue);
                }

            }
        } else if (requireConfigJson == null) {
            requireConfigJson = requireFromMainConfig;
        }

        return requireConfigJson;
    }

    private Map<String, Object> getMainConfigPropsMap(XConfig config) {
        String mainConfigFile = getMainConfigFileName();

        Map<String, Object> mainConfigJson = loadJsonConfig(config.getClientBase(), mainConfigFile);
        if (mainConfigJson == null || mainConfigFile.length() == 0) {
            mainConfigJson = testMainConfigJson;
            log.error("File main.config.json wasn't found, default file is used");
        }
        return mainConfigJson;
    }

    private String getMainConfigFileName() {
        String mainConfigFile = configSettings.getMainConfigFile();
        if (isEmpty(mainConfigFile)) {
            mainConfigFile = DEFAULT_MAIN_CONFIG_FILE;
        }
        return mainConfigFile;
    }

    private Map<String, Object> getRequireConfigPropsMap(XConfig config) {
        Map<String, Object> requireConfigJson =
                loadJsonConfig(config.getClientBase(), getRequireConfigFileName());

        return requireConfigJson;
    }

    private String getRequireConfigFileName() {
        String requireConfigFile = configSettings.getRequireConfigFile();
        if (isEmpty(requireConfigFile)) {
            requireConfigFile = DEFAULT_REQUIRE_CONFIG_FILE;
        }
        return requireConfigFile;
    }

    @Override
    public void updateFromJson(XConfig config, Map<String, Object> mainConfigJson) {
        config.putAll(mainConfigJson);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> loadJsonConfig(String clientBase, String configFile) {
        Map<String, Object> configJson = null;
        if (!configFile.startsWith("/")) {
            configFile = clientBase + "/" + configFile;
        }
        try {
            Resource res = getResource(configFile);
            if (res == null || !res.exists()) {
                return configJson;
            }
            InputStream is = res.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            configJson = objectMapper.readValue(is, HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return configJson;

    }

    /**
     * Получить ресурс по имени файла.
     *
     * @param configFile имя файла
     * @return @link Resource
     */
    public Resource getResource(String configFile) {
        return getApplicationContext().getResource("classpath:/static/" + configFile);
    }

}
