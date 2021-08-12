package ru.croc.ctp.jxfw.solr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.solr.core.DefaultQueryParser;
import org.springframework.data.solr.core.QueryParser;
import org.springframework.data.solr.core.convert.CustomConversions;
import org.springframework.data.solr.core.convert.MappingSolrConverter;
import org.springframework.data.solr.core.convert.SolrConverter;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;

import java.util.ArrayList;

/**
 * Настройки JXFWSolr.
 */
@Configuration
@ComponentScan(
        basePackages = {
                "ru.croc.ctp.jxfw.solr.services",
                "ru.croc.ctp.jxfw.solr.load.impl",
                "ru.croc.ctp.jxfw.solr.facade.webclient"
        }
)
@XFWEnableSolrRepositories(basePackages = {"ru.croc.ctp.jxfw.solr.repo"})
public class XfwSolrConfig {

    /**
     * типы которые мы используем но специально не поддержанные со стороны solrj
     * конвертим этими конверторами. (помошник для корректного {@link org.apache.solr.common.SolrInputDocument})
     *
     * @return {@link SolrConverter}
     */
    @Bean
    public SolrConverter solrConverter() {
        @SuppressWarnings("rawtypes")
        ArrayList<Converter> list = CustomSolrConverters.getInstance();

        MappingSolrConverter converter = new MappingSolrConverter(new SimpleSolrMappingContext());
        converter.setCustomConversions(new CustomConversions(list));
        converter.afterPropertiesSet();

        return converter;
    }

    /**
     * Адаптер запроса из spring-data-solr в SolrQuery.
     * @return {@link QueryParser}
     */
    @Bean
    public QueryParser solrQueryParser() {
        DefaultQueryParser queryParser = new DefaultQueryParser();
        queryParser.registerConverter(CustomSolrConverters.LocalDateTimeToUtcStringConverter.INSTANCE);
        queryParser.registerConverter(CustomSolrConverters.ZonedDateTimeToUtcStringConverter.INSTANCE);
        return queryParser;
    }
}
