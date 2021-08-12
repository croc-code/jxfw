package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.store.StoreService;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportDomainToWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Процессор для импортирования доменных объектов в БД.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@Component
public class DomainToItemWriter implements ImportDomainToWriter {
    
    private static final Logger log = LoggerFactory.getLogger(DomainToItemWriter.class);

    private StoreService storeService;

    @Autowired
    public void setMultiStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public void write(List<? extends List<? extends DomainTo>> pack) throws Exception {
        final List<DomainTo> loadObjects = new ArrayList<>();
        loadObjects.addAll(pack.get(0));        
        log.debug("Start writing list of DomainTO: {}", loadObjects);
        storeService.store(loadObjects);
    }
}
