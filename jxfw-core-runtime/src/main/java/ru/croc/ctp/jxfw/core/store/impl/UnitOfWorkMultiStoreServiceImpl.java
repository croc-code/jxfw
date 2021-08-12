package ru.croc.ctp.jxfw.core.store.impl;

import java8.lang.Iterables;
import java8.util.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException;
import ru.croc.ctp.jxfw.core.facade.webclient.ConvertContext;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToMapper;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.core.impl.CorePreferences;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.StoreResult;
import ru.croc.ctp.jxfw.core.store.UnitOfWorkMultiStoreService;
import ru.croc.ctp.jxfw.core.store.UnitOfWorkSingleStoreService;
import ru.croc.ctp.jxfw.core.store.events.BeforeStoreEvent;
import ru.croc.ctp.jxfw.core.store.events.DomainObjectStoreEvent;
import ru.croc.ctp.jxfw.core.validation.ObjectValidator;
import ru.croc.ctp.jxfw.core.validation.meta.XFWFacadeValidationGroup;
import ru.croc.ctp.jxfw.core.validation.meta.XFWReadOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Реализация интерфейса {@link UnitOfWorkMultiStoreService}.
 *
 * @since 1.1
 */
@Service
public class UnitOfWorkMultiStoreServiceImpl extends ApplicationObjectSupport
        implements UnitOfWorkMultiStoreService, ApplicationEventPublisherAware {

    private static final Logger log = LoggerFactory.getLogger(UnitOfWorkMultiStoreServiceImpl.class);

    private ObjectValidator validator;

    private ApplicationEventPublisher applicationEventPublisher;

    private Map<String, UnitOfWorkSingleStoreService> services;

    private DomainToServicesResolverWebClient resolverWebClient;

    private CorePreferences corePreferences;

    @Override
    @Deprecated
    public StoreResult store(List<? extends DomainObject<?>> domainObjects) {
        @SuppressWarnings("unchecked") final StoreContext storeContext = new StoreContext(Collections.EMPTY_LIST);
        storeContext.addAll(domainObjects);
        store(storeContext);
        return storeContext.getStoreResult();
    }

    @Override
    public void store(StoreContext storeContext) {
        if (!storeContext.getOriginalsObjects().isEmpty()) {
            //TODO перенести конвертация в фасад WebClient
            ConvertContext convertContext = new ConvertContext(storeContext, new ArrayList<>());
            Set<XInvalidDataException> causes = new HashSet<>();
            Iterables.forEach(storeContext.getOriginalsObjects(), (dto) -> {
                try {
                    final String typeName = dto.getType();
                    @SuppressWarnings("unchecked") final DomainToMapper<? extends DomainObject<?>> domainToService =
                            resolverWebClient.resolveToService(typeName);

                    DomainObject<?> domainObject = domainToService.fromTo(dto, convertContext);
                    storeContext.add(domainObject);

                    if (domainObject instanceof Editable) {
                        if (dto.getTs() != -1 && !((Editable) domainObject).getVersion().equals(dto.getTs())) {
                            // эти сообщение не отправляются клиенту,
                            //только в серверный лог , т.е. локализовывать их как бы не зачем
                            causes.add(new XInvalidDataException.Builder<>(
                                    "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.ts.message",
                                    "Incorrect version in object {0} with identifier {1} (clientTs = {2} serverTs={3})")
                                    .identity(domainObject)
                                    .addArgument(dto.getTs())
                                    .addArgument(((Editable) domainObject).getVersion())
                                    .build());
                            convertContext.obsoleteObjects.add(dto);
                        }
                    }
                } catch (XObjectNotFoundException e) {
                    causes.add(e);
                    convertContext.deletedObjects.add(dto);
                }
            });
            if (causes.size() > 0) {
                throw new XOptimisticConcurrencyException(
                        "ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException.message",
                        "Conflicts detected in the data being stored", causes,
                        convertContext.obsoleteObjects, convertContext.deletedObjects);
            }

            validate(storeContext, XFWFacadeValidationGroup.class);

            addRemovedDomainObjects(storeContext, convertContext);
        }

        log.info("Transformed uow, domain objects: \n{}", storeContext.getDomainObjects());

        log.debug("Publish event BeforeStoreEvent, domain objects = {}", storeContext.getDomainObjects());
        applicationEventPublisher.publishEvent(new BeforeStoreEvent(this, storeContext));

        if (corePreferences.isEnableDomainObjectStoreEvent()) {
            Iterables.forEach(new ArrayList<>(storeContext.getDomainObjects()), domainObject -> {
                log.debug("Publish event DomainObjectStoreEvent, domain object = {}", domainObject);
                applicationEventPublisher.publishEvent(
                        new DomainObjectStoreEvent<DomainObject<?>>(domainObject, storeContext));
            });
        }

        validate(storeContext);

        Maps.forEach(getServices(), (name, service) -> {
            log.debug("Store objects by concrete store service, service = {}, domain objects = {}",
                    name, storeContext.getDomainObjects());
            service.store(storeContext);
        });
    }

    private void addRemovedDomainObjects(StoreContext storeContext, ConvertContext convertContext) {
        //по мотивам https://jira.croc.ru/browse/JXFW-881
        //DomainObjectFactory определяет навигируемые свойства со стороны Many, которые НЕ прищли с клиента, но при этом
        //удалена связь со стороны One, как те которые стоит удалить в базе.
        //Возможно это не правильно т.к. есть шанс что навигируемые свойсвта не должны быть удалены, но при это связь
        //до стороны One должна обнулится. В этом случае DomainObjectFactory нужно научить различать такой случай.
        Iterables.forEach(convertContext.objects, domainObject -> {
            if (domainObject != null
                    && domainObject.isRemoved() != null
                    && storeContext != null
                    && storeContext.getDomainObjects() != null) {
                if (domainObject.isRemoved() && !storeContext.getDomainObjects().contains(domainObject)) {
                    storeContext.add(domainObject);
                }
            }
        });
    }

    private void validate(StoreContext storeContext, Class<?>... validationGroups) {
        final List<DomainObject<?>> result = new ArrayList<>();
        for (DomainObject<?> domain : storeContext.getDomainObjects()) {
            final boolean isReadonly = DomainObjectUtil.isReadOnlyAnnotationIncludeSuperClasses(domain);
            if (isReadonly) {
                final XFWReadOnly readOnlyAnnotation = (XFWReadOnly) DomainObjectUtil
                        .getReadOnlyAnnotationIncludeSuperClasses(domain);

                if (readOnlyAnnotation != null) {
                    if (readOnlyAnnotation.action() == XFWReadOnly.Action.IGNORE) {
                        continue;
                    }
                    if (domain.isRemoved()) {
                        throw new XInvalidDataException.Builder<>(
                                "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException"
                                        + ".readonly.delete.message",
                                "You attempted to delete read only domain object {0} {1}.")
                                .identity(domain).build();
                    }
                }
            }

            validator.validateAndThrow(domain, domain.getTypeName(), validationGroups);

            result.add(domain);
         }


        storeContext.removeAll();
        storeContext.addAll(result);
        log.debug("Validate UoW, validated uow={}", result);
    }

    private Map<String, UnitOfWorkSingleStoreService> getServices() {
        if (services == null) {
            setServices(getApplicationContext().getBeansOfType(
                    UnitOfWorkSingleStoreService.class));
        }
        if (services.isEmpty()) {
            throw new IllegalStateException("Not found any instances of the UnitOfWorkSingleStoreService");
        }

        return services;
    }

    /**
     * Установка сервисов сохранения.
     *
     * @param services - список сервисов сохранения
     */
    public void setServices(Map<String, UnitOfWorkSingleStoreService> services) {
        Assert.notNull(services, "services should not be null");

        if (services.isEmpty()) {
            throw new IllegalStateException("No persistance services registered");
        }
        this.services = services;
    }

    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Autowired
    public void setValidator(ObjectValidator validator) {
        this.validator = validator;
    }

    @Autowired
    public void setResolverWebClient(DomainToServicesResolverWebClient resolverWebClient) {
        this.resolverWebClient = resolverWebClient;
    }

    @Autowired
    public void setCorePreferences(CorePreferences corePreferences) {
        this.corePreferences = corePreferences;
    }
}