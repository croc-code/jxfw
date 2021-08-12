package ru.croc.ctp.jxfw.core.facade.webclient;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * Поставщик предикатов, имеет разные имплементациий в каждом типе хранилищ.
 */
public interface PredicateProvider {

    /**
     * Построить предикат по ObjectFilter.
     * @param domainTypeName тип доменного объекта.
     * @param objectFilter фильтр, пришедший с клиента
     * @param sort сортировка
     * @param pageable пагинация
     * @return предикат
     */
    Predicate buildPredicate(
            @Nonnull String domainTypeName,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable);

    /**
     * Построить предикат по Id.
     * @param domainTypeName тип доменного объекта.
     * @param id ид доменного объекта.
     * @return предикат
     */
    Predicate buildPredicateById(
            @Nonnull String domainTypeName,
            @Nonnull Serializable id);

    /**
     * Поддерживает ли поставщик данный тип доменных объектов.
     * @param domainTypeName тип доменного объекта.
     * @return да\нет
     */
    boolean accepts(String domainTypeName);
}
