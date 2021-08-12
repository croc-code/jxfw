package ru.croc.ctp.jxfw.core.validation.jdk7.meta;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import ru.croc.ctp.jxfw.core.validation.jdk7.validator.MaxExclusiveValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Проаннотированное поле должно быть не позже (исключительно) укзанной даты.
 * <p/>
 * Поддерживаемые типы:
 * <ul>
 *     <li>{@code java.time.LocalDate}</li>
 *     <li>{@code java.time.LocalTime}</li>
 *     <li>{@code java.time.LocalDateTime}</li>
 * </ul>
 * <p/>
 * {@code null} elements are considered valid.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MaxExclusiveValidator.class})
public @interface XFWMaxExclusive {
    /**
     * @return сообщение по-умолчанию.
     */
    String message() default "{ru.croc.ctp.jxfw.core.validation.MaxExclusive.message}";

    /**
     * @return группы зависимых ограничений.
     */
    Class<?>[] groups() default { };

    /**
     * @return the payload associated to the constraint.
     */
    Class<? extends Payload>[] payload() default { };

    /**
     * @return максимальная дата.
     */
    String value();
}
