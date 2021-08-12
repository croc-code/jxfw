package ru.croc.ctp.jxfw.core.validation.impl.meta;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import ru.croc.ctp.jxfw.core.validation.impl.validator.FacadeReadOnlyValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Не для использования разработчиками!
 * данная аннотация проставляется во время генерации сущностей.
 *
 * @author Sergey Verkhushin
 */
@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {FacadeReadOnlyValidator.class})
@Documented
public @interface XFWFacadeReadOnlyCheck {

    /**
     * @return the error message template.
     */
    String message() default "Trying to modify readonly object or field";

    /**
     * @return the groups the constraint belongs to.
     */
    Class<?>[] groups() default {};

    /**
     * @return the payload associated to the constraint.
     */
    Class<? extends Payload>[] payload() default {};
}
