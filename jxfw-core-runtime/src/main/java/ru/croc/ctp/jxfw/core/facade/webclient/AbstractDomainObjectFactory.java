package ru.croc.ctp.jxfw.core.facade.webclient;

import static com.google.common.collect.Sets.newHashSet;

import java8.lang.Iterables;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.domain.Identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Основа Фабрики для доменных объектов.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public abstract class AbstractDomainObjectFactory {
    /**
     * Сервис для поиска сервисов трансформации.
     */
    protected DomainToServicesResolver domainToServicesResolver;
    /**
     * Контекст конвертации объектов.
     */
    protected ConvertContext context;

    /**
     * Основа Фабрики для доменных объектов.
     *
     * @param context контекст конвертации объектов.
     * @param domainToServicesResolver сервис для поиска сервисов трансформации.
     */
    protected AbstractDomainObjectFactory(ConvertContext context, DomainToServicesResolver domainToServicesResolver) {
        this.context = context;
        this.domainToServicesResolver = domainToServicesResolver;
    }

    /**
     * Создать объект по его ИД и имени доменного Типа.
     *
     * @param id   - ИД объекта.
     * @param type - имя доменного типа объекта.
     * @return объект, полученый либо из контекста конвертации, если был передан с клиента в UoW.
     */
    @SuppressWarnings("unchecked")
    public <T extends DomainObject<?>> T create(String id, String type) {
        DomainToService<T, ?> toService = domainToServicesResolver.resolveToService(type);

        Identity<?> identity = new DomainObjectIdentity<>(toService.parseKey(id), type);
        T result =  findInContext(identity);
        if (result != null) {
            return result;
        }

        if (context != null) {
            for (DomainTo vo : context.voList) {
                if (new DomainObjectIdentity<>(toService.parseKey(vo.getId()), vo.getType()).equals(identity)) {
                    return create(toService, vo);
                }
            }
            for (DomainTo vo : context.voList) {
                if (vo.isNew() && id.equals(vo.getId())) {
                    //тип доменного объекта не совпадает с типом свойства
                    if (!vo.getType().equals(type)) {
                        toService =  domainToServicesResolver.resolveToService(vo.getType());
                    }
                    return create(toService, vo);
                }
            }
        }
        return null;
    }

    /**
     * Создать доменный объект по DomainTO.
     * 
     * @param toService - DomainTo сервис 
     * @param dto - DomainTo объект
     * @return доменный объект.
     */
	public <T extends DomainObject<?>> T create(final DomainToService<T, ?> toService, final DomainTo dto) {
        return findInContext(new DomainObjectIdentity<>(toService.parseKey(dto.getId()), dto.getType()));
    }

    /**
     * Создать список объектов одного доменного типа по их идентификаторам.
     *
     * @param ids  - список идентификаторов
     * @param type - тип объектов
     * @return {@link List} объектов
     */
    public <T extends DomainObject<?>> List<T> createList(List<String> ids, String type) {
        final List<T> ol = new ArrayList<>();
        createAll(ids, type, ol);
        return ol;
    }

    /**
     * Создать набор объектов одного доменного типа по их идентификаторам.
     *
     * @param ids  - список идентификаторов
     * @param type - тип объектов
     * @return {@link java.util.Set} объектов
     */
    public <T extends DomainObject<?>> Set<T> createSet(List<String> ids, String type) {
    	final Set<T> os = newHashSet();
    	createAll(ids, type, os);
    	return os;
    }

    /**
     * Ищет доменный объект в контексте.
     *
     * @param identity доменный объект
     * @return экземляр доменного объекта из контекста, если в контексте такой объект отсутствует {@code null}.
     */
    @SuppressWarnings("unchecked")
	protected  <T extends DomainObject<?>> T findInContext(Identity<?> identity) {
        List<DomainObject<?>> domainObjects = context != null ? context.objects : Collections.emptyList();
        for (DomainObject<?> domainObject : domainObjects) {
            if (new DomainObjectIdentity<>(domainObject).equals(identity)) {
                return (T) domainObject;
            }
        }
        return null;
    }

    private void createAll(List<String> ids, String type, Collection<? extends DomainObject<?>> collection) {
        if (ids != null) {
            Iterables.forEach(ids, id -> collection.add(create(id, type)));
        }
    }
}
