package ru.croc.ctp.jxfw.solr.config.support;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.solr.repository.config.SolrRepositoryConfigExtension;

/**
 * Указываются специфичные ссылки на бины, которые необходимы в процессе создания репозиториев.
 * {@link org.springframework.data.solr.core.QueryParser} чтобы можно было расширить прикладным проектам описав бин.
 *
 * @author SMufazzalov
 * @since 1.5
 */
public class XfwSolrRepositoryConfigExtension extends SolrRepositoryConfigExtension {
    @Override
    public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
        super.postProcess(builder, config);
        builder.addPropertyReference("solrQueryParser", "solrQueryParser");
    }
}
