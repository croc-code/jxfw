package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * объявляет массив наименований для поля либо типа.
 *
 * @see XFWElementLabel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface XFWElementLabels {

    /**
     * @return массив наименований.
     */
    XFWElementLabel[] value();
}
