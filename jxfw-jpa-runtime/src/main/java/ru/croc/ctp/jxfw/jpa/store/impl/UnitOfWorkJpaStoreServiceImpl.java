package ru.croc.ctp.jxfw.jpa.store.impl;

import java8.util.function.Predicate;
import java8.util.stream.IntStreams;
import java8.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.domain.Identity;
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.StoreResult;
import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkSingleStoreServiceImpl;
import ru.croc.ctp.jxfw.jpa.domain.DomainJpaObject;
import ru.croc.ctp.jxfw.jpa.store.impl.util.StoreSortHelper;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Конкретная реализация класса {@link UnitOfWorkSingleStoreServiceImpl} для JPA хранилища.
 *
 * @since 1.1
 */
@Service
public class UnitOfWorkJpaStoreServiceImpl extends UnitOfWorkSingleStoreServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(UnitOfWorkJpaStoreServiceImpl.class);

    @Autowired
    private VersionActuator versionActuator;

    @Override
    @Transactional
    public void store(StoreContext storeContext) {
        super.store(storeContext);
    }

    @Override
    public void beforeStore(List<? extends DomainObject<?>> uow) {
        log.debug("Before sorting: {}", uow);
        int beforeSortSize = uow.size();
        StoreSortHelper.sortWithCompileAndRuntimeInfo(uow);
        if (beforeSortSize != uow.size()) {
            XInvalidDataException.Builder exceptionBuilder = new XInvalidDataException.Builder<>();
            IntStreams.range(0, uow.size())
                    .filter(index -> uow.subList(index, uow.size()).contains(uow.get(index)))
                    .mapToObj(uow::get)
                    .findAny()
                    .ifPresentOrElse(exceptionBuilder::identity, () -> exceptionBuilder.identity(Identity.UNKNOWN));
            throw exceptionBuilder.build();
        }
        log.debug("After sorting: {}", uow);
    }

    @Override
    public void afterStore(final StoreResult storeResult) {
        for (final DomainTo dto : storeResult.getUpdatedObjects()) {
            DomainObject<?> updatedDomainObject = storeResult.getUpdatedDomainObjects().stream()
                    .filter(domainObject -> {
                        return domainObject.getId().equals(dto.getId())
                                && domainObject.getTypeName().equals(dto.getType());
                    }).findFirst().get();
            dto.setTs(((Editable) updatedDomainObject).getVersion());
        }
    }

    @Override
    public boolean accepted(DomainObject<?> object, List<? extends DomainObject<?>> uow) {
        if (object instanceof DomainJpaObject) {
            versionActuator.actuate(object);
            return true;
        }

        return false;
    }

    private boolean checkReference(
            final DomainObject<?> domainObject,
            List<? extends DomainObject<?>> uow,
            final Predicate<XfwAnnotation> condition
    ) {
        return StreamSupport.stream(uow).anyMatch(new Predicate<DomainObject<?>>() {
            @Override
            public boolean test(final DomainObject<?> object) {
                XfwClass metadata = object.getMetadata();
                if (metadata != null) {
                    List<XfwReference> references = metadata.getEReferences();
                    return StreamSupport.stream(references).filter(new Predicate<XfwReference>() {
                        @Override
                        public boolean test(XfwReference ref) {
                            String instanceTypeName = ref.getEType().getInstanceTypeName();
                            if (instanceTypeName.equals(domainObject.getClass().getName())) {
                                XfwAnnotation annotation =
                                        ref.getEAnnotation(XFWConstants.getUri(Transient.class.getSimpleName()));
                                if (condition.test(annotation)) {
                                    List<? extends DomainObject<?>> values = object
                                            .obtainValueByPropertyName(ref.getName());
                                    return StreamSupport
                                            .stream(values)
                                            .anyMatch(new Predicate<DomainObject<?>>() {
                                                @Override
                                                public boolean test(DomainObject<?> val) {
                                                    return val.getId().equals(domainObject.getId());
                                                }
                                            });
                                }
                            }
                            return false;
                        }
                    }).findFirst().orElse(null) != null;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void beforeSave(DomainObject<?> entity, List<? extends DomainObject<?>> uow) {
        if (entity.isNew()) {
            Field idField = ReflectionUtils.findField(entity.getClass(),
                    new ReflectionUtils.AnnotationFieldFilter(Id.class));
            if (idField != null && idField.isAnnotationPresent(GeneratedValue.class)) {
                ReflectionUtils.setField(idField, entity, null);
            }
        }
    }

}
