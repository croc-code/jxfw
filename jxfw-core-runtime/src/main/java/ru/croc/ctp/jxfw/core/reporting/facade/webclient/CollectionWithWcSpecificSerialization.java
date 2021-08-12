package ru.croc.ctp.jxfw.core.reporting.facade.webclient;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Класс-обертка коллекции, которая должна быть особым образом сериализована в json.
 * @author OKrutova
 * @since 1.6
 */
@JsonSerialize(using = WcSpecificSerializer.class)
public class CollectionWithWcSpecificSerialization {

    private final Iterable<? extends ItemWithWcSpecificSerialization> collection;


    /**
     * Конструктор.
     * @param collection  коллекция.
     */
    public CollectionWithWcSpecificSerialization(Iterable<? extends ItemWithWcSpecificSerialization> collection) {
        this.collection = collection;
    }

    public Iterable<? extends ItemWithWcSpecificSerialization> getCollection() {
        return collection;
    }
}
