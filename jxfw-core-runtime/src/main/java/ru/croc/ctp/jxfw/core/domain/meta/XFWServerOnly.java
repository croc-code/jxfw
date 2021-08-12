package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Отмечает свойства и типы, которые предназначены для использования серверной частью и не передаются клиенту.
 *
 * @author Sergey Verkhushin
 * @since 1.9.2
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface XFWServerOnly {
}
