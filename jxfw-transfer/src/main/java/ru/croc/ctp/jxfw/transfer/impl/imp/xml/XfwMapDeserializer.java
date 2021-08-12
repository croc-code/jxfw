package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Кастомная реализация десереилизатора XML в Map.
 * Используется для анмаршилнга XML файла с данными для импорта.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class XfwMapDeserializer extends MapDeserializer {

    private static final long serialVersionUID = -3108702962398163568L;

    /**
     * Конструктор.
     *
     * @param source                исходный десериализатор.
     * @param keyDeserializer       десериализатор ключей
     * @param valueDeserializer     десериализатор значений
     * @param valueTypeDeserializer десериализатор простых типов
     * @param ignorable             множество игнорируемых свойств
     */
    protected XfwMapDeserializer(MapDeserializer source,
                                 KeyDeserializer keyDeserializer,
                                 JsonDeserializer<Object> valueDeserializer,
                                 TypeDeserializer valueTypeDeserializer,
                                 NullValueProvider nuller,
                                 Set<String> ignorable) {

        super(source, new XfwMapKeyDeserializer(), valueDeserializer, valueTypeDeserializer, nuller, ignorable);
    }

    /**
     * Конструктор.
     *
     * @param source исходный десериализатор.
     */
    public XfwMapDeserializer(MapDeserializer source) {
        super(source);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected XfwMapDeserializer withResolved(KeyDeserializer keyDeser,
            TypeDeserializer valueTypeDeser, 
            JsonDeserializer<?> valueDeser,
            NullValueProvider nuller,
            Set<String> ignorable) {
        return new XfwMapDeserializer(this, new XfwMapKeyDeserializer(), (JsonDeserializer<Object>) valueDeser,
                valueTypeDeser, nuller, ignorable);
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final Map<Object, Object> result = super.deserialize(parser, context);
        if (result.containsKey("oid")) {
            result.put("domainType", ((FromXmlParser) parser).getStaxReader().getLocalName());
        }
        return result;
    }
}
