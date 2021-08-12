package ru.croc.ctp.jxfw.core.validation.jdk7.meta;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import ru.croc.ctp.jxfw.core.validation.jdk7.validator.MinInclusiveValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Проаннотированное поле должно быть не раньше (включительно) укзанной даты.
 * JDK 7
 * <p/>
 * Поддерживаемые типы:
 * <ul>
 *     <li>{@code org.threeten.bp.LocalDate}</li>
 *     <li>{@code org.threeten.bp.LocalTime}</li>
 *     <li>{@code org.threeten.bp.LocalDateTime}</li>
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
@Constraint(validatedBy = {MinInclusiveValidator.class })
public @interface XFWMinInclusive {
    /**
     * @return сообщение по-умолчанию.
     */
    String message() default "{ru.croc.ctp.jxfw.core.validation.MinInclusive.message}";

    /**
     * @return группы зависимых ограничений.
     */
    Class<?>[] groups() default { };

    /**
     * @return the payload associated to the constraint.
     */
    Class<? extends Payload>[] payload() default { };

    /**
     * @return минимальная дата.
     */
    String value();
}
