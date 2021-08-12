package ru.croc.ctp.jxfw.core.facade.webclient;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.domain.Identity;
import ru.croc.ctp.jxfw.core.store.StoreContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Контекст конвертации между TO и доменной сущности.
 *
 * @since 1.0
 */
public class ConvertContext {

    /**
     * Список конвертируемых объектов. Нужен методу конвертации для логики
     * установки навигируемых свойств. Конвертируемый объект vo также
     * принадлежит этому списку.
     */
    public List<DomainTo> voList;
    
    /**
     * Объекты с некорректной версией ts
     */
    public final List<DomainTo> obsoleteObjects = new ArrayList<>();
    
    /**
     * Объекты, не найденные в хранилище
     */
    public final List<DomainTo> deletedObjects = new ArrayList<>();

    /**
     * Список сконвертированных объектов. Очередной сконвертированный объект
     * добавляется в этот список.
     */
    public List<DomainObject<?>> objects;

    /**
     * Контекст сохранения, если он есть
     */
    public StoreContext storeContext;

    /**
     * Конструктор.
     *
     * @param voList - список ТО объектов.
     * @param objects  - список доменных объектов.
     */
    public ConvertContext(List<DomainTo> voList, List<DomainObject<?>> objects) {
        assert (voList != null);
        assert (objects != null);

        this.voList = voList;
        this.objects = objects;
    }

    /**
     * Конструктор.
     *
     * @param storeContext - контекст сохранения.
     * @param objects  - список доменных объектов.
     */
    public ConvertContext(StoreContext storeContext, List<DomainObject<?>> objects) {
        this(storeContext.getOriginalsObjects(), objects);
        this.storeContext = storeContext;
    }

    public boolean containeInConvertedObject(Identity<?> identity) {
        assert (identity != null);
        
        return objects.stream()
                .map(domainObject -> new DomainObjectIdentity<>(domainObject))
                .anyMatch(identity::equals);
    }
}
