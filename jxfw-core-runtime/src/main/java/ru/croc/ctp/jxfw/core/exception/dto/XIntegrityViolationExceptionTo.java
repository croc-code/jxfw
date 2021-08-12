package ru.croc.ctp.jxfw.core.exception.dto;

import ru.croc.ctp.jxfw.core.exception.exceptions.XIntegrityViolationException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;

/**
 * TO для исключения типа XIntegrityViolationException.
 *
 * @author SMufazzalov
 * @since версия jXFW 1.2.0, дата 07.04.2016
 * @deprecated since 1.6
 */
public class XIntegrityViolationExceptionTo extends XExceptionTo {
    /**
     * Конструктор.
     *
     * @param ex       XIntegrityViolationException.
     * @param resolver резолвер шаблона
     * @param locale   требуемая локаль
     */
    public XIntegrityViolationExceptionTo(XIntegrityViolationException ex, XfwMessageTemplateResolver resolver,
                                          Locale locale) {
        super(ex, resolver, locale);
    }
}
