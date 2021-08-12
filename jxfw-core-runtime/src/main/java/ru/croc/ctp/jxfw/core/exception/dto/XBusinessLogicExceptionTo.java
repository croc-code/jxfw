package ru.croc.ctp.jxfw.core.exception.dto;


import ru.croc.ctp.jxfw.core.exception.exceptions.XBusinessLogicException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by SMufazzalov on 06.04.2016.
 */
public class XBusinessLogicExceptionTo extends XExceptionTo {

    private Set<DomainViolationTo> violations = new HashSet<>();

    /**
     * Конструктор.
     *
     * @param ex XBusinessLogicException
     */
    public XBusinessLogicExceptionTo(XBusinessLogicException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(ex, resolver, locale);
        ex.getViolations().forEach( violation ->
            this.violations.add(new DomainViolationTo(violation,resolver,locale))
        );
    }

    public Set<DomainViolationTo> getViolations() {
        return violations;
    }
}
