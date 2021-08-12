package ru.croc.ctp.jxfw.core.exception.dto;

import ru.croc.ctp.jxfw.core.exception.exceptions.XUniqueViolationException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;
import java.util.Set;

/**
 * DTO Исключение нарушения ограничения уникальности.
 *
 * @author OKrutova
 * @since 1.7.0
 */
public class XUniqueViolationExceptionTo extends XBusinessLogicExceptionTo {

    /**
     * Наименование типа-владельца навигируемого свойства, которое вызвало нарушение ограничения уникальности.
     */
    private final String entityTypeName;

    /**
     * Наименования навигируемых свойств, которые вызвали нарушение ограничения уникальности.
     * Может быть не задано.
     */
    private final Set<String> properties;

    /**
     * Конструктор.
     *
     * @param ex       XUniqueViolationException
     * @param resolver резолвер шаблона
     * @param locale   требуемая локаль
     */
    public XUniqueViolationExceptionTo(XUniqueViolationException ex, XfwMessageTemplateResolver resolver,
                                       Locale locale) {
        super(ex, resolver, locale);
        entityTypeName = ex.getEntityTypeName();
        properties = ex.getProperties();
    }
}
