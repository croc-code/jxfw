package ru.croc.ctp.jxfw.wc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.filter.CharacterEncodingFilter;
import ru.croc.ctp.jxfw.core.facade.webclient.file.LocalResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.wc.web.config.ConfigModuleProvider;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.Filter;
import javax.servlet.ServletContext;

/**
 * Конфигурация jXFW для работы с WC.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
@Configuration
@ComponentScan(basePackages = {"ru.croc.ctp.jxfw.wc"})
public class WebClientLoaderConfig {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ConfigModuleProvider configModuleProvider;

    /**
     * Бин для конвертирования строки с датой в формате "yyyy-MM-dd'T'HH:mm:ss.SSS"
     * принятой в WC в объект {@link LocalDate}.
     *
     * @return Бин типа {@code Converter<String, LocalDate>}
     * @see Converter
     */
    @Bean
    public Converter<String, LocalDate> addLocalDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            }
        };
    }

    /**
     * Бин для конвертирования строки с датой в формате "yyyy-MM-dd'T'HH:mm:ss.SSS"
     * принятой в WC в объект {@link LocalDateTime}.
     *
     * @return Бин типа {@code Converter<String, LocalDateTime>}
     * @see Converter
     */
    @Bean
    public Converter<String, LocalDateTime> addLocalDateTimeConverter() {
        return new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String source) {
                return LocalDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
            }
        };
    }

    /**
     * Бин для конвертирования строки с датой в формате "yyyy-MM-dd'T'HH:mm:ss.SSSX" принятой в WC в объект
     * {@link ZonedDateTime}.
     *
     * @return Бин типа {@code Converter<String, ZonedDateTime>}
     * @see Converter
     */
    @Bean
    public Converter<String, ZonedDateTime> addZonedDateTimeConverter() {
        return new Converter<String, ZonedDateTime>() {

            @Override
            public ZonedDateTime convert(String source) {
                return ZonedDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"));
            }
        };
    }

    /**
     * Регистрация фильтра-бина ({@code characterEncodingFilter()}) для энкодинга запросов в UTF-8.
     *
     * @return объект-регистратор {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(characterEncodingFilter());
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    /**
     * Spring бин фильтра {@link CharacterEncodingFilter} для энкодинга запросов в UTF-8.
     *
     * @return объект-фильтр {@link Filter}
     */
    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    /**
     * Сервис временного хранения загружаемых пользователем на сервер файлов.
     *
     * @param rootDirAbsolutePath Путь папки для хранения контента
     * @return Сервис временного хранения
     */
    @Bean
    @ConditionalOnMissingBean(name = "resourceStore")
    public ResourceStore resourceStore(@Value("${rootDirectory:}") String rootDirAbsolutePath) {
        if (rootDirAbsolutePath == null || rootDirAbsolutePath.isEmpty()) {
            rootDirAbsolutePath = ((File) servletContext
                    .getAttribute("javax.servlet.context.tempdir"))
                    .getAbsolutePath();
        }

        Long quotaPerUser = configModuleProvider.loadConfig().getFilesModuleConfig().getQuotaPerUser();

        return new LocalResourceStore(rootDirAbsolutePath, quotaPerUser);
    }
}
