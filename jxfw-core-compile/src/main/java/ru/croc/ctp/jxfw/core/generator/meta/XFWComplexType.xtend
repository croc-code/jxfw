package ru.croc.ctp.jxfw.core.generator.meta

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.core.generator.impl.XFWComplexTypeProcessor

/**
 * Аннотация для комплексных типов
 * 
 * @author Nosov Alexander
 */
@Target(ElementType.TYPE)
@Active(XFWComplexTypeProcessor)
@Retention(RetentionPolicy.SOURCE)
annotation XFWComplexType {
}
