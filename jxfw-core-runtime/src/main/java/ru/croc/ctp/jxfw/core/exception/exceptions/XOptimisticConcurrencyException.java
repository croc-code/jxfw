package ru.croc.ctp.jxfw.core.exception.exceptions;

import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XOptimisticConcurrencyExceptionTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.List;
import java.util.Locale;
import java.util.Set;


/**
 * Исключение, возникающее при нарушении оптимистической блокировки.
 *
 * @author SMufazzalov
 * @author OKrutova
 * @since версия jXFW 1.2.0, дата 07.04.2016
 */
@SuppressWarnings("serial")
public class XOptimisticConcurrencyException extends XInvalidDataException {

    /**
     * Устаревшие объекты.
     */
    private final List<DomainTo> obsoleteObjects;

    /**
     * Удаленные объекты.
     */
    private final List<DomainTo> deletedObjects;


    private final Set<XInvalidDataException> causes;


    /**
     * Конструктор.
     *
     * @param bundleCode      идентификатор ресурсов с сообщением или шаблоном сообщения об ошибке
     * @param defaultMessage  сообщение или шаблон сообщения. Используется, если
     *                        идентификтаор ресурса не задан или ресурс не найден.
     * @param causes          причины.
     * @param obsoleteObjects Устаревшие объекты.
     * @param deletedObjects  Удаленные объекты.
     */
    public XOptimisticConcurrencyException(String bundleCode, String defaultMessage,
                                           Set<XInvalidDataException> causes,
                                           List<DomainTo> obsoleteObjects,
                                           List<DomainTo> deletedObjects) {
        super(new Builder(bundleCode, defaultMessage));
        // это исключение искуственное, его StackTrace не интересен
        setStackTrace(new StackTraceElement[]{});
        this.causes = causes;
        this.obsoleteObjects = obsoleteObjects;
        this.deletedObjects = deletedObjects;
    }

    public List<DomainTo> getObsoleteObjects() {
        return obsoleteObjects;
    }

    public List<DomainTo> getDeletedObjects() {
        return deletedObjects;
    }

    public Set<XInvalidDataException> getCauses() {
        return causes;
    }

    @Override
    public XExceptionTo toTo(XfwMessageTemplateResolver resolver, Locale locale) {
        return new XOptimisticConcurrencyExceptionTo(this, resolver, locale);
    }

    @Override
    public String toString() {
        return super.toString()
                + " causes=" + causes;
    }

}