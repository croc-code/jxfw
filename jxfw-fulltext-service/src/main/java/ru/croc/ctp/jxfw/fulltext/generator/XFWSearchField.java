package ru.croc.ctp.jxfw.fulltext.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, для указания дополнительной информации, относящейся к генерируемым скриптам полнотекстового хранилища.
 *
 * @author SMufazzalov
 * @since 1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XFWSearchField {

    /**
     * Имя поля модели в хранилище.
     *
     * @return валидное имя поля, например ("index_code")
     */
    String name() default "";

    /**
     * stored: "true" если нужно чтение значений данного поля. Т.е. Solr используется в качестве базы данных.
     *
     * @return true/false
     */
    boolean stored() default true;

    /**
     * indexed: "true" если нужен поиск по этому полю. (поиск, сортировка, фасеты)
     *
     * @return true/false
     */
    boolean indexed() default true;

    /**
     * Тип специфичный для хранилища.
     *
     * @return валидный тип поля хранилища. Например для Solr "solr.UUIDField", "solr.StrField"
     */
    String type() default "";

}
