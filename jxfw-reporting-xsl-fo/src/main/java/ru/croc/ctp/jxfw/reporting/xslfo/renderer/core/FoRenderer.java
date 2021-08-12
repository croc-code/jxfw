package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.IRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Класс, реализующий IRenderer для рендеринга XSLFO профиля отчета в XSLFO. ;)
 * Created by vsavenkov on 13.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoRenderer implements IRenderer {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(FoRenderer.class);

    /**
     * Метод, который рендерит XSLFO в заданный формат.
     * Формат определяется конкретной реализацией IRenderer
     * @param inputStream       - поток, содержащий XSL-FO, подлежащий экспорту
     * @param outputStream      - поток, в который должен быть выведен результат экспорта
     * @param textEncoding      - кодировка текста во входном и выходном потоках
     * @param cultureInfo       - информация о региональных настройках
     */
    @Override
    public void render(InputStream inputStream, OutputStream outputStream, Charset textEncoding,
                       Locale cultureInfo,OutputFormat format) {

        // прямое копирование входного потока в выходной
        try {
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            logger.error("Error copy stream", e);
        }
    }
}
