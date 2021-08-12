package ru.croc.ctp.jxfw.core.store.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.IdentityMapping;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.StoreResult;
import ru.croc.ctp.jxfw.core.store.UnitOfWorkSingleStoreService;

/**
 * Базовый абстрактный класс реализации интерфейса.
 * {@link UnitOfWorkSingleStoreService}
 */
public abstract class UnitOfWorkSingleStoreServiceImpl implements
        UnitOfWorkSingleStoreService {
    
    private final Logger log = LoggerFactory.getLogger(UnitOfWorkSingleStoreServiceImpl.class);

    /**
     * Сервис для поиска доменных сервисов по типу сущности.
     */
    protected DomainServicesResolver serviceResolver;
    
    @Override
    @Deprecated
    public StoreResult store(List<? extends DomainObject<?>> uow) {
        @SuppressWarnings("unchecked")
        final StoreContext storeContext = new StoreContext(Collections.EMPTY_LIST);
        storeContext.addAll(uow);
        store(storeContext);
        return storeContext.getStoreResult();
    }

    @Override
    public void store(StoreContext storeContext) {
        List<? extends DomainObject<?>> uow = storeContext.getDomainObjects();
        Assert.notNull(uow, "uow should not be null");

        StoreResult result = new StoreResult();

        List<DomainObject<?>> acceptedOnlyObjects = filterAcceptedOnly(uow);

        beforeStore(acceptedOnlyObjects);

        doSaveUoW(uow, result, acceptedOnlyObjects);

        afterStore(result);
               
        storeContext.setStoreResult(storeContext.getStoreResult() != null ? new StoreResult(storeContext.getStoreResult(), result) : result);
    }

    /**
     * Выбирает из входного списка только те объекты, которые могут быть
     * сохранены в хранилище.
     *
     * @param uow Входной массив объектов для сохранения (UoW)
     * @return Список объектов, совместимых с хранилищем
     */
    private List<DomainObject<?>> filterAcceptedOnly(
            List<? extends DomainObject<?>> uow) {
        return StreamSupport.stream(uow).filter(o -> accepted(o, uow))
                .collect(Collectors.toList());
    }

    /**
     * Этап до сохранения всего UoW.
     *
     * @param uow набор доменных объектов на сохранение, для конкретного типа хранилища.
     */
    public void beforeStore(List<? extends DomainObject<?>> uow) {
    }

    /**
     * Сохранение в хранилище данных списка объектов UoW.
     *
     * @param uow                 список объектов на сохранение
     * @param result              результат сохранения, который будет отправлен. Наполняется в процессе.
     * @param acceptedOnlyObjects принятые объекты на основании принадлежности к конкретному типу хранилища
     */
    public void doSaveUoW(List<? extends DomainObject<?>> uow,
                          StoreResult result,
                          List<DomainObject<?>> acceptedOnlyObjects) {
        for (DomainObject<?> entity : acceptedOnlyObjects) {
            Serializable originalId = entity.getId();
            beforeSave(entity, uow);

            DomainObject<?> updatedEntity = doSave(entity, uow);
            result.getUpdatedDomainObjects().add(updatedEntity);

            if (originalId != null && !originalId.equals(entity.getId())) {
                result.getIdMapping().add(new IdentityMapping(entity, originalId));
            }

            result.getOriginalDomainObjects().add(entity);
        }
    }

    /**
     * Этап после сохранения всего UoW.
     *
     * @param storeResult результат сохранения набора доменных объектов
     */
    public void afterStore(StoreResult storeResult) {
    }

    /**
     * Проверка, будет ли объект сохраняться этим модулем
     *
     * @param object  объект для которого выполняется проверка
     * @param uow список объектов на сохранение
     * @return Возвращает true, если объект будет сохранен модулем. Иначе  false.
     */
    public abstract boolean accepted(DomainObject<?> object, List<? extends DomainObject<?>> uow);

    /**
     * Этап до сохранения сущности из UoW.
     *
     * @param entity  сохраняемая сущность
     * @param uow     набор доменных объектов для сохранения
     */
    public void beforeSave(DomainObject<?> entity,
                           List<? extends DomainObject<?>> uow) {
    }

    /**
     * Сохраняет сущность в хранилище.
     *
     * @param entity Сохраняемый объект
     * @param uow    Список всех объектов для сохранения (UoW)
     * @return доменный объект.
     */
    @SuppressWarnings("unchecked")
    protected DomainObject<?> doSave(DomainObject<?> entity,
                                     List<? extends DomainObject<?>> uow) {
        return serviceResolver.resolveService(entity).save(entity);
    }

    /**
     * Получение сервиса для работы с доменным объектом.
     *
     * @return сервис
     */
    protected DomainServicesResolver getServiceResolver() {
        Assert.notNull(serviceResolver, "serviceResolver has not been set");
        return serviceResolver;
    }

    @Autowired
    public void setServiceResolver(DomainServicesResolver serviceResolver) {
        this.serviceResolver = serviceResolver;
    }

}
