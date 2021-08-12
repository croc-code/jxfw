package ru.croc.ctp.jxfw.core.validation.impl.validator;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWNewRemoveCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Валидация для флагов {@link DomainObject#isRemoved()} и {@link DomainObject#isNew()}.
 * 
 * @see XFWNewRemoveCheck
 */
@Component
public class NewRemoveValidator implements ConstraintValidator<XFWNewRemoveCheck, DomainObject<?>> {

    @Override
    public void initialize(XFWNewRemoveCheck constraintAnnotation) {
    }

    @Override
    public boolean isValid(DomainObject<?> value, ConstraintValidatorContext context) {
        if (value.isRemoved() && value.isNew()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{ru.croc.ctp.jxfw.core.exception.exceptions"
                    + ".XInvalidDataException.newremove.message}").addConstraintViolation();
            return false;
        }
        return true;
    }

}
