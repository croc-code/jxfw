package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.core.exception.exceptions.XInterruptedException;
import ru.croc.ctp.jxfw.core.facade.webclient.ConvertContext;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.UnitOfWorkMultiStoreService;
import ru.croc.ctp.jxfw.core.store.events.AfterStoreEvent;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupWriter;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;
import ru.croc.ctp.jxfw.transfer.domain.DomainToServicesResolverTransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Компонент записи группы импорта доменных объектов в репозитории
 * с помощью универсального механизма UoW.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("importGroupWriter")
@JobScope
public class DefaultImportGroupWriter implements ImportGroupWriter {
    private static final Logger log = LoggerFactory.getLogger(DefaultImportGroupWriter.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private UnitOfWorkMultiStoreService unitOfWorkMultiStoreService;
    @Autowired
    private DomainToServicesResolverTransfer domainToServicesResolver;
    @Autowired
    private DomainFacadeIgnoreService domainFacadeIgnoreService;


    @Value("#{jobParameters[resourceId]}")
    private String resourceId;

    @Override
    public void write(List<? extends ImportGroup> importGroups) throws Exception {
        log.debug("Starting to write import groups: {}", getLogDisplayGroups(importGroups));
        final StoreContext storeContext = createStoreContext(importGroups);
        try {
            unitOfWorkMultiStoreService.store(storeContext);
        } catch (XInterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JobInterruptedException(e.getMessage());
        }
        log.debug("Publish event AfterStoreEvent, domain objects = {}", storeContext.getDomainObjects());
        applicationEventPublisher.publishEvent(new AfterStoreEvent(this, storeContext));
        log.debug("Writing groups finished.");
    }

    /**
     * Формирует из группы импорта {@link StoreContext}.
     *
     * @param importGroups группа импорта
     * @return новый контекст сохранения.
     */
    private StoreContext createStoreContext(List<? extends ImportGroup> importGroups) {
        final List<DomainTo> loadObjects = getAllLoadObjects(importGroups);
        final StoreContext storeContextForReading = new StoreContext(loadObjects);
        final ConvertContext convertContext = new ConvertContext(storeContextForReading, new ArrayList<>());

        for (DomainTo dto : loadObjects) {
            if (domainFacadeIgnoreService.isIgnore(dto.getType(), "transfer")) {
                continue;
            }
            storeContextForReading.add(domainToServicesResolver
                    .resolveToService(dto.getType())
                    .fromTo(dto, convertContext));
        }

        return StoreContext.fromDomainObjects(storeContextForReading.getDomainObjects());
    }

    private List<DomainTo> getAllLoadObjects(List<? extends ImportGroup> importGroups) {
        final List<DomainTo> loadObjects = new ArrayList<>();
        for (ImportGroup group : importGroups) {
            loadObjects.addAll(group.getObjects());
        }
        return loadObjects;
    }

    private String getLogDisplayGroups(List<? extends ImportGroup> importGroups) {
        return importGroups.stream()
                .map(group -> String.format("index: %d size: %d", group.getId(), group.getObjects().size()))
                .collect(Collectors.joining(", "));
    }
}
