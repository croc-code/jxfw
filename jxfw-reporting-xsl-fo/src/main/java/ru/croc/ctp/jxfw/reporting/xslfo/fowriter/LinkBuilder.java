package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Строитель ссылок.
 *
 * @author PaNovikov
 * @since 05.07.2017.
 */
public class LinkBuilder {

    private static final String EXTERNAL_DESTINATION = "external-destination";
    private static final String INTERNAL_DESTINATION = "internal-destination";
    private static final String TITLE = "title";
    private static final String CLASS = "class";
    /**
     * Содержание ссылки.
     */
    protected String content = "ссылка";
    /**
     * Внешняя ссылка.
     */
    protected String externalDestination;
    /**
     * Внутреннняя ссылка.
     */
    protected String internalDestination;
    /**
     * Css класс.
     */
    protected String cssClass = StringUtils.EMPTY;
    /**
     * Подсказка.
     */
    protected String title = "перейти";

    private static final String ILLEGAL_STATE_DESTINATION_MESSAGE = "Can not be external and internal destination";

    /**
     * Получить объект класса.
     * @param content текст ссылки
     * @return объект класса
     */
    public static LinkBuilder create(String content) {
        LinkBuilder linkBuilder = new LinkBuilder();
        if (StringUtils.isNotEmpty(content)) {
            linkBuilder.content = content;
        }
        return linkBuilder;
    }

    /**
     * Утсановить внешнюю ссылку.
     * @param externalDestination внешняя ссылка
     * @return linkBuilder
     */
    public LinkBuilder setExternalDestination(String externalDestination) {
        if (StringUtils.isNotEmpty(internalDestination)) {
            throw new IllegalStateException(ILLEGAL_STATE_DESTINATION_MESSAGE);
        }
        this.externalDestination = externalDestination;
        return this;
    }

    /**
     * Утсановить внешнюю ссылку в пределах приложения.
     * @param appDestination внешняя ссылка
     * @return linkBuilder
     */
    public LinkBuilder setAppDestination(String appDestination) {
        return setExternalDestination("#" + appDestination);
    }

    /**
     * Установить внутренню ссылку.
     * @param internalDestination внутрення ссылка
     * @return linkBuilder
     */
    public LinkBuilder setInternalDestination(String internalDestination) {
        if (StringUtils.isNotEmpty(externalDestination)) {
            throw new IllegalStateException(ILLEGAL_STATE_DESTINATION_MESSAGE);
        }
        this.internalDestination = internalDestination;
        return this;
    }

    /**
     * Установить подсказку.
     * @param title подсказка
     * @return linkBuilder
     */
    public LinkBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Установить css class.
     * @param cssClass css класс
     * @return linkBuilder
     */
    public LinkBuilder setCssClass(String cssClass) {
        this.cssClass = cssClass;
        return this;
    }

    /**
     * Отрисовка ссылки.
     * @param xmlStreamWriter отрисовщик xml
     */
    protected void writeLink(XMLStreamWriter xmlStreamWriter) {
        try {
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX,
                    XslFoProfileWriter.XSLFO_LINK_ELEMENT,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            if (StringUtils.isNotEmpty(externalDestination)) {
                xmlStreamWriter.writeAttribute(LinkBuilder.EXTERNAL_DESTINATION,
                        "url('" + externalDestination + "')");
            }
            if (StringUtils.isNotEmpty(internalDestination)) {
                xmlStreamWriter.writeAttribute(LinkBuilder.INTERNAL_DESTINATION, internalDestination);
            }
            xmlStreamWriter.writeAttribute(LinkBuilder.TITLE, title);
            xmlStreamWriter.writeAttribute(LinkBuilder.CLASS, cssClass);
            xmlStreamWriter.writeCharacters(content);
            xmlStreamWriter.writeEndElement();
        } catch (XMLStreamException e) {
            throw new ReportException("Error by writing link", e);
        }
    }

}