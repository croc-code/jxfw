package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.IRenderer;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.XslFoCulture;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.XslFoTreeBuilder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.WordRenderer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * Основной класс компонента. Реализует метод <c>render()</c>
 * Created by vsavenkov on 22.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@Component
public class XslFo2WordRenderer implements IRenderer {

    /**
     * логгер.
     */
    private static final Logger logger = LoggerFactory.getLogger(XslFo2WordRenderer.class);

    /**
     * Запуск процесса преобразования из XSL-FO в формат Word.
     *
     * @param inputXmlTextReader - XmlTextReader с XSL-FO
     * @param cultureInfo        - информация о региональных настройках
     * @return com.aspose.words.Document
     * @throws Exception - генерится XMLStreamReader`ом
     */
    public Document render(XmlTextReader inputXmlTextReader, Locale cultureInfo) throws Exception {

        if (inputXmlTextReader == null) {
            throw new ArgumentNullException("inputXmlTextReader");
        }

        // Инициализация настройки CultureInfo для отчета
        XslFoCulture.setCultureInfo(cultureInfo);
        //Построение дерева объектов
        builder.buildTree(inputXmlTextReader);
        wordRenderer.createDocument(builder.getArea());

        return wordRenderer.getWordDocument();
    }

    // Создание объектов Aspose.Words
    private WordRenderer wordRenderer;
    private XslFoTreeBuilder builder;

    /**
     * Основной метод компонента - запуск процесса преобразования из XSL-FO в формат Word.
     *
     * @param inputStream  - поток, содержащий XSL-FO, подлежащий экспорту
     * @param outputStream - поток, в который должен быть выведен результат экспорта
     * @param textEncoding - кодировка текста в выходном потоке (не используется в Word)
     * @param cultureInfo  - информация о региональных настройках
     */
    @Override
    public void render(InputStream inputStream, OutputStream outputStream, Charset textEncoding,
                       Locale cultureInfo, OutputFormat format) {

        // проверка на то, что входной поток не пустой и открыт
        if (inputStream == null) {
            throw new ArgumentNullException("inputStream");
        }
        /* TODO: а оно надо?
        if (!inputStream.CanRead)
            throw new ArgumentException("Не возможно произвести чтение из потока с XSL-FO.");
        if (inputStream.Length == 0)
            throw new ArgumentException("Передан пустой поток с XSL-FO.");
        */
        if (outputStream == null) {
            throw new ArgumentNullException("outputStream");
        }
        /* TODO: а оно надо?
        if (!outputStream.CanWrite)
            throw new ArgumentException("Не возможно произвести запись в потока с XSL-FO.");
        */

        int outputFormat = SaveFormat.DOC;
        switch (format) {
            case PDF:
                outputFormat = SaveFormat.PDF;
                break;
            case XPS:
                outputFormat = SaveFormat.XPS;
                break;
        }

        // Создаем объект класса XmlTextReader для чтения исходного XML-FO файла
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XmlTextReader streamReader = null;
        try {
            streamReader = new XmlTextReader(inputStream);
            Document doc = render(streamReader, cultureInfo);
            // Вызываем шаманскую функцию, которая делает ХОРОШО :)
            if (outputFormat == SaveFormat.PDF || outputFormat == SaveFormat.XPS) {
                doc.updateTableLayout();
            }
            doc.save(outputStream, outputFormat);
            outputStream.flush();
        } catch (Exception e) {
            String message = "Error render xsl-fo to word.";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        } finally {
            if (null != streamReader) {
                try {
                    streamReader.close();
                } catch (XMLStreamException e) {
                    logger.error("Error close stream reader.", e);
                }
            }
        }
    }

    @Autowired
    public void setBuilder(XslFoTreeBuilder builder) {
        this.builder = builder;
    }

    @Autowired
    public void setWordRenderer(WordRenderer wordRenderer) {
        this.wordRenderer = wordRenderer;
    }

    //endregion
}
