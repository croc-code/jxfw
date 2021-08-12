package ru.croc.ctp.jxfw.solr.config.support;

import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.solr.core.QueryParser;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;
import org.springframework.util.Assert;

import java.lang.reflect.Field;

/**
 * Регистрация реализаций {@link org.springframework.beans.factory.FactoryBean}, заводов Solr репозиториев
 *
 * @author SMufazzalov
 * @since 1.5
 */
public class XfwSolrRepositoryFactoryBean extends SolrRepositoryFactoryBean {
    /**
     * Набор базовых solr операций, полученный при при конфигурации завода репозиториев.
     */
    public SolrTemplate solrTemplate;

    public QueryParser getSolrQueryParser() {
        return solrQueryParser;
    }

    public void setSolrQueryParser(QueryParser solrQueryParser) {
        this.solrQueryParser = solrQueryParser;
    }

    private QueryParser solrQueryParser;

    /**
     * Конструктор.
     *
     * @param repositoryInterface (например UserRepository.class)
     */
    public XfwSolrRepositoryFactoryBean(Class repositoryInterface) {
        super(repositoryInterface);
    }

    /**
     * Подробное описание в {@link package-info.java}
     * @return {@link RepositoryFactorySupport}
     */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        Assert.notNull(solrQueryParser, "bean solrQueryParser should be present");

        RepositoryFactorySupport factory = super.doCreateRepositoryFactory();

        try {
            Field solrOperations = factory.getClass().getDeclaredField("solrOperations");
            solrOperations.setAccessible(true);
            SolrTemplate template = (SolrTemplate) solrOperations.get(factory);
            template.registerQueryParser(
                    Query.class, solrQueryParser
            );
            this.solrTemplate = template;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Couldn't fetch SolrRepositoryFactory#solrOperations private field through reflection,"
                            + " needed while constructing jXFW solr repositories",
                    e
            );
        }
        return factory;
    }
}
