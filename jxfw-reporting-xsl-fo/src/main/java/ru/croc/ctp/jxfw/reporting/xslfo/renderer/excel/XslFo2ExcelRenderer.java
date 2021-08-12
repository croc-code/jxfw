package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.ISpacesRemover;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.XmlTextReader;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.impl.DefaultSpacesRemover;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.ExcelRowHeightsSetter;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.collection.IntArray;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.focolor.ExcelColors;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.impl.DefaultExcelRowHeightsSetter;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.XslFoEventException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.PageSequenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.logging.AreaLogHelper;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager.ILayoutManager;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.layout.layoutmanager.LayoutManagerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.tree.AreaTreeBuilder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.rendering.excel.ExcelRenderer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.stream.XMLStreamException;

/**
 * Главный класс компонента XslFO2Excel.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("deprecation")
@Component
public class XslFo2ExcelRenderer {

    /**
     * логгер.
     */
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XslFo2ExcelRenderer.class);

    /**
     * слушатели события.
     */
    private List<XslFoEventException> listeners = new ArrayList<>();

    /**
     * Конструктор для того, чтобы погасить исключения при инициализации IntArray.
     *
     * @throws XslFoException - генерируется при инициализации IntArray
     */
    public XslFo2ExcelRenderer() throws XslFoException {
    }

    /**
     * Добавляет слушателя события.
     *
     * @param listener - слушатель
     */
    public void addListener(XslFoEventException listener) {
        listeners.add(listener);
    }

    /**
     * Стратегия установки высоты строк в книге Excel.
     */
    private ExcelRowHeightsSetter heightsSetter;

    /**
     * обработка события, возникающего при исключении внутри компонентов.
     *
     * @param args - параметры события
     */
    private void eventException(XslFoEventArgs args) {
        // Notify everybody that may be interested.
        for (XslFoEventException listener : listeners) {
            listener.eventException(this, args);
        }
    }

    /**
     * Объект компонента Workbook для работы с представлением файла Excel.
     */
    private Workbook excel;

    /**
     * XmlReader, содержащий XSL-FO, подлежащий экспорту.
     */
    private XmlTextReader reader;

    /**
     * Кодировка текста во входном и выходном потоках.
     */
    private Charset textEncoding;

    /**
     * Дерево областей - область со всеми вложенными областями.
     */
    private RootArea rootArea;

    /**
     * Массив координат по горизонтали.
     */
    private IntArray horizontalCoordList = new IntArray(1);

    /**
     * Массив координат по вертикали.
     */
    private IntArray verticalCoordList = new IntArray(1);

    /**
     * Нужно ли делать лог.
     */
    private boolean logging;

    /**
     * Объект политры.
     */
    private ExcelColors excelColors;

    /**
     * Свойство - нужно ли делать лог файл.
     *
     * @return boolean  - возвращает true, если нужно логировать в файл и false в противном случае
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * Свойство - нужно ли делать лог файл.
     *
     * @param isLogging - нужно ли делать лог файл
     */
    public void setLogging(boolean isLogging) {
        logging = isLogging;
    }

    /**
     * Реализация удаления двойных пробелов.
     */
    private ISpacesRemover spacesRemover = new DefaultSpacesRemover();

    /**
     * Инициализация.
     *
     * @param fileFormat - формат файла
     * @throws XslFoException - генерирует в случае ошибки загрузки компонента Aspose.Cells
     */
    private void initialize(int fileFormat) throws XslFoException {

        // Создаем объект компонента Workbook для работы с представлением файла Excel
        excel = new Workbook(fileFormat);

        if (excel == null) {
            throw new XslFoException("Ошибка загрузки компонента Aspose.Cells",
                    "XslFo2ExcelRenderer.initialize");
        }

        // Создаем класс палитры Excel
        excelColors = new ExcelColors(excel);

        // Добавляем пустой временный стиль, нужно для работы функции GetExcelStyle
        excel.getStyles().add();

        // Устанавливаем стиль по умолчанию
        GlobalData.defaultExcelStyle(excel.getStyles().get(0));

        // Очищаем кэш измерений (на всякий случай)
        Dimension.clearCache();
    }

    /**
     * Сохраняем Excel файл.
     *
     * @param outputStream - Поток, в который должен быть выведен результат экспорта. Должен быть проинициализирован
     * @param outputFormat - Формат сохраняемого файла
     * @throws Exception - генерируется сторонними компонентами
     */
    private void save(OutputStream outputStream, int outputFormat) throws Exception {

        if (excel != null) {
            // Сохраняем файл в поток
            excel.save(outputStream, outputFormat);
            outputStream.flush();

            // Освобождаем объект Excel
            excel = null;
        }
    }


    /**
     * Непосредственный запуск процесса преобразования из XSL-FO в формат Excel 2000.
     * Объект вычитки XML файла должен быть проинициализирован и установлен на элемент с данными.
     *
     * @throws Exception - генерируется при работе с потоком
     */
    private void executeProcessing() throws Exception {

        // Создаем коренной узел
        rootArea = new RootArea();

        // Строим дерево областей - область со всеми вложенными областями
        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "Before buildAreaTree : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));
        }

        AreaTreeBuilder areaTreeBuilder = getAreaTreeBuilder();
        areaTreeBuilder.buildAreaTree();
        // Очищаем кэш измерений
        Dimension.clearCache();
        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "After buildAreaTree : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));

        }

        // Обрабатываем Root область
        processRootArea(rootArea, excel.getWorksheets().get(0));
    }

    public AreaTreeBuilder getAreaTreeBuilder() throws XslFoException {
        AreaTreeBuilder areaTreeBuilder = new AreaTreeBuilder(reader, rootArea);
        areaTreeBuilder.setSpacesRemover(spacesRemover);
        return areaTreeBuilder;
    }

    /**
     * Обработка главной области.
     *
     * @param rootArea  - Главная область
     * @param worksheet - Лист Excel
     * @throws XslFoException - генерируется при отсутствии подчинённых областей
     */
    private void processRootArea(RootArea rootArea, Worksheet worksheet) throws XslFoException {

        if (rootArea.getChildrenList().size() == 0) {
            String exceptionString = "Элементы <fo:page-sequence> не найдены!";
            throw new XslFoException(String.format(GlobalData.getCultureInfo(), GlobalData.EXCEPTION_STRING_SIMPLE,
                    exceptionString), "XslFo2ExcelRenderer.processRootArea");
        }

        // Получаем первое описание свойств страницы
        PageSequenceArea pageSequenceArea = (PageSequenceArea) rootArea.getChildrenList().get(0);

        // Получаем ссылку на класс, содержащий параметры страницы
        SimplePageMasterArea simplePageMasterArea = (SimplePageMasterArea) rootArea.getLayoutMasterSet().get(
                pageSequenceArea.getPropertyValue(FoPropertyType.MASTER_REFERENCE));

        // Устанавливаем ширину корневой области
        setRootWidth(rootArea, simplePageMasterArea);

        // Получаем нужный менеджер построения разметки контента
        ILayoutManager layoutManager = LayoutManagerFactory.getInstance(rootArea, simplePageMasterArea);

        // Производим построение разметки контента
        layoutManager.doLayout(rootArea, horizontalCoordList, verticalCoordList);

        // Выводим области на лист Excel
        ExcelRenderer excelRenderer = new ExcelRenderer(excel, excelColors);

        // Выставляем свойства страницы
        excelRenderer.setPageSetup(simplePageMasterArea, worksheet);

        // Установка колонтитулов
        excelRenderer.setCatchword(pageSequenceArea, worksheet);
        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "Before renderToExcel : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));
        }

        excelRenderer.renderToExcel(worksheet, rootArea, horizontalCoordList, verticalCoordList);

        if (logger.isDebugEnabled()) {
            logWithTimestamp(String.format(GlobalData.getCultureInfo(),
                    "After renderToExcel : %1$d kb", Runtime.getRuntime().totalMemory() / 1024));

        }
    }

    /**
     * Установка ширины области элемента &lt;fo:root&gt; c учетом количества колонок.
     *
     * @param rootArea             - Экземпляр области SimplePageMasterArea
     * @param simplePageMasterArea - Ширина области элемента &lt;fo:root&gt;
     */
    private void setRootWidth(RootArea rootArea, SimplePageMasterArea simplePageMasterArea) {

        //////////////////////////////////////////////////////////////////////////
        // 21.08.2007 DKL Ошибка: Инцидент 257988
        // Устанавливаем ширину корневой области исходя из ориентации листа
        // Так как Excel поддерживает только portrait/landscape ориентацию, было принято трактовать
        // значения атрибута reference-orientation отличные от 0 как landscape режим
        int resultWidth = (simplePageMasterArea.getReferenceOrientation() == 0)
                ? simplePageMasterArea.getPageContentWidth() : simplePageMasterArea.getPageContentHeight();

        //
        //////////////////////////////////////////////////////////////////////////

        // Сумма ширин расстояний между колонками
        int totalColumnGapSum = (rootArea.getColumnCount() - 1) * rootArea.getColumnGap();

        // Получаем ширину области элемента <fo:root> c учетом количества колонок
        if (totalColumnGapSum < resultWidth) {
            // Сумма значений column-gap не превысила ширину страницы
            resultWidth = (resultWidth - totalColumnGapSum) / rootArea.getColumnCount();
        } else {
            // Сумма значений column-gap превысила ширину страницы
            // Устанавливаем количество колонок = 1
            rootArea.setColumnCount(1);
        }

        rootArea.getBorderRectangle().setWidth(resultWidth);
    }

    /**
     * Заканчиваем процесс записи.
     *
     * @throws XslFoException     - генерируется IntArray`ем
     * @throws XMLStreamException - генерируется XMLEventReader`ом
     */
    private void uninitialize() throws XslFoException, XMLStreamException {
        // Закрываем объект чтения
        if (reader != null) {
            reader.close();
        }

        // Удаляем дерево
        rootArea = null;

        // Удаляем массивы координат
        horizontalCoordList = new IntArray(1);
        verticalCoordList = new IntArray(1);

        if (logger.isDebugEnabled()) {
            // Собираем мусор
            Runtime.getRuntime().gc();
        }
    }

    /**
     * Основной метод компонента - запуск процесса преобразования из XSL-FO в формат Excel 2000.
     *
     * @param inputXmlReader - XmlReader, содержащий XSL-FO, подлежащий экспорту. Должен быть проинициализирован
     * @param outputStream   - Поток, в который должен быть выведен результат экспорта. Должен быть проинициализирован
     * @param textEncoding   - Кодировка текста во входном и выходном потоках. Должна быть задана
     * @param cultureInfo    - Информация о региональных настройках. Должна быть задана
     * @throws Exception - генерируется в случае некорректных параметров
     */
    public void render(InputStream inputXmlReader, OutputStream outputStream, Charset textEncoding,
                       Locale cultureInfo, OutputFormat format) throws Exception {
        try {
            if (inputXmlReader == null) {
                throw new XslFoException("Не задан объект чтения XML файла!", "XslFo2ExcelRenderer.render");
            }

            if (outputStream == null) {
                throw new XslFoException("Не задан объект потока записи результирующего файла!",
                        "XslFo2ExcelRenderer.render");
            }

            if (textEncoding == null) {
                throw new XslFoException("Не задана кодировка текста во входном и выходном потоках!",
                        "XslFo2ExcelRenderer.render");
            }

            if (cultureInfo == null) {
                throw new XslFoException("Не задана информация о региональных настройках!",
                        "XslFo2ExcelRenderer.render");
            }

            this.reader = new XmlTextReader(inputXmlReader);
            this.textEncoding = textEncoding;

            // Инициализирует информацию о региональных настройках.
            GlobalData.setCultureInfo(cultureInfo);

            // Инициализируем объект чтения XML-файла
            // TODO: не знаю как это перетаскивать, попробуем без
            //reader.setWhitespaceHandling(WhitespaceHandling.None);
            //reader.MoveToContent();

            int outputFormat = SaveFormat.EXCEL_97_TO_2003;
            switch (format) {
                case EXCEL2010:
                    outputFormat = SaveFormat.XLSX;
                    break;
            }

            // Инициализация
            initialize(outputFormat);

            // Запускаем обработку элементов
            executeProcessing();

            //установить высоту строк
            resizeRows();

            // Сохраняем разультаты
            save(outputStream, outputFormat);
        } catch (Exception ex) {
            String message;
            /* TODO: не знаю как это в Java перетащить
            if (ex.InnerException != null) {
                sMessage = String.format(GlobalData.getCultureInfo(),
                        "Ошибка: {0} Вложенное исключение: {1} Источник: {2}",
                        ex.Message, ex.InnerException.Message, ex.Source);
            } else {
            */
            message = String.format(GlobalData.getCultureInfo(), "Ошибка: %1$s",
                    ex.getMessage());
            //}

            // Вызываем обработчик подписчика события
            if (0 < listeners.size()) {
                XslFoEventArgs args = new XslFoEventArgs(message);
                eventException(args);
            }

            // Записываем в журнал
            logger.debug(message);

            /* TODO: не знаю как это в Java перетащить
#if !ADD_LOGGING
            */
            throw ex;
            /* TODO: не знаю как это в Java перетащить
#endif
            */
        } finally {
            // Заканчиваем процесс преобразования
            uninitialize();
        }
    }

    private void resizeRows() throws Exception {
        if (excel != null) {
            ExcelRowHeightsSetter rowHeightsSetter = getHeightsSetter();
            if (rowHeightsSetter == null) {
                rowHeightsSetter = new DefaultExcelRowHeightsSetter();
            }
            rowHeightsSetter.setHeightsOfRows(excel);
        }
    }

    /**
     * Логирование записи c форматом '[ВРЕМЯ]   :ЗНАЧЕНИЕ'.
     *
     * @param value - логируемая запись
     */
    private void logWithTimestamp(String value) {
        value = AreaLogHelper.getValueWithTimestampForLog(value);
        logger.debug(value);
    }

    @Autowired(required = false)
    public void setSpacesRemover(ISpacesRemover spacesRemover) {
        this.spacesRemover = spacesRemover;
    }

    public ExcelRowHeightsSetter getHeightsSetter() {
        return heightsSetter;
    }

    @Autowired(required = false)
    public void setHeightsSetter(ExcelRowHeightsSetter heightsSetter) {
        this.heightsSetter = heightsSetter;
    }
}
