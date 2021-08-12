package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.IRenderer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Класс, осуществляющий рендеринг XSL-FO в Excel.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@Component
public class MsExcelRenderer implements IRenderer {

    private XslFo2ExcelRenderer xslFo2ExcelRenderer;

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(MsExcelRenderer.class);

    /**
     * Метод, который рендерит XSLFO в заданный формат.
     * Формат определяется конкретной реализацией IRenderer
     * @param inputStream   - поток, содержащий XSL-FO, подлежащий экспорту
     * @param outputStream  - поток, в который должен быть выведен результат экспорта
     * @param textEncoding  - кодировка текста в выходном потоке (не используется в Excel)
     * @param cultureInfo   - информация о региональных настройках
     */
    @Override
    public void render(InputStream inputStream, OutputStream outputStream, Charset textEncoding,
                       Locale cultureInfo, OutputFormat format) {

        // проверка на то, что входной поток не пустой и открыт
        if (null == inputStream) {
            throw new ArgumentNullException("Не возможно произвести чтение из потока с XSL-FO.");
        }

        // Проверка на культуру
        if (cultureInfo == null) {
            cultureInfo = Locale.getDefault();
        }

        // Создаем объект класса XmlReader для чтения исходного XML-FO файла
        try {
            xslFo2ExcelRenderer.render(inputStream, outputStream, textEncoding, cultureInfo, format);
        } catch (Exception e) {
            String message = "Error render xsl-fo to excel.";
            logger.error(message, e);
            throw new ReportException(message, e);
        }
    }

    @Autowired
    public void setXslFo2ExcelRenderer(XslFo2ExcelRenderer xslFo2ExcelRenderer) {
        this.xslFo2ExcelRenderer = xslFo2ExcelRenderer;
    }

}
