package ru.croc.ctp.jxfw.solr.load.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.PreCheckSecurityEvent;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;
import ru.croc.ctp.jxfw.solr.load.events.SolrBeforeLoadEvent;
import ru.croc.ctp.jxfw.solr.load.events.SolrPreCheckSecurityEvent;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;

/**
 * Реализация {@link ru.croc.ctp.jxfw.core.load.LoadService} для Solr модуля.
 *
 * @author smufazzalov
 * @since jxfw 1.6.0
 */
@Service("solrLoadService")
@Order(0)
public class SolrLoadServiceImpl extends LoadServiceImpl {


    /**
     * Конструктор.
     *
     * @param domainServicesResolver {@link DomainServicesResolver}
     * @param publisher {@link ApplicationEventPublisher}
     * @param criteriaComposer  имплементация PredicateProvider для SOLR
     *
     */
    public SolrLoadServiceImpl(DomainServicesResolver domainServicesResolver, ApplicationEventPublisher publisher,
                               PredicateProvider criteriaComposer) {
        super(domainServicesResolver, publisher, criteriaComposer);
    }

    @Nonnull
    @Override
    protected DomainService resolveDomainService(String typeName) {
        DomainService domainService;
        try {
            domainService = domainServicesResolver.resolveFulltextService(typeName);
            if (domainService == null) {
                domainService = super.resolveDomainService(typeName);

            }
        } catch (Exception e1) {
            throw new RuntimeException("DomainService для " + typeName + " не определен");
        }
        return domainService;
    }

    @Override
    public boolean accepted(String domainTypeName) {
        // т.к. при fulltext feature для fulltext комплекта не готовится ecore
        if (domainTypeName.contains("solr.")) {
            return true;
        }

        Class<?> domainType = DomainObjectUtil.getDomainObjectType(domainTypeName);
        Annotation solrDocument = domainType.getAnnotation(SolrDocument.class);
        return solrDocument != null;
    }

    @Nonnull
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> PreCheckSecurityEvent<T>
        createPreCheckSecurityEvent(@Nonnull QueryParams<T, ID> queryParams,
                                @Nonnull LoadContext<T> loadContext) {
        return new SolrPreCheckSecurityEvent<>(queryParams, loadContext);
    }

    @Nonnull
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> BeforeLoadEvent<T>
        createBeforeLoadEvent(@Nonnull QueryParams<T, ID> queryParams,
                          @Nonnull LoadContext<T> loadContext) {
        return new SolrBeforeLoadEvent<>(queryParams, loadContext);
    }

}
