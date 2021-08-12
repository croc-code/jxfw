package ru.croc.ctp.jxfw.core.generator.meta

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.core.generator.impl.XFWEnumProcessor

/**
 * Перечисление в xtend-модели.
 * @since 1.6
 */
@Target(ElementType.TYPE)
@Active(XFWEnumProcessor)
@Retention(RetentionPolicy.SOURCE)
annotation XFWEnum {
}
