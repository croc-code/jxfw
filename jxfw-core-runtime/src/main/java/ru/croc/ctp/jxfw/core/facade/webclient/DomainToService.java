package ru.croc.ctp.jxfw.core.facade.webclient;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;

import java.io.Serializable;
import java.util.List;

/**
 * Интерфейс сервисов трансформации DTO-объектов.
 *
 * @param <T>  Тип доменного объекта
 * @param <ID> Тип ключа доменного объекта
 */
public interface DomainToService<T extends DomainObject<ID>, ID extends Serializable> extends DomainToMapper<T> {

    /**
     * Сериализует переданный идентификатор доменного объекта в строку.
     *
     * @param key Идентификатор доменного объекта
     * @return Строковое представление идентификатора доменного объекта
     */
    String serializeKey(ID key);

    /**
     * Десериализует идентификатор из строкового представления.
     *
     * @param key Строковое пердставление идентификатора доменного объекта
     * @return Десериализованный идентификатор
     */
    ID parseKey(String key);

    /**
     * Создает доменный объект по строковому представлению ключа.
     *
     * @param key Строковое представление ключа
     * @return Созданный доменный объект
     */
    T createNewDomainObject(String key);

    /**
     * Находит доменный объект по строковому представлению ключа.
     *
     * @param key Строковое представление ключа
     * @return Найденный доменный объект
     * @deprecated use {@link DomainToService#getDomainObjectById(String, LoadContext)}
     */
    @Deprecated
    T getDomainObjectById(String key);

    /**
     * Находит доменный объект по строковому представлению ключа.
     *
     * @param key Строковое представление ключа
     * @param loadContext контекст загрузки
     * @return Найденный доменный объект
     */
    T getDomainObjectById(String key, LoadContext<T> loadContext);

    /**
     * Находит доменный сервис по типу переданного объекта и выполняет преобразование в DTO.
     *
     * @param domainObject Доменный объект, который нужно преобразовать в DTO
     * @param expand Массивные ссылочные свойства, которые необходимо добавить при преобразовании в DTO
     * @return DTO соответсвующий переданному доменному объекту
     */
    DomainTo toToPolymorphic(T domainObject, String... expand);

    /**
     * Находит доменный сервис по типу переданного объекта и выполняет преобразование в DTO.
     *
     * @param domainObjectList Доменный объект, который нужно преобразовать в DTO
     * @param type тип доменных объектов
     * @param expand Массивные ссылочные свойства, которые необходимо добавить при преобразовании в DTO
     * @return DTO соответсвующий переданному доменному объекту
     */
    List<DomainTo> toToPolymorphic(List<T> domainObjectList, String type, String... expand);

}
