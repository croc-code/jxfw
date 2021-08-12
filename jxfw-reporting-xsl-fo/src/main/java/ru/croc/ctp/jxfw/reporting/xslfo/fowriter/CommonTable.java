package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AlignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ValignClass;

import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Класс, выполняющий формирование таблицы. Служит для структурирования ReportWriter'а.
 * Created by vsavenkov on 24.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public final class CommonTable extends TablePresentation {

    /**
     * Конструктор класса.
     * @param showHeader      - Признак того, требуется ли показывать заголовок.
     *                        Имеет смысл только для нетранспонированных таблиц.
     * @param tableStyle      - Стиль, использующийся для отрисовки таблицы
     * @param headerRowStyle  - Стиль, использующийся для отрисовки заголовка таблицы
     * @param xmlStreamWriter - Поток, в котором надо формировать таблицу
     */
    public CommonTable(boolean showHeader, ReportStyle tableStyle, ReportStyle headerRowStyle, XMLStreamWriter
            xmlStreamWriter) {
        super(showHeader, tableStyle, headerRowStyle, xmlStreamWriter);
    }

    /**
     * Начинает строку таблицы.
     * @param reportStyle    - Имя класса, назначаемого строке
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void startRow(ReportStyle reportStyle) throws XMLStreamException {

        //Если это не траспонируемая таблица и формировался заголовок,
        //то надо его сбросить в поток.
        if (tableState == TableState.HEADER) {
            pasteColumnsGroup(headerRowStyle);
        }

        if ((tableState == TableState.HEADER) || (tableState == TableState.START)) {
            //Выведем тэг, открывающий тело таблицы
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLEBODY_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
        }

        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_ROW_IN,
                XslFoProfileWriter.XSLFO_NAMESPACE);
        ReportStyle.writeToXml(xmlStreamWriter, reportStyle);

        //Новое состояние - формирование тела таблицы
        if (tableState != TableState.FOOTER) {
            tableState = TableState.BODY;
        }

        //начали новый ряд
        rowCounter++;
        cellCounter = 1; //колонок в нем еще нет...
    }

    /**
     * Закрываем ряд.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void endRow() throws XMLStreamException {

        //Закроем элемент ряда.
        xmlStreamWriter.writeEndElement();
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

        /*
            Здесь заложена бага при подсчете номера текущей колонки. Временный workaround сделан, но нужно изменить
            работу с колонками!

            Дело в том, что список m_oColumnHandlers содержит как родительские, так и вложенные
            колонки, которые лежат в ряд, никак не разделяясь. В счетчике колонок это не учитывается.
            Поэтому, например, первой колонкой может оказаться вовсе не первая, а родительская,
            объединяющая, скажем, три дочерних колонки.
        */
        CColumnDescription col = columnHandlers.get(cellCounter);

        // Если это колонка не нижнего уровня, то нужно найти таковую
        while (!col.getIsLeaf()) {
            cellCounter++;
            col = columnHandlers.get(cellCounter);
        }

        ReportStyle rs = new ReportStyle(reportStyle);
        if (!rs.containsKey("text-align")) {
            //выравнивание по горизонтали
            fillAlign(rs, col.getAlign());
        }

        if (!rs.containsKey("display-align")) {
            //по вертикали
            fillValign(rs, col.getVAlign());
        }

        if (colSpan > 1) {
            rs.put(XslFoProfileWriter.XML_ATTRIBUTE_COLSPAN, String.valueOf(colSpan));
        }

        if (rowSpan > 1) {
            rs.put(XslFoProfileWriter.XML_ATTRIBUTE_ROWSPAN, String.valueOf(rowSpan));
        }

        //начинаем ячейку
        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLECELL_IN,
                XslFoProfileWriter.XSLFO_NAMESPACE);
        rs.writeToXml(xmlStreamWriter);

        //запишем данные:
        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_BLOCK_IN,
                XslFoProfileWriter.XSLFO_NAMESPACE);
        //Если есть тип
        if (dataType != null && dataType.length() != 0) {
            xmlStreamWriter.writeAttribute(XslFoProfileWriter.XML_ATTRIBUTE_VT, dataType);
        }
        writeCellData(cellData);
        xmlStreamWriter.writeEndElement();

        //Закроем элемент ячейки
        xmlStreamWriter.writeEndElement();

        cellCounter += colSpan; //номер ячейки на чем закончили
    }

    private void writeCellData(Object cellData) throws XMLStreamException {
        if (cellData instanceof CellDataWithLinks) {
            CellDataWithLinks cellDataWithLinks = (CellDataWithLinks) cellData;
            cellDataWithLinks.writeCellDataWithLinks(xmlStreamWriter);
            return;
        } else if (cellData instanceof String && StringUtils.isNotEmpty(cellData.toString())) {
            //иначе при наличии "<" в тексте ячейки полученный xslFo будет некорректно парситися
            //бесконечный цикл + OOM (проверено на com.sun.xml.internal.stream.XMLEventReaderImpl)
            xmlStreamWriter.writeCData(cellData.toString());
        } else {
            xmlStreamWriter.writeCharacters(cellData.toString());
        }
    }

    /**
     * Начать подвал.
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void startFooter() throws XMLStreamException {

        switch (tableState) {
            case HEADER:
                //Остановились на формировании заголовка. Надо его теперь выложить.
                pasteColumnsGroup(headerRowStyle);
                break;
            case BODY:
                //Закроем тело таблицы (TBODY):
                xmlStreamWriter.writeEndElement();
                break;
            default:
                break;
        }
        //Откроем блок подвала
        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLEFOOT_IN,
                XslFoProfileWriter.XSLFO_NAMESPACE);

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
        }

        if (tableState == TableState.BODY) {
            xmlStreamWriter.writeEndElement();
        } else if (tableState == TableState.FOOTER) {
            endFooter();
        }

        xmlStreamWriter.writeEndElement();
        tableState = TableState.END;
    }

    /**
     * Формирует описание заголовка и колонок для нетранспонированной таблицы.
     * @param headerRowStyle    - стиль строки заголовков
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    private void pasteColumnsGroup(ReportStyle headerRowStyle) throws XMLStreamException {

        //Колонки заданы?
        if (columnHandlers.size() == 0) {
            return;
        }

        ReportStyle rs;
        int columnNumber = 1;  //номер колонки
        //Начнем блок колонок:
        //надо вывести все листовые колонки:
        for (CColumnDescription col : columnHandlers) {
            if (col.getIsLeaf()) {
                //Откроем тег:
                xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_COL_IN,
                        XslFoProfileWriter.XSLFO_NAMESPACE);
                rs = new ReportStyle(col.getColumnStyle());
                rs.put(XslFoProfileWriter.XML_ATTRIBUTE_COL_NUMBER, String.valueOf(columnNumber));
                //Номер
                columnNumber += 1;
                //Ширина
                if (!StringUtils.isBlank(col.getColumnWidth())) {
                    rs.put(XslFoProfileWriter.XML_ATTRIBUTE_COL_WIDTH, col.getColumnWidth());
                } else if (!rs.containsKey(XslFoProfileWriter.XML_ATTRIBUTE_COL_WIDTH)) {
                    rs.put(XslFoProfileWriter.XML_ATTRIBUTE_COL_WIDTH, "proportional-column-width(1)");
                }

                rs.writeToXml(xmlStreamWriter);
                //закроем тег
                xmlStreamWriter.writeEndElement();
            }
        }

        //Заголовок надо выводить?
        if (!showHeader) {
            return;
        }

        //Начнем заголовок:
        xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLEHEADER_IN,
                XslFoProfileWriter.XSLFO_NAMESPACE);

        //Выполняем разметку дерева:
        columnHandlers.get(0).treeMarkUp();

        //Теперь сделаем копию массива с колонками, чтобы не разрушать упорядоченность:
        //Массив сортированых колонок
        List<CColumnDescription> sortedColumns = new ArrayList<>(columnHandlers);
        //Отсортируем:
        sortedColumns.sort(new CColumnDescription.SortByLevel());
        //Теперь будем выводить по уровням:
        //максимальная глубина заголовка
        int  maxDepth = sortedColumns.get(sortedColumns.size() - 1).getTreeLevel();
        //Последний уровень, на котором мы работали
        int lastLevel = 0;

        for (CColumnDescription col : sortedColumns) {
            if (col.getTreeLevel() == 0) {
                //корень
                continue;
            }
            if (lastLevel != col.getTreeLevel())  {
                //перешли на новый уровень
                if (lastLevel != 0) {
                    //это не первый ряд в заголовке!
                    xmlStreamWriter.writeEndElement(); //закром предыдущий ряд
                }

                xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_ROW_IN,
                        XslFoProfileWriter.XSLFO_NAMESPACE);
                ReportStyle.writeToXml(xmlStreamWriter, headerRowStyle);

                lastLevel = col.getTreeLevel();
            }
            //добавим ячейку в ряд:
            rs = new ReportStyle(col.getHeaderCellStyle());

            //по горизонтали
            fillAlign(rs, col.getHeaderAlign());

            //по вертикали
            fillValign(rs, col.getHeaderVAlign());

            //Теперь надо проставить объединения ячеек:
            if (col.getTreeWidth() > 1) {
                rs.put("number-columns-spanned", String.valueOf(col.getTreeWidth()));
            }
            //И объеденение рядов:
            if ((col.getIsLeaf()) && (col.getTreeLevel() < maxDepth)) {
                rs.put("number-rows-spanned", String.valueOf(maxDepth - col.getTreeLevel() + 1));
            }
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_TABLECELL_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            //Выведем стиль заголовка:
            rs.writeToXml(xmlStreamWriter);

            //Содержимое - наименование колонки:
            xmlStreamWriter.writeStartElement(XslFoProfileWriter.XSLFO_PREFIX, XslFoProfileWriter.XML_BLOCK_IN,
                    XslFoProfileWriter.XSLFO_NAMESPACE);
            xmlStreamWriter.writeCharacters(col.getCaption());
            xmlStreamWriter.writeEndElement();

            //Заканчиваем ячейку
            xmlStreamWriter.writeEndElement();
        }
        //Закроем ряд:
        xmlStreamWriter.writeEndElement();
        //Закроем заголовок:
        xmlStreamWriter.writeEndElement();
    }

    /**
     * Заполняет стилю выравнивание по горизонтали.
     * @param reportStyle   - заполняемый стиль
     * @param align         - выравнивание данных в ячейках колонки по горизонтали
     */
    private static void fillAlign(ReportStyle reportStyle, AlignClass align) {

        switch (align) {
            //по горизонтали
            case ALIGN_LEFT:
                reportStyle.put("text-align", "left");
                break;
            case ALIGN_RIGHT:
                reportStyle.put("text-align", "right");
                break;
            case ALIGN_CENTER:
                reportStyle.put("text-align", "center");
                break;

            default:
                // для остальных случаев не заполняем
        }
    }

    /**
     * Заполняет стилю выравнивание по вертикали.
     * @param reportStyle   - заполняемый стиль
     * @param valign         - выравнивание данных в ячейках колонки по вертикали
     */
    private static void fillValign(ReportStyle reportStyle, ValignClass valign) {

        switch (valign) {
            //по вертикали
            case VALIGN_TOP:
                reportStyle.put("display-align", "before");
                break;
            case VALIGN_BOTTOM:
                reportStyle.put("display-align", "after");
                break;
            case VALIGN_MIDDLE:
                reportStyle.put("display-align", "center");
                break;

            default:
                // для остальных случаев не заполняем
        }
    }
}
