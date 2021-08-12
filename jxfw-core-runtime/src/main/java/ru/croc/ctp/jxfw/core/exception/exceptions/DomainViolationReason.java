package ru.croc.ctp.jxfw.core.exception.exceptions;

/**
 * Виды причин ошибки {@link DomainViolation}.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public enum DomainViolationReason {
    /**
     * Ошибка бизнес-логики.
     */
    BUSINESS_lOGIC,
    /**
     * Ошибка нарушения целостности данных.
     */
    INTEGRITY,
    /**
     * Ошибка разделения доступа/безопасности.
     */
    SECURITY
}
