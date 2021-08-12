package ru.croc.ctp.jxfw.core.exception.exceptions;

import ru.croc.ctp.jxfw.core.exception.dto.XBusinessLogicExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Обобщенная ошибка бизнес-логики.
 *
 * @author SMufazzalov от 07.04.2016.
 * @author OKrutova
 * @since jXFW 1.2.0
 */
@SuppressWarnings("serial")
public class XBusinessLogicException extends XInvalidDataException {

    /**
     * Коллекция нарушений бизнес-логики со связями с объектами/свойствами, вызвавшими нарушение.
     */
    private final Set<DomainViolation> violations;

    /**
     * Создать новый XBusinessLogicException.
     *
     * @param message сообщение об ошибке
     */
    public XBusinessLogicException(String message) {
        this(new Builder(null, message));
    }

    /**
     * Создать новый XBusinessLogicException.
     *
     * @param message    сообщение об ошибке
     * @param violations список нарушений
     */
    public XBusinessLogicException(String message, Set<DomainViolation> violations) {
        this(new Builder(null, message).addViolations(violations));
    }

    /**
     * Создать новый XBusinessLogicException.
     *
     * @param message    сообщение об ошибке
     * @param cause      причина которая сохраняется для последуещего извлечения через {@link #getCause()}
     * @param violations список нарушений
     */
    public XBusinessLogicException(String message, Throwable cause, Set<DomainViolation> violations) {
        this(new Builder<>(null, message).cause(cause).addViolations(violations));
    }

    /**
     * Конструктор.
     *
     * @param builder - билдер.
     */
    protected XBusinessLogicException(Builder<?> builder) {
        super(builder);
        violations = builder.violations;
    }

    public Set<DomainViolation> getViolations() {
        return violations;
    }

    @Override
    public XExceptionTo toTo(XfwMessageTemplateResolver resolver, Locale locale) {
        return new XBusinessLogicExceptionTo(this, resolver, locale);
    }

    @Override
    public String toString() {
        return super.toString()
                + " violations=" + violations;
    }


    /**
     * Билдер исключения XBusinessLogicException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XInvalidDataException.Builder<T> {

        private Set<DomainViolation> violations = new HashSet<>();


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
         * Добавить описание нарушения.
         *
         * @param violation нарушение
         * @return билдер
         */
        public T addViolation(DomainViolation violation) {
            this.violations.add(violation);
            return getThis();
        }

        /**
         * Добавить описания нарушений.
         *
         * @param violations нарушения
         * @return билдер
         */
        public T addViolations(Set<DomainViolation> violations) {
            this.violations.addAll(violations);
            return getThis();
        }

        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XBusinessLogicException build() {
            return new XBusinessLogicException(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }


    }
}
