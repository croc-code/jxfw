package ru.croc.ctp.jxfw.core.store.events;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.impl.ResolvableTypeHelper;
import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Событие, генерируемое для каждого доменного объекта в контексте сохранения. 
 * @author akogun
 * @since 1.5
 * @param <T> конкретный тип доменного объекта
 */
@SuppressWarnings("serial")
public final class DomainObjectStoreEvent<T extends DomainObject<?>> extends StoreEvent implements ResolvableTypeProvider {

    /**
     * @param domainObject доменный объект, для которого сгенерировано событие (не может быть {@code null}).
     * @param storeContext контекст сохранения
     */
    public DomainObjectStoreEvent(T domainObject, StoreContext storeContext) {
        super(domainObject, storeContext);
    }

    /**
     * @return доменный объект.
     */
    @SuppressWarnings("unchecked")
    public T getDomainObject() {
        return (T) getSource();
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableTypeHelper.getResolvableTypeForEvent(this);
    }
}