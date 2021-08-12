package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ru.croc.ctp.jxfw.core.reporting.impl.XfwReportProfileImpl;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.resolver.ResourceResolver;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.XmlUtil;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


/**
 * Имплементация для XSL_FO.
 *
 * @author OKrutova
 * @since 1.6
 */
public class XslfoReportProfile extends XfwReportProfileImpl {

    private static final Logger logger = LoggerFactory.getLogger(XslfoReportProfile.class);
    private final ReportClass reportProfile;
    private static Schema schema;

    /**
     * Конструктор.
     *
     * @param resource ресурс из класспаса
     */
    public XslfoReportProfile(Resource resource) {
        super(resource);
        try (InputStream fileInputStream = getStream()) {
            reportProfile = ProfileLoader.loadProfile(fileInputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        return reportProfile.getT();
    }


    // region класс ProfileLoader

    /**
     * Внутренний класс ReportService'a.
     * Осуществляет десериализацию отчета
     */
    protected static final class ProfileLoader {

        private static final String XML_BINDING_TYPE_POSTFIX = "Type";

        /**
         * Конструктор - приватный, ибо все методы статические, объектов данного класса не создается.
         */
        private ProfileLoader() {
        }

        /**
         * Загружает профиль отчета. Валидирует и десериализует его.
         *
         * @param fileInputStream поток файла отчёта
         * @return десериализованный отчет
         *
         * @throws Exception ошибки
         */
        public static ReportClass loadProfile(InputStream fileInputStream) throws Exception {
            String profileAsString = getProfileAsString(fileInputStream);

            //валидация профиля отчета против схемы
            validateUsingValidator(profileAsString);

            Document reportXml = null;
            try {
                reportXml = XmlUtil.createXmlDocument(profileAsString);
            } catch (Exception e) {
                logger.error("Error by create xml document");
            }
            JAXBContext jaxbContext = null;
            Unmarshaller jaxbUnmarshaller = null;
            try {
                jaxbContext = JAXBContext.newInstance(ReportClass.class);
                jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                ReportClass reportClass = (ReportClass) jaxbUnmarshaller.unmarshal(reportXml);
                return reportClass;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ReportException("Error by unmarshaling");
            }
        }

        private static String getProfileAsString(InputStream fileInputStream) throws IOException {
            StringWriter writer = new StringWriter();
            IOUtils.copy(fileInputStream, writer, StandardCharsets.UTF_8);
            return writer.toString();
        }
    }

    /**
     * Провалидировать профиль отчета против xsd.
     * @param profileAsString профиль отчета
     * @throws IOException ошибки
     * @throws SAXException ошибки
     */
    public static void validateUsingValidator(String profileAsString) throws IOException, SAXException {
        Schema schema = getSchema();
        Validator validator = schema.newValidator();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(profileAsString.getBytes(StandardCharsets.UTF_8));
        validator.validate(new StreamSource(inputStream));
    }

    private static synchronized Schema getSchema() throws IOException, SAXException {
        if (schema == null) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new ResourceResolver());
            Resource resource = resolver.getResource("classpath:xsd/Croc.XmlFramework.ReportService.xsd");
            schema = schemaFactory.newSchema(new StreamSource(resource.getInputStream()));
        }

        return schema;
    }

    public ReportClass getReportProfile() {
        return reportProfile;
    }
}
