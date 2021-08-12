package ru.croc.ctp.jxfw.core.exception.dto;


import ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;

/**
 * TO для исключения типа XSecurityException.
 * @author SMufazzalov
 * @since версия jXFW 1.2.0, дата 07.04.2016
 * @deprecated since 1.6
 */
public class XSecurityExceptionTo extends XExceptionTo {

    /**
     * Конструктор.
     *
     * @param ex XSecurityException
     */
    public XSecurityExceptionTo(XSecurityException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(ex, resolver, locale);
    }
}
