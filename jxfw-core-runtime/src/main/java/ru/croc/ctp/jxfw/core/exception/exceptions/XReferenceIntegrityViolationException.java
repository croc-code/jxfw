package ru.croc.ctp.jxfw.core.exception.exceptions;

import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XReferenceIntegrityViolationExceptionTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;
import java.util.Set;

/**
 * Исключение нарушения ссылочной целостности.
 *
 * @author SMufazzalov
 * @author OKrutova
 * @since версия jXFW 1.2.0, дата 07.04.2016
 */
public class XReferenceIntegrityViolationException extends XIntegrityViolationException {

    private static final long serialVersionUID = 5570460326070042113L;

    /**
     * Причина нарушения ссылочной целостности.
     */
    private String reason;
    /**
     * Наименование типа-владельца навигируемого свойства, которое вызвало нарушение ссылочной целостности
     * Может быть не задано (null или пустая строка).
     */
    private String entityTypeName;
    /**
     * Наименование навигируемого свойства, которое вызвало нарушение ссылочной целостности.
     * Может быть не задано (null или пустая строка).
     */
    private String navigationPropName;

    /**
     * Конструктор.
     *
     * @param message            сообщение об ошибке.
     * @param cause              причина которая сохраняется для последуещего извлечения через {@link #getCause()}
     * @param violations         список нарушений
     * @param entityTypeName     {@link #getEntityTypeName()}
     * @param navigationPropName {@link #getNavigationPropName()}
     * @param reason             {@link #getReason()} ()}
     */
    public XReferenceIntegrityViolationException(String message,
                                                 Throwable cause,
                                                 Set<DomainViolation> violations,
                                                 String entityTypeName,
                                                 String navigationPropName,
                                                 String reason) {
        this(message, cause, violations);
        this.entityTypeName = entityTypeName;
        this.navigationPropName = navigationPropName;
        this.reason = reason;
    }

    /**
     * Конструктор.
     *
     * @param message    сообщение об ошибке.
     * @param cause      причина которая сохраняется для последуещего извлечения через {@link #getCause()}
     * @param violations список нарушений
     */
    public XReferenceIntegrityViolationException(String message, Throwable cause, Set<DomainViolation> violations) {
        super(message, cause, violations);
    }

    /**
     * Конструктор.
     *
     * @param builder билдер.
     */
    public XReferenceIntegrityViolationException(Builder<?> builder) {
        super(builder);
        this.entityTypeName = builder.entityTypeName;
        this.navigationPropName = builder.navigationPropName;
        this.reason = builder.reason;
    }

    /**
     * {@link #entityTypeName}.
     *
     * @return String или null
     */
    public String getEntityTypeName() {
        return entityTypeName;
    }

    /**
     * {@link #navigationPropName}.
     *
     * @return String или null
     */
    public String getNavigationPropName() {
        return navigationPropName;
    }

    /**
     * {@link #reason}.
     *
     * @return String или null
     */
    public String getReason() {
        return reason;
    }

    @Override
    public XExceptionTo toTo(XfwMessageTemplateResolver resolver, Locale locale) {
        return new XReferenceIntegrityViolationExceptionTo(this, resolver, locale);
    }

    /**
     * Билдер исключения XBusinessLogicException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XIntegrityViolationException.Builder<T> {

        private String entityTypeName;
        private String navigationPropName;
        private String reason;

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
         * Добавить описание Наименование типа-владельца навигируемого свойства.
         *
         * @param entityTypeName Наименование типа-владельца навигируемого свойства
         * @return билдер
         */
        public T entityTypeName(String entityTypeName) {
            this.entityTypeName = entityTypeName;
            return getThis();
        }

        /**
         * Добавить описание Наименование навигируемого свойства.
         *
         * @param navigationPropName Наименование навигируемого свойства
         * @return билдер
         */
        public T navigationPropName(String navigationPropName) {
            this.navigationPropName = navigationPropName;
            return getThis();
        }

        /**
         * Добавить описание Причина нарушения ссылочной целостности..
         *
         * @param reason Причина нарушения ссылочной целостности.
         * @return билдер
         */
        public T reason(String reason) {
            this.reason = reason;
            return getThis();
        }


        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XReferenceIntegrityViolationException build() {
            return new XReferenceIntegrityViolationException(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }

    }

}
