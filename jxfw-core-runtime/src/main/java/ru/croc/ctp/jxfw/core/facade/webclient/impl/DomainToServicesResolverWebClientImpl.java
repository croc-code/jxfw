package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;

import javax.annotation.Nonnull;

/**
 * Реализация сервиса поиска сервисов трансформации(доменных объектов) фасада WebClient по типу сущности.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Service
public class DomainToServicesResolverWebClientImpl extends ApplicationObjectSupport
        implements DomainToServicesResolverWebClient {

    @Autowired
    private DomainServicesResolver domainServicesResolver;

    @SuppressWarnings({"rawtypes"})
    @Nonnull
    @Override
    public DomainToService resolveToService(String typeName) {
        DomainToService domainToService = domainServicesResolver.resolveDomainService(typeName, "ToService");
        if (domainToService == null) {
            throw new RuntimeException("Not found TO service for " + typeName);
        }
        return domainToService;
    }

    @SuppressWarnings({"rawtypes"})
    @Nonnull
    @Override
    public DomainToService resolveFulltextToService(String typeName) {
        //для кейса, когда имя типа получали типизировано SOME_FULLTEXT_OBJECT#TYPE_NAME
        String prefix = "solr.";
        if (typeName.startsWith(prefix)) {
            typeName = typeName.replace(prefix, "");
        }

        DomainToService domainToService = domainServicesResolver.resolveDomainService(
                "solr" + typeName.substring(0,1).toUpperCase() + typeName.substring(1),
                "ToService");
        if (domainToService == null) {
            throw new RuntimeException("Not found TO service for " + typeName);
        }
        return domainToService;
    }
}
