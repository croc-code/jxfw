package ru.croc.ctp.jxfw.solr.config.support;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import java.lang.annotation.Annotation;

/**
 * Регистрация дпоолнительных определений бинов. Производится на более раннем уровне нежели в методах,
 * на этапе процессинга самих определений.
 *
 * @author SMufazzalov
 * @since 1.5
 */
public class XfwSolrRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getAnnotation()
     */
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableSolrRepositories.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport#getExtension()
     */
    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new XfwSolrRepositoryConfigExtension();
    }
}
