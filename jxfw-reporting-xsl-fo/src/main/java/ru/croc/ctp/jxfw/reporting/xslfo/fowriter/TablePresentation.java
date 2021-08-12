package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AlignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ValignClass;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 * Created by vsavenkov on 14.03.2017.. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public abstract class TablePresentation {

    private static final Logger logger = LoggerFactory.getLogger(TablePresentation.class);

    private static final String SPACE_BEFORE = "space-before";

    /**
     * Конструктор класса.
     * @param showHeader       - Признак того, требуется ли показывать заголовок.
     *                          Имеет смысл только для нетранспонированных таблиц.
     * @param tableStyle        - Стиль таблицы
     * @param headerRowStyle    - Стиль строк заголовка таблицы
     * @param xmlStreamWriter        - Поток, в котором надо формировать таблицу
     */
    public TablePresentation(boolean showHeader,
                             ReportStyle tableStyle,
                             ReportStyle headerRowStyle,
                             XMLStreamWriter xmlStreamWriter) {

        String savedSpaceBefore = null;

        // Стиль строк заголовка таблицы
        this.headerRowStyle = headerRowStyle;
        //объект формирования XML-представления
        this.xmlStreamWriter = xmlStreamWriter;
        //Список для ведения идентификаторов колонок
        columnHandlers = new ArrayList<>();
        //Сразу же сформируем корень дерева колонок: он будет с идентификатором 0.
        columnHandlers.add(CColumnDescription.getRoot());

        //Сохраним нужные параметры:
        this.showHeader = showHeader;
        try {
            // Хитрости:
            // 1. fo:table не поимает space-before, и при экспорте в PDF указанный пробел будет не виден.
            // Поэтому делаю fo:block для установки пространства до таблицы
            if (tableStyle != null && tableStyle.containsKey(SPACE_BEFORE)) {
                // Добавить блок
                this.xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_BLOCK_IN,
                        XslFoProfileWriter.XSLFO_NAMESPACE);
                // В него поместить нужный space-before
                savedSpaceBefore = tableStyle.get(SPACE_BEFORE);
                this.xmlStreamWriter.writeAttribute(SPACE_BEFORE, savedSpaceBefore);
                // Убрать из стиля space-before
                tableStyle.remove(SPACE_BEFORE);
                // Закрыть блок
                this.xmlStreamWriter.writeEndElement();
            }
            //Начнем с того, что создадим корневой элемент - таблицу.
            this.xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLE_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);

            ReportStyle.writeToXml(this.xmlStreamWriter, tableStyle);
        } catch (XMLStreamException e) {
            logger.error("Error by writing xml", e);
        }
        //Проставим начальные значения:
        tableState = TableState.START;

        // Возвращаем в стиль таблицы space-before
        if (savedSpaceBefore != null) {
            tableStyle.put(SPACE_BEFORE, savedSpaceBefore);
        }
    }

    /**
     * Добавление новой колонки к заголовку таблицы.
     * @param strCaption        - Содержимое заголовка
     * @param enAlign           - Выравнивание содержимого колонки
     * @param enVAlign          - Вертикальное выравнивание содержимого колонки
     * @param reportStyle            - Стиль колонки (не ячейки!!!)
     * @param headerStyle      - Стиль ячеек заголовка колонки
     * @param strColumnWidth    - Ширина
     * @param enHeaderAlign     - Выравнивание заголовка
     * @param enHeaderVAlign    - Вертикальное выравнивание заголовка
     * @param parentColumnDescriptor           - Дескриптор родительской колонки - или 0 для верхнего уровня
     * @return int  - Дескриптор созданной колонки
     */
    public int addColumn(String strCaption,
                         AlignClass enAlign,
                         ValignClass enVAlign,
                         ReportStyle reportStyle,
                         ReportStyle headerStyle,
                         String strColumnWidth,
                         AlignClass enHeaderAlign,
                         ValignClass enHeaderVAlign,
                         int parentColumnDescriptor) {
        //Режим формирования заголовка
        tableState = TableState.HEADER;

        //Подсчет числа колонок ниженго уровня
        if (parentColumnDescriptor == 0) {
            columnsNumber++;
        } else if (columnHandlers.get(parentColumnDescriptor).getIsLeaf()) {
            columnsNumber++;
        }

        columnHandlers.add(new CColumnDescription(strCaption,
                enAlign,
                enVAlign,
                reportStyle,
                headerStyle,
                strColumnWidth,
                enHeaderAlign,
                enHeaderVAlign,
                columnHandlers.get(parentColumnDescriptor)));
        return columnHandlers.size() - 1;
    }

    /**
     * Увеличивает счетчик колонок.
     */
    public void omitCell() {
        cellCounter += 1;
    }

    /**
     * Число колонок нижнего уровня.
     * @return число колонок нижнего уровня
     */
    public int getColumnsNumber() {
        return columnsNumber;
    }

    /**
     * Ряд.
     *
     * @return Номер текущего ряда.
     */
    public int getCurrentRow() {
        return rowCounter;
    }

    /**
     * Начинает строку таблицы.
     * @param reportStyle    - Стиль строки
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void startRow(ReportStyle reportStyle) throws XMLStreamException;

    /**
     * Закрываем ряд.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void endRow() throws XMLStreamException;

    /**
     * Добавляет ячейку внутрь ряда.
     * @param cellData      - Данные ячейки
     * @param colSpan       - Количество объединяемых столбцов
     * @param rowSpan       - Количество объединяемых строк
     * @param reportStyle   - Стиль
     * @param dataType      - Тип данных ячейки(int, string, datetime ...)
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void addCell(Object cellData,
                                 int colSpan,
                                 int rowSpan,
                                 ReportStyle reportStyle,
                                 String dataType) throws XMLStreamException;

    /**
     * Начать подвал.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void startFooter() throws XMLStreamException;

    /**
     * Окончание формирования подвала.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void endFooter() throws XMLStreamException;

    /**
     * Для вывода сырых данных во время формирования таблицы.
     * @param data   - Выводимые данные
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void rawOutput(String data) throws XMLStreamException;

    /**
     * Завершение формирования таблицы.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public abstract void endTable() throws XMLStreamException;

    /**
     * Перечисление состояний формирования таблицы.
     */
    protected enum TableState {
        /**
         * Начало формирования.
         */
        START,
        /**
         * Конец формирования.
         */
        END,
        /**
         * Заголовок.
         */
        HEADER,
        /**
         * Тело.
         */
        BODY,
        /**
         * Подвал.
         */
        FOOTER
    }

    /**
     * Формирователь XML-представления.
     */
    protected XMLStreamWriter xmlStreamWriter;

    /**
     * Признак того, что надо показывать заголовок.
     */
    protected boolean showHeader;

    /**
     * Состояние процесса формирования таблицы.
     */
    protected TableState tableState;

    /**
     * Список для управления идентификаторами колонок.
     */
    protected List<CColumnDescription> columnHandlers;

    /**
     * Счетчик числа рядов в таблице.
     */
    protected int rowCounter;

    /**
     * Счетчик колонок в строке.
     */
    protected int cellCounter;

    /**
     * Число колонок нижнего уровня.
     */
    private int columnsNumber;

    /**
     * Стиль строк заголовка таблицы.
     */
    protected ReportStyle headerRowStyle;
}