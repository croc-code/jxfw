package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Класс, выполняющий формирование талицы. Служит для структурирования ReportWriter'а.
 * Created by vsavenkov on 24.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class TransponedTable extends TablePresentation {
    private static final Logger logger = LoggerFactory.getLogger(TransponedTable.class);

    /**
     * Конструктор класса.
     * @param showHeader        - Признак того, требуется ли показывать заголовок.
     *                          Имеет смысл только для нетранспонированных таблиц.
     * @param tableStyle        - Стиль, использующийся для отрисовки таблицы
     * @param headerRowStyle    - Стиль, использующийся для отрисовки заголовка таблицы
     * @param xmlStreamWriter   - Поток, в котором надо формировать таблицу
     */
    public TransponedTable(boolean showHeader, ReportStyle tableStyle, ReportStyle headerRowStyle, XMLStreamWriter
            xmlStreamWriter) {
        super(showHeader, tableStyle, headerRowStyle, xmlStreamWriter);

        //Для транспонированной таблицы понадобится еще коллекция рядов...
        rows = new Hashtable<>();
        rowStyles = new ArrayList<>();
    }

    /**
     * Начинает строку таблицы.
     * @param reportStyle    - Имя класса, назначаемого строке
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void startRow(ReportStyle reportStyle) throws XMLStreamException {

        if (tableState == TableState.HEADER) {
            pasteColumnsGroup(headerRowStyle);
        }

        if (tableState == TableState.FOOTER) {
            //ряд в нетранспонируемый подвал.
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_ROW_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            ReportStyle.writeToXml(xmlStreamWriter, reportStyle);
        } else {
            //Новое состояние - формирование тела таблицы
            tableState = TableState.BODY;
        }

        rowStyles.add(rowCounter, reportStyle);
        //начали новый ряд
        rowCounter++;
        cellCounter = 0; //колонок в нем еще нет...
    }

    /**
     * Закрываем ряд.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void endRow() throws XMLStreamException {

        //Закроем элемент ряда.
        if (tableState == TableState.FOOTER) {
            xmlStreamWriter.writeEndElement();
        } else {
            //сброс табличного ряда...
            for (int i = 0; i < spanMap.length; i++) {
                spanMap[i]--;
            }
        }
    }

    private void addHeaderCell(Object data, int colSpan, int rowSpan, ReportStyle cellsStyle,
                               ReportStyle headerCellStyle, String dataType) throws XMLStreamException {

        //ищем свободное место:
        int freeSpacePosition; //индекс, куда будем ячейку вставлять
        for (freeSpacePosition = cellCounter; (freeSpacePosition < spanMap.length)
                && (spanMap[freeSpacePosition] != 0); freeSpacePosition++) {}
        //если дырку не нашли - кидаем эксепшен.
        if (freeSpacePosition == spanMap.length) {
            throw new RuntimeException("Нет свободного места в ряду!");
        }

        if (!rows.containsKey(freeSpacePosition)) {
            //Нет соответствующего ряда. Надо добавить.
            rows.put(freeSpacePosition, new RowPresentation(cellsStyle));
            headerCellStyle = ReportStyle.merge(rowStyles.get(0), headerCellStyle);
        }
        //Теперь в этот ряд надо добавить ячейку:
        rows.get(freeSpacePosition).addCell(data, colSpan, rowSpan, headerCellStyle, dataType);
        //отмечаем все ячейки с текущей и на весь rowspan как занятые:
        for (cellCounter = freeSpacePosition;
             cellCounter < Math.min(spanMap.length, freeSpacePosition + colSpan); cellCounter++) {
            spanMap[cellCounter] = rowSpan;
        }
    }

    /**
     * Добавляет ячейку внутрь ряда.
     * @param cellData     - Данные ячейки
     * @param colSpan      - Количество объединяемых столбцов
     * @param rowSpan      - Количество объединяемых строк
     * @param reportStyle  - Стиль
     * @param dataType  - Тип данных ячейки(int, string, datetime ...)
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void addCell(Object cellData, int colSpan, int rowSpan, ReportStyle reportStyle, String dataType)
            throws XMLStreamException {

        if (tableState == TableState.FOOTER) {
            //для нетранспонированной таблицы

            if (colSpan > 1) {
                reportStyle.put(XslFoProfileWriter.XML_ATTRIBUTE_COLSPAN, String.valueOf(colSpan));
            }

            if (rowSpan > 1) {
                reportStyle.put(XslFoProfileWriter.XML_ATTRIBUTE_ROWSPAN, String.valueOf(rowSpan));
            }

            //начинаем ячейку
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLECELL_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            ReportStyle.writeToXml(xmlStreamWriter, reportStyle);

            //запишем данные:
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_BLOCK_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            //Если есть тип
            if (dataType != null && dataType.length() != 0) {
                xmlStreamWriter.writeAttribute(XslFoProfileWriter.XML_ATTRIBUTE_VT, dataType);
            }
            xmlStreamWriter.writeCharacters(cellData.toString());
            xmlStreamWriter.writeEndElement();

            //Закроем элемент ячейки
            xmlStreamWriter.writeEndElement();
        } else {
            //для транспонированной:
            //ищем свободное место:
            int freeSpacePosition; //индекс, куда будем ячейку вставлять
            for (freeSpacePosition = cellCounter;
                 (freeSpacePosition < spanMap.length) && (spanMap[freeSpacePosition] != 0);
                 freeSpacePosition++) {}
            //если дырку не нашли - кидаем эксепшен.
            if (freeSpacePosition == spanMap.length) {
                throw new RuntimeException("Нет свободного места в ряду!");
            }

            if (!rows.containsKey(freeSpacePosition)) {
                //Нет соответствующего ряда. Надо добавить.
                rows.put(freeSpacePosition, new RowPresentation(null));
                reportStyle = ReportStyle.merge(rowStyles.get(0), reportStyle);
            }
            //Теперь в этот ряд надо добавить ячейку:
            rows.get(freeSpacePosition).addCell(cellData, colSpan, rowSpan, reportStyle, dataType);
            //отмечаем все ячейки с текущей и на весь rowspan как занятые:
            for (cellCounter = freeSpacePosition;
                 cellCounter < Math.min(spanMap.length, freeSpacePosition + colSpan); cellCounter++) {
                spanMap[cellCounter] = rowSpan;
            }
        }
    }

    /**
     * Начать подвал.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void startFooter() throws XMLStreamException {

        if (tableState == TableState.HEADER) {
            pasteColumnsGroup(headerRowStyle);
            tableState = TableState.BODY;
        }

        if (tableState == TableState.BODY) {
            compileTransponedTable();
            //Закроем тело таблицы (TBODY):
            xmlStreamWriter.writeEndElement();
        }

        //Откроем блок подвала
        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XML_TABLEFOOT_IN);
        //Переключим состояние:
        tableState = TableState.FOOTER;
    }

    /**
     * Окончание формирования подвала.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void endFooter() throws XMLStreamException {
        xmlStreamWriter.writeEndElement();
    }

    /**
     * Для вывода сырых данных во время формирования таблицы.
     * Возможно, потом ее надо будет доработать, чтобы корректно обрабатывались "отложенные ряды"
     * @param data   - Выводимые данные
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void rawOutput(String data) throws XMLStreamException {
        xmlStreamWriter.writeCharacters(data);
    }

    /**
     * Завершение формирования таблицы.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void endTable() throws XMLStreamException {

        if (tableState == TableState.HEADER) {
            pasteColumnsGroup(headerRowStyle);
            tableState = TableState.BODY;
        }

        if (tableState == TableState.BODY) {
            compileTransponedTable();
            //Элемент TBODY
            xmlStreamWriter.writeEndElement();
        } else if (tableState == TableState.FOOTER) {
            endFooter();
        }

        //Завершаем элемент таблицы
        xmlStreamWriter.writeEndElement();
        tableState = TableState.END;
    }

    /**
     * Формирует описание заголовка и колонок для транспонированной таблицы.
     * @param headerStyle    - стиль строки заголовков
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private void pasteColumnsGroup(ReportStyle headerStyle) throws XMLStreamException {

        List<CColumnDescription> sortedColumns; //Массив сортированых колонок
        int lastLevel;    //Последний уровень, на котором мы работали
        int maxDepth;    //максимальная глубина заголовка

        //сразу переключимся в режим формирования тела таблицы:
        tableState = TableState.BODY;

        //Колонки заданы?
        if (columnHandlers.size() == 0) {
            //Отвалим :)
            return;
        }

        //Выполняем разметку дерева:
        CColumnDescription description = columnHandlers.get(0);
        description.treeMarkUp();

        //Надо сформировать таблицу:
        spanMap = new int[description.getTreeWidth()];

        //Заголовок надо выводить?
        if (showHeader) {
            //Теперь сделаем копию массива с колонками, чтобы не разрушать упорядоченность:
            sortedColumns = new ArrayList<>(columnHandlers);
            //Отсортируем:
            sortedColumns.sort(new CColumnDescription.SortByLevel());
            //Теперь будем выводить по уровням:
            lastLevel = 0;
            maxDepth = sortedColumns.get(sortedColumns.size() - 1).getTreeLevel();
            for (CColumnDescription col : sortedColumns) {
                if (col.getTreeLevel() == 0) {
                    //корень
                    continue;
                }
                if (lastLevel != col.getTreeLevel()) {
                    //перешли на новый уровень
                    if (lastLevel != 0) {
                        //это не первый ряд в заголовке!
                        endRow(); //закром предыдущий ряд
                    }

                    startRow(headerStyle); //начнем новый ряд

                    lastLevel = col.getTreeLevel();
                }
                //добавим ячейку в ряд:
                addHeaderCell(col.getCaption(), col.getTreeWidth(), col.getIsLeaf()
                        ? (maxDepth - col.getTreeLevel() + 1)
                        : 1, col.getColumnStyle(), col.getHeaderCellStyle(), "string");
            }
            //Закроем ряд:
            endRow();
        }
    }

    /**
     * Функция сливает в поток ряды транспонированной таблицы.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private void compileTransponedTable() throws XMLStreamException {

        if (rows == null) {
            throw new RuntimeException(
                    "compileTransponedTable может быть вызвана только для транспонированной таблицы.");
        }

        //Выведем набор колонок:
        for (int i = 0; i < getCurrentRow(); i++) {
            //Откроем тег:
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_COL_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            // Получить стиль строки, которая стала столбцом
            ReportStyle rs = new ReportStyle(rowStyles.get(i));
            // Номер
            rs.put(XslFoProfileWriter.XML_ATTRIBUTE_COL_NUMBER, String.valueOf(i + 1));
            //Ширина
            if (!rs.containsKey(XslFoProfileWriter.XML_ATTRIBUTE_COL_WIDTH)) {
                rs.put(XslFoProfileWriter.XML_ATTRIBUTE_COL_WIDTH, "proportional-column-width(1)");
            }
            // Заполнить атрибуты стиля
            rs.writeToXml(xmlStreamWriter);
            //закроем тег
            xmlStreamWriter.writeEndElement();
        }

        //Выведем тэг, открывающий тело таблицы
        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLEBODY_IN,
                XslFoProfileWriter.XSLFO_NAMESPACE);

        for (int i = 0; i < rows.size(); i++) {
            xmlStreamWriter.writeCharacters(rows.get(i).toString());
        }
    }

    /**
     * Коллекция рядов для транспонированной таблицы.
     */
    private Hashtable<Integer, RowPresentation> rows;

    private List<ReportStyle> rowStyles;

    /**
     * Таблица для учета объединений.
     */
    private int[] spanMap = new int[0];

    /**
     * Служебный класс, представляющий ряд таблицы. Используется для формирования транспонированного представления.
     */
    private final class RowPresentation {

        /**
         * Конструктор класса.
         * @param style    - Стиль, назначаемый строке
         */
        public RowPresentation(ReportStyle style) throws XMLStreamException {

            buffer = new ByteArrayOutputStream();
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            try {
                rowXmlWriter = factory.createXMLStreamWriter(buffer, StandardCharsets.UTF_8.name());
                // TODO: надо будет вывод форматировать с помощью TransformerFactory
                // m_oXmlWriter.formatting = Formatting.Indented;

                //Начнем ряд
                rowXmlWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_ROW_IN,
                        XslFoProfileWriter.XSLFO_NAMESPACE);
                ReportStyle.writeToXml(rowXmlWriter, style);
            } catch (XMLStreamException e) {
                logger.error("Error by writing xml in RowPresentation", e);
                throw e;
            }
        }

        /**
         * Добавление ячейки к ряду.
         * @param data         - Данные ячейки
         * @param colSpan      - Число объединяемых столбцов
         * @param rowSpan      - Число объединяемых рядов
         * @param style        - Класс, назначаемый ячейке
         * @param dataType   - Тип данных
         * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
         */
        public void addCell(Object data, int colSpan, int rowSpan, ReportStyle style, String dataType) throws
                XMLStreamException {

            ReportStyle reportStyle = new ReportStyle(style);
            // col-span пишется как row-span, и наоборот. Таблица-то транспонированная!
            if (colSpan > 1) {
                reportStyle.put(XslFoProfileWriter.XML_ATTRIBUTE_ROWSPAN, String.valueOf(colSpan));
            }

            if (rowSpan > 1) {
                reportStyle.put(XslFoProfileWriter.XML_ATTRIBUTE_COLSPAN, String.valueOf(rowSpan));
            }

            //начинаем ячейку
            rowXmlWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLECELL_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            // Записать стиль
            ReportStyle.writeToXml(rowXmlWriter, reportStyle);

            //запишем данные:
            rowXmlWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_BLOCK_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            //Если есть тип
            if (dataType != null && dataType.length() != 0) {
                rowXmlWriter.writeAttribute(XslFoProfileWriter.XML_ATTRIBUTE_VT, dataType);
            }
            rowXmlWriter.writeCharacters(data.toString());
            rowXmlWriter.writeEndElement();

            //Закроем элемент ячейки
            rowXmlWriter.writeEndElement();
        }

        /**
         * Строка с содержимым.
         * @return String  - Строка ряда
         */
        @Override
        public String toString() {

            //Закроем все:
            try {
                rowXmlWriter.writeEndElement();
            } catch (XMLStreamException e) {
                logger.error("Error by writing xml", e);
            }
            // Вернем сформированный XML без объявления ненужного нэймспейса
            return buffer.toString().replace(" xmlns:fo=\"http://www.w3.org/1999/XSL/Format\"", StringUtils.EMPTY);
        }

        /**
         * Формирователь XML.
         */
        private XMLStreamWriter rowXmlWriter;

        /**
         * Буфер формирования ряда.
         */
        private ByteArrayOutputStream buffer;
    }
}
