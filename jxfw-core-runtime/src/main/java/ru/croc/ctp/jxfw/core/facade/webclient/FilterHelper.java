package ru.croc.ctp.jxfw.core.facade.webclient;

import com.querydsl.core.types.Path;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

/**
 * Интерфейс для помощи в создании фильтра по конкретным доменным сущностям.
 * Реализация интерфейса, как правило является TO-сервис сущности.
 *
 * @param <T> тип доменной сущности.
 * @since 1.0
 */
public interface FilterHelper<T extends DomainObject<?>> {

    /**
     * @return создает {@link Path} для конкретной сушности.
     */
    Path<T> createPath();

    /**
     * Парсинг значения свойства по его названию.
     * Здесь можно произвести необходимые моификации со значением свойства прежде чем вернуть,
     * например обернуть в прокси-обертку.
     *
     * @param propName название свойства
     * @param value    значение свойства
     * @return значение свойства
     */
    Object parsePropValue(String propName, Object value);

    /**
     * Возвращает имя свойства для создания запроса в QueryDsl.
     *
     * @param propName - название свойства в модели
     * @return - название свойства для запроса
     */
    default String createPathProperty(String propName) {
        return propName;
    }

    /**
     * Применятеся ли регистрозависимость при фильтрации.
     * @return да/нет
     */
    boolean isIgnoreCaseForOperatorOfFiltering();

}
