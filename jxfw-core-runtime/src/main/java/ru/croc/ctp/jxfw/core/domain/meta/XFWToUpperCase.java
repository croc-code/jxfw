package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для поля, говорит о том что при трансформации из TO объекта 
 * значения поля будут трансформированы в UPPER_CASE. 
 *
 * @author Nosov Alexander
 * @since 1.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XFWToUpperCase {
}
