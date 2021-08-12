package ru.croc.ctp.jxfw.reporting.birt.impl;


import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import ru.croc.ctp.jxfw.core.reporting.impl.XfwReportProfileImpl;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * Имплементация для BIRT.
 *
 * @author OKrutova
 * @since 1.6
 */
public class BirtReportProfile extends XfwReportProfileImpl {

    private final Document doc;
    private final XPath xpath = XPathFactory.newInstance().newXPath();

    /**
     * Конструктор.
     *
     * @param resource ресурс из класспаса
     */
    public BirtReportProfile(Resource resource) {
        super(resource);
        try (InputStream inputStream = getStream()) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            doc = dbf.newDocumentBuilder().parse(inputStream);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }


    /**
     * Получается, что при каждом запросе списка отчетов каждый отчет открывается, чтобы найти название.
     * Не очень-то эффективно. Пока оставлю так, хотя не ясно где вообще нужно это название.
     *
     * @return имя отчета
     */
    @Override
    public String getTitle() {
        try {
            XPathExpression expr = xpath.compile("//text-property[@name='title']");
            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (Exception ex) {
            return getName();
        }
    }


}
