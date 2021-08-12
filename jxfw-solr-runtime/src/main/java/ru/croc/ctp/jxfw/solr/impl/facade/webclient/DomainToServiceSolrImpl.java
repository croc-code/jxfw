package ru.croc.ctp.jxfw.solr.impl.facade.webclient;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.DomainToServiceImpl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Абстрактный класс для сервисов транформации модуля SOLR.
 *
 * @param <T>  - тип доменной сущности
 * @param <IdT> - тип первичного ключа
 */
public abstract class DomainToServiceSolrImpl<T extends DomainObject<IdT>, IdT extends Serializable>
        extends DomainToServiceImpl<T, IdT> {

    /**
     * Конструктор.
     *
     * @param resourceStore - сервис сохранения бинарных файлов.
     */
    public DomainToServiceSolrImpl(ResourceStore resourceStore, DomainToServicesResolver resolver) {
        super(resourceStore, resolver);
    }

    /**
     * Метод получения идентификаторов в формате Base64 доменных объектов из списка.
     *
     * @param list - список доменных объектов
     * @return список идентификаторов
     */
    @SuppressWarnings("unchecked")
    public List<String> getObjectIdsBase64(Collection<? extends DomainObject<?>> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::getIdInBase64).collect(Collectors.toList());
    }

    /**
     * Метод получения идентификаторов в формате Base64 доменного объекта.
     *
     * @param domainObject - доменный объект
     * @return ид в формате base64
     */
    @SuppressWarnings("unchecked")
    public String getIdInBase64(DomainObject<?> domainObject) {
        return domainObject == null ? null : serializeKey((IdT) domainObject.getId());
    }

}
