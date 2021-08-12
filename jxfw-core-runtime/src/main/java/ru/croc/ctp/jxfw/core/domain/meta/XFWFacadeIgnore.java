package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Аннотация для игнорирования поля/класса доменного объекта в заданных фасадах.
 * <p>Фасады на которые расспространяется действие аннотации можно указать способами
 * указанными ниже(перечесляются в порядке убывания приоритета):
 * <ol>
 *     <li>параметры аннотации</li>
 *     <li>domain.ignore.facades в application.properties</li>
 *     <li>не указавая явно, расспространяется на все фасады</li>
 * </ol>
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Target(value = {ElementType.FIELD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface XFWFacadeIgnore {
    /**
     * Список фасадов на которые будет расспространяться действие аннотации.
     *
     * @return список названий фасадов.
     */
    String[] facades() default {};
}
