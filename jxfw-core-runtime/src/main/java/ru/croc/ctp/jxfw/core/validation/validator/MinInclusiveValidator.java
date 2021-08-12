package ru.croc.ctp.jxfw.core.validation.validator;

import ru.croc.ctp.jxfw.core.validation.meta.XFWMinInclusive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидация для минимальной даты.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class MinInclusiveValidator implements ConstraintValidator<XFWMinInclusive, Temporal> {

    private String constraintDateStr;

    @Override
    public void initialize(XFWMinInclusive constraintAnnotation) {
        constraintDateStr = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Temporal date, ConstraintValidatorContext context) {
        if (date == null) {
            return true;
        }
        if (date instanceof LocalDate) {
            final LocalDate minDate = LocalDate.parse(constraintDateStr);
            return ((LocalDate) date).isAfter(minDate) || ((LocalDate) date).isEqual(minDate);
        }
        if (date instanceof LocalDateTime) {
            final LocalDateTime minDate = LocalDateTime.parse(constraintDateStr);
            return ((LocalDateTime) date).isAfter(minDate) || ((LocalDateTime) date).isEqual(minDate);
        }
        if (date instanceof LocalTime) {
            final LocalTime minDate = LocalTime.parse(constraintDateStr);
            return ((LocalTime) date).isAfter(minDate) || date.equals(minDate);
        }
        throw new RuntimeException("Unknown date/time format");

    }
}
