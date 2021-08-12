package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Класс с серверными функциями, внедряемыми в xsl-t шаблон.
 * Created by vsavenkov on 04.04.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class Calculator {

    private static final Logger logger = LoggerFactory.getLogger(Calculator.class);

    /**
    * Возвращает xml со списком возможных форматов рендеринга (за искл. самого HTML).
    * @param outputFormats спискок доступных форматов
    * @return XmlElement
    */
    public static Node outputFormats(String [] outputFormats) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        try {
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            ByteArrayInputStream mainIs = new ByteArrayInputStream("<outputFormats/>".getBytes("UTF-8"));
            Document document = documentBuilder.parse(mainIs);
            if (outputFormats != null) {
                for (String format : outputFormats) {
                    if (!"HTML".equalsIgnoreCase(format)) {
                        Element xmlNode = document.createElement("format");
                        xmlNode.setAttribute("n", format);
                        document.getDocumentElement().appendChild(xmlNode);
                    }
                }
            }
            Element returnValue = document.getDocumentElement();
            return returnValue;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error("Error by creating output formats",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Используется при рендеринге xsl-fo в html для вставки значения по-умолчанию в атрибут src.
     * @return  значение по-умолчанию в атрибута src html-элемента embed
     */
    public static String getEmbedSvgSourceString() {
        // пока пустая заглушка
        return "x-get-report.aspx?data=";
    }
}
