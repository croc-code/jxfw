package ru.croc.ctp.jxfw.cass.predicate;

import com.datastax.driver.core.querybuilder.Select;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.cass.facade.webclient.SelectComposer;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.Nullable;

/**
 * Контекст запроса к С*.
 * @param <T> класс доменного объекта.
 *
 * @author smufazzalov
 * @since jXFW 1.6.0
 */
public class CassandraQueryContext<T> implements Predicate {
    private Class<T> clazz;
    private ObjectFilter objectFilter;
    private Sort sort;
    private Pageable pageable;
    private SelectComposer selectComposer;
    private Select select;

    /**
     * Конструктор.
     * @param clazz класс доменного объекта
     * @param selectComposer selectComposer
     * @param objectFilter фильтрация с WC
     * @param sort сортировки
     * @param pageable пагинация
     */
    public CassandraQueryContext(
            Class<T> clazz,
            SelectComposer selectComposer,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable
    ) {
        this.clazz = clazz;
        this.selectComposer = selectComposer;
        this.objectFilter = objectFilter;
        this.sort = sort;
        this.pageable = pageable;
    }

    private CassandraQueryContext(Select select) {
        this.select = select;
    }

    public ObjectFilter getObjectFilter() {
        return objectFilter;
    }

    public Sort getSort() {
        return sort;
    }

    public Pageable getPageable() {
        return pageable;
    }

    @Override
    public Predicate not() {
        throw new NotImplementedException();
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C context) {
        throw new NotImplementedException();
    }

    @Override
    public Class<? extends Boolean> getType() {
        throw new NotImplementedException();
    }

    public Class<T> getClazz() {
        return clazz;
    }

    /**
     * Запрос драйвера С*.
     * @return вернуть {@link Select}
     */
    public Select getStatement() {
        if (select != null) {
            return select;
        }

        if (sort != null && sort.isSorted()) {
            return selectComposer.toStatement(objectFilter, sort);
        } else if (pageable != null) {
            return selectComposer.toStatement(objectFilter, pageable);
        }

        return selectComposer.toStatement(objectFilter, clazz);
    }

    /**
     * Обертка для передачи запроса доменным сервисам С*.
     * @param select запрос
     * @return {@see Predicate}
     */
    public static CassandraQueryContext of(Select select) {
        return new CassandraQueryContext(select);
    }
}
