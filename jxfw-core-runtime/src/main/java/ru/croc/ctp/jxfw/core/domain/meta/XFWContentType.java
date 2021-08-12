package ru.croc.ctp.jxfw.core.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для простановки contentType.
 * Значение для атрибута accept input'a - см. <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/Input#attr-accept">MDN</a>.
 * Варианты значения: список расширений (".jpg,.png,.doc"), MIME type без расширения, "audio/", "video/\", "image/*".
 *
 * @author Nosov Alexander
 * @author SMufazzalov
 * @since 1.3
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XFWContentType {

    /**
     * Дефолтное значение для значения аннотации.
     */
    String DEFAULT = "image";

    /**
     * ContentType хранящихся данных.
     *
     * @return строка и 3 варианта задания "audio", "video", "image".
     */
    String value() default DEFAULT;

    /**
     * Значение для атрибута accept у input элемента на клиенте.
     *
     * @return строку достоверного MIME типа без каких либо расширений (file extensions),
     * “audio/*”, “video/*”, “image/*” либо список расширений ".jpg,.png,.doc"
     */
    String acceptFileTypes() default "";
}
