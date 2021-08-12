package ru.croc.ctp.jxfw.core.store;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.List;
import java.util.Locale;

/**
 * Интерфейс сервиса сохранения.
 *
 * @author AKogun
 * @since 1.5
 */
public interface StoreService {

    /**
     * Deprecated используйте {@link StoreService#store(ru.croc.ctp.jxfw.core.store.StoreContext)}.
     *
     * @param uow список dto
     * @return {@link StoreResult}
     */
    @Deprecated
    StoreResult store(List<DomainTo> uow);

    /**
     * Deprecated используйте {@link StoreService#store(ru.croc.ctp.jxfw.core.store.StoreContext)}.
     *
     * @param uow    список dto
     * @param hints  хинты
     * @param locale локаль
     * @param txId   txId
     * @return {@link StoreResult}
     */
    @Deprecated
    StoreResult store(List<DomainTo> uow, List<String> hints, Locale locale, String txId);

    /**
     * Сохранить.
     *
     * @param storeContext {@link StoreContext}
     * @return {@link StoreResult}
     */
    StoreResult store(StoreContext storeContext);
}