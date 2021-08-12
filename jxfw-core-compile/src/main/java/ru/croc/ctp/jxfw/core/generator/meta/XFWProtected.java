package ru.croc.ctp.jxfw.core.generator.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки полей типа String, которые будут маскированы
 * при трансформации объекта доменной модели в TO.
 * 
 * @author ANosov
 *         29.01.2015
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface XFWProtected {

    /**
     * Значение строки которая будет подставлена вместо истенного значения.
     * 
     * @return строка которая будет подставлена
     */
    String value() default "[PROTECTED]";

}
