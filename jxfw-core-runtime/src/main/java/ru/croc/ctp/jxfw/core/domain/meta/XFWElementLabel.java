package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для задания наименований элементов доменной модели на разных языках.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XFWElementLabels.class)
public @interface XFWElementLabel {

    /**
     * @return Строка для отображения наименования типа сущности или её свойства на
     *         языке, указанном в lang.
     */
    String value();

    /**
     * @return Двухбуквенный код языка ISO 639-1. Если не указан, то будет использован
     *         код, заданный в свойстве jxfw.default-lang в xtend.properties
     */
    String lang() default "";

    /**
     *  Может применяться только на классе, использоваться для переопределения
     *  именований полей родителя в классах наследниках.
     * @return Имя свойста, для которого указывается наименование.
     *
     */
    String propName() default "";
}
