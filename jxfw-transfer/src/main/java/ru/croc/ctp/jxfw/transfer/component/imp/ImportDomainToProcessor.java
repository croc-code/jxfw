package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Компонент для обработки объектов DomainTo при импорте, обработка производится до преобразования в доменные объекты.
 * 
 * @author AKogun
 * @since 1.4
 */
@Component
public class ImportDomainToProcessor implements ItemProcessor<List<DomainTo>, List<DomainTo>> {
    
    @Autowired
    private DomainServicesResolver domainServicesResolver;

    @Override
    public List<DomainTo> process(List<DomainTo> domainToObjects) throws Exception {
        
        List<DomainTo> processedDomainToObjects = new ArrayList<>();
        
        for (DomainTo domainToObject: domainToObjects) {
            if (domainToObject == null) {
                continue;
            }
            
            DomainTo processedDomainToObject = process(domainToObject);
            if (processedDomainToObject != null) {
                DomainObject domainObject = findDomainObjectFor(processedDomainToObject);
                if (domainObject instanceof Editable) {
                    processedDomainToObject.setId(domainObject.getId().toString());
                    processedDomainToObject.setTs(((Editable) domainObject).getVersion());
                } else {
                    processedDomainToObject.setNew(true);
                    processedDomainToObject.setTs(-1L);
                }
                processedDomainToObjects.add(processedDomainToObject);
            }
        }
        
        return processedDomainToObjects;
    }
    
    /**
     * Метод, вызываемый при обработке каждого импортируемого объектра, может быть переопределен 
     * для изменения отдельных объектов перед загрузкой или фильтрации.
     * @param domainToObject TO объект, который подлежит загрузке.
     * @return TO объект после обработки - null, если объект не должен быть загружен.
     * @throws Exception любая проблема.
     */
    @Nullable
    protected DomainTo process(@Nonnull DomainTo domainToObject) throws Exception {
        return domainToObject;
    }       

    /**
     * Метод для определения, является ли загружаемый TO объект новым или версией существующего в хранилище, 
     * по умолчанию поиск происходит по id, может быть переопределен для реализации поиска по другим критериям.
     * @param domainToObject загружаемый объект.
     * @param <T> тип доменного объекта.
     * @return текущая версия доменного объекта, соответствующего загружаемому - null, если загружаемый объект
     *      является новым.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Nullable
    protected <T extends DomainObject> T findDomainObjectFor(@Nonnull DomainTo domainToObject) {
        DomainService domainService = domainServicesResolver.resolveService(domainToObject.getType());
        
        if (domainService != null) {
            try {
                DomainObject domainObject = domainService.getObjectById(domainToObject.getId());
                return (T)domainObject;
            } catch (XObjectNotFoundException nfe) {
                //ничего не делаем
            }
        }
        
        return null;
    }

}