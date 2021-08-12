package ru.croc.ctp.jxfw.core.validation.validator;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Min;

/**
 * Валидатор для минимального числового значения.
 *
 * @author Nosov Alexander
 * @since 1.1
 */

public class MinValidatorForNumber implements ConstraintValidator<Min, Number> {

    private long minValue;

    @Override
    public void initialize(Min minValue) {
        this.minValue = minValue.value();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext) {
        // null values are valid
        if (value == null) {
            return true;
        } else if (value instanceof Double) {
            // обработка NaN, а так же positive infinity и negative infinity
            if ((Double) value == Double.POSITIVE_INFINITY) {
                return true;
            } else if (Double.isNaN((Double) value) || (Double) value == Double.NEGATIVE_INFINITY) {
                return false;
            }
        } else if (value instanceof Float) {
            if ((Float) value == Float.POSITIVE_INFINITY) {
                return true;
            } else if (Float.isNaN((Float) value) || (Float) value == Float.NEGATIVE_INFINITY) {
                return false;
            }
        }

        // остальные случаи
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).compareTo(BigDecimal.valueOf(minValue)) != -1;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).compareTo(BigInteger.valueOf(minValue)) != -1;
        } else {
            long longValue = value.longValue();
            return longValue >= minValue;
        }
    }
}
