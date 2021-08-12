package ru.croc.ctp.jxfw.report.renderer.core.common;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;

import java.io.ByteArrayInputStream;
import javax.xml.stream.XMLStreamException;

/**
 * Тесты Вспомогательного класса для работы с объектом чтения XML-файла.
 * Created by vsavenkov on 23.08.2017.
 */
public class XmlTextReaderTest {

    /**
     * Имя узла источников данных.
     */
    private static final String DATA_SOURCES_NODE_NAME = "data-sources";

    /**
     * Имя узла источника данных.
     */
    private static final String DATA_SOURCE_NODE_NAME = "jxfw-data-source";

    /**
     * Имя пустого узла.
     */
    private static final String EMPTY_NODE_NAME = "common";

    /**
     * Префикс.
     */
    private static final String PREFIX = "r";

    /**
     * Пустой узел.
     */
    private static final String EMPTY_NODE = String.format("<%1$s:%2$s/>", PREFIX, EMPTY_NODE_NAME);


    /**
     * Имя следующего узла.
     */
    private static final String NEXT_NODE_NAME = "next";

    /**
     * Следующий узел.
     */
    private static final String NEXT_NODE = String.format("<%1$s:%2$s/>", PREFIX, NEXT_NODE_NAME);

    /**
     * Имя узла источника данных.
     */
    private static final String NAMESPACE_ATTRIBUTE =
            String.format("xmlns:%1$s=\"http://www.croc.ru/Schemas/XmlFramework/ReportService\"", PREFIX);

    /**
     * Начало узла источника данных.
     */
    private static final String DATA_SOURCE_START = "<" + PREFIX +":" + DATA_SOURCE_NODE_NAME;

    /**
     * Окончание узла источника данных.
     */
    private static final String DATA_SOURCE_ATTRIBUTES =
            String.format("%1$s:class-name=\"ru.croc.ctp.just.datasources.DataSources\" %1$s:n=\"MainData\"", PREFIX);

    /**
     * Содержимое узла источника данных.
     */
    private static final String DATA_SOURCE_CONTENT = String.format("<%1$s:%2$s %3$s/>",
            PREFIX, DATA_SOURCE_NODE_NAME, DATA_SOURCE_ATTRIBUTES);

    /**
     * Результат теста.
     */
    private static final String DATA_SOURCE_NODE = String.format("<%1$s:%2$s %3$s></%1$s:%2$s>",
            PREFIX, DATA_SOURCE_NODE_NAME, DATA_SOURCE_ATTRIBUTES);

    /**
     * Содержимое XML отчёта.
     */
    private static final String CONTENT =
            String.format("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><%1$s:report %1$s:assembly=\"just-reports.jar\" "
                    + "%1$s:class=\"ru.croc.ctp.just.reports.CrimeCaseReport\" %1$s:t=\"Уголовное дело\" %2$s"
                    + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.croc"
                    + ".ru/Schemas/XmlFramework/ReportService Croc.XmlFramework.ReportService.xsd\">"
                    + "<!-- Общие стили -->%3$s<!-- Параметры отчета --><%1$s:params><!-- Id документа -->"
                    + "<%1$s:param %1$s:n=\"DocumentID\" %1$s:vt=\"uuid\" /></%1$s:params><%1$s:%4$s>%5$s%6$s"
                    + "</%1$s:%4$s></%1$s:report>",
                    PREFIX, NAMESPACE_ATTRIBUTE, EMPTY_NODE, DATA_SOURCES_NODE_NAME, DATA_SOURCE_CONTENT, NEXT_NODE);

    /**
     * объект чтения XML.
     */
    private XmlTextReader reader;

    @Before
    public void loadXml() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(CONTENT.getBytes());
        try {
            reader = new XmlTextReader(inputStream);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @After
    public void closeReader() {

        if (null != reader) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMoveToElement() {

        try {
            reader.moveToElement(PREFIX + ":" + DATA_SOURCE_NODE_NAME);
            Assert.assertEquals(PREFIX + ":" + DATA_SOURCE_NODE_NAME, reader.getPrefixedName());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReadOuterXml() {

        try {
            reader.moveToElement(PREFIX + ":" + DATA_SOURCE_NODE_NAME);
            String outerXml = reader.readOuterXml();
            Assert.assertEquals(DATA_SOURCE_NODE, outerXml);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void  testIsEmptyElement() {

        try {
            reader.moveToElement(PREFIX + ":" + EMPTY_NODE_NAME);
            Assert.assertTrue(reader.isEmptyElement());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void  testIsNotEmptyElement() {

        try {
            reader.moveToElement(PREFIX + ":" + DATA_SOURCES_NODE_NAME);
            Assert.assertFalse(reader.isEmptyElement());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void  testSkipToNextElement() {

        try {
            reader.moveToElement(PREFIX + ":" + DATA_SOURCE_NODE_NAME);
            reader.skipToNextElement();
            Assert.assertEquals(PREFIX + ":" + NEXT_NODE_NAME, reader.getPrefixedName());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void  testReadStartElement() {

        try {
            reader.moveToElement(PREFIX + ":" + DATA_SOURCES_NODE_NAME);
            reader.readStartElement();
            Assert.assertEquals(PREFIX + ":" + DATA_SOURCE_NODE_NAME, reader.getPrefixedName());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
