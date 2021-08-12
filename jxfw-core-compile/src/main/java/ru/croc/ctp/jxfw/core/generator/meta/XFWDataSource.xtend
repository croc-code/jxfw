package ru.croc.ctp.jxfw.core.generator.meta

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.core.generator.impl.XFWDataSourceProcessor

/**
 * Сервис источника данных
 * Автоматически генерируется контроллер для получения данных по http.
 */
@Target(ElementType.METHOD, ElementType.TYPE)
@Active(XFWDataSourceProcessor)
@Retention(RetentionPolicy.SOURCE)
annotation XFWDataSource {
	/**
	 * путь, нужно указывать согласно конвенции по наименованию классов в Java,
	 * т.к. это значение в дальнейшем будет использоваться при генерации DataSet классов.
	 * @return маппинг для контроллера @RequestMapping 
	 */
	String value
}
