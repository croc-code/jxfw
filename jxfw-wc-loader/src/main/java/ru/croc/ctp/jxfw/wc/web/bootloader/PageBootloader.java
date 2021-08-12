package ru.croc.ctp.jxfw.wc.web.bootloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.wc.web.config.ConfigModuleProvider;
import ru.croc.ctp.jxfw.wc.web.config.XConfig;
import ru.croc.ctp.jxfw.wc.web.mvc.BrowserInfo;
import ru.croc.ctp.jxfw.wc.web.mvc.MainPageModel;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация Bootloader'a.
 *
 * @since 1.0
 */
@Component
public class PageBootloader implements IBootloader, ResourceLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(PageBootloader.class);

    private final XConfig config;
    private ResourcePatternResolver patternResolver;
    private BootloaderConfig loaderConfig;

    /**
     * @param configProvider - провайдер для получения конфигурационного объекта.
     */
    @Autowired
    public PageBootloader(ConfigModuleProvider configProvider) {
        config = configProvider.loadConfig();
        parseLoaderConfig(config);
    }

    @SuppressWarnings("unchecked")
    private void parseLoaderConfig(XConfig config) {
        BootloaderConfig loaderConfig = new BootloaderConfig();
        loaderConfig.bootDir = "boot";
        loaderConfig.appCache = new AppCacheConfig();
        loaderConfig.appCache.enabled = this.config.isSupportOffline();
        loaderConfig.requirejsConfig = new RequirejsConfig();
        loaderConfig.requirejsConfig.enabled = true;
        loaderConfig.requirejsConfig.scriptPath = "vendor/require.js";
        loaderConfig.requirejsConfig.loadInline = false;

        Map<String, Object> loaderToken = config.getLoader();
        if (loaderToken != null) {
            loaderConfig.bootDir = loaderToken.get("bootDir").toString();

            //Loader provider
            String loaderProvider = (String) loaderToken.get("provider");
            if (loaderProvider != null) {
                switch (loaderProvider.toLowerCase()) {
                    case "webpack":
                        loaderConfig.requirejsConfig.enabled = false;
                        loaderConfig.loaderProvider = LoaderProvider.WEBPACK;
                        break;
                    case "requirejs":
                        loaderConfig.requirejsConfig.enabled = true;
                        loaderConfig.loaderProvider = LoaderProvider.REQUIREJS;
                        break;
                    default:
                        throw new RuntimeException("Configuration error: unsupported js-loader provider :'" + loaderProvider + "'. Use 'requirejs' or 'webpack'");
                }
            }

            // requirejs
            Map<String, Object> requireConfig = (Map<String, Object>) loaderToken
                    .get("requirejs");
            if (requireConfig != null && loaderConfig.loaderProvider == LoaderProvider.REQUIREJS) {
                if (requireConfig.get("enabled") != null) {
                    loaderConfig.requirejsConfig.enabled = (Boolean) requireConfig
                            .get("enabled");
                }
                if (requireConfig.get("scriptPath") != null) {
                    loaderConfig.requirejsConfig.scriptPath = (String) requireConfig
                            .get("scriptPath");
                }
                if (requireConfig.get("loadAs") != null) {
                    loaderConfig.requirejsConfig.loadInline = requireConfig
                            .get("loadAs").equals("inline");
                }
            }

            // Scripts
            List<Object> scrpiptsTokens = (List<Object>) loaderToken
                    .get("scripts");
            parseFilesMatchConfig(scrpiptsTokens, loaderConfig.scripts);

            // AppCache
            Map<String, Object> appCacheToken = (Map<String, Object>) loaderToken
                    .get("appCache");
            if (appCacheToken != null) {
                if (appCacheToken.get("enabled") != null) {
                    loaderConfig.appCache.enabled = (Boolean) appCacheToken
                            .get("enabled");
                }
                if (loaderConfig.appCache.enabled) {
                    List<Object> includeToken = (List<Object>) appCacheToken
                            .get("include");
                    if (includeToken != null) {
                        parseFilesMatchConfig(includeToken,
                                loaderConfig.appCache.include);
                    }
                }
            }

            // styles
            List<Object> stylesToken = (List<Object>) loaderToken
                    .get("styles");
            parseFilesMatchConfig(stylesToken, loaderConfig.styles);

            //без main.config.json
            Boolean testConfigJson = (Boolean) loaderToken.get("testConfig");
            if (testConfigJson != null && testConfigJson) {
                loaderConfig.requirejsConfig.scriptPath = null;
            }
        }
        this.loaderConfig = loaderConfig;
    }

    @SuppressWarnings("unchecked")
    private void parseFilesMatchConfig(List<Object> scriptsTokens, List<BootloaderScriptConfig> scriptsConfig) {
        for (Object value : scriptsTokens) {
            BootloaderScriptConfig script = null;
            if (value instanceof String) { // value простой путь до скрипта (например: shim/es5-sham.js)
                script = new BootloaderScriptConfig((String) value);
            } else {
                // value сложная стркутура (Map) которая содержит в себе условие 
                // определения для какого типа устройств скрипты
                ScriptMatchSpecification specIfMatch = null;
                ScriptMatchSpecification specIfNotMatch = null;
                Map<String, Object> map = (Map<String, Object>) value;
                Map<String, Object> jsonMatchSpec = (Map<String, Object>) map.get("if-match");
                if (jsonMatchSpec != null) {
                    // bundle level include filter spec
                    specIfMatch = parseConfigMatchSpec(jsonMatchSpec);
                }
                jsonMatchSpec = (Map<String, Object>) map.get("if-not-match");
                if (jsonMatchSpec != null) {
                    // bundle level exclude filter spec
                    specIfNotMatch = parseConfigMatchSpec(jsonMatchSpec);
                }
                List<String> files = new ArrayList<>();
                Object include = map.get("include");
                if (include != null) {
                    if (include instanceof String) {
                        files.add((String) include);
                    } else {
                        files.addAll((List<String>) include);
                    }
                }
                if (files.size() > 0) {
                    script = new BootloaderScriptConfig(files, specIfMatch,
                            specIfNotMatch);
                }
            }
            if (script != null) {
                scriptsConfig.add(script);
            }
        }
    }

    private ScriptMatchSpecification parseConfigMatchSpec(Map<String, Object> jsonMatchSpec) {
        ScriptMatchSpecification spec = new ScriptMatchSpecification();
        String value = (String) jsonMatchSpec.get("browser");
        if (value != null && !"".equals(value.trim())) {
            int sepIdx = value.indexOf('.');
            if (sepIdx > -1) {
                spec.browser = value.substring(0, sepIdx);
                try {
                    spec.browserVersion = Integer.parseInt(value.substring(sepIdx + 1));
                } catch (NumberFormatException e) {
                    log.error(e.getMessage(), e);
                }
            } else {
                spec.browser = value;
            }
        }

        value = (String) jsonMatchSpec.get("device");
        if (value != null && !"".equals(value.trim())) {
            spec.isMobileDevice = "mobile".equals(value);
        }

        if (jsonMatchSpec.get("screen") != null) {
            switch ((String) jsonMatchSpec.get("screen")) {
                case "extra-small":
                    spec.screen = DeviceScreenSizes.ExtraSmall;
                    break;
                case "small":
                    spec.screen = DeviceScreenSizes.Small;
                    break;
                case "medium":
                    spec.screen = DeviceScreenSizes.Medium;
                    break;
                case "large":
                default:
                    spec.screen = DeviceScreenSizes.Large;
                    break;
            }
        }
        if ((spec.browser == null || "".equals(spec.browser.trim()))
                && spec.isMobileDevice == null && spec.screen == null) {
            return null;
        }

        return spec;
    }

    @Override
    public Boolean isSupportAppCache() {
        // TODO Auto-generated method stub
        return null;
    }

    public RequirejsConfig getRequirejsConfig() {
        return loaderConfig.requirejsConfig;
    }

    @Override
    public List<String> getScripts(MainPageModel model,
                                   HttpServletRequest request) {
        return getMatchedFiles(loaderConfig.scripts, model.getBrowser(), request.getParameterMap());
    }

    /**
     * @return Массив скриптов, которые необходимо загрузить в первую очередь.
     */
    public Resource[] getBootScripts() {
        // Зачитаем содержимое всех файлов из директории "boot", заданной в
        // конфигурации main.config.json/loader/bootDir
        String pattern = "classpath:/static/" + config.getClientBase() + "/"
                + loaderConfig.bootDir + "/*.js";
        try {
            return patternResolver.getResources(pattern);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getMatchedFiles(
            List<BootloaderScriptConfig> matchSpec,
            BrowserInfo browserInfo,
            Map<String, String[]> parameters) {

        ScriptMatchSpecification runtimeSpec = parseRuntimeMatchSpec(
                browserInfo, parameters);

        List<String> scriptFiles = new ArrayList<>();
        for (BootloaderScriptConfig scriptConfig : matchSpec) {
            if (scriptConfig.ifMatch != null) {
                if (!scriptConfig.ifMatch.isMatch(runtimeSpec)) {
                    continue;
                }
            }
            if (scriptConfig.ifNotMatch != null) {
                if (scriptConfig.ifNotMatch.isMatch(runtimeSpec)) {
                    continue;
                }
            }
            scriptFiles.addAll(scriptConfig.scripts);
        }
        return scriptFiles;
    }

    private static ScriptMatchSpecification parseRuntimeMatchSpec(
            BrowserInfo browserInfo, Map<String, String[]> parameters) {
        ScriptMatchSpecification spec = new ScriptMatchSpecification();
        spec.browser = browserInfo.getCapabilities().getBrowser().getName();
        spec.browserVersion = getIntBrowserVersion(browserInfo);

        if (parameters != null) {
            Set<String> keys = parameters.keySet();
            for (String key : keys) {
                if (key.equalsIgnoreCase("device")) {
                    spec.isMobileDevice = "mobile".equals(parameters.get(key)[0]);
                } else if (key.equalsIgnoreCase("screen")) {
                    String value = parameters.get(key)[0];
                    String[] parts = value.split(":");
                    if (parts.length == 2) {
                        try {
                            int width = Integer.parseInt(parts[0]);
                            if (width < 768) {
                                spec.screen = DeviceScreenSizes.ExtraSmall;
                            } else if (width >= 1200) {
                                spec.screen = DeviceScreenSizes.Large;
                            } else if (width >= 992) {
                                spec.screen = DeviceScreenSizes.Medium;
                            } else {
                                spec.screen = DeviceScreenSizes.Small;
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return spec;
    }

    /**
     * Получает из информации о бразуере версию в виде целого числа.
     *
     * @param browserInfo информация о браузере
     * @return целочисленная версия браузера
     */
    private static Integer getIntBrowserVersion(BrowserInfo browserInfo) {
        final Double browserVersion = browserInfo.getVersion();
        return (browserVersion != null) ? browserVersion.intValue() : null;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        if (resourceLoader instanceof ResourcePatternResolver) {
            patternResolver = (ResourcePatternResolver) resourceLoader;
        } else {
            throw new IllegalStateException("ResourceLoader should implement ResourcePatternResolver interface");
        }
    }
}
