package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация - значение по умолчанию (поддерживаются простые свойства).
 * Created by SMufazzalov on 30.12.2015.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface XFWDefaultValue {

    /**
     * Строка, содержащая значение, которое будет преобразовано к типу свойства и установлено в качестве значения
     * по умолчанию для объектов, создаваемых в web client'е. В случае свойств типов dateTime, date, time в качестве
     * начального значения используется строка в каноническом формате ISO 8601 ("yyyy-MM-dd'T'HH:mm:ss") или
     * ("yyyy-MM-dd'T'HH:mm:ss±HH:mm"). Также для временных типов можно задать признак
     * {@link XFWDefaultValue#asCurrent} = true (текущее время), для остальных типов данный признак будет
     * игнорироваться.
     *
     * @return Строка, содержащая значение
     */
    String value() default "";

    /**
     * Признак использования текущего времени/даты как начального значения.
     *
     * @return true/false
     */
    boolean asCurrent() default false;

}
