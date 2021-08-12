package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннтоация для типа Enum в JXFW. Используется для конфигурирования типа.
 *
 * @author Nosov Alexander
 *         on 19.05.15.
 * @deprecated since 1.6
 * @see ru.croc.ctp.jxfw.core.generator.meta.XFWEnum
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface XFWEnum {

    /**
     * @return признак флага.
     */
    boolean isFlags() default false;
}
