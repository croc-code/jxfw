package ru.croc.ctp.jxfw.core.domain;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

/**
 * Интерфейс Spring-сервисов для доменных моделей.
 *
 * @param <T>  - тип модели
 * @param <TK> - тип для первичного ключа
 * @param <Q>  - тип предиката для запроса
 */
public interface DomainService<T extends DomainObject<?>, TK extends Serializable, Q> {

    /**
     * Создать новый объект.
     *
     * @param id - ИД новго объекта
     * @return новый объект
     */
    T createNew(TK id);

    /**
     * Получить все объекты модели.
     *
     * @return список объектов
     */
    Iterable<T> getObjects();

    /**
     * Получить все объекты модели.
     *
     * @param query - по запросу
     * @return список объектов
     */
    Iterable<T> getObjects(final Q query);

    /**
     * Получить все объекты модели.
     *
     * @param query    запрос
     * @param pageable пагинация
     * @return список объектов
     */
    Iterable<T> getObjects(final Predicate query, final Pageable pageable);

    /**
     * Получить все объекты модели.
     *
     * @param query запрос
     * @param sort  сортировка
     * @return список объектов
     */
    Iterable<T> getObjects(final Predicate query, final Sort sort);

    /**
     * Получить объект модели по ИД.
     *
     * @param id - ИД объекта, которого необходимо найти
     * @return объект
     */
    T getObjectById(TK id);

    /**
     * Получить объект модели по ИД.
     *
     * @param id        - ИД объекта, которого необходимо найти
     * @param saveState - зафиксировать загруженное состояние в доменном объекте
     * @return объект
     */
    T getObjectById(TK id, boolean saveState);

    /**
     * Получить объект модели по предикату.
     *
     * @param query     - предикат по которому необходимо найти объект
     * @param id        - ИД объекта, которого необходимо найти
     * @return объект
     */
    T getObjectById(Q query, TK id);

    /**
     * Получить объект модели по предикату.
     *
     * @param query     - предикат по которому необходимо найти объект
     * @param id        - ИД объекта, которого необходимо найти
     * @param saveState - зафиксировать загруженное состояние в доменном объекте
     * @return объект
     */
    T getObjectById(Q query, TK id, boolean saveState);

    /**
     * Сохранить объект модели.
     *
     * @param entity - объект который необходимо сохранить
     * @return сохраненный объект
     */
    T save(T entity);

    /**
     * Сохранить объект модели с возможностью фиксации сохраненного состояния.
     *
     * @param entity    - объект который необходимо сохранить
     * @param saveState - зафиксировать сохраненное состояние в доменном объекте
     * @return сохраненный объект
     */
    T save(T entity, boolean saveState);

    /**
     * Удалить объект по ID.
     *
     * @param id - ИД объекта, который необходимо удалить
     */
    void delete(final TK id);

    /**
     * Удалить все объекты модели типа T.
     */
    void deleteAll();

    /**
     * Валидация для READ_ONLY режима.
     *
     * @param entity - сущность, которую нужно валидировать
     * @return флаг прошла или не прошла валидация
     */
    boolean validate(T entity);

    /**
     * Валидация для READ_ONLY режима.
     *
     * @param entity - сущность, которую нужно валидировать
     * @param facade - {@code true} валидация режима при получении с фасада, иначе валидация перед сохранением.
     * @param context - контекст валидатора
     * @return флаг прошла или не прошла валидация
     */
    boolean validate(T entity, boolean facade, ConstraintValidatorContext context);

    /**
     * Возвращает ограничение на максимальное количество объектов, возвращаемое сервисом.
     * при значении <0 нет ограничения
     * @return максимальное количество объектов, которое может вернуть доменный сервис.
     */
    int getMaxObjects();

    /**
     * Метод для загрузки навигируемых свойств.
     * @param domainObjects список объектов для которых необходимо загрузить свойства.
     * @param preloads список навигируемых свойств.
     * @return список загруженных объектов.
     */
    default List<DomainObject<?>> getPreloads(Iterable<? extends T> domainObjects, List<List<String>> preloads) {
        throw new UnsupportedOperationException(
            "Service " + this.getClass().getSimpleName() + " doesn't support new preloads"
        );
    }
}
