package ru.croc.ctp.jxfw.core.facade.webclient;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.util.Map;

/**
 * Сервис, устанавливающий значения полей в доменный объект.
 *
 * @since 1.6
 * @author OKrutova
 */
public interface DomainDeserializer {

    /**
     * Установить значения полей в доменный объект.
     * @param domainObject доменный объект
     * @param properties набор свойств
     */
    void setProperties(DomainObject<?> domainObject, Map<String, Object> properties);
}
