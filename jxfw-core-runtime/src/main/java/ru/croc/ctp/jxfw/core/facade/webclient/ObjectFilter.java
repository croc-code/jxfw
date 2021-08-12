package ru.croc.ctp.jxfw.core.facade.webclient;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Условия фильтра списка объектов, переданные в параметре $filter http-запроса.
 *
 * @since 1.1
 */
public class ObjectFilter {

    private Map<String, Object> map;

    /**
     * Конструктор по-умолчанию.
     */
    public ObjectFilter() {
        super();
        this.map = new HashMap<>();
    }

    /**
     * Конструктор.
     *
     * @param map - {@link Map} c набором свойств-значения для фильтрации.
     */
    public ObjectFilter(Map<String, Object> map) {
        super();
        this.map = map;
    }

    public Boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Добавить проверку идентичности для свойства field и значением value.
     *
     * @param field - поле, по которому выполняется проверка.
     * @param value - проверяемое значение типа {@link Integer}.
     */
    public void addEquals(String field, Integer value) {
        map.put(field, value);
    }

    /**
     * Добавить проверку идентичности для свойства field и значением value.
     *
     * @param field - поле, по которому выполняется проверка.
     * @param value - проверяемое значение типа {@link String}.
     */
    public void addEquals(String field, String value) {
        map.put(field, value);
    }

    /**
     * Добавить простое правило проверки для свойства field и значением value.
     *
     * @param fieldName - имя поля.
     * @param operator - оператор проверки. (например, > < !=)
     * @param value - проверяемое значение типа {@link Object}.
     */
    public void addSimple(String fieldName, String operator, Object value) {
        if (operator != null) {
            Map<String, Object> map = new HashMap<>();
            map.put(operator, value);
            this.map.put(fieldName, map);
        } else {
            map.put(fieldName, value);
        }
    }

    /**
     * Добавить правило проверки для свойства field и на попадание в диапозоны значений range.
     *
     * @param fieldName - имя поля.
     * @param range - диапозоны значений {@link Map}.
     */
    public void addRange(String fieldName, Map<String, Object> range) {
        map.put(fieldName, range);
    }

    /**
     * @return множество Entry из набора свойств для проверки фильтра. 
     */
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }
}
