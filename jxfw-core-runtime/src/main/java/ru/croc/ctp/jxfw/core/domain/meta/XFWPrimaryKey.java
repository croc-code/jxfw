package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ru.croc.ctp.jxfw.core.domain.XFWPrimaryKeyType;

/**
 * Помечаются ключевые поля модели.
 *
 * @author SMufazzalov
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XFWPrimaryKey {

    /**
     * Имя колонки.
     * @return имя
     */
    String name() default "";
    /**
     * порядок.
     *
     * @return порядок поля в ключе.
     */
    int order();

    /**
     * тип ключа.
     *
     * @return тип ключа
     */
    XFWPrimaryKeyType type() default XFWPrimaryKeyType.CLUSTERED;

    /**
     * Сортировка.
     *
     * @return По возрастанию.
     */
    Ordering ordering() default Ordering.ASCENDING;

    /**
     * Порядок сортировки.
     */
    enum Ordering {
        /**
         * По возрастанию.
         */
        ASCENDING,

        /**
         * По убыванию.
         */
        DESCENDING;
    }
}
