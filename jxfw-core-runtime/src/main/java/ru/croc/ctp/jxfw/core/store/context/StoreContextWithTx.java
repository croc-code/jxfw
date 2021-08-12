package ru.croc.ctp.jxfw.core.store.context;

import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Устарел, пользуйтесь {@link StoreContext#getTxId}.
 * Контекст сохранения c txId, для возможности ...
 *
 * @author AKogun
 * @since 1.6
 */
@Deprecated
public class StoreContextWithTx extends StoreContextDelegate {

    private StoreContextWithTx(StoreContext storeContext) {
        super(storeContext);
    }

    /**
     * Установить txId.
     *
     * @param txId txId
     * @return {@link StoreContextWithTx}
     */
    public StoreContextWithTx putTxId(String txId) {
        setTxId(txId);
        return this;
    }

    /**
     * /**
     * Получить обертку {@link StoreContextWithTx} над другим контекстом.
     *
     * @param storeContext контекстом
     * @return {@link StoreContextWithTx}
     */
    public static StoreContextWithTx from(StoreContext storeContext) {
        return new StoreContextWithTx(storeContext);
    }
}