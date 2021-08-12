package ru.croc.ctp.jxfw.core.exception.dto;

import ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * TO для исключения типа XOptimisticConcurrencyException.
 *
 * @author SMufazzalov
 * @since версия jXFW 1.2.0, дата 07.04.2016
 */
public class XOptimisticConcurrencyExceptionTo extends XExceptionTo {

    public static class DomainObjectIdentityTo {

        private final String type;
        private final String id;

        public DomainObjectIdentityTo(String type, String id) {

            this.type = type;
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }

    /**
     * Устаревшие объекты.
     */
    private final List<DomainObjectIdentityTo> obsoleteObjects = new ArrayList<>();

    /**
     * Удаленные объекты.
     */
    private final List<DomainObjectIdentityTo> deletedObjects = new ArrayList<>();

    private final Set<XExceptionTo> causes = new HashSet<>();

    /**
     * @param ex XOptimisticConcurrencyException.
     */
    public XOptimisticConcurrencyExceptionTo(XOptimisticConcurrencyException ex, XfwMessageTemplateResolver resolver, Locale locale) {
        super(ex, resolver, locale);
        ex.getObsoleteObjects().forEach(domainTo ->
                obsoleteObjects.add(new DomainObjectIdentityTo(domainTo.getType(), domainTo.getId()))
        );
        ex.getDeletedObjects().forEach(domainTo ->
                deletedObjects.add(new DomainObjectIdentityTo(domainTo.getType(), domainTo.getId()))
        );
        ex.getCauses().forEach(cause -> causes.add(cause.toTo(resolver, locale)));
    }

    public List<DomainObjectIdentityTo> getObsoleteObjects() {
        return obsoleteObjects;
    }

    public List<DomainObjectIdentityTo> getDeletedObjects() {
        return deletedObjects;
    }
}
