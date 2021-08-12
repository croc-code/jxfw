package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

/**
 * Десериализация ключей для итогового отображения.
 * Необходимо для тех случаев, когда в исходном XML файле встречается перечисление одинаковых тегов,
 * например в случае когда поле сущности хранит множество ключей на другую сущность.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class XfwMapKeyDeserializer extends KeyDeserializer {

    @Override
    public Object deserializeKey(String content, DeserializationContext context)
            throws IOException {
        final XMLStreamReader staxReader = ((FromXmlParser) context.getParser()).getStaxReader();
        if (!"oid".equals(content) && staxReader.isStartElement()) { // проверяем что это не аттрибут oid
            final int attributeCount = staxReader.getAttributeCount();
            if (attributeCount > 0) {
                final QName name = staxReader.getAttributeName(0);
                if ("oid".equals(name.getLocalPart())) {
                    return content + "_" + ThreadLocalRandom.current().nextInt(); // делаем ключ мапы уникальным
                }
            }
        }
        return content;
    }
}