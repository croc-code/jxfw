package ru.croc.ctp.jxfw.core.exception.dto;


import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;


/**
 * TO для исключения типа XObjectNotFoundExceptionTO.
 *
 * @author SMufazzalov
 * @since 1.2.0
 * @deprecated since 1.6
 */
public class XObjectNotFoundExceptionTo extends XInvalidDataExceptionTo {

    /**
     * Конструктор.
     *
     * @param ex            XObjectNotFoundException
     * @param resolver резолвер шаблона
     * @param locale        требуемая локаль
     */
    public XObjectNotFoundExceptionTo(XObjectNotFoundException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(ex, resolver, locale);
    }

}
