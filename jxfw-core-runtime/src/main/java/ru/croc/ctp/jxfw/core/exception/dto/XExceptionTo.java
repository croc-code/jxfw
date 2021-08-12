package ru.croc.ctp.jxfw.core.exception.dto;

import static com.google.common.base.Throwables.getStackTraceAsString;

import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.Locale;
import java.util.Map;


/**
 * Транспортный объект исключения.
 * @author SMufazzalov.
 */
public class XExceptionTo extends ExceptionTo {

    private final String sourceMachineName;

    private final String source;

    private final Map<String, String> data;

    private final String innerException;

    private final String helpLink;

    private String sourceLogEntryUniqueId;

    private boolean containsUserDescription;


    /**
     * Конструктор.
     *
     * @param ex XException
     * @param resolver резолвер шаблона
     * @param locale требуемая локаль
     */
    public XExceptionTo(XException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(resolver.resolve(ex.getMessageTemplate(), locale),
                ex.getClass().getSimpleName(),
                getStackTraceAsString(ex),
                ex.getParentClasses());
        helpLink = ex.getHelpLink();
        innerException = (ex.getCause() != null) ? ex.getCause().getMessage() : null;
        data = ex.getData();
        source = ex.getSource();
        sourceMachineName = ex.getSourceMachineName();
        containsUserDescription = ex.isContainsUserDescription();
        sourceLogEntryUniqueId = ex.getSourceLogEntryUniqueId();
    }

    public String getSourceMachineName() {
        return sourceMachineName;
    }

    public String getMessage() {
        return message;
    }

    public String getClassName() {
        return className;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public String getSource() {
        return source;
    }

    public String getSourceLogEntryUniqueId() {
        return sourceLogEntryUniqueId;
    }

    public boolean isContainsUserDescription() {
        return containsUserDescription;
    }

    public Map<String, String> getData() {
        return data;
    }

    public String getInnerException() {
        return innerException;
    }

    public String getHelpLink() {
        return helpLink;
    }

}
