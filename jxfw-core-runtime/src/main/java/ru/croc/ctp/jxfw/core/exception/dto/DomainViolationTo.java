package ru.croc.ctp.jxfw.core.exception.dto;

import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolation;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolationItem;
import ru.croc.ctp.jxfw.core.exception.exceptions.DomainViolationReason;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;
import java.util.Set;

/**
 * Транспортный объект Описание ошибки бизнес-логики.
 *
 * @since jXFW 1.6.0
 */
public class DomainViolationTo {

    private final Set<DomainViolationItem> items;

    /**
     * Сообщение об ошибке.
     */
    private final String message;

    /**
     * Подробное описание ошибки и ее возможных причин.
     */
    private final String description;

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
     * Конструктор.
     *
     * @param violation Описание ошибки бизнес-логики
     * @param resolver резолвер шаблона
     * @param locale требуемая локаль
     */
    public DomainViolationTo(DomainViolation violation, XfwMessageTemplateResolver resolver, Locale locale) {
        items = violation.getItems();
        message = resolver.resolve(violation.getMessageTemplate(), locale);
        description = resolver.resolve(violation.getDescriptionTemplate(), locale);
        reason = violation.getReason();
        ruleId = violation.getRuleId();
        ignorable = violation.isIgnorable();
    }

    public Set<DomainViolationItem> getItems() {
        return items;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
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
}
