package ru.croc.ctp.jxfw.reporting.xslfo.impl.util;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Вспомогательные методы при работе с XML.
 * Created by vsavenkov on 07.07.2017.
 *
 * @since jXFW 1.6.0
 */
public class XmlUtil {

    /**
     * Создаёт DOM-документ на основе переданного XML.
     * @param content   - строка, содержащая XML
     * @return Document - возвращает DOM-документ
     * @throws Exception исключения генерятся классами, связанными с DocumentBuilder`ом
     */
    public static Document createXmlDocument(String content) throws Exception {

        ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder =  builderFactory.newDocumentBuilder();
        
        return documentBuilder.parse(stream);
    }
    
    /**
     * Создаёт xml документ.
     * @param inputStream поток, содержащий XML
     * @return w3c.dom.Document
     * @throws Exception при чтении файла или разборе документа
     */
    public static Document createXmlDocument(InputStream inputStream) throws Exception {

        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return createXmlDocument(result.toString());
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
}
