package ru.croc.ctp.jxfw.wc.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;


/**
 * Утилиты для работы с хешами.
 *
 * @author Nosov Alexander
 */
public final class HashUtils {

    /**
     * вычислить хеш от массива байтов.
     * 
     * @param bytes - входной массив
     * @return хеш в виде строки
     */
    public static String computeHash(final byte[] bytes) {
        return computeHash(bytes, HashStyle.Minified);
    }

    /**
     * вычислить хеш от массива байтов.
     *
     * @param bytes - входной массив
     * @param style - стиль хеш-функции   
     * @return хеш в виде строки
     * 
     * @see HashUtils.HashStyle
     */
    public static String computeHash(final byte[] bytes, final HashStyle style) {
        final byte[] hash = DigestUtils.md5Digest(bytes);
        final String hashStr = Base64.encodeBase64String(hash);
        if (style == HashStyle.Minified) {
            return StringUtils.strip(hashStr, "=");
        }
        return hashStr;
    }

    /**
     * Стиль хеша.
     */
    enum HashStyle {
        /**
         * без знаков '='.
         */
        Minified,
        /**
         * обычный.
         */
        Default
    }

}
