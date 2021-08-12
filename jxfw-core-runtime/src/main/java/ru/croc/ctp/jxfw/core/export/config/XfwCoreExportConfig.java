package ru.croc.ctp.jxfw.core.export.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.export.ExportDataProviderBuilder;
import ru.croc.ctp.jxfw.core.export.ExportFormatter;
import ru.croc.ctp.jxfw.core.export.impl.ExportDataProviderBuilderImpl;
import ru.croc.ctp.jxfw.core.export.impl.ExportFormatterImpl;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadService;

/**
 * Конфигурация подситемы экспорта.
 */
@Configuration
@ComponentScan("ru.croc.ctp.jxfw.core.export.facade.webclient")
public class XfwCoreExportConfig {


    /**
     * Билдер провайдера данных для экспорта по умолчанию. Строит
     * провайдер с возможностью запроса данных из БД чанками для JPA хранилищ.
     *
     * @param chunkSize   размер чанка данных для одного запроса из БД при экспорте
     * @param loadService сервис загрузки
     * @return бин
     */
    @Bean
    ExportDataProviderBuilder exportDataProviderBuilder(@Value("${jxfw.export.chunkSize:500}") int chunkSize,
                                                        @Autowired LoadService loadService) {

        return new ExportDataProviderBuilderImpl(loadService, chunkSize);

    }


    /**
     * Форматтер по умолчанию для выгрузки во все форматы.
     *
     * @param messageSource messageSource
     * @return бин
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    ExportFormatter defaultExportFormatter(MessageSource messageSource) {
        return new ExportFormatterImpl(messageSource);
    }



}
