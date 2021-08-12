package ru.croc.ctp.jxfw.reporting.xslfo.style;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 * Класс реализует функциональность работы со стилем.
 * Created by vsavenkov on 14.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ReportStyle {

    /**
     * Имя стиля - его ключ.
     */
    private String name;

    public String getName() {
        return name;
    }

    /**
     * Сеттер имени стиля.
     * @param name имя стиля
     */
    public void setName(String name) {
        //Проверить на пустоту
        if (StringUtils.isBlank(name)) {
            throw new ArgumentNullException("Name");
        }
        // Установить новое значение имени стиля
        this.name = name;
    }

    /**
     * Хранилище атрибутов стиля.
     */
    protected final Map<String, String> styleAttributes = new HashMap<>();

    /**
     * Строковое представление стиля.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        styleAttributes.forEach((key, value) ->
                sb.append(String.format("%1$s=\"%2$s\" ", key, value))
        );
        return sb.toString();
    }

    /**
     * Метод записывает стиль в атрибуты текущего тэга XmlWriter'а.
     * @param xmlStreamWriter    - Writer XslFo-потока
     * @throws XMLStreamException из метода XMLStreamWriter.writeAttribute
     */
    public void writeToXml(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        for (String key : styleAttributes.keySet()) {
            xmlStreamWriter.writeAttribute(key, styleAttributes.get(key));
        }
    }

    /**
     * Статический вариант записи атрибутов стиля в XmlWriter.
     * @param xmlStreamWriter    - XmlWriter XslFo-потока
     * @param reportStyle        - Записываемый стиль
     * @throws XMLStreamException из метода XMLStreamWriter.writeToXml
     */
    public static void writeToXml(XMLStreamWriter xmlStreamWriter, ReportStyle reportStyle) throws XMLStreamException {
        if (reportStyle != null) {
            reportStyle.writeToXml(xmlStreamWriter);
        }
    }

    /**
     * Возвращает стиль, основанный на изменениях текущего стиля по отношению к reportStyle.
     * @param reportStyle        - Стиль, относительно которого выявляются различия
     * @return ReportStyle  - Новый стиль, который равен различиям между текущим стилем и reportStyle
     */
    private ReportStyle diff(ReportStyle reportStyle) {
        // Если нет стиля, с которым проводить сравнение, вернуть свою копию
        if (reportStyle == null) {
            return new ReportStyle(this);
        }
        // Создать копию стиля, с которым производится сравнение
        ReportStyle rs = new ReportStyle(reportStyle);
        // Сравнить все атрибуты
        styleAttributes.forEach((key, value) -> {
            if (rs.containsKey(key) && rs.get(key) == styleAttributes.get(key)) {
                // при равенстве их значений удалить их из вновь созданного стиля
                rs.remove(key);
            } else {
                // Если атрибута нет или его значение отлично, изменить значение
                // в новом стиле
                rs.put(key, styleAttributes.get(key));
            }
        });
        // вернуть полученный стиль
        return rs;
    }

    /**
     * Пустой конструктор.
     */
    public ReportStyle() {
    }

    /**
     * Размер хранилища стилей.
     * @return размер хранилища.
     */
    public int size() {
        return styleAttributes.size();
    }

    public boolean isEmpty() {
        return styleAttributes.isEmpty();
    }

    /**
     * Ключи хранилища стилей.
     * @return набор ключей
     */
    public Set<String> keys() {
        return styleAttributes.keySet();
    }

    /**
     * Конструктор стиля.
     * @param name     - Имя стиля
     * @param value    - Атрибуты стиля в формате атрибут="значение"
     */
    public ReportStyle(String name, String value) {
        // Получить имя класса
        this.name = name;
        try {
            fillStyleAttributes(value);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            StringBuilder msg = new StringBuilder();
            msg.append("Ошибка в описании стиля name = ");
            msg.append(name);
            msg.append(" value = ");
            msg.append(value);
            throw new ReportException(msg.toString(), e);
        }
    }

    private void fillStyleAttributes(String value) throws ParserConfigurationException, IOException, SAXException {
        NamedNodeMap attributes = getAttributes(value);
        for ( int i = 0; i < attributes.getLength(); i ++) {
            Attr attr = (Attr) attributes.item(i);
            styleAttributes.put(attr.getName(), attr.getValue());
        }
    }

    /**
     * Получить аттрибуты.
     * @param value строка
     * @return аттрибуты
     * @throws ParserConfigurationException ошибка
     * @throws SAXException ошибка
     * @throws IOException ошибка
     */
    public static NamedNodeMap getAttributes(String value)
            throws ParserConfigurationException, SAXException, IOException {
        String xml = "<style " + value  + "></style>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        NodeList nodeList = doc.getElementsByTagName("style");
        Node node = nodeList.item(0);
        return node.getAttributes();
    }

    /**
     * Конструктор стиля, основанного на другом стиле.
     * @param reportStyle    - Базовый стиль, на котором основан вновь создаваемый стиль
     */
    public ReportStyle(ReportStyle reportStyle) {
        // Если стиль не пустой - создать его копию.
        // Иначе - просто создать пустой стиль
        if (reportStyle != null) {
            this.name = reportStyle.getName();
            styleAttributes.clear();
            styleAttributes.putAll(reportStyle.styleAttributes);
        }
    }
    /// <summary>
    /// Возвращает/устанавливает атрибут с именем sKey
    /// </summary>
    /// <param name="sKey">Имя запрашиваемого/устанавливаемого атрибута</param>
    /// <returns>Значение атрибута стиля, если этот атрибут есть.
    /// Если атрибута нет - пустую строку</returns>

    /**
     *  Возвращает атрибут с именем key.
     * @param key   - Имя запрашиваемого/устанавливаемого атрибута
     * @return атрибут с именем key
     */

    public String get(Object key) {
        return styleAttributes.get(key);
    }

    /**
     * Кладёт стиль в хранилище.
     * @param key ключ
     * @param value значение
     * @return значение связанное с ключом
     */
    public String put(String key, String value) {
        return styleAttributes.put(key, value);
    }

    /**
     * Убирает стиль из хранилища.
     * @param key ключ
     * @return значение связанное с ключом
     */
    public String remove(Object key) {
        return styleAttributes.remove(key);
    }

    /**
     * Метод объединяет стиль со стилем, переданным в параметре,
     * приоритетным является стиль параметра, т.е. все совпадающие значения
     * в текущем стиле будут заменены.
     * @param styleName    - Строка, содержащая описание стиля, с которым необходимо объединить текущий стиль
     * @return  - Результирующий стиль
     */
    public ReportStyle merge(String styleName) {

        ReportStyle newStyle;
        // Создать из строки описания стиля сам стиль
        newStyle = new ReportStyle(getName(), styleName);
        // Объединить текущий стиль и созданный и вернуть
        return merge(newStyle);
    }

    /**
     * Метод объединяет стиль со стилем, переданным в параметре,
     * приоритетным является стиль параметра, т.е. все совпадающие значения
     * в текущем стиле будут заменены.
     * @param style    - Стиль, с которым необходимо объединить текущий стиль
     * @return ReportStyle - Результирующий стиль
     */
    public ReportStyle merge(ReportStyle style) {

        // Создать копию текущего стиля
        ReportStyle newStyle = new ReportStyle(this);
        // Добавить новые или заменить атрибуты текущего стиля атрибутами стиля, с которым
        // происходит объединение
        if (style != null) {
            newStyle.styleAttributes.putAll(style.styleAttributes);
        }
        // Вернуть новый стиль
        return newStyle;
    }

    /**
     * Объединяет два стиля. Приоритетным являются значения во втором стиле.
     * @param style         - первый стиль
     * @param mergingStyle  - второй стиль
     * @return ReportStyle - Результирующий стиль
     */
    public static ReportStyle merge(ReportStyle style, ReportStyle mergingStyle) {
        if (style == null) {
            return new ReportStyle(mergingStyle);
        } else {
            return style.merge(mergingStyle);
        }
    }

    /**
     * Проверка наличия аттрибута.
     * @param key ключ
     * @return true если есть аттрибут с таким ключом
     */
    public boolean containsKey(String key) {
        return styleAttributes.containsKey(key);
    }

}