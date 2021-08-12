package ru.croc.ctp.jxfw.core.load;

import com.querydsl.core.types.Predicate;
import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;

/**
 * Интерфейс фабрики для постройки {@link QueryParamsBuilder}.
 *
 * @author smufazzalov
 * @since jxfw 1.6.0
 */
public interface QueryParamsBuilderFactory {
    
    /**
     * Получить {@link QueryParamsBuilder}.
     *
     * @param <T>  тип доменного объекта
     * @param <ID> тип идентификатора доменного объекта
     * @param type доменный тип
     * @return {@link QueryParamsBuilder}
     */
    <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(Class<T> type);

    /**
     * Получить {@link QueryParamsBuilder}.
     *
     * @param <T>  тип доменного объекта
     * @param <ID> тип идентификатора доменного объекта
     * @param typeName имя типа доменного объекта
     * @return {@link QueryParamsBuilder}
     */
    <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(String typeName);

    /**
     * Получить {@link QueryParamsBuilder}.
     *
     * @param <T>  тип доменного объекта
     * @param <ID> тип идентификатора доменного объекта
     * @param type доменный тип
     * @param predicate predicate
     * @return {@link QueryParamsBuilder}
     */
    <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(
            Class<T> type,
            Predicate predicate
    );
    
    /**
     * Получить {@link QueryParamsBuilder}.
     *
     * @param <T>  тип доменного объекта
     * @param <ID> тип идентификатора доменного объекта
     * @param typeName имя типа доменного объекта
     * @param predicate predicate
     * @return {@link QueryParamsBuilder}
     */
    <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(
            String typeName,
            Predicate predicate);

    /**
     * Получить {@link QueryParamsBuilder}.
     *
     * @param <T>  тип доменного объекта
     * @param <ID> тип идентификатора доменного объекта
     * @param type доменный тип
     * @param id       значение идентификатора доменного объекта
     * @return {@link QueryParamsBuilder}
     */
    <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(
            Class<T> type, 
            ID id
    );
    
    /**
     * Получить {@link QueryParamsBuilder}.
     *
     * @param <T>  тип доменного объекта
     * @param <ID> идентификатор доменного объекта
     * @param typeName имя типа доменного объекта
     * @param id       значение идентификатора доменного объекта
     * @return {@link QueryParamsBuilder}
     */
    <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(
            String typeName, 
            ID id
    );
    
}
