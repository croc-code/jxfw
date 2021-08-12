package ru.croc.ctp.jxfw.core.generator.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечаются все поля доменных сущностей типа Blob. Аннотация управляет генерацией дополнительных полей.
 * По умолчанию дополнительные поля всегда генерятся, повлиять на данное поведение можно также указав в файле
 * xtend.properties флаг jxfw.generateAdditionalFieldsForBlobType=false
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD})
public @interface XFWBlobInfo {

    /**
     * @return генерировать дополнительные поля true/false.
     */
    boolean value() default true;

    /**
     * Суффикс для генерации поля для хранения значения размера контента.
     *
     * @return Суффикс имени дополнительного поля
     */
    String sizeFieldNameSuffix() default "Size";

    /**
     * Суффикс для генерации поля для хранения имени файла.
     *
      * @return Суффикс имени дополнительного поля
     */
    String fileNameFieldNameSuffix() default "FileName";
    
    /**
     * Суффикс для генерации поля для хранения типа контента.
     * 
      * @return Суффикс имени дополнительного поля
     */
    String contentTypeFieldNameSuffix() default "MimeContentType";
}
