package ru.croc.ctp.jxfw.core.load.context;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.context.StoreContextKeys;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Nonnull;

/**
 * Контекст загрузки содержащий контекст сохранения.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public class LoadContextWithStoreContext<T extends DomainObject<? extends Serializable>> extends LoadContextDelegate<T> {

    /**
     * Создает {@link LoadContextDelegate}.
     *
     * @param loadContext оборачиваемый контекст
     */
    private LoadContextWithStoreContext(final LoadContext<T> loadContext) {
        super(loadContext);
    }

    /**
     * Помещает {@link StoreContext} в {@link LoadContext}.
     *
     * @param storeContext
     *        помещаемый {@link StoreContext}
     * @return {@link LoadContextWithStoreContext}
     */
    public LoadContextWithStoreContext<T> putStoreContext(StoreContext storeContext) {
        getCommonObjects().put(LoadContextKeys.STORE_CONTEXT, storeContext);
        return this;
    }

    /**
     * Получает {@link StoreContext} из {@link LoadContext}.
     *
     * @return {@link StoreContext}.
     */
    public StoreContext getStoreContext() {
        return (StoreContext) this.getCommonObjects().get(LoadContextKeys.STORE_CONTEXT);
    }

    /**
     * Проверяет, существует ли {@link StoreContext}.
     *
     * @return {@code true} в случае существования.
     */
    public boolean exists() {
        return this.getCommonObjects().containsKey(LoadContextKeys.STORE_CONTEXT);
    }

    /**
     * Получить обертку {@link LoadContextWithStoreContext} над другим контекстом.
     *
     * @param loadContext контекстом
     * @return {@link LoadContextWithStoreContext}
     */
    public static <T extends DomainObject<? extends Serializable>> LoadContextWithStoreContext<T> from(LoadContext<T> loadContext) {
        return new LoadContextWithStoreContext<>(loadContext);
    }

    public static class Builder<T extends DomainObject<? extends Serializable>> extends LoadContext.Builder<T> {

        private final StoreContext storeContext;

        public Builder(@Nonnull StoreContext storeContext) {
            this.storeContext = storeContext;
            withPrincipal(storeContext.getPrincipal())
                .withTimeZone(storeContext.getTimeZone())
                .withLocale(storeContext.getLocale())
                .withHints(storeContext.getHints());
        }

        @Override
        public Builder<T> withLocale(@Nonnull Locale locale) {
            return (Builder<T>) super.withLocale(locale);
        }

        @Override
        public Builder<T> withTimeZone(@Nonnull TimeZone timeZone) {
            return (Builder<T>) super.withTimeZone(timeZone);
        }

        @Override
        public Builder<T> withHints(@Nonnull List<String> hints) {
            return (Builder<T>) super.withHints(hints);
        }

        @Override
        public Builder<T> withPrincipal(Principal principal) {
            return (Builder<T>) super.withPrincipal(principal);
        }

        @Override
        @SuppressWarnings("unchecked")
        public LoadContextWithStoreContext<T> build() {
            LoadContext<T> loadContext = super.build();
            Map<String, Object> commonObjects = (Map<String, Object>) storeContext.getCommonObjects()
                    .get(StoreContextKeys.LOAD_CONTEXT_COMMON_OBJECT);
            if (commonObjects != null) {
                loadContext.getCommonObjects().putAll(commonObjects);
            }
            return LoadContextWithStoreContext.from(loadContext).putStoreContext(storeContext);
        }
    }
}
