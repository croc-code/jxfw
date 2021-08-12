package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import org.junit.Test;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.ColumnBuilder;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Демонстрация инициализации и способа проверки парсера
 */
public class XmlTextReaderTest {

    @Test
    public void createXmlTextReader() throws XMLStreamException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XslFoProfileWriter writer = new XslFoProfileWriter(outputStream, null);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        XmlTextReader xmlTextReader = new XmlTextReader(inputStream);
    }

    @Test
    public void addSomeInfoToXmlTextReader() throws XMLStreamException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XslFoProfileWriter writer = new XslFoProfileWriter(outputStream, null);
        writer.tableStart();
        writer.tableRowStart();
        writer.tableAddColumn(ColumnBuilder.create().setCaption("CAPTION"));
        writer.tableRowEnd();
        writer.tableEnd();

        writer.flush();

        System.err.println(outputStream.toString("UTF-8"));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        XmlTextReader xmlTextReader = new XmlTextReader(inputStream);

        //System.out.println(xmlTextReader.hasChildren());


        while (xmlTextReader.hasNext()) {
            XMLEvent event = xmlTextReader.peek();
            // System.out.println(event);
            xmlTextReader.skip();
        }

    }
}
