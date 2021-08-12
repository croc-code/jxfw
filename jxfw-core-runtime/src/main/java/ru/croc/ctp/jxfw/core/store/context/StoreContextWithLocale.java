package ru.croc.ctp.jxfw.core.store.context;

import ru.croc.ctp.jxfw.core.store.StoreContext;

import java.util.Locale;

/**
 * Устарел, пользуйтесь {@link StoreContext#getLocale()}.
 * Контекст сохранения c поддержкой Locale.
 *
 * @author AKogun
 * @since 1.6
 */
@Deprecated
public class StoreContextWithLocale extends StoreContextDelegate {

    private StoreContextWithLocale(StoreContext storeContext) {
        super(storeContext);
    }

    /**
     * Установить локаль.
     *
     * @param locale локаль
     * @return {@link StoreContextWithLocale}
     */
    public StoreContextWithLocale putLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    /**
     * Получить обертку {@link StoreContextWithLocale} над другим контекстом.
     *
     * @param storeContext контекстом
     * @return {@link StoreContextWithLocale}
     */
    public static StoreContextWithLocale from(StoreContext storeContext) {
        return new StoreContextWithLocale(storeContext);
    }

}
