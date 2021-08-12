package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

public class XslfoReportProfileTest {

    @Test(expected = SAXException.class)
    public void validateReportProfileAlien() throws IOException, SAXException {
        String xmlRecords =
                "<data>" +
                        " <employee>" +
                        "   <name>John</name>" +
                        "   <title>Manager</title>" +
                        " </employee>" +
                        " <employee>" +
                        "   <name>Sara</name>" +
                        "   <title>Clerk</title>" +
                        " </employee>" +
                        "</data>";

        XslfoReportProfile.validateUsingValidator(xmlRecords);
    }

    @Test
    public void validateReportProfile() throws IOException, SAXException {
        String xmlRecords =
                "<r:report\n" +
                        "        r:t=\"Отчет для проверки тегов\"\n" +
                        "        xmlns:r=\"http://www.croc.ru/Schemas/XmlFramework/ReportService\"\n" +
                        "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "        xsi:schemaLocation=\"http://www.croc.ru/Schemas/XmlFramework/ReportService Croc.XmlFramework.ReportService.xsd\"\n" +
                        ">\n" +
                        "    <r:common>\n" +
                        "        <r:style-class r:n=\"RED\">background-color=\"#cc0000\"</r:style-class>\n" +
                        "        <r:style-class r:n=\"GREEN\">background-color=\"#009933\"</r:style-class>\n" +
                        "        <r:style-class r:n=\"BLUE\">background-color=\"#0033cc\"</r:style-class>\n" +
                        "    </r:common>\n" +
                        "\n" +
                        "</r:report>";

        XslfoReportProfile.validateUsingValidator(xmlRecords);
    }
}
