package ru.croc.ctp.jxfw.core.exception.exceptions;

import static com.google.common.collect.Sets.newHashSet;

import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XUniqueViolationExceptionTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;
import java.util.Set;

/**
 * Исключение нарушения ограничения уникальности.
 *
 * @author OKrutova
 * @since 1.7.0
 */
public class XUniqueViolationException extends XIntegrityViolationException {

    /**
     * Наименование типа-владельца свойства, которое вызвало нарушение ограничения уникальности.
     */
    private final String entityTypeName;

    /**
     * Наименования свойств, которые вызвали нарушение ограничения уникальности.
     * Может быть не задано.
     */
    private final Set<String> properties;


    /**
     * Конструктор.
     *
     * @param message    сообщение об ошибке.
     * @param cause      причина которая сохраняется для последуещего извлечения через {@link #getCause()}.
     * @param violations список нарушений.
     */
    public XUniqueViolationException(String message, Throwable cause, Set<DomainViolation> violations) {
        super(message, cause, violations);
        entityTypeName = "";
        properties = newHashSet();
    }

    /**
     * Конструктор.
     *
     * @param message        сообщение об ошибке.
     * @param cause          причина которая сохраняется для последуещего извлечения через {@link #getCause()}.
     * @param violations     список нарушений.
     * @param entityTypeName наименование типа-владельца
     * @param properties     наименования свойств
     */
    public XUniqueViolationException(String message, Throwable cause, Set<DomainViolation> violations,
                                     String entityTypeName,
                                     Set<String> properties
    ) {
        super(message, cause, violations);
        this.entityTypeName = entityTypeName;
        this.properties = properties;
    }

    /**
     * Конструктор.
     *
     * @param builder билдер
     */
    private XUniqueViolationException(Builder<?> builder) {
        super(builder);
        this.entityTypeName = builder.entityTypeName;
        this.properties = builder.properties;
    }

    public String getEntityTypeName() {
        return entityTypeName;
    }

    public Set<String> getProperties() {
        return properties;
    }

    @Override
    public XExceptionTo toTo(XfwMessageTemplateResolver resolver, Locale locale) {
        return new XUniqueViolationExceptionTo(this, resolver, locale);
    }


    /**
     * Билдер исключения XUniqueViolationException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XIntegrityViolationException.Builder<T> {


        private String entityTypeName;

        private Set<String> properties = newHashSet();

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
         * Добавить описание Наименование типа-владельца свойства.
         *
         * @param entityTypeName Наименование типа-владельца свойства
         * @return билдер
         */
        public T entityTypeName(String entityTypeName) {
            this.entityTypeName = entityTypeName;
            return getThis();
        }

        /**
         * Добавить наименование свойства.
         *
         * @param property наименование свойства
         * @return билдер
         */
        public T property(String property) {
            this.properties.add(property);
            return getThis();
        }

        /**
         * Добавить наименование свойства.
         *
         * @param properties наименования свойств
         * @return билдер
         */
        public T properties(Set<String> properties) {
            this.properties.addAll(properties);
            return getThis();
        }


        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XUniqueViolationException build() {
            return new XUniqueViolationException(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }

    }


}
