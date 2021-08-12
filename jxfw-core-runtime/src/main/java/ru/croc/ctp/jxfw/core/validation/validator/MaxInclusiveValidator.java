package ru.croc.ctp.jxfw.core.validation.validator;

import ru.croc.ctp.jxfw.core.validation.meta.XFWMaxInclusive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидация для максимальной даты.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class MaxInclusiveValidator implements ConstraintValidator<XFWMaxInclusive, Temporal> {

    private String constraintDateStr;

    @Override
    public void initialize(XFWMaxInclusive constraintAnnotation) {
        constraintDateStr = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Temporal date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        if (date instanceof LocalDate) {
            final LocalDate maxDate = LocalDate.parse(constraintDateStr);
            return ((LocalDate) date).isBefore(maxDate) || ((LocalDate) date).isEqual(maxDate);
        }
        if (date instanceof LocalDateTime) {
            final LocalDateTime maxDate = LocalDateTime.parse(constraintDateStr);
            return ((LocalDateTime) date).isBefore(maxDate) || ((LocalDateTime) date).isEqual(maxDate);
        }
        if (date instanceof LocalTime) {
            final LocalTime maxDate = LocalTime.parse(constraintDateStr);
            return ((LocalTime) date).isBefore(maxDate) || date.equals(maxDate);
        }
        throw new RuntimeException("Unknown date/time format");
    }
}
