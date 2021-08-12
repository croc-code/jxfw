package ru.croc.ctp.jxfw.core.generator.meta

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.core.generator.impl.ToStringProcessor

/**
 * Active Аннотация для генерации метода toString() для доменных объектов.
 * 
 * @author Nosov Alexander
 * @since 1.3
 */
@Target(ElementType::TYPE)
@Active(ToStringProcessor)
@Retention(RetentionPolicy.SOURCE)
annotation XFWToString {
    /**
    * Включать или не включать имена полей в вывод toString.
    * @since 1.3
    */
    boolean includeFieldNames = true

    /**
    * Имена полей, которые попадут в вывод toString.
    * <b>Не может быть заполнен одновременно с exclude</b>
    * @since 1.3
    */
    String[] of = #[]
    /**
    * Имена полей, которые будут исключены из вывода toString.
    * <b>Не может быть заполнен одновременно с of</b>
    * @since 1.3
    */
    String[] exclude = #[]

    /**
    * Флаг вызова метода toString у родительского класса.
    * @since 1.3
    */
    boolean callSuper = false

    /**
    * Флаг вывода hashcode в toString.
    * @since 1.3
    */
    boolean hashcode = false

    /**
    * Флаг вывода метода {@code System.identityhashcode} в toString.
    * @since 1.3
    */
    boolean identityHashcode = false
}
