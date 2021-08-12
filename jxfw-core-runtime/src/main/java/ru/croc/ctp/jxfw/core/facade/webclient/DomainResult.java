package ru.croc.ctp.jxfw.core.facade.webclient;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * Контейнер для возвращаемых значений из системы в WebClient
 * используется в контроллерах.
 *
 * @author Nosov Alexander
 * @since 1.1
 * @see ControllerBase
 */
public final class DomainResult extends HashMap<String, Object> {

    private static final long serialVersionUID = 620786638309256582L;

    private static final String RESULT_FIELD_NAME = "result";

    private static final String HINTS_FIELD_NAME = "hints";

    private static final String MORE_FIELD_NAME = "more";

    private DomainResult(Builder builder) {
        this.put(RESULT_FIELD_NAME, builder.result);
        this.put(MORE_FIELD_NAME, builder.more);
        this.put(HINTS_FIELD_NAME, builder.hints);
    }

    /**
     * Билдер для создания результата в Java-стиле.
     */
    public static class Builder {

        private Object result;

        private Map<String, Object> hints;

        private Object more;

        /**
         * Кладем свои результаты.
         *
         * @param result - сами результаты
         * @return - билдер
         */
        public Builder result(Object result) {
            this.result = result;
            return this;
        }

        /**
         * Кладем preload значения.
         *
         * @param more - preload значения
         * @return - билдер
         */
        public Builder more(Object more) {
            this.more = more;
            return this;
        }

        /**
         * Сюда кладем значение hints.
         *
         * @param hints - хинты для отображения
         * @return - билдер
         */
        public Builder hints(Map<String, Object> hints) {
            this.hints = hints == null ? Maps.newHashMap() : hints;
            return this;
        }

        /**
         * Строим результат с помощью билдера.
         *
         * @return - объект результата
         */
        public DomainResult build() {
            return new DomainResult(this);
        }
    }
}
