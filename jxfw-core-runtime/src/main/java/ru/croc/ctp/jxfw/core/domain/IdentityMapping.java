package ru.croc.ctp.jxfw.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Тип данных для хранения связи "старый ИД - новый ИД".
 * Возвращается в {@link StoreResult}
 *
 * @since 1.1
 */
@JsonIgnoreProperties({"object"})
public class IdentityMapping {

    private final DomainObject<?> object;
    private final Serializable originalId;

    /**
     * Конструктор.
     *
     * @param object     - объект у которого меняется ИД
     * @param originalId - исходный ИД object
     */
    public IdentityMapping(DomainObject<?> object, Serializable originalId) {
        super();
        this.object = object;
        this.originalId = originalId;
    }

    public DomainObject<?> getObject() {
        return object;
    }

    @JsonProperty("type")
    public String getTypeName() {
        return object.getTypeName();
    }

    @JsonProperty("id")
    public Serializable getOriginalId() {
        return originalId;
    }

    public Serializable getNewId() {
        return object.getId();
    }
    
    @Override
    public String toString() {
        return String.format("IdentityMapping {OriginalId: %s, NewId: %s}", getOriginalId(), getNewId());
    }
}