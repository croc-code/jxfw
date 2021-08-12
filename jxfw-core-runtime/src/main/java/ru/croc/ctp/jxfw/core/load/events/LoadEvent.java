package ru.croc.ctp.jxfw.core.load.events;

import com.querydsl.core.types.Predicate;

import java8.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import javax.annotation.Nonnull;


/**
 * Базовый класс событий конвейера загрузки.
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 *
 * @author SMufazzalov
 * @since 1.4
 */
public abstract class LoadEvent<T extends DomainObject<?>> implements ResolvableTypeProvider {

    private final QueryParams<T, ?> queryParams;
    private final LoadContext<T> loadContext;

    /**
     * Конструтор.
     *
     * @param queryParams параметры запроса
     * @param loadContext контекст загрузки
     */
    public LoadEvent(
            QueryParams<T, ?> queryParams,
            LoadContext<T> loadContext
    ) {
        this.loadContext = loadContext;
        this.queryParams = queryParams;
    }


    /**
     * Возвращает условие запроса.
     *
     * @return условие запроса.
     */
    @Nonnull
    public Predicate getPredicate() {
        return queryParams.getPredicate();
    }

    /**
     * Возвращает сортировку, если она была передана.
     *
     * @return сортировка.
     */
    public Optional<Sort> getSort() {
        return Optional.ofNullable(queryParams.getSort());
    }

    /**
     * Возвращает разбиение на страницы, если оно было передано.
     *
     * @return разбиение на страницы.
     */
    public Optional<Pageable> getPageable() {
        return Optional.ofNullable(queryParams.getPageable());
    }

    /**
     * Возвращает ID искомого объекта, если оно было передано.
     *
     * @return ID искомого объекта.
     */
    public Optional<Object> getObjectId() {
        return Optional.ofNullable(queryParams.getId());
    }

    /**
     * Класс доменного объекта.
     * @return Класс доменного объекта.
     */
    @Nonnull
    public String getDomainObjectTypeName() {
        return queryParams.getDomainObjectTypeName();
    }

    /**
     * Контекст загрузки.
     * @return Контекст загрузки.
     */

    @Nonnull
    public LoadContext<T> getLoadContext() {
        return loadContext;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("typeName", queryParams.getDomainObjectTypeName())
                .append("objectId", getObjectId())
                .append("predicate", (Predicate)getPredicate())
                .append("pageable", getPageable())
                .append("sort", getSort())
                .append("loadContext", getLoadContext())
                .toString();
    }


    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType
                .forClassWithGenerics(
                        this.getClass(),
                        ResolvableType.forClass(
                                DomainObjectUtil.getDomainObjectType(
                                        queryParams.getDomainObjectTypeName().replaceFirst("solr.", "")
                                )
                        )
                );
    }

}
