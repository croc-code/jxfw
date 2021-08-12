package ru.croc.ctp.jxfw.core.exception.dto;


import ru.croc.ctp.jxfw.core.exception.exceptions.XReferenceIntegrityViolationException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;

/**
 * TO для исключения типа XReferenceIntegrityViolationException.
 *
 * @author SMufazzalov
 * @since версия jXFW 1.2.0, дата 07.04.2016
 */
public class XReferenceIntegrityViolationExceptionTo extends XExceptionTo {

    private String reason;
    private String entityTypeName;
    private String navigationPropName;

    /**
     * @param ex XReferenceIntegrityViolationException {@link XReferenceIntegrityViolationException}.
     */
    public XReferenceIntegrityViolationExceptionTo(XReferenceIntegrityViolationException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(ex, resolver, locale);
        reason = ex.getReason();
        entityTypeName = ex.getEntityTypeName();
        navigationPropName = ex.getNavigationPropName();
    }

    public String getReason() {
        return reason;
    }

    public String getEntityTypeName() {
        return entityTypeName;
    }

    public String getNavigationPropName() {
        return navigationPropName;
    }
}
