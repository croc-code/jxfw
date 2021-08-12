package ru.croc.ctp.jxfw.core.validation.impl.meta;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.validation.impl.validator.NewRemoveValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Не для использования разработчиками! Данная аннотация проставляется во время генерации сущностей и предназначена для
 * недопущения одновременной установки двух флагов {@link DomainObject#isRemoved()} и {@link DomainObject#isNew()}.
*/
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {NewRemoveValidator.class})
@Documented
public @interface XFWNewRemoveCheck {

    /**
     * @return the error message template.
     */
    String message() default "Illegal domain object state. Cannot be set isRemoved and isNew together.";

    /**
     * @return the groups the constraint belongs to.
     */
    Class<?>[] groups() default {};

    /**
     * @return the payload associated to the constraint.
     */
    Class<? extends Payload>[] payload() default {};
}
