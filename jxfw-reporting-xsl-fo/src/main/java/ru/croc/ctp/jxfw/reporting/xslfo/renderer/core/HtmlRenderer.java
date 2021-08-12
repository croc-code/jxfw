package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.IRenderer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Класс, реализующий IRenderer для рендеринга XSLFO профиля отчета в HTML.
 * Created by vsavenkov on 03.04.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class HtmlRenderer implements IRenderer {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(HtmlRenderer.class);

    /**
     * Шаблон.
     */
    public static final String X_FO2HTML_XSL = "x-fo2html.xsl";


    private final XfwReportProfileManager profileManager;

    public HtmlRenderer(XfwReportProfileManager profileManager) {
        this.profileManager = profileManager;
    }


    @Override
    public void render(InputStream inputStream,
                       OutputStream outputStream,
                       Charset textEncoding,
                       Locale cultureInfo, OutputFormat format) throws Exception {

        try (InputStream fileTemplate = profileManager.getResource(X_FO2HTML_XSL).getInputStream()) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(fileTemplate));
            //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            //transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            //устанавливаеам параметр с доступными форматами для данного отчёта

            /*FIXME JXFW-1092  outputFormatsAsString не используется в трансформации
            String outputFormatsAsString = StringUtils.join(outputFormats, ",");
            transformer.setParameter("outputFormatsAsString", outputFormatsAsString);*/

            transformer.setOutputProperty(OutputKeys.ENCODING, textEncoding.name());
            transformer.transform(new StreamSource(inputStream), new StreamResult(outputStream));


            outputStream.flush();
        } catch (Exception e) {
            logger.error("Произошла ошибка при попытке рендеринга xml в html ", e);
            throw e;
        }
    }

}
