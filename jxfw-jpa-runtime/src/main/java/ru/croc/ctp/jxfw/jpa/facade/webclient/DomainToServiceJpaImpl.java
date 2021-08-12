package ru.croc.ctp.jxfw.jpa.facade.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;
import ru.croc.ctp.jxfw.jpa.hibernate.impl.util.ProxyHelper;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

/**
 * Базовый класс, для генерируемых ТО сервисов.
 *
 * @param <T>   - тип доменной модели
 * @param <IDT> - тип первичного ключа
 */
public abstract class DomainToServiceJpaImpl<T extends DomainObject<IDT>, IDT extends Serializable>
        extends DomainToServiceImpl<T, IDT> {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DomainToServiceJpaImpl.class);

    /**
     * Конструктор для создания экземпляра сервиса.
     *
     * @param resourceStore Сервис для работы с промежуточным хранилищем загруженных файлов
     */
    public DomainToServiceJpaImpl(ResourceStore resourceStore, DomainToServicesResolver resolver) {
        super(resourceStore, resolver);
    }

    @Autowired
    private JpaContext jpaContext;

    /**
     * Удалить доменный объект из сессии.
     *
     * @param domainObject - добменный объект
     */
    //TODO: подумать над переносом в доменный сервис 
    public void detachEntity(DomainObject<?> domainObject) {
        final Class<?> clazz = ProxyHelper.getRealClass(domainObject);
        final EntityManager entityManager = jpaContext.getEntityManagerByManagedType(clazz);
        entityManager.detach(domainObject);
    }

    @Override
    public DomainTo toToPolymorphic(T domainObject, String... expand) {
        return super.toToPolymorphic(ProxyHelper.initializeAndUnproxy(domainObject), expand);
    }

    @Override
    public List<DomainTo> toToPolymorphic(List<T> domainObjectList, String type, String... expand) {
        return super.toToPolymorphic(domainObjectList.stream().map(t -> ProxyHelper.initializeAndUnproxy(t))
            .collect(Collectors.toList()), type, expand);
    }


    @Override
    public DomainTo toTo(T domainObject, String... expand) {
        return super.toTo(domainObject, expand);
    }
}