package ru.croc.ctp.jxfw.core.load;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Компонент загрузки доменных объектов и публикации событий конвейера загрузки.
 *
 * @author smufazzalov
 * @since jxfw 1.6.0
 */
public interface LoadService {

    /**
     * Загрузка доменных объектов, хинтов и прелоадов
     * с публикациен событий.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param loadContext Контекст загрузки {@link LoadContext}
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * @return список доменных объектов
     */
    @Nonnull
    <T extends DomainObject<ID>, ID extends Serializable> List<T> load(@Nonnull QueryParams<T, ID> queryParams,
                                                                       @Nonnull LoadContext<T> loadContext);

    /**
     * Метод определяет возможность обработки запроса сервисом по типу объекта (не обязательно доменному объекту).
     *
     * @param typeName имя типа
     * @return да/нет
     */
    boolean accepted(String typeName);

    /**
     * Загрузка доменного объекта, хинтов и прелоадов
     * с публикациен событий.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param loadContext контекст загрузки {@link LoadContext}
     * 
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * 
     * @return доменный объект
     * @throws IllegalStateException
     */
    @Nonnull
    <T extends DomainObject<ID>, ID extends Serializable> T loadOne(QueryParams<T, ID> queryParams, LoadContext<T>
            loadContext) throws IllegalStateException;

    /**
     * TODO javadoc
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param loadContext контекст загрузки {@link LoadContext}
     * 
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * 
     * @return {@link Optional} от доменного объекта
     * @throws IllegalStateException
     */
    @Nonnull
    <T extends DomainObject<ID>, ID extends Serializable> Optional<T> loadOptional(QueryParams<T, ID> queryParams,
                                                                                   LoadContext<T> loadContext) throws
            IllegalStateException;

}
