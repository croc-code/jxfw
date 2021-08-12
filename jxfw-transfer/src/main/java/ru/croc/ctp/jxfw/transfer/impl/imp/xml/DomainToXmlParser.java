package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToProperty;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolver;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Набор функций для парсинга DTO в xml.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class DomainToXmlParser {

    /** Вычитывает из парсера DomainTo объекта.
     * @param reader парсер установленный на начало объекта.
     * @param transferPropertyResolver ресолвер типов.
     * @return доменный объект десериализованный из xml.
     * @throws XMLStreamException ошибка парсинга, данный формат сообщения не предусмотрен.
     */
    public static DomainTo parseDomainTo(XMLStreamReader reader, TransferPropertyResolver transferPropertyResolver)
            throws XMLStreamException {
        if (!reader.hasName() || reader.getEventType() != XMLStreamConstants.START_ELEMENT || !reader.hasNext()) {
            return null;
        }

        final DomainTo dto = new DomainTo();
        dto.setType(reader.getLocalName());
        dto.setId(reader.getAttributeValue(null, "oid"));

        nextTag(reader);
        while (!isEndOfTag(reader, dto.getType())) {
            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                final String property = reader.getLocalName();
                final String typeOfProperty = reader.getAttributeValue("urn:schemas-microsoft-com:datatypes", "dt");
                final String oid = reader.getAttributeValue("", "oid");

                if (oid != null) {
                    dto.addProperty(property, oid);
                } else if (typeOfProperty != null) {
                    transferPropertyResolver.addTypedValue(dto, property, typeOfProperty, reader.getElementText());
                } else {
                    reader.next();
                    if (isEndOfTag(reader, property)) {
                        dto.addProperty(property, null);
                    } else {
                        if (isStartTag(reader)) {
                            nextTag(reader);
                        }

                        final String tagName = reader.getLocalName();
                        final List<String> objects = new ArrayList<>();

                        while (!isEndOfTag(reader, property)) {
                            if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                                objects.add(reader.getAttributeValue(null, "oid"));
                            }
                            reader.next();
                        }

                        if (objects.size() == 1) {
                            dto.addProperty(property, objects.get(0));
                        } else if (objects.size() > 1) {
                            dto.addProperty(property, objects);
                        }

                        continue;
                    }
                }
            }

            reader.next();
        }
        return dto;
    }

    /** Проверяет установлен ли курсор на конец указаного тега.
     * @param reader парсер.
     * @param tagName имя тега.
     * @return true, если курсор установлен на конец тега.
     */
    private static boolean isEndOfTag(XMLStreamReader reader, String tagName) {
        return reader.getEventType() == XMLStreamConstants.END_ELEMENT
                && reader.hasName()
                && tagName.equals(reader.getLocalName())
                || reader.getEventType() == XMLStreamConstants.END_DOCUMENT;
    }

    /** Устанавливает курсор на следующий тег.
     * @param reader парсер.
     */
    private static void nextTag(XMLStreamReader reader) throws XMLStreamException {
        reader.next();
        while (isStartTag(reader)) {
            if (reader.getEventType() == XMLStreamConstants.END_DOCUMENT) {
                return;
            }
            reader.next();
        }
    }

    /** Проверяет установлен ли курсор на начале элемента.
     * @param reader парсер.
     * @return true, если курсор на начале элемента, иначе false.
     */
    private static boolean isStartTag(XMLStreamReader reader) throws XMLStreamException {
        return reader.getEventType() != XMLStreamConstants.START_ELEMENT || !reader.hasName();
    }
}
