package ru.croc.ctp.jxfw.jpa.load.events;

import com.querydsl.core.BooleanBuilder;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;

import javax.annotation.Nonnull;


/**
 * Событие для применения прикладной логики к условиям загрузки JPA-сущностей
 * Публикуется в конвейере чтения после {@link JpaPreCheckSecurityEvent}, до загрузки данных из хранилища.
 * Отличается от базового тем, что тип возвращаемого предиката - BooleanBuilder.
 *
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 * @author OKrutova
 * @since 1.6
 */
public class JpaBeforeLoadEvent<T extends DomainObject<?>> extends BeforeLoadEvent<T> {

    /**
     * Создание нового события.
     *
     * @param queryParams параметры запроса.
     * @param loadContext контекст read конвейера.
     */
    public JpaBeforeLoadEvent(QueryParams<T, ?> queryParams, LoadContext<T> loadContext) {
        super(queryParams, loadContext);
    }

    @Override
    @Nonnull
    public BooleanBuilder getPredicate() {
        if (!(super.getPredicate() instanceof BooleanBuilder)) {
            throw new IllegalStateException("Predicate is not a BooleanBuilder instance for " + getDomainObjectTypeName());
        }
        return (BooleanBuilder) super.getPredicate();
    }
}
