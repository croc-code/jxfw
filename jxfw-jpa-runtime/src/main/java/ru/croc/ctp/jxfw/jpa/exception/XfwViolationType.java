package ru.croc.ctp.jxfw.jpa.exception;

/**
 * Перечисление значений, описывающих вид нарушения ограничения
 * (constraint violation) СУБД.
 */
public enum XfwViolationType {

    /**
     * Нарушение ограничения.
     * Используется, если определение вида нарушения невозможно или не
     * требуется.
     */
    IntegrityViolation ,

    /**
     * Нарушение ссылочной целостности.
     */
    ReferenceViolation,

    /**
     * Нарушение ограничения NOT NULL.
     */
    NotNull,

    /**
     * Нарушение уникальности.
     */
    Unique,

    /**
     * Нарушение ограничения CHECK.
     */
    Check
}
