package ru.croc.ctp.jxfw.solr.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Predicate;

/**
 * Утилиты для работы с jxfw solr модулем.
 */
public class XfwSolrUtils {

    private static Predicate<Field> hasPrimaryKeyAnnotation = field -> field.getAnnotation(XFWPrimaryKey.class) != null;

    /**
     * получить ключ закодированный в base64 строку.
     *
     * @param key - ключ
     * @param clz - Доменный тип
     * @return - закодированный ключ
     */
    public static String serializeKey(String key, Class<? extends DomainObject<String>> clz) {
        return serializeKey(key, useSyntheticKey(clz));
    }

    /**
     * получить ключ закодированный в base64 строку
     * в дизайн тайме уже ясно какой тип ключа, и его можно просто передать флагом
     * поэтому лишний overhead.
     * @param key - ключ
     * @param isComplexKey true/false
     * @return закодированный ключ
     */
    public static String serializeKey(String key, boolean isComplexKey) {
        if (isComplexKey) {
            return Base64.getEncoder().encodeToString(key.getBytes());
        } else {
            ArrayList<String> list = new ArrayList<>();
            list.add(key);
            ObjectMapper mapper = new ObjectMapper();
            try {
                return Base64.getEncoder().encodeToString((mapper.writeValueAsString(list)).getBytes());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Couldn't serialize key: " + key, e);
            }
        }
    }

    /**
     * определение, что класс использует комплексный ключ (runtime)
     * в дизайн тайме уже ясно какой тип ключа, и его можно просто передать флагом
     * поэтому лишний overhead.
     *
     * @param clz - доменный тип
     * @return наличие полей аннотированных @PrimaryKey более одного
     */
    private static boolean useSyntheticKey(Class<? extends DomainObject<String>> clz) {
        Field[] fields = clz.getDeclaredFields();
        return Arrays.stream(fields).filter(hasPrimaryKeyAnnotation).count() > 1L;
    }

    /**
     * получить нормальный ключ из закодированной base64 строки.
     *
     * @param encodedKey ключ в виде строки base64
     * @param isComplexKey true/false
     * @return нормальный ключ
     */
    public static String parseKey(String encodedKey, boolean isComplexKey) {
        if (isComplexKey) {
            return new String(Base64.getDecoder().decode(encodedKey));
        } else {
            String json = new String(Base64.getDecoder().decode(encodedKey));
            ObjectMapper mapper = new ObjectMapper();
            try {
                return "" + mapper.readValue(json.getBytes(), ArrayList.class).get(0);
            } catch (Exception e) {
                throw new RuntimeException("Couldn't parseKey encodedKey: " + encodedKey, e);
            }
        }
    }

    /**
     * получить нормальный ключ из закодированной base64 строки
     *
     * @param encodedKey ключ в виде строки base64.
     * @param clz - Доменный тип.
     * @return ключ
     */
    public static String parseKey(String encodedKey, Class<? extends DomainObject<String>> clz) {
        return parseKey(encodedKey, useSyntheticKey(clz));
    }
}
