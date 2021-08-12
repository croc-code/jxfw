package ru.croc.ctp.jxfw.core.domain;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Сервис для поиска сервисов трансформации(доменных объектов) по типу сущности.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public interface DomainToServicesResolver {

    /**
     * Ресолвер To сервисов для полнотекста.
     *
     * @param typeName имя (simpleName) типа доменного объекта (может иметь префикс "solr.").
     * @return доменный сервис трансформации
     */
    @Nullable
    <S extends DomainToService<T, ?>, T extends DomainObject<?>>
    S resolveFulltextToService(String typeName);

    /**
     * Поиск реализации доменного сервиса по имени доменного типа и последовательности из одного и более суфиксов.
     *
     * @param typeName - имя доменного типа
     * @return доменный сервис трансформации
     */
    @Nonnull
    <S extends DomainToService<T, ?>, T extends DomainObject<?>>
    S resolveToService(String typeName);
}
