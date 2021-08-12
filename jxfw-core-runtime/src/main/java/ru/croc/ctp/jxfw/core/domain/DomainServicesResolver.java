package ru.croc.ctp.jxfw.core.domain;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис для поиска доменных сервисов по типу сущности.
 *
 * @since 1.1
 */
public interface DomainServicesResolver {

    /**
     * Поиск реализации доменного сервиса по доменному типу.
     *
     * @param entity Доменный тип
     * @return Найденный доменный сервис
     */
    @Nonnull
    @SuppressWarnings("rawtypes")
    DomainService resolveService(DomainObject<?> entity);

    /**
     * Поиск реализации доменного сервиса по имени доменного типа.
     *
     * @param typeName Имя доменного типа
     * @return Найденный доменный сервис или null, если сервис не найден.
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    DomainService resolveService(String typeName);

    /**
     * Ресолвер сервисов для полнотекста.
     *
     * @param typeName название (simpleName) типа доменного объекта (может иметь префикс "solr.").
     * @return соответствующий доменный сервис.
     */
    @Nullable
    @SuppressWarnings("rawtypes")
    DomainService resolveFulltextService(String typeName);

    /**
     * Ресолвер To сервисов для полнотекста.
     *
     * @param typeName название типа доменного объекта
     * @return сервис трансформации доменных объектов
     * @deprecated метод вынесен в {@link DomainToServicesResolver}.
     */
    @Deprecated
    @Nullable
    @SuppressWarnings("rawtypes")
    DomainToService resolveFulltextToService(String typeName);

    /**
     * Поиск реализации доменного сервиса по имени доменного типа и последовательности из одного и более суфиксов.
     *
     * @param typeName - имя доменного типа
     * @return доменный сервис трансформации
     *
     * @deprecated метод вынесен в {@link DomainToServicesResolver}, использовать его.
     */
    @Nonnull
    @SuppressWarnings("rawtypes")
    DomainToService resolveToService(String typeName);

    /**
     * Поиск реализации доменного сервиса по имени, получаемому
     * по доменному объекту и последовательности из одного и более суфиксов.
     *
     * @param domainObject - доменный объект.
     * @param suffixes     - обязательный параметр
     * @param <T>          - тип доменного сервиса
     * @return доменный сервис
     */
    @Nullable
    <T> T resolveDomainService(DomainObject<?> domainObject, String... suffixes);

    /**
     * Поиск реализации доменного сервиса по имени доменного типа и последовательности из одного и более суфиксов.
     *
     * @param typeName - имя доменного типа
     * @param suffixes - не обязательный параметр
     * @param <T>      - тип доменного сервиса
     * @return доменный сервис
     */
    @Nullable
    <T> T resolveDomainService(String typeName, String... suffixes);

}
