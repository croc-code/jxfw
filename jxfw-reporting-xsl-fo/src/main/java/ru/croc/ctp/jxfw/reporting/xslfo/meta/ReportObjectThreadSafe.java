package ru.croc.ctp.jxfw.reporting.xslfo.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Атрибут, задающие признак "потокобезопасности объекта".
 * Для класса, помеченного таким атрибутом, может выполняться использование
 * одного экземпляра объекта в конкурентных потоках
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ReportObjectThreadSafe {
}
