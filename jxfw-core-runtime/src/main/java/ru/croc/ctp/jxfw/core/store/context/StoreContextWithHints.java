package ru.croc.ctp.jxfw.core.store.context;

import ru.croc.ctp.jxfw.core.store.StoreContext;

import java.util.List;

/**
 * Устарел, пользуйтесь {@link StoreContext#getHints()}.
 * Контекст сохранения c hints, для управления логикой в рамках отработки конвейера сохранения.
 *
 * @author AKogun
 * @since 1.6
 */
@Deprecated
public class StoreContextWithHints extends StoreContextDelegate {

    private StoreContextWithHints(StoreContext storeContext) {
        super(storeContext);
    }

    /**
     * Установить хинты.
     *
     * @param hints хинты
     * @return {@link StoreContextWithHints}
     */
    public StoreContextWithHints putHints(List<String> hints) {
        setHints(hints);
        return this;
    }

    /**
     * Получить обертку {@link StoreContextWithHints} над другим контекстом.
     *
     * @param storeContext контекстом
     * @return {@link StoreContextWithHints}
     */
    public static StoreContextWithHints from(StoreContext storeContext) {
        return new StoreContextWithHints(storeContext);
    }

}