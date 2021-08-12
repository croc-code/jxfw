package ru.croc.ctp.jxfw.core.facade.webclient.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.StoreResultDto;
import ru.croc.ctp.jxfw.core.facade.webclient.StoreResultToService;
import ru.croc.ctp.jxfw.core.store.AggregateStoreResult;
import ru.croc.ctp.jxfw.core.store.StoreResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Сервис для трансформации объектов {@link StoreResult} в объект передачи.
 */
@Service
public class StoreResultToServiceImpl implements StoreResultToService {

    private XfwMessageTemplateResolver messageTemplateResolver;

    public XfwMessageTemplateResolver getMessageTemplateResolver() {
        return messageTemplateResolver;
    }
    @Autowired
    public void setMessageTemplateResolver(XfwMessageTemplateResolver messageTemplateResolver) {
        this.messageTemplateResolver = messageTemplateResolver;
    }


    @Override
    public StoreResultDto toTo(StoreResult storeResult, Locale locale) {
        return toTo(storeResult, false, locale);
    }
    
    @Override
    public StoreResultDto toTo(AggregateStoreResult aggregateStoreResult, Locale locale) {
        final List<StoreResultDto> dtos = new ArrayList<>();
        for (StoreResult sr: aggregateStoreResult.getStoreResults()) {
            dtos.add(toTo(sr, true, locale));
        }
        
        StoreResultDto resultDto = new StoreResultDto();
        resultDto.put("results", dtos);
        
        return resultDto;
    }
    
    private StoreResultDto toTo(StoreResult storeResult, boolean addIds, Locale locale) {
        StoreResultDto dto = new StoreResultDto();
        
        if (addIds && !storeResult.getErrorObjects().isEmpty()) {
            dto.put(StoreResultDto.IDS_FIELD_NAME, getIdentity(storeResult.getErrorObjects()));
        }

        if (!storeResult.getIdMapping().isEmpty()) {
            dto.put(StoreResultDto.NEW_IDS_FIELD_NAME, storeResult.getIdMapping());
        }

        if (!storeResult.getOriginalObjects().isEmpty()) {
            dto.put(StoreResultDto.ORIGINAL_OBJECTS_FIELD_NAME, storeResult.getOriginalObjects());
        }

        if (!storeResult.getUpdatedObjects().isEmpty()) {
            dto.put(StoreResultDto.UPDATED_OBJECTS_FIELD_NAME, storeResult.getUpdatedObjects());
        }

        XException error = storeResult.getError();
        if (error != null) {
            dto.put(StoreResultDto.ERROR_FIELD_NAME, error.toTo(messageTemplateResolver, locale));
        }

        return dto;
    }
    
    private  List<StoreResultDto> getIdentity(List<DomainTo> errorObjects) {
        final List<StoreResultDto> dtos = new ArrayList<>();
        for (DomainTo ro: errorObjects) {
            StoreResultDto dto = new StoreResultDto();
            dto.put("type", ro.getType());
            dto.put("id", ro.getId());
            dtos.add(dto);
        }       
        return dtos;
    }
    
}
