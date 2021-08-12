package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

/**
 * Модификатор стандартного десериализатора заменяющий {@link MapDeserializer}
 * на свою реализацию {@link XfwMapDeserializer}.
 *
 * @author Nosov Alexander
 * @see MapDeserializer
 * @see XfwMapDeserializer
 * @since 1.4
 */
public class MapDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public JsonDeserializer<?> modifyMapDeserializer(DeserializationConfig config,
                                                     MapType type,
                                                     BeanDescription beanDesc,
                                                     JsonDeserializer<?> deserializer) {
        final MapDeserializer standardDeserializer
                = (MapDeserializer) super.modifyMapDeserializer(config, type, beanDesc, deserializer);
        return new XfwMapDeserializer(standardDeserializer);
    }
}