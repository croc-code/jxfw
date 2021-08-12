package ru.croc.ctp.jxfw.core.exception.dto;

import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;
import java.util.Map;

/**
 * Created by SMufazzalov on 06.04.2016.
 */
public class XInvalidDataExceptionTo extends XExceptionTo {

    private Map<String, String> identities;

    /**
     * Конструктор.
     *
     * @param ex       XInvalidDataException
     * @param resolver резолвер шаблона
     * @param locale   требуемая локаль
     */
    public XInvalidDataExceptionTo(XInvalidDataException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(ex, resolver, locale);
        identities = ex.getIdentities();
    }

    public Map<String, String> getIdentities() {
        return identities;
    }

}
