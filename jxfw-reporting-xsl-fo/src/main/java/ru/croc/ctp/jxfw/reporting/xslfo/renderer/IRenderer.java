package ru.croc.ctp.jxfw.reporting.xslfo.renderer;

import ru.croc.ctp.jxfw.core.reporting.OutputFormat;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Created by vsavenkov on 03.04.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public interface IRenderer {

    /**
     * Метод, который рендерит XSLFO в заданный формат.
     * Формат определяется конкретной реализацией IRenderer
     * @param inputStream       - поток, содержащий XSL-FO, подлежащий экспорту
     * @param outputStream      - поток, в который должен быть выведен результат экспорта
     * @param textEncoding      - кодировка текста во входном и выходном потоках
     * @param cultureInfo       - информация о региональных настройках
     * @throws Exception при отрисовик отчёта
     */
    void render(InputStream inputStream,
                OutputStream outputStream,
                Charset textEncoding,
                Locale cultureInfo,
                OutputFormat format) throws Exception;
}
