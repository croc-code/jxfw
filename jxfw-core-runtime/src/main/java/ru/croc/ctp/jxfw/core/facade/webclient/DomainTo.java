package ru.croc.ctp.jxfw.core.facade.webclient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * DTO для доменных объектов.
 *
 * @author SPlaunov
 */
@JsonSerialize(using = DomainToSerializer.class)
@JsonDeserialize(using = DomainToDeserializer.class)
public class DomainTo {
    private final Map<String, DomainToProperty> propertyMetadata = new HashMap<>();

    private final Map<String, Object> properties = new HashMap<>();

    private final Map<String, Object> original = new HashMap<>();

    private String id;

    private String type;

    private Long ts = -1L;

    private Boolean isNew = false;

    private Boolean isRemoved = false;

    /**
     * Конструктор по-умолчанию.
     */
    public DomainTo() {
    }

    /**
     * @param type - Имя класса доменного объекта.
     * @param id   - ИД для объекта
     */
    public DomainTo(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Boolean isNew() {
        return isNew;
    }

    public void setNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public Boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(Boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * Добавить новое или заменить существующие свойство в объект TO.
     *
     * @param name  - имя свойства
     * @param value - значение свойства
     */
    public void addProperty(String name, Object value) {
        properties.put(name, value);
    }

    /**
     * Получить значение свойства из объекта TO.
     *
     * @param name - имя свойства
     * @return значение свойства, может быть {@code null} если отсуствует.
     */
    public Object getProperty(String name) {
        return properties.get(name);
    }

    /**
     * Копировать значение свойства объекта ТО по его имени.
     *
     * @param name     - имя свойства
     * @param consumer - потребитель, куда будет скопированно значение.
     */
    public void copyPropValue(String name, Consumer<? super Object> consumer) {
        if (properties.containsKey(name)) {
            consumer.accept(properties.get(name));
        }
    }

    /**
     * Выполнить действие для каждого свойства объекта ТО.
     *
     * @param action - действие которое необходимо выполнить.
     */
    public void forEachProperty(BiConsumer<? super String, ? super Object> action) {
    	forEachProperty(p -> true, action);
    }
    
    /**
     * Выполнить действие для каждого Lob свойства объекта ТО.
     *
     * @param action - действие которое необходимо выполнить.
     */
    public void forEachLobProperty(BiConsumer<? super String, ? super Object> action) {
	    forEachProperty(getLobPropValuePredicate(), action);
    }
    
    private void forEachProperty(Predicate<Object> include, BiConsumer<? super String, ? super Object> action) {
        properties.entrySet().stream().filter(entry -> include.test(entry.getValue())).forEach(entry -> {
    	    action.accept(entry.getKey(), entry.getValue());
        });    	
    }
    
    @SuppressWarnings("unchecked")
    private Predicate<Object> getLobPropValuePredicate() {
        return v -> {
    	    return (v instanceof Map && "LobPropValue".equals(((Map<String, Object>) v).get("$value")));
        };
    }
    
    /**
     * Добавить информацию о полях объекта.
     *
     * @param fieldName имя поля
     * @param property  информация о типе поля
     */
    public void addPropertyMetadata(String fieldName, DomainToProperty property) {
        this.propertyMetadata.put(fieldName, property);
    }

    @JsonIgnore
    public Map<String, DomainToProperty> getPropertyMetadata() {
        return propertyMetadata;
    }

    /**
     * Добавить первоначальное значение свойства.
     *
     * @param name  - имя свойства
     * @param value - значение свойства
     */
    void addOriginal(String name, Object value) {
        original.put(name, value);
    }

    public Map<String, Object> getOriginal() {
        return original;
    }

    @Override
    public String toString() {
        return "DomainTo["
                + "type=" + type
                + ", id=" + id
                + ", isNew=" + isNew
                + ", isRemoved=" + isRemoved
                + ", " + properties + "]";
    }

}
