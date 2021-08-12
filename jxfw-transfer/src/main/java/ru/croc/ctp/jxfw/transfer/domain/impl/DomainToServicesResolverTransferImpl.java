package ru.croc.ctp.jxfw.transfer.domain.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.transfer.domain.DomainToServicesResolverTransfer;

import javax.annotation.Nonnull;

/**
 * Сервис для поиска сервисов трансформации(доменных объектов) фасада Transfer по типу сущности.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Service
public class DomainToServicesResolverTransferImpl extends ApplicationObjectSupport
        implements DomainToServicesResolverTransfer {
    private static final String SERVICE_NAME_SUFFIX = "ToServiceTransfer";

    @Autowired
    private DomainServicesResolver domainServicesResolver;

    @SuppressWarnings({"rawtypes"})
    @Nonnull
    @Override
    public DomainToService resolveToService(String typeName) {
        DomainToService domainToService = domainServicesResolver
                .resolveDomainService(typeName, SERVICE_NAME_SUFFIX);
        if (domainToService == null) {
            throw new RuntimeException("Not found TO service transfer for " + typeName);
        }
        return domainToService;
    }

    @SuppressWarnings({"rawtypes"})
    @Nonnull
    @Override
    public DomainToService resolveFulltextToService(String typeName) {
        DomainToService domainToService = domainServicesResolver.resolveDomainService(
                "solr" + typeName.substring(0, 1).toUpperCase() + typeName.substring(1),
                SERVICE_NAME_SUFFIX);
        if (domainToService == null) {
            throw new RuntimeException("Not found TO service for " + typeName);
        }
        return domainToService;
    }
}

