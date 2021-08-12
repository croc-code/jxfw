package ru.croc.ctp.jxfw.wc.config;


import static org.springframework.util.StringUtils.parseLocaleString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.croc.ctp.jxfw.wc.config.locale.WebClientCookieLocaleResolver;
import ru.croc.ctp.jxfw.wc.web.config.ConfigModuleProvider;

/**
 * Конфигурация локали для приложения.
 *
 * @since 1.1
 */
@Configuration
public class LocaleConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private ConfigModuleProvider configModuleProvider;


    /**
     * @return компонент для определиня локали на основе cookie.
     */
    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        WebClientCookieLocaleResolver localeResolver = new WebClientCookieLocaleResolver();
        localeResolver.setCookieName("X-Lang");
        final String defaultLanguage = configModuleProvider.loadConfig().getDefaultLanguage();
        localeResolver.setDefaultLocale(parseLocaleString(defaultLanguage));
        return localeResolver;
    }

}
