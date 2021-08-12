package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Класс - обёртка, содержащий и объект чтения XML и текущее вычитанное событие.
 * Попытка скрестить ужа с ежом, т.к. не нашёл способа получить доступ к текущему состоянию XMLEventReader`а.
 * Заодно сюда же запихну вспомогательные методы, отсутствующие в родных классах, но бывшие в .Net версии
 * Created by vsavenkov on 13.10.2017.
 */
public class XmlTextReader {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(XmlTextReader.class);

    /**
     * объект чтения XML.
     */
    private XMLEventReader reader;

    /**
     * Текущее вычитанное событие.
     */
    private XMLEvent event;

    /**
     * Конструктор.
     * @param inputStream - поток для чтения XML
     * @throws XMLStreamException генерируется XMLInputFactory
     */
    public XmlTextReader(InputStream inputStream) throws XMLStreamException {

        XMLInputFactory factory = XMLInputFactory.newFactory();
        reader = factory.createXMLEventReader(inputStream);
        event = reader.nextEvent();
    }

    /**
     * A utility function to check if this event is a StartElement.
     * @see StartElement
     * @return boolean возвращает true, если курсор в начале элемента и  false в противном случае
     */
    public boolean isStartElement() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        // возвращаю состояние события
        return event.isStartElement();
    }

    /**
     * A utility function to check if this event is a EndElement.
     * @see EndElement
     * @return boolean возвращает true, если курсор в конце элемента и  false в противном случае
     */
    public boolean isEndElement() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        // возвращаю состояние события
        return event.isEndElement();
    }

    /**
     * Check if there are more events.
     * Returns true if there are more events and false otherwise.
     * @return true if the event reader has more events, false otherwise
     */
    public boolean hasNext() {

        return reader.hasNext();
    }

    /**
     * Check the next XMLEvent without reading it from the stream.
     * Returns null if the stream is at EOF or has no more XMLEvents.
     * A call to peek() will be equal to the next return of next().
     * @see XMLEvent
     * @return XMLEvent возвращает следующее событие
     * @throws XMLStreamException генерирует XMLEventReader
     */
    public XMLEvent peek() throws XMLStreamException {

        return reader.peek();
    }

    /**
     * Frees any resources associated with this Reader.  This method does not close the
     * underlying input source.
     * @throws XMLStreamException if there are errors freeing associated resources
     */
    public void close() throws XMLStreamException {

        reader.close();
    }

    /**
     * Return the line number where the current event ends,
     * returns -1 if none is available.
     * @return the current line number
     */
    public int getLineNumber() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        return event.getLocation().getLineNumber();
    }

    /**
     * Return the column number where the current event ends,
     * returns -1 if none is available.
     * @return the current column number
     */
    public int getColumnNumber() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        return event.getLocation().getColumnNumber();
    }

    /**
     * Return the byte or character offset into the input source this location
     * is pointing to. If the input source is a file or a byte stream then
     * this is the byte offset into that stream, but if the input source is
     * a character media then the offset is the character offset.
     * Returns -1 if there is no offset available.
     * @return the current offset
     */
    public int getCharacterOffset() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        return event.getLocation().getCharacterOffset();
    }

    /**
     * Returns an integer code for this event.
     * @return int code for this event.
     */
    public int getEventType() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        return event.getEventType();
    }

    /**
     * Skips any insignificant space events until a START_ELEMENT or
     * END_ELEMENT is reached. If anything other than space characters are
     * encountered, an exception is thrown. This method should
     * be used when processing element-only content because
     * the parser is not able to recognize ignorable whitespace if
     * the DTD is missing or not interpreted.
     * @throws XMLStreamException if anything other than space characters are encountered
     */
    public void nextTag() throws XMLStreamException {

        event = reader.nextTag();
    }

    /**
     * Reads the content of a text-only element. Precondition:
     * the current event is START_ELEMENT. Postcondition:
     * The current event is the corresponding END_ELEMENT.
     * @return String возвращает текст узла
     * @throws XMLStreamException if the current event is not a START_ELEMENT
     *              or if a non text element is encountered
     */
    public String getText() throws XMLStreamException {

        if (event.isCharacters()) {
            return event.asCharacters().getData();
        } else {
            throw new XMLStreamException("Reader cursor not on characters position. Offset:" + getCharacterOffset());
        }
    }

    /**
     *  Skips the children of the current node.
     *  Попытка сделать аналог System.Xml.XmlReader::Skip
     * @throws XMLStreamException генерирует XMLEventReader
     */
    public void skip() throws XMLStreamException {

        // если текущее событие - окончание элемента
        // или следующее событие - начало или окончание элемента, то просто двигаюсь
        if (event.isEndElement() || peek().isEndElement() || peek().isStartElement()) {
            event = reader.nextEvent();
        } else if (event.isStartElement()) {
            // иначе: мы д.б. в начале узла
            // запоминаю имя текущего узла
            String tag = getPrefixedName();
            int count = 0;
            // двигаюсь, пока не достигну окончания узла с учётом уровня вложенности
            while (reader.hasNext()) {
                if (event.isStartElement()) {
                    count++;
                } else if (event.isEndElement()) {
                    count--;
                    if (count == 0 && tag.equals(getPrefixedName())) {
                        break;
                    }
                }
                event = reader.nextEvent();
            }
        }
    }

    /**
     * Перемещение указателя на ближайший XML элемент.
     * @throws XMLStreamException генерится XMLEventReader`ом
     */
    public void moveToElement() throws XMLStreamException {

        moveToElement(null);
    }

    /**
     * Перемещение указателя на ближайший XML элемент c заданным именем.
     * @param elementName - Имя Xml-элемента на который нужно перейти
     * @throws XMLStreamException генерится XMLEventReader`ом
     */
    public void moveToElement(String elementName) throws XMLStreamException {

        // пока есть куда двигаться
        while (reader.hasNext()) {
            // если код события = начало элемента, то
            if (event.isStartElement()) {
                // если имя элемента не задано или совпадает с заданным, то останавливаюсь
                if (StringUtils.isBlank(elementName) || elementName.equals(getPrefixedName())) {
                    break;
                }
            }
            event = reader.nextEvent();
        }
    }

    /**
     * Checks that the current node is an element and advances the reader to the next node.
     * Попытка сделать аналог System.Xml.XmlReader::ReadStartElement
     * @throws XMLStreamException генерируется XMLEventReader`ом
     */
    public void readStartElement() throws XMLStreamException {

        readStartElement(null);
    }

    /**
     * Checks that the current node is an element and advances the reader to the next node.
     * Попытка сделать аналог System.Xml.XmlReader::ReadStartElement
     * @param parentNodeName - имя родительского узла, за пределы которого не надо вылезать
     * @throws XMLStreamException генерируется XMLEventReader`ом
     */
    public void readStartElement(String parentNodeName) throws XMLStreamException {

        // если есть подчинённые элементы или элемент пуст, то
        if (hasChildren() || isEmptyElement()) {
            // двигаюсь, пока не встречу начало элемента
            while (reader.hasNext()) {
                event = reader.nextEvent();
                if (event.isStartElement()
                        || (!StringUtils.isBlank(parentNodeName) && event.isEndElement()
                        && parentNodeName.equals(getPrefixedName()))) {
                    break;
                }
            }
        } else {
            // иначе: просто подвинусь
            event = reader.nextEvent();
        }
    }

    /**
     * Получение списка атрибутов текущего узла.
     * Указатель должен быть установлен на XML элемент
     * @return Map  - возвращает карту атрибутов текущего узла в виде [имя]->[значение]
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getAttributeList() {

        if (!reader.hasNext() || !event.isStartElement()) {
            return null;
        }

        Map<String, String> returnValue = new HashMap<>();
        // Перебираем все атрибуты
        Iterator<Object> iterator = event.asStartElement().getAttributes();
        while (iterator.hasNext()) {
            Attribute next = (Attribute) iterator.next();
            returnValue.put(next.getName().getLocalPart(), next.getValue());
        }
        // если нет атрибутов, то верну null
        return 0 == returnValue.size() ? null : returnValue;
    }

    /**
     * Возвращает имя текущего элемента с префиксом типа fo:table.
     * @return имя текущего элемента с префиксом, если он задан
     */
    public String getPrefixedName() {

        // если событие отсутствует, то генерирую исключение
        if (null == event) {
            throw  new IllegalStateException("Event is null");
        }

        QName name = null;
        if (event.isStartElement()) {
            name = event.asStartElement().getName();
        } else if (event.isEndElement()) {
            name = event.asEndElement().getName();
        }
        if (null != name) {
            if (StringUtils.isBlank(name.getPrefix())) {
                return name.getLocalPart();
            } else {
                return StringUtils.join(Arrays.asList(name.getPrefix(), name.getLocalPart()), ":");
            }
        } else {
            throw new IllegalStateException("Event not is START_ELEMENT or END_ELEMENT");
        }
    }

    /**
     * Перемещение указателя на начало следующего XML элемента.
     * @throws XMLStreamException - генерится XMLEventReader`ом
     */
    public void skipToNextElementWithComment() throws XMLStreamException {

        skipToNextElement();

        // Переходим на следующий элемент

        ///////////////////////////////////////////////////////////
        // 03.05.2006 DKL
        // Добавлено условие oReader.NodeType == XmlNodeType.Comment
        // №178382 Рендеринг в Excel: при наличии в XSL-FO xml-комментариев и указании атрибута vt="string" для
        // данного блока экспорт падает.
        // Компонент вместо пропуска комментариев ошибочно обрабатывал их.

        while (event.getEventType() == XMLStreamConstants.END_ELEMENT
                && reader.hasNext() && event.getEventType() == XMLStreamConstants.COMMENT) {
            skipToNextElement();
        }

        //
        ///////////////////////////////////////////////////////////
    }

    /**
     * Перемещение указателя на начало следующего XML элемента.
     * @throws XMLStreamException генерится XMLEventReader`ом
     */
    public void skipToNextElement() throws XMLStreamException {

        event = reader.nextTag();
        if (reader.hasNext()
                && !event.isStartElement()
                && !event.isCharacters()
                && XMLStreamConstants.CDATA != event.getEventType()) {
            moveToNextElementOrText();
        }
    }

    /**
     * Перемещение указателя на ближайший стартовый или конечный тег. Исключаем теги в которых не реализуется рекурсия.
     * @throws XMLStreamException - генерится XMLStreamReader`ом
     */
    public void moveToNextElement() throws XMLStreamException {

        event = reader.nextEvent();
        while (reader.hasNext()) {
            if (event.isStartElement()
                    || event.isEndElement()) {
                break;
            }
            event = reader.nextEvent();
        }
    }

    /**
     * Перемещение указателя на ближайший стартовый,конечный тег и текст.
     * @throws XMLStreamException генерится XMLEventReader`ом
     */
    public void moveToNextElementOrText() throws XMLStreamException {

        if (!reader.hasNext()) {
            return;
        }
        event = reader.nextEvent();
        while (reader.hasNext()) {
            if (event.isStartElement()
                    || event.isEndElement()
                    || XMLStreamConstants.CHARACTERS == event.getEventType()
                    || XMLStreamConstants.CDATA == event.getEventType()) {
                break;
            }
            event = reader.nextEvent();
        }
    }

    /**
     * Gets a value indicating whether the current node is an empty element (for example, <MyElement/>).
     * Попытка сделать аналог System.Xml.XmlTextReader::IsEmptyElement
     * @return boolean  - возвращает true if the current node is an element that ends with />; otherwise, false.
     * @exception XMLStreamException    - генерится ValidatingStreamReader`ом
     */
    public boolean isEmptyElement() throws XMLStreamException {

        // похож ли текущий элемент на <MyElement/>
        // если я правильно понял, то для пустого тэга генерятся подряд два события: START_ELEMENT и END_ELEMENT
        // соответственно, мы д.б. в начале тэга
        boolean returnValue = event.isStartElement();
        // если есть куда двигаться, то двинемся
        XMLEvent event = null;
        if (returnValue && reader.hasNext()) {
            event = reader.peek();
        }

        // верну признак, что мы были в начале тэга и передвинулись сразу на конец тэга
        return returnValue && (null != event && event.isEndElement());
    }

    /**
     * When overridden in a derived class, reads the content, including markup,
     * representing this node and all its children.
     * Попытка сделать аналог System.Xml.XmlReader::ReadOuterXml
     * @return String   - If the reader is positioned on an element or an attribute node, this method
     *                      returns all the XML content, including markup, of the current node and all
     *                      its children; otherwise, it returns an empty string.
     * @throws XMLStreamException генерируется XMLEventReader`ом
     */
    public String readOuterXml() throws XMLStreamException {

        // содрано с https://stackoverflow.com/questions/14051923/stax-xml-all-content-between-two-required-tags
        StringWriter sw = new StringWriter();
        XMLOutputFactory of = XMLOutputFactory.newInstance();
        XMLEventWriter xw = of.createXMLEventWriter(sw);

        String tag = getPrefixedName();
        int count = 0;
        try {
            while (reader.hasNext()) {
                if (event.isStartElement()) {
                    xw.add(event);
                    count++;
                } else if (event.isEndElement()) {
                    count--;
                    xw.add(event);
                    if (count == 0 && tag.equals(getPrefixedName())) {
                        break;
                    }
                } else {
                    xw.add(event);
                }
                event = reader.nextEvent();
            }
        } finally {
            if (xw != null) {
                xw.close();
            }
            if (null != sw) {
                sw.flush();
                try {
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error close StringWriter", e);
                }
            }
        }

        return sw.toString();
    }

    /**
     * Возвращает признак наличия подчинённых узлов.
     * @return boolean возвращает true, если есть подчинённые узлы и false в противном случае
     * @throws XMLStreamException генерирует XMLEventReader
     */
    public boolean hasChildren() throws XMLStreamException {

        if (reader.hasNext()) {
            if (reader.peek().isStartElement()) {
                return true;
            }
        }

        return false;

    }
}
