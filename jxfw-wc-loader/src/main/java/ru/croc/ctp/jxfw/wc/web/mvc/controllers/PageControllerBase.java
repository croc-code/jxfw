package ru.croc.ctp.jxfw.wc.web.mvc.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import ru.croc.ctp.jxfw.wc.util.DeepClone;
import ru.croc.ctp.jxfw.wc.web.bootloader.IBootloader;
import ru.croc.ctp.jxfw.wc.web.config.ConfigModuleProvider;
import ru.croc.ctp.jxfw.wc.web.config.XConfig;
import ru.croc.ctp.jxfw.wc.web.controllers.XHomeController;
import ru.croc.ctp.jxfw.wc.web.mvc.BrowserInfo;
import ru.croc.ctp.jxfw.wc.web.mvc.MainPageModel;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Базовый класс для MVC-контроллеров главной страницы {@link BootloaderController} и
 * {@link XHomeController}.
 *
 * @since 1.0
 */
public class PageControllerBase extends ApplicationObjectSupport {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private IBootloader bootloader;

    @Autowired
    private ConfigModuleProvider configModuleProvider;

    private BrowserInfo browserInfo;

    protected XConfig getConfig() {
        return configModuleProvider.loadConfig();
    }

    /**
     * Инициализация {@link MainPageModel} для предачи ее в слой представления,
     * в котором скрипты из модели будут напечатаны в html.
     *
     * @param model   - модель с данными (скриптами), которое необходимо передать в слой представление
     * @param request - объект html-запроса
     */
    protected void initializePageModel(MainPageModel model, HttpServletRequest request) {
        // путь до eg. main.js
        setModelsMainScript(model);
        //компонент, управляющий загрузкой страниц  и скриптов
        model.setBootloader(bootloader);
        //передать в модель, контейнер с данными о браузере
        setModelsBrowserInfo(model, request);
        //объект xconfig в виде строки (мерж XConfig + main.config.json)
        setModelsClientConfig(model);
        //объект require (мерж объекта requirejs из main.config.json + require.config.json)
        setModelsRequireConfig(model, request);
        //стили для модели
        model.setStyles(getStylesConfig());

    }

    /**
     * Мерж объекта requirejs из main.config.json + require.config.json.
     *
     * @param model   model контейнер модели главной страницы
     * @param request {@link HttpServletRequest}
     */
    private void setModelsRequireConfig(MainPageModel model, HttpServletRequest request) {
        Map<String, Object> requireConfigObject = getRequireConfig();
        String lang = localeResolver.resolveLocale(request).toString();
        model.setLocale(lang);
        if (requireConfigObject != null) {
            if (!"".equals(lang.trim())) {
                requireConfigObject.put("locale", model.getLocale());
            }

            String jsonRequireConfig;
            try {
                jsonRequireConfig = new ObjectMapper().writeValueAsString(requireConfigObject);
            } catch (JsonProcessingException e) {
                // Вероятно ошибка в json-файле
                throw new RuntimeException(e);
            }
            model.setRequireConfigJsonString(jsonRequireConfig);
        }
    }

    /**
     * Объект xconfig в виде строки (мерж XConfig + main.config.json).
     *
     * @param model контейнер модели главной страницы
     */
    private void setModelsClientConfig(MainPageModel model) {
        if (getConfig().getClientJson() != null) {
            final String jsonClientConfig;
            try {
                jsonClientConfig = new ObjectMapper().writeValueAsString(getClientConfig());
            } catch (JsonProcessingException e) {
                // Вероятно ошибка в json-файле
                throw new RuntimeException(e);
            }
            model.setClientConfigJsonString(jsonClientConfig);
        }
    }

    private void setModelsBrowserInfo(MainPageModel model, HttpServletRequest request) {
        if (request != null) {
            String userAgentString = request.getHeader("User-Agent");
            final UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            browserInfo = BrowserInfo.create(userAgent, userAgentString);
            model.setBrowser(browserInfo);
        }
    }

    /**
     * WebClient использует библиотеку RequireJS для загрузки модулей.
     * В RequireJS точкой входа в приложение является “main” скрипт, заданный в атрибуте data-main тега script
     *
     * @param model контейнер модели главной страницы
     */
    private void setModelsMainScript(MainPageModel model) {
        //По умолчанию этот скрипт называется main.js, но его реальное наименование формируется серверной логикой
        final String mainScriptName = getMainScriptName();

        if (!StringUtils.isEmpty(mainScriptName)) {
            model.setMainScriptPath(getConfig().getClientBase() + "/" + getMainScriptName());

            final String contextPath = servletContext.getContextPath();
            model.setMainScriptServerPath(contextPath + "/" + model.getMainScriptPath());
        }
    }

    /**
     * @return Наименование "main" скрипта для require.js (точки входа в клиентском приложении).
     */
    protected String getMainScriptName() {
        return "";
    }

    /**
     * @return копию JSON-объекта конфигурации (объединения XConfig и main.config.json) для отправки на клиент.
     */
    private Map<String, Object> getClientConfig() {
        return getConfig() != null
                ? DeepClone.deepClone(getConfig()) : null;
    }

    /**
     * @return копию JSON-объекта конфигурации RequireJS (из require.config.json) для отправки на клиент.
     */
    protected Map<String, Object> getRequireConfig() {
        return getConfig().getRequireJson() != null ? DeepClone.deepClone(getConfig()
                .getRequireJson()) : null;
    }

    /**
     * Получить настройку для стилей.
     *
     * @return список {@code List<Object>} с настройками стилей.
     */
    @SuppressWarnings("all")
    protected List<Object> getStylesConfig() {
        final List<Object> styles = (List<Object>) getConfig().getLoader().get("styles");
        return StreamSupport.stream(styles)
                .map(o -> {
                    if (o instanceof Map) {
                        Map config = (Map) o;
                        if (config.containsKey("if-match")) {
                            String browser = (String) ((Map) config.get("if-match")).get("browser");
                            if (browser.equalsIgnoreCase(browserInfo.getBrowser())) {
                                return ((String) config.get("include"));
                            }
                        }
                        return null;
                    }
                    return o;
                }).filter(o -> o != null)
                .collect(Collectors.toList());
    }

    public IBootloader getBootloader() {
        return bootloader;
    }


    protected void setupModel(Model model, MainPageModel pageModel) {
        model.addAttribute("clientBase", getConfig().getClientBase());
        model.addAttribute("clientConfig", "var xconfig = " + pageModel.getClientConfigJsonString() + ";");

        final String requireConfig;

        if (bootloader.getRequirejsConfig().enabled) {
            requireConfig = "var require = " + pageModel.getRequireConfigJsonString() + ";";
        } else {
            requireConfig = "(window[\"_i18n\"] = window[\"_i18n\"] || {}).locale =\"" + pageModel.getLocale() + "\";" +
                    System.lineSeparator() +
                    "__webpack_public_path__ =\""+servletContext.getContextPath()+"/" + getConfig().getClientBase() + "/\";";
        }

        model.addAttribute("requireConfig", requireConfig);
        model.addAttribute("styles", pageModel.getStyles());
        model.addAttribute("mainScriptServerPath", pageModel.getMainScriptServerPath());

        // inlineскрипты из папки boot
        List<String> scripts = pageModel.getBootScripts();
        StringBuilder builder = new StringBuilder();
        if (scripts != null) {
            builder.append(System.lineSeparator()).append("/* <![CDATA[ */").append(System.lineSeparator());
            for (String script : scripts) {
                builder.append(script);
            }
            builder.append(System.lineSeparator()).append("/* ]]> */").append(System.lineSeparator());
        }

        model.addAttribute("bootScripts", builder.toString());

    }

}
