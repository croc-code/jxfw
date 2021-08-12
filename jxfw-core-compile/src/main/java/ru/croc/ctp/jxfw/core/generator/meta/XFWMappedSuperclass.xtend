package ru.croc.ctp.jxfw.core.generator.meta

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.core.generator.impl.XfwMappedSuperclassProcessor

/**
 * Аннотация для абстрактных классов необходимых для маппинга общих своиств сущностей.
 *
 * @author Pyatykh Sergey.
 */
@Target(ElementType.TYPE)
@Active(XfwMappedSuperclassProcessor)
@Retention(RetentionPolicy.SOURCE)
annotation XFWMappedSuperclass {
}