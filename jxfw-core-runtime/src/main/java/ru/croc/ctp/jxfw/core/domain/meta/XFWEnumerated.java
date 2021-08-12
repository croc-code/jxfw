package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * помеченное поле берет свое значение из перечисления.
 *
 * @author Nosov Alexander
 *         on 19.05.15.
 * @deprecated since 1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface XFWEnumerated {
    
    /**
     * @return значения для поля будут из этого перечисления (помечается аннотацией @see XFWEnum). 
     */
    Class<? extends Enum<?>> value();
}
