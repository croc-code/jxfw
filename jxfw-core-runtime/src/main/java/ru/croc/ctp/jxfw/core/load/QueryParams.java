package ru.croc.ctp.jxfw.core.load;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Объект передается в качестве аргумента {@link LoadService}' у,
 * и содержит в себе необходимые данные для выполнения запроса.
 *
 * @param <T>  тип
 * @param <ID> идентификатор.
 */
public interface QueryParams<T extends DomainObject<ID>, ID extends Serializable> {

    /**
     * Идентификатор.
     *
     * @return Идентификатор
     */
    ID getId();

    /**
     * Пагинация.
     *
     * @return {@link Pageable}
     */
    Pageable getPageable();

    /**
     * Сортировки.
     *
     * @return {@link Sort}
     */
    Sort getSort();

    /**
     * Предикат запроса. Метод возвращает предикат,
     * соответствующий типу хранилища. В случае запроса по идентификатору здесь
     * необходимо вернуть валидный предикат, который будет модифицироваться в событиях до загрузки.
     * Условие по идентификатору добавляется через логическое И к предикату, возвращенному из событий
     * непосредственно перед запросом данных в LoadService.
     *
     * @return {@link Predicate}
     */
    @Nonnull
    Predicate getPredicate();


    /**
     * Данных об доп. подружаемых свойствах (прелоады).
     *
     * @return список свойств
     */
    Set<String> getPreloads();

    /**
     * Получить тип доменного объекта.
     * @return тип доменного объекта
     */
    @Nonnull
    String getDomainObjectTypeName();
}
