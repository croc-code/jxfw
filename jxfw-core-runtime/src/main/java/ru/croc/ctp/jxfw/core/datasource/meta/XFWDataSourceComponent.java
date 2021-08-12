package ru.croc.ctp.jxfw.core.datasource.meta;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркерная аннотация компонента - источника данных jXFW.
 *
 * @author OKrutova
 * @since 1.6.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public @interface XFWDataSourceComponent {
}
