package ru.croc.ctp.jxfw.core.validation.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для обозначения конкретных полей доменных объектов, только для чтения.
 *
 * @author SMufazzalov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Documented
public @interface XFWReadOnly {

    /**
     * @return провека включена/выключена.
     */
    boolean value() default true;

    /**
     * @return только для фасадов.
     */
    boolean facade() default false;

    /**
     * @return действие при возникновении ошибки валидации.
     */
    Action action() default Action.EXCEPTION;

    /**
     * Действие, которое произойдет при ошибке валидации.
     */
    enum Action {
        /**
         * Игнорирование ошибки валидации. Объект будет просто исключен из UoW при валидации.
         * 
         * @see UnitOfWorkMultiStoreServiceImpl#validate(java.util.List, java.lang.Class[])
         */
        IGNORE,
        /**
         * Выбрасывается исключение при ошибке валидации.
         */
        EXCEPTION
    }
}
