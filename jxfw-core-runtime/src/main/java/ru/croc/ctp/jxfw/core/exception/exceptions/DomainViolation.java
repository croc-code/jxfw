package ru.croc.ctp.jxfw.core.exception.exceptions;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Описание ошибки бизнес-логики. Описание одной ошибки,  ее может вызвать как значение одного свойства одного объекта,
 * так и комбинация значений свойств нескольких объектов.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public class DomainViolation {

    private final Set<DomainViolationItem> items;

    /**
     * Сообщение об ошибке.
     */
    private final XfwMessageTemplate messageTemplate;

    /**
     * Подробное описание ошибки и ее возможных причин.
     */
    private final XfwMessageTemplate descriptionTemplate;

    /**
     * Причина ошибки.
     */
    private final DomainViolationReason reason;

    /**
     * Идентификатор правила, нарушение которого описывает текущий объект.
     * Используется для идентификации игнорируемых проверок.
     */
    private final String ruleId;

    /**
     * Признак игнорируемой ошибки. Если установлен, то также должно быть задано свойство.
     */
    private final boolean ignorable;

    /**
     * Конструктор по умолчанию.
     */
    public DomainViolation() {
        this(new Builder());
    }

    /**
     * Конструктор.
     *
     * @param message Сообщение об ошибке.
     */
    public DomainViolation(String message) {
        this(new Builder().message(null, message));
    }

    /**
     * Конструктор.
     *
     * @param message Сообщение об ошибке.
     * @param reason  Причина (вид)
     */
    public DomainViolation(String message, DomainViolationReason reason) {
        this(new Builder().message(null, message).reason(reason));
    }

    private DomainViolation(Builder builder) {
        this.messageTemplate
                = new XfwMessageTemplate(builder.messageBundleCode, builder.message,
                builder.arguments.toArray(new String[0]));
        this.descriptionTemplate
                = new XfwMessageTemplate(builder.descriptionBundleCode, builder.description,
                builder.descrArguments.toArray(new String[0]));
        this.items = builder.items;
        this.ignorable = builder.ignorable;
        this.reason = builder.reason;
        this.ruleId = builder.ruleId;
    }

    /**
     * Конструктор.
     *
     * @param domainObject Объект, вызвавший ошибку
     */
    public DomainViolation(DomainObjectIdentity<?> domainObject) {
        this(new Builder().addViolationItem(new DomainViolationItem(domainObject)));
    }

    /**
     * Конструктор.
     *
     * @param domainObject Объект, вызвавший ошибку
     * @param propertyName Наименование свойства, вызвавшего ошибку
     */
    public DomainViolation(DomainObjectIdentity<?> domainObject, String propertyName) {
        this(new Builder().addViolationItem(new DomainViolationItem(domainObject, propertyName)));
    }

    /**
     * Билдер ошибки бизнес-логики.
     */
    public static class Builder {
        private String message;
        private String messageBundleCode;
        private String description;
        private String descriptionBundleCode;
        private DomainViolationReason reason;
        private String ruleId;
        private boolean ignorable;
        private LinkedList<String> descrArguments = new LinkedList<>();
        private LinkedList<String> arguments = new LinkedList<>();
        private Set<DomainViolationItem> items = new HashSet<>();

        /**
         * Конструктор.
         */
        public Builder() {
        }

        /**
         * Установить сообщение.
         *
         * @param bundleCode     идентификатор ресурсов с сообщением или шаблоном сообщения об ошибке
         * @param defaultMessage сообщение или шаблон сообщения. Используется, если
         *                       идентификтаор ресурса не задан или ресурс не найден.
         * @return билдер
         */
        public Builder message(String bundleCode, String defaultMessage) {
            this.messageBundleCode = bundleCode;
            this.message = defaultMessage;
            return this;
        }


        /**
         * Установить описание.
         *
         * @param bundleCode         идентификатор ресурсов с описанием или шаблоном описания
         * @param defaultDescription описание или шаблон описания Используется, если
         *                           идентификтаор ресурса не задан или ресурс не найден.
         * @return билдер
         */
        public Builder description(String bundleCode, String defaultDescription) {
            this.descriptionBundleCode = bundleCode;
            this.description = defaultDescription;
            return this;
        }

        /**
         * Установить причину.
         *
         * @param reason причина
         * @return билдер
         */
        public Builder reason(DomainViolationReason reason) {
            this.reason = reason;
            return this;
        }

        /**
         * Установить правило.
         *
         * @param ruleId правило
         * @return билдер
         */
        public Builder ruleId(String ruleId) {
            this.ruleId = ruleId;
            return this;
        }

        /**
         * Установить признак игнорируемости.
         *
         * @param ignorable признак игнорируемости
         * @return билдер
         */
        public Builder ignorable(boolean ignorable) {
            this.ignorable = ignorable;
            return this;
        }

        /**
         * Добавить аргумент для подстановки в шаблон сообщения.
         *
         * @param argument аргумент
         * @return билдер
         */
        public Builder addMessageArgument(Object argument) {
            this.arguments.add(String.valueOf(argument));
            return this;
        }

        /**
         * Добавить аргумент для подстановки в шаблон описания.
         *
         * @param argument аргумент
         * @return билдер
         */
        public Builder addDescriptionArgument(Object argument) {
            this.descrArguments.add(String.valueOf(argument));
            return this;
        }

        /**
         * Добавить описание нарушения.
         *
         * @param item описание нарушения
         * @return билдер
         */
        public Builder addViolationItem(DomainViolationItem item) {
            this.items.add(item);
            return this;
        }

        /**
         * Добавить описание нарушения.
         *
         * @param domainObject доменный объект
         * @param propertyName Имя свойства доменного объекта
         * @return билдер
         */
        public Builder addViolationItem(DomainObject<?> domainObject, String propertyName) {
            return addViolationItem(new DomainObjectIdentity<>(domainObject), propertyName);
        }

        /**
         * Добавить описание нарушения.
         *
         * @param domainObject доменный объект
         * @return билдер
         */
        public Builder addViolationItem(DomainObject<?> domainObject) {
            return addViolationItem(new DomainObjectIdentity<>(domainObject));
        }

        /**
         * Добавить описание нарушения.
         *
         * @param identity     идентификация доменного объекта
         * @param propertyName Имя свойства доменного объекта
         * @return билдер
         */
        public Builder addViolationItem(DomainObjectIdentity<?> identity, String propertyName) {
            return addViolationItem(new DomainViolationItem(identity, propertyName));
        }

        /**
         * Добавить описание нарушения.
         *
         * @param identity идентификация доменного объекта
         * @return билдер
         */
        public Builder addViolationItem(DomainObjectIdentity<?> identity) {
            return addViolationItem(new DomainViolationItem(identity));
        }


        /**
         * Построить объект.
         *
         * @return Описание ошибки бизнес-логики
         */
        public DomainViolation build() {
            return new DomainViolation(this);
        }


    }

    public Set<DomainViolationItem> getItems() {
        return items;
    }

    public XfwMessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    public XfwMessageTemplate getDescriptionTemplate() {
        return descriptionTemplate;
    }

    public DomainViolationReason getReason() {
        return reason;
    }

    public String getRuleId() {
        return ruleId;
    }

    public boolean isIgnorable() {
        return ignorable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DomainViolation that = (DomainViolation) obj;

        if (ignorable != that.ignorable) {
            return false;
        }
        if (items != null ? !items.equals(that.items) : that.items != null) {
            return false;
        }
        if (messageTemplate != null ? !messageTemplate.equals(that.messageTemplate) : that.messageTemplate != null) {
            return false;
        }
        if (descriptionTemplate != null ? !descriptionTemplate.equals(that.descriptionTemplate)
                : that.descriptionTemplate != null) {
            return false;
        }
        if (reason != that.reason) {
            return false;
        }
        return ruleId != null ? ruleId.equals(that.ruleId) : that.ruleId == null;
    }

    @Override
    public int hashCode() {
        int result = items != null ? items.hashCode() : 0;
        result = 31 * result + (messageTemplate != null ? messageTemplate.hashCode() : 0);
        result = 31 * result + (descriptionTemplate != null ? descriptionTemplate.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (ruleId != null ? ruleId.hashCode() : 0);
        result = 31 * result + (ignorable ? 1 : 0);
        return result;
    }


    @Override
    public String toString() {
        return "DomainViolation{"
                + "items=" + items
                + ", messageTemplate=" + messageTemplate
                + ", descriptionTemplate=" + descriptionTemplate
                + ", reason=" + reason
                + ", ruleId='" + ruleId + '\''
                + ", ignorable=" + ignorable
                + '}';
    }
}
