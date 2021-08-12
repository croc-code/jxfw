package ru.croc.ctp.jxfw.core.load;

import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.annotation.Nonnull;

/**
 * Общий контекст конвейера загрузки. 
 * Применяется в источниках данных недоменных типов.
 * 
 * @param <T> тип загружаемых данных
 * @param <U> тип дополнительно загружаемых данных
 * 
 * @author AKogun
 * @since 1.6.0
 */
public class GeneralLoadContext<T, U> {

    private final Locale locale;
    private final TimeZone timeZone;
    private final List<String> hints;
    private final Principal principal;
    
    private final Map<String, Object> commonObjects = new HashMap<>();
    
    private GeneralLoadResult<T, U> loadResult;

    /**
     * Конструктор.
     */
    public GeneralLoadContext() {
        this(new Builder<>());
    }

    protected GeneralLoadContext(Builder<T, U> builder) {
        this.locale = builder.locale;
        this.timeZone = builder.timeZone;
        this.hints = builder.hints;
        this.principal = builder.principal;
    }

    @Nonnull
    public Locale getLocale() {
        return locale;
    }

    @Nonnull
    public TimeZone getTimeZone() {
        return timeZone;
    }

    @Nonnull
    public List<String> getHints() {
        return hints;
    }

    public Optional<Principal> getPrincipal() {
        return Optional.ofNullable(principal);
    }


    /**
     * Временные объекты, которые доступны между шагами обработки контекста,
     * доступ осуществляется по ключу, в хранилище не сохраняются.
     *
     * @return мапу ключ - значение.
     */
    @Nonnull
    public Map<String, Object> getCommonObjects() {
        return commonObjects;
    }


    /**
     * Билдер контекста.
     */
    public static class Builder<T, U> {
        
        private Locale locale = Locale.getDefault();
        private TimeZone timeZone = TimeZone.getDefault();
        private List<String> hints = new ArrayList<>();
        private Principal principal;

        /**
         * Установить локаль.
         *
         * @param locale локаль
         * @return себя
         */
        public Builder<T, U> withLocale(@Nonnull Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Установить временную зону.
         *
         * @param timeZone временная зона
         * @return себя
         */
        public Builder<T, U> withTimeZone(@Nonnull TimeZone timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        /**
         * Установить хинты.
         *
         * @param hints хинты
         * @return себя
         */
        public Builder<T, U> withHints(@Nonnull List<String> hints) {
            this.hints = StreamSupport.stream(hints)
                    .map(hint -> {
                        try {
                            return URLDecoder.decode(hint, "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            return hint;
                        }
                    })
                    .collect(Collectors.toList());
            return this;
        }

        /**
         * Установить принципала.
         *
         * @param principal принципал
         * @return себя
         */
        public Builder<T, U> withPrincipal(Principal principal) {
            this.principal = principal;
            return this;
        }


        /**
         * Построить контекст.
         * @return контекст загрузки
         */
        public GeneralLoadContext<T, U> build() {
            return new GeneralLoadContext<>(this);
        }
    }
    
    /**
     * Задать результат загрузки.
     * @param loadResult результат загрузки
     */
    public void setLoadResult(GeneralLoadResult<T, U> loadResult) {
        this.loadResult = loadResult;
    }
    
    /**
     * Получить результат загрузки.
     * @return результат загрузки
     */
    public GeneralLoadResult<T, U> getLoadResult() {
        return loadResult;
    }
}
