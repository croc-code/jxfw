package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Элементы всех перечислений в модели должны быть помечены этой аннотацией.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XFWEnumId {
    /**
     * @return значение энумератора.
     */
    int value();
}
