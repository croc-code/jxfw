package ru.croc.ctp.jxfw.core.store.impl;

import java8.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.StoreResult;
import ru.croc.ctp.jxfw.core.store.StoreService;
import ru.croc.ctp.jxfw.core.store.UnitOfWorkMultiStoreService;
import ru.croc.ctp.jxfw.core.store.events.AfterStoreEvent;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Реализация {@link StoreService}.
 */
@Service
public class StoreServiceImpl implements StoreService, ApplicationEventPublisherAware {
    
    private static final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    
    private DomainServicesResolver servicesResolver;
    private DomainToServicesResolverWebClient domainToServicesResolver;


    private UnitOfWorkMultiStoreService uowStoreService;
   
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Конструктор.
     * @param uowStoreService {@link UnitOfWorkMultiStoreService}
     * @param servicesResolver {@link DomainServicesResolver}
     * @param domainToServicesResolver {@link DomainToServicesResolverWebClient}
     */
    @Autowired
    public StoreServiceImpl(UnitOfWorkMultiStoreService uowStoreService,
                            DomainServicesResolver servicesResolver,
                            DomainToServicesResolverWebClient domainToServicesResolver) {
        this.uowStoreService = uowStoreService;
        this.servicesResolver = servicesResolver;
        this.domainToServicesResolver = domainToServicesResolver;
    }
    
    @Override
    public StoreResult store(List<DomainTo> uow) {
        return store(uow, null, null, null);
    }

    @Override
    public StoreResult store(List<DomainTo> uow, List<String> hints, Locale locale, String txId) {
        StoreContext storeContext = new StoreContext.StoreContextBuilder()
                .withUow(uow)
                .withHints(hints)
                .withLocale(locale)
                .withTxId(txId)
                .build();
        return store(storeContext);
    }
    
    @Override
    public StoreResult store(StoreContext storeContext) {
        
        uowStoreService.store(storeContext);
        
        updateVersion(storeContext.getStoreResult());
        
        log.debug("Publish event AfterStoreEvent, domain objects = {}", storeContext.getDomainObjects());
        applicationEventPublisher.publishEvent(new AfterStoreEvent(this, storeContext));

        return storeContext.getStoreResult();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void updateVersion(StoreResult sr) {
        for (DomainObject<?> domainObject : sr.getOriginalDomainObjects()) {
            final DomainService domainService
                    = servicesResolver.resolveService(domainObject);
            if (!domainObject.isRemoved() && !domainObject.isNew()) {
                final DomainObject<? extends Serializable> entity =
                        domainService.getObjectById((domainObject.getId()));
                
                //т.к. комплексный ключ необходимо привести к строковому виду
                final String id = stringRepresentationOfId(domainObject);
                final String type = entity.getTypeName();
                
                DomainTo updated = StreamSupport.stream(sr.getUpdatedObjects()).filter(domainTo -> {
                    return id.equals(domainTo.getId()) && type.equals(domainTo.getType());
                }).findFirst().orElseGet(() -> {
                    DomainTo dto = new DomainTo(type, id);
                    sr.getUpdatedObjects().add(dto);
                    return dto;
                });
                
                if (entity instanceof Editable) {
                    updated.setTs(((Editable) entity).getVersion());
                }
            }
        }
    }
    

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String stringRepresentationOfId(DomainObject<?> domainObject) {
        Serializable id = domainObject.getId();
        if (id instanceof String) {
            return (String) id;
        } else {
            //сериализация ключа
            DomainToService domainToService = domainToServicesResolver.resolveToService(domainObject.getTypeName());
            return domainToService.serializeKey(id);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;        
    }

}