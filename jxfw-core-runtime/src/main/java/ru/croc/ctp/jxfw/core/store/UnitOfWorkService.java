package ru.croc.ctp.jxfw.core.store;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.util.List;


/**
 * Сервис сохранения набора объектов (UoW).
 * @since 1.1
 */
public interface UnitOfWorkService {

    /**
     * Сохраняет объекты из UoW в хранилище.
     *
     * @param storeContext контекст сохранения, после выполнения операции, содержит данные о результате сохранения
     */
    void store(StoreContext storeContext);

    /**
     * Сохраняет объекты из UoW в хранилище.
     *
     * @param uow Набор объектов для сохранения
     * @return Результат сохранения UoW
     * @deprecated с версии 1.5, рекомендуется использовать {@code store(storeContext)}.
     */
    @Deprecated
    StoreResult store(List<? extends DomainObject<?>> uow);
}