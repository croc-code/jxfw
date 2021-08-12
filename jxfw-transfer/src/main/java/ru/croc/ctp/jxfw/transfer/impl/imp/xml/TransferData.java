package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Контейнер для парсинга xml формата XfwTransferData.
 * Неизмвестно какая схема XML будет у представления доменных сущностей.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@XmlRootElement(name = TransferData.ROOT_TAG)
public class TransferData {

    /**
     * Имя корневого тега у файла с выгруженными данными.
     */
    public static final String ROOT_TAG = "data-transfer";

    private Map<String, Object> data = new TreeMap<>();

    @JacksonXmlProperty(localName = "objects")
    @JsonDeserialize(keyUsing = XfwMapKeyDeserializer.class)
    private List<Map<String, Object>> objects;

    /**
     * Получить значение.
     *
     * @return значение
     */
    @JsonAnyGetter
    public Map<String, Object> get() {
        return data;
    }

    /**
     * Установить значение.
     *
     * @param name  ключ
     * @param value значение
     */
    @JsonAnySetter
    public void set(String name, Object value) {
        data.put(name, value);
    }

    public List<Map<String, Object>> getObjects() {
        return objects;
    }
}
