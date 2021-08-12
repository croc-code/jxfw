package ru.croc.ctp.jxfw.core.load;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.annotation.Nonnull;

/**
 * Контекст конвейера загрузки доменных объектов.
 *
 * @param <T> доменный тип загружаемых данных
 * @author AKogun
 * @since 1.6.0
 */
public class LoadContext<T extends DomainObject<? extends Serializable>>
        extends GeneralLoadContext<T, DomainObject<? extends Serializable>> {

    private final boolean isForExport;

    /**
     * Конструктор.
     */
    public LoadContext() {
        super();
        isForExport = false;
    }

    /**
     * Конструктор.
     *
     * @param builder билдер
     */
    protected LoadContext(Builder<T> builder) {
        super(builder);
        isForExport = builder.isForExport;
    }

    @Override
    public LoadResult<T> getLoadResult() {
        return (LoadResult<T>) super.getLoadResult();
    }


    public boolean isForExport() {
        return isForExport;
    }

    /**
     * Билдер контекста.
     *
     * @param <T> доменный тип загружаемых данных
     */
    public static class Builder<T extends DomainObject<? extends Serializable>> extends GeneralLoadContext.Builder<T,
            DomainObject<?>> {

        private boolean isForExport = false;

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
        public LoadContext<T> build() {
            return new LoadContext<>(this);
        }

        /**
         * Пометить контекст для формирования файла экспорта.
         * По умолчанию контекст не является контекстом для экпорта.
         *
         * @return себя
         */
        public Builder<T> forExport() {
            isForExport = true;
            return this;
        }

    }
}