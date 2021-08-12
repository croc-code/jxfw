package ru.croc.ctp.jxfw.core.config;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.querydsl.core.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.exception.dto.ExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.ExceptionToSerializer;
import ru.croc.ctp.jxfw.core.export.config.XfwCoreExportConfig;
import ru.croc.ctp.jxfw.core.facade.webclient.TupleSerializer;
import ru.croc.ctp.jxfw.core.facade.webclient.file.EnrichHeadersFilter;
import ru.croc.ctp.jxfw.core.impl.CorePreferences;
import ru.croc.ctp.jxfw.core.impl.XFWApplicationEventMulticasterImpl;
import ru.croc.ctp.jxfw.core.metamodel.runtime.MetamodelAnalyticsComponent;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.core.store.events.DomainObjectStoreEvent;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.metamodel.runtime.analitycs.XfwClassAnalytics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Конфигурация Spring бинов, необходимых для jXFW.
 *
 * @since 1.1
 */
@Configuration
@ComponentScan(
        basePackages = {
                "ru.croc.ctp.jxfw.core.datasource",
                "ru.croc.ctp.jxfw.core.domain",
                "ru.croc.ctp.jxfw.core.exception",
                "ru.croc.ctp.jxfw.core.facade",
                "ru.croc.ctp.jxfw.core.load",
                "ru.croc.ctp.jxfw.core.reporting",
                "ru.croc.ctp.jxfw.core.store",
                "ru.croc.ctp.jxfw.core.validation",
                "ru.croc.ctp.jxfw.core.localization",
                "ru.croc.ctp.jxfw.core.converters",
                "ru.croc.ctp.jxfw.core.impl"

        })
@Import(XfwCoreExportConfig.class)
public class XfwCoreConfig {

    private final static Logger logger = LoggerFactory.getLogger(XfwCoreConfig.class);

    /**
     * Стандартный форматтер для передачи и получения дат и времени для WC.
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Стандартный форматтер для передачи и получения дат для WC.
     */
    public static final DateTimeFormatter DATE_FORMATTER = ofPattern("yyyy-MM-dd'T'00:00:00.000");

    /**
     * Стандартный форматтер для передачи и получения времени для WC.
     */
    public static final DateTimeFormatter TIME_FORMATTER = ofPattern("1900-01-01'T'HH:mm:ss.SSS");

    /**
     * @return фильтр для добавления доп. заголовков.
     */
    @Bean
    EnrichHeadersFilter headersEnrichFilter() {
        return new EnrichHeadersFilter();
    }

    /**
     * Регистрация фильтра {@link ShallowEtagHeaderFilter} для получения дополнительных хедеров
     * в ответе типа contentLength.
     *
     * @return {@link FilterRegistrationBean}
     */
    @Bean
    public FilterRegistrationBean shallowEtagHeaderFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new ShallowEtagHeaderFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    /**
     * @return модуль Jackson'а для сериализации сущностей дат в JSON.
     */
    @Bean
    Module xfwJavaTimeModule() {
        com.fasterxml.jackson.datatype.jsr310.JavaTimeModule javaTimeModule =
                new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(DateTimeFormatter.ISO_INSTANT));

        return javaTimeModule;
    }

    /**
     * @param xfwJacksonModule  модуль с сущностями фреймворка.
     * @param xfwJavaTimeModule модуль с датами.
     * @return замена стандартной сериализации в JSON.
     */
    @Bean
    @Primary
    @Autowired
    protected ObjectMapper serializingObjectMapper(Module xfwJacksonModule, Module xfwJavaTimeModule) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModules(xfwJavaTimeModule, xfwJacksonModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    /**
     * @return модуль Jackson'а для сериализации сущностей фреймворка в JSON.
     */
    @Bean
    Module xfwJacksonModule() {
        SimpleModule module = new SimpleModule("jxfwModule", new Version(1, 0, 0, null, null, null));
        module.addSerializer(ExceptionTo.class, new ExceptionToSerializer());
        module.addSerializer(Tuple.class, new TupleSerializer());
        return module;
    }

    /**
     * @return пост-процессор для обеспечения валидации аргументов и возвращаемого результата методов контроллеров.
     */
    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    /**
     * Метамодель в рантайм.
     *
     * @return - Метамодель в рантайм.
     */
    @Bean
    XfwModel xfwModel() {
        return XfwModelFactory.getInstance();
    }


    /**
     * Конфигурация фабрики валидаторов.
     *
     * @param messageSource бандл ресурсов приложения
     * @return фабрика
     */
    @Bean
    LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(messageSource);
        return validatorFactoryBean;
    }

    private static final String XFW_MESSAGES_BUNDLE = "xfwMessages";

    /**
     * Подключает бандл xfwMessages к уже сконфигурированному в приложении messageSource.
     * xfwMessages подключается как родитель, что дает возможность переопределять сообщения
     * jxfw в прикладных проектах.testXOptimisticConcurrencyException
     *
     * @param messageSource бандл ресурсов приложения
     * @return InitializingBean
     */
    @Bean
    InitializingBean xfwMessageSourceExtender(MessageSource messageSource) {
        return () -> {
            if (!injectParentMessageSource(messageSource)) {
                logger.info("Resource bundle \""
                        + XFW_MESSAGES_BUNDLE + "\" not included into message source hierarchy automatically");
            }
        };
    }

    /**
     * Добавляет бандл ресурсов JXFW в корень иерархии ресурсов прикладного приложения.
     *
     * @param messageSource бандл ресурсов приложения
     * @return успех\ неуспех
     */
    boolean injectParentMessageSource(MessageSource messageSource) {
        if (messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hms = (HierarchicalMessageSource) messageSource;
            if ((hms instanceof AbstractResourceBasedMessageSource)
                    && ((AbstractResourceBasedMessageSource) hms).getBasenameSet().contains(XFW_MESSAGES_BUNDLE)) {
                return true;
            }
            if (hms.getParentMessageSource() == null) {
                ResourceBundleMessageSource parent = new ResourceBundleMessageSource();
                parent.setBasename(XFW_MESSAGES_BUNDLE);
                hms.setParentMessageSource(parent);
                return true;
            } else {
                return injectParentMessageSource(hms.getParentMessageSource());
            }
        }
        return false;
    }

    /**
     * Бин запускает все анализаторы метамодели, которые есть в контесте приложения,
     * по событию поднятия контекста.
     *
     * @param analytics анализаторы метамодели
     * @param xfwModel  метамодель
     * @param enabled включено. По умолчанию включено.
     *                Управляется параметром jxfw.analytics.enabled из свойств приложения
     * @return бин
     */
    @Bean
    MetamodelAnalyticsComponent metamodelAnalyticsComponent(@Autowired(required = false)
                                                                    List<XfwClassAnalytics> analytics,
                                                            @Autowired XfwModel xfwModel,
                                                            @Value("${jxfw.analytics.enabled:true}") boolean enabled) {
        return new MetamodelAnalyticsComponent(analytics, xfwModel, enabled);
    }

    /**
     * Бин проверки конфигурации для настройки ru.croc.ctp.jxfw.core.store.events.domainObjectStoreEvent.enable. В
     * случае выключенной настройки и наличия обработчиков событий DomainObjectStoreEvent в лог будет выведено
     * предупреждение.
     *
     * @param corePreferences
     *        сервис настроек модуля.
     * @param applicationEventMulticaster
     *        бин, содержащий обработчики событий.
     * @return бин логгирования.
     */
    @Bean
    DomainObjectStoreEventConfig domainObjectStoreEventConfig(
            @Autowired CorePreferences corePreferences,
            @Autowired XFWApplicationEventMulticasterImpl applicationEventMulticaster) {
        return new DomainObjectStoreEventConfig(corePreferences.isEnableDomainObjectStoreEvent(),
                applicationEventMulticaster);
    }

    private static class DomainObjectStoreEventConfig {

        private final boolean enableDomainObjectStoreEvent;

        private final XFWApplicationEventMulticasterImpl applicationEventMulticaster;

        private DomainObjectStoreEventConfig(boolean enableDomainObjectStoreEvent,
                                            XFWApplicationEventMulticasterImpl applicationEventMulticaster) {
            this.enableDomainObjectStoreEvent = enableDomainObjectStoreEvent;
            this.applicationEventMulticaster = applicationEventMulticaster;
        }

        /**
         * Отрабатывает при старте контекста.
         */
        @EventListener(ContextRefreshedEvent.class)
        public void contextRefreshedEvent() {
            if (!enableDomainObjectStoreEvent) {
                Object event = new DomainObjectStoreEvent<>(DomainObjectUtil.getDomainObjectStub(), null);

                if (applicationEventMulticaster.existsListener(event)) {
                    logger.warn("DomainObjectStoreEvent listener(s) never used");
                }
            }
        }
    }
}
