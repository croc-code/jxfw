package ru.croc.ctp.jxfw.core.validation.jdk7.validator;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.Temporal;
import ru.croc.ctp.jxfw.core.validation.meta.XFWMaxExclusive;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидация для максимальной даты.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class MaxExclusiveValidator implements ConstraintValidator<XFWMaxExclusive, Temporal> {

    private String constraintDateStr;

    @Override
    public void initialize(XFWMaxExclusive constraintAnnotation) {
        constraintDateStr = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Temporal date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        if (date instanceof LocalDate) {
            final LocalDate maxDate = LocalDate.parse(constraintDateStr);
            return ((LocalDate) date).isBefore(maxDate);
        }
        if (date instanceof LocalDateTime) {
            final LocalDateTime maxDate = LocalDateTime.parse(constraintDateStr);
            return ((LocalDateTime) date).isBefore(maxDate);
        }
        if (date instanceof LocalTime) {
            final LocalTime maxDate = LocalTime.parse(constraintDateStr);
            return ((LocalTime) date).isBefore(maxDate);
        }
        throw new RuntimeException("Unknown date/time format");

    }
}
