package ru.croc.ctp.jxfw.core.domain.meta.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Указывает, что поле является временным и не будет передаваться на слой хранения.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface XFWTransient {

    /**
     * Устанавливать ли на фасаде поле как временное.
     * Поумолчанию {@code true}.
     * 
     * @return {@code boolean}.
     */
    boolean temp() default true;
}
