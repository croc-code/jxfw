package ru.croc.ctp.jxfw.core.exception.exceptions;

import java.util.Set;

/**
 * Исключение нарушения целостности.
 * Это может быть нарушение ограничений доменной модели или нарушение каких-либо бизнес-правил.
 *
 * @author SMufazzalov
 * @author OKrutova
 * @see XBusinessLogicException
 * @see XInvalidDataException
 * @see XException
 * @since версия jXFW 1.2.0, дата 07.04.2016
 */
public class XIntegrityViolationException extends XBusinessLogicException {

    /**
     * Конструктор.
     * @param message    сообщение об ошибке.
     * @param cause      причина которая сохраняется для последуещего извлечения через {@link #getCause()}.
     * @param violations список нарушений.
     */
    public XIntegrityViolationException(String message, Throwable cause, Set<DomainViolation> violations) {
        super(message, cause, violations);
    }


    /**
     * Конструктор.
     *
     * @param builder - билдер.
     */
    protected XIntegrityViolationException(Builder<?> builder) {
        super(builder);
    }


    /**
     * Билдер исключения XIntegrityViolationException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XBusinessLogicException.Builder<T> {


        /**
         * Конструктор.
         *
         * @param bundleCode     идентификатор ресурсов с сообщением или шаблоном сообщения об ошибке
         * @param defaultMessage сообщение или шаблон сообщения. Используется, если
         *                       идентификтаор ресурса не задан или ресурс не найден.
         */
        public Builder(String bundleCode, String defaultMessage) {
            super(bundleCode, defaultMessage);
        }


        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XIntegrityViolationException build() {
            return new XIntegrityViolationException(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }


    }


}
