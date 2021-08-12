package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringEscapeUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataReader;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataRow;
import ru.croc.ctp.jxfw.reporting.xslfo.data.IDataTable;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.AggregateFunctionUnknownException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentNullException;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.ColumnBuilder;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.RowCellBuilder;
import ru.croc.ctp.jxfw.reporting.xslfo.fowriter.XslFoProfileWriter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportObjectFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ExpressionEvaluator;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.IReportFormatter;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ReportFormatterData;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AlignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.BoundPresentationEnumClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ColClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EncodingTypeClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.RowClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TableLayoutClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.UpperBoundPresentationEnumClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.UseTypeClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ValignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.xml.stream.XMLStreamException;

/**
 * Created by vsavenkov on 19.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TableLayout extends ReportAbstractLayout {


    /**
     * класс, описывающий колонку таблицы лэйаута.
     */
    protected class LayoutColumn {

        /**
         * простой конструктор.
         * @param rsFieldName    - название поля рекордсета
         * @param formattersNode - элемент с набором профилей форматтеров
         */
        public LayoutColumn(String rsFieldName, List<AbstractFormatterClass> formattersNode) {

            this(null,
                rsFieldName,
                "{#" + rsFieldName + "}",
                false,
                AlignClass.ALIGN_NONE,
                ValignClass.VALIGN_NONE,
                StringUtils.EMPTY,
                XslFoProfileWriter.CELL_CLASS_NAME,
                XslFoProfileWriter.THEADER_CLASS_NAME,
                XslFoProfileWriter.SUBTOTAL_CLASS_NAME,
                XslFoProfileWriter.SUBTITLE_CLASS_NAME,
                XslFoProfileWriter.SUBTOTAL_CLASS_NAME,
                false,
                0,
                0,
                StringUtils.EMPTY,
                StringUtils.EMPTY,
                StringUtils.EMPTY,
                StringUtils.EMPTY,
                StringUtils.EMPTY,
                StringUtils.EMPTY,
                EncodingTypeClass.TEXT,
                VarTypesClass.STRING,
                formattersNode);
        }

        /**
         * сложный конструктор.
         * @param parentColumn               - родительская колонка
         * @param title                      - заголовок колонки
         * @param rsFieldName                - поле рекордсета
         * @param isHidden                   - скрытый столбец
         * @param align                      - горизонтальное выравнивание
         * @param valign                     - вертикальное выравнивание
         * @param width                      - ширина
         * @param cellCssClass               - css-класс ячеек колонки
         * @param headerCssClass             - css-класс заголовка колонки
         * @param totalCssClass              - css-класс итоговой строки колонки
         * @param subTitleCssClass           - css-класс строки подзаголовка колонки
         * @param subTotalCssClass           - css-класс строки подитогов колонки
         * @param columnIsCounter            - признак того, что колонка является счетчиком строк
         * @param counterStart               - начальное значение счетчика
         * @param counterIncrement           - приращение
         * @param aggregationFunction        - функция-агрегат
         * @param aggregationString          - строка, выводимая в строке итогов
         * @param aggregationStringSubTitle  - строка, выводимая в подзаголовке
         * @param aggregationStringSubTotals - строка, выводимая в строке подитогов
         * @param aggregationColspan         - количество объединяемых столбцов в строке итогов
         * @param rowspanBy                  - наименование колонки в рекордсете на основании равенства
         *                          последовательных данных в которой производится rowspan соответствующей колонки
         *                          таблицы лэйаута
         * @param encodingType               - Encoding ячейки(как воспринимать содержимое - как текст или как xml)
         * @param type                       - Тип данных ячейки(строка, целое, время...)
         * @param formattersNode             - xml-элемент с набором профилей форматтеров
         */
        public LayoutColumn(LayoutColumn parentColumn, String title, String rsFieldName, boolean isHidden,
                AlignClass align, ValignClass valign, String width, String cellCssClass,  String headerCssClass,
                String totalCssClass, String subTitleCssClass, String subTotalCssClass, boolean columnIsCounter,
                int counterStart, int counterIncrement, String aggregationFunction, String aggregationString,
                String aggregationStringSubTitle, String aggregationStringSubTotals, String aggregationColspan,
                String rowspanBy, EncodingTypeClass encodingType, VarTypesClass type,
                List<AbstractFormatterClass> formattersNode) {

            this.parentColumn = parentColumn;
            this.title = title;
            this.rsFileldName = rsFieldName;
            this.isHidden = isHidden;
            this.align = align;
            this.valign = valign;
            this.width = width;
            this.cellCssClass = cellCssClass;
            this.headerCssClass = headerCssClass;
            this.totalCssClass = totalCssClass;
            this.subTitleCssClass = subTitleCssClass;
            this.subTotalCssClass = subTotalCssClass;
            this.columnIsCounter = columnIsCounter;
            this.counterStart = counterStart;
            this.counterIncrement = counterIncrement;
            this.aggregationFunction = aggregationFunction;
            this.aggregationString = aggregationString;
            this.aggregationStringSubTitle = aggregationStringSubTitle;
            this.aggregationStringSubTotals = aggregationStringSubTotals;
            this.aggregationColspan = aggregationColspan;
            this.rowspanBy = rowspanBy.trim();
            this.encoding = encodingType;
            this.type = type;

            this.formatters = formattersNode;
            //текущее значение счетчика == начальному
            this.counterCurrent = this.counterStart;

            resetTotals(true);
        }

        //region приватные переменные и публичные свойства, им соответствующие
        /**
         * родительская колонка.
         */
        private LayoutColumn parentColumn;

        /**
         * заголовок колонки таблицы лэйаута.
         */
        private String title;

        /**
         * заголовок колонки таблицы лэйаута.
         * @return String   - возвращает заголовок колонки
         */
        public String getTitle() {
            return title;
        }

        /**
         * наимнование колонки рекордсета для получения данных колонки.
         */
        private String rsFileldName;

        public String getRsFileldName() {
            return rsFileldName;
        }

        /**
         * признак скрытого столбца.
         */
        private boolean isHidden;

        public boolean isHidden() {
            return isHidden;
        }

        /**
         * горизонтальное выравнивание.
         */
        private AlignClass align;

        public AlignClass getAlign() {
            return align;
        }

        /**
         * вертикальное выравнивание.
         */
        private ValignClass valign;

        public ValignClass getValign() {
            return valign;
        }

        /**
         * ширина.
         */
        private String width;

        public String getWidth() {
            return width;
        }

        /**
         * css-класс ячеек колонки.
         */
        private String cellCssClass;

        public String getCellCssClass() {
            return cellCssClass;
        }

        /**
         * css-класс заголовка колонки.
         */
        private String headerCssClass;

        public String getHeaderCssClass() {
            return headerCssClass;
        }

        /**
         * css-класс итоговой строки колонки.
         */
        private String totalCssClass;

        public String getTotalCssClass() {
            return totalCssClass;
        }

        /**
         * css-класс строки подзаголовка колонки.
         */
        private String subTitleCssClass;

        public String getSubTitleCssClass() {
            return subTitleCssClass;
        }

        /**
         * css-класс подитоговой строки колонки.
         */
        private String subTotalCssClass;

        public String getSubTotalCssClass() {
            return subTotalCssClass;
        }

        /**
         * признак того, что колонка является счетчиком строк.
         */
        private boolean columnIsCounter;

        public boolean isColumnIsCounter() {
            return columnIsCounter;
        }

        /**
         * начальное значение счетчика.
         */
        private int counterStart;

        public int getCounterStart() {
            return counterStart;
        }

        /**
         * приращение счетчика.
         */
        private int counterIncrement;

        public int getCounterIncrement() {
            return counterIncrement;
        }

        /**
         * текущее значение счетчика.
         */
        private int counterCurrent;

        public int getCounterCurrent() {
            return counterCurrent;
        }

        /**
         * Признак того, что данная колонка указана через group-by в качестве группирующей колонки.
         */
        private boolean isGroupingColumn;

        public boolean isGroupingColumn() {
            return isGroupingColumn;
        }

        public void setGroupingColumn(boolean groupingColumn) {
            isGroupingColumn = groupingColumn;
        }

        /**
         * Признак того, что для данной колонки нужно выводить подзаголовок.
         */
        private boolean hasSubTitle;

        public boolean isHasSubTitle() {
            return hasSubTitle;
        }

        public void setHasSubTitle(boolean hasSubTitle) {
            this.hasSubTitle = hasSubTitle;
        }

        /**
         * Признак того, что для данной колонки нужно выводить подитоги.
         */
        private boolean hasSubTotal;

        public boolean isHasSubTotal() {
            return hasSubTotal;
        }

        public void setHasSubTotal(boolean hasSubTotal) {
            this.hasSubTotal = hasSubTotal;
        }

        /**
         * Признак того, что для данной колонки ожидается вывод подитога.
         */
        private boolean pendingForSubtotal;

        public boolean isPendingForSubtotal() {
            return pendingForSubtotal;
        }

        public void setPendingForSubtotal(boolean pendingForSubtotal) {
            this.pendingForSubtotal = pendingForSubtotal;
        }

        /**
         * функция-агрегат.
         */
        private String aggregationFunction;

        public String getAggregationFunction() {
            return aggregationFunction;
        }

        /**
         * строка, выводимая в строке итогов.
         */
        private String aggregationString;

        public String getAggregationString() {
            return aggregationString;
        }

        /**
         * строка, выводимая в строке подитогов.
         */
        private String aggregationStringSubTotals;

        public String getAggregationStringSubTotals() {
            return aggregationStringSubTotals;
        }

        /**
         * строка, выводимая в подзаголовке.
         */
        private String aggregationStringSubTitle;

        public String getAggregationStringSubTitle() {
            return aggregationStringSubTitle;
        }

        /**
         * количество объединяемых столбцов в строке итогов.
         */
        private String aggregationColspan;

        public String getAggregationColspan() {
            return aggregationColspan;
        }

        /**
         * наименование колонки в рекордсете на основании равенства последовательных данных в которой
         * производится rowspan соответствующей колонки таблицы лэйаута.
         */
        private String rowspanBy;

        public String getRowspanBy() {
            return rowspanBy;
        }

        /**
         * Возвращает массив имён колонок rowspan`а.
         * @return String[] - Возвращает массив имён колонок
         */
        public String[] getRowspanByNames() {
            if (rowspanBy.length() == 0) {
                return new String[0]; //если пустая строка - вернем пустой массив
            } else {
                return rowspanBy.split("(;\t)");
            }
        }

        /**
         * Тип инкодинга содержимого ячейки.
         */
        private EncodingTypeClass encoding;

        public EncodingTypeClass getEncoding() {
            return encoding;
        }

        private VarTypesClass type;

        /**
         * Возвращает строковое описание типа.
         * @return е - пр
         */
        public String getType() {

            String stringType;

            // если поле задано, то верну его значение
            if (null != type) {
                stringType = type.value();
            } else {
                // иначе: по-умолчанию - string
                stringType = VarTypesClass.STRING.value();
            }

            return stringType;
        }

        /**
         * константа со значением ошибочного значения хэндла.
         */
        private final int invalidHandleValue = -1;

        /**
         * Хэндл колонки в XslFOProfileWriter.
         */
        private int columnHandle = invalidHandleValue;

        /**
         * Получает хэндл колонки в XslFOProfileWriter.
         * @param repGen - репорт-райтер
         * @return int  - возвращает хэндл колонки в XslFOProfileWriter.
         */
        public int getCalcColumnHandle(XslFoProfileWriter repGen) {
            if (columnHandle == invalidHandleValue && !isHidden) {
                if (parentColumn == null) {
                    ColumnBuilder columnBuilder = ColumnBuilder.create()
                            .setCaption(title)
                            .setAlign(align)
                            .setValign(valign)
                            .setColumnWidth(width)
                            .setHeaderCellClass(headerCssClass);
                    // колонка верхнего уровня
                    columnHandle = repGen.tableAddColumn(columnBuilder);
                } else {
                    // колонка дочерняя
                    int parentHandle = parentColumn.getCalcColumnHandle(repGen);
                    if (parentHandle == invalidHandleValue) {
                        // если родительская колонка не вернула хэндл, то значит она скрытая. Скроем и себя тоже
                        isHidden = true;
                    } else {
                        columnHandle = repGen.tableAddSubColumn(parentHandle, title, align, valign, null, width,
                                align.ALIGN_NONE, valign.VALIGN_NONE, headerCssClass);
                    }
                }
            }
            return columnHandle;
        }

        /**
         * последовательность эвалуаторов/форматтеров.
         */
        private List<AbstractFormatterClass> formatters;

        public List<AbstractFormatterClass> getFormatters() {
            return formatters;
        }

        /**
         * результат агргативной ф-ции на весь столбец.
         */
        private BigDecimal total;

        public BigDecimal getTotal() {
            return total;
        }

        /**
         * вспомогательное количество для подсчета среднего значения.
         */
        private int totalCountForAverage;

        /**
         * вспомогательная сумма для подсчета среднего значения.
         */
        private BigDecimal totalSumForAverage;

        /**
         * вспомогательная коллекция уникальных значений.
         */
        private SortedSet totalCollectionForDistinctCount;

        /**
         * результат агргативной ф-ции на весь столбец.
         */
        private BigDecimal subTotal;

        public BigDecimal getSubTotal() {
            return subTotal;
        }

        /**
         * Признак того, что промежуточный итог по столбцу сброшен.
         */
        private boolean subtotalReset;

        public boolean isSubtotalReset() {
            return subtotalReset;
        }

        public void setSubtotalReset(boolean subtotalReset) {
            this.subtotalReset = subtotalReset;
        }

        /**
         * Признак того, что общий итог по столбцу сброшен.
         */
        private boolean totalReset;

        public boolean isTotalReset() {
            return totalReset;
        }

        public void setTotalReset(boolean totalReset) {
            this.totalReset = totalReset;
        }

        /**
         * вспомогательное количество для подсчета среднего значения.
         */
        private int subTotalCountForAverage;

        /**
         * вспомогательная сумма для подсчета среднего значения.
         */
        private BigDecimal subTotalSumForAverage;

        /**
         * вспомогательная коллекция уникальных значений.
         */
        private SortedSet subTotalCollectionForDistinctCount;

        //endregion

        //region работа со счетчиком

        /**
         * Инкрементирует счетчик, если столбец является счетчиком.
         */
        public void incrementCounter() {
            if (columnIsCounter) {
                counterCurrent += counterIncrement;
            }
        }

        //endregion

        //region работа с итогами и подитогами

        /**
         * сбрасывает значения промежуточных итогов.
         * @param withGlobalTotals - признак сбрасывания глобальных итогов
         */
        public void resetTotals(boolean withGlobalTotals) {

            // Поставлю признак того, что промежуточные итоги сброшены
            subtotalReset = true;

            // Поставлю признак того, что общие итоги сброшены
            if (withGlobalTotals) {
                totalReset = true;
            }

            switch (aggregationFunction) {

                case "max":
                    subTotal = Converter.DECIMAL_MIN_VALUE;
                    if (withGlobalTotals) {
                        total = Converter.DECIMAL_MIN_VALUE;
                    }
                    break;

                case "min":
                    subTotal = Converter.DECIMAL_MAX_VALUE;
                    if (withGlobalTotals) {
                        total = Converter.DECIMAL_MAX_VALUE;
                    }
                    break;

                case "sum":
                    subTotal = BigDecimal.ZERO;
                    if (withGlobalTotals) {
                        total = BigDecimal.ZERO;
                    }
                    break;

                case "count":
                    subTotal = BigDecimal.ZERO;
                    if (withGlobalTotals) {
                        total = BigDecimal.ZERO;
                    }
                    break;

                case "average":
                    subTotal = BigDecimal.ZERO;
                    subTotalCountForAverage = 0;
                    subTotalSumForAverage = BigDecimal.ZERO;
                    if (withGlobalTotals) {
                        total = BigDecimal.ZERO;
                        totalCountForAverage = 0;
                        totalSumForAverage = BigDecimal.ZERO;
                    }
                    break;

                case "distinctcount":
                    subTotalCollectionForDistinctCount = new ConcurrentSkipListSet();
                    subTotal = BigDecimal.ZERO;
                    if (withGlobalTotals) {
                        totalCollectionForDistinctCount = new ConcurrentSkipListSet();
                        total = BigDecimal.ZERO;
                    }
                    break;

                default:
                    subTotal = BigDecimal.ZERO;
                    if (withGlobalTotals) {
                        total = BigDecimal.ZERO;
                    }
                    break;
            }
        }

        /**
         * выполняет действия описанный в агрегативной ф-ции.
         * @param currentValue - текущее значение
         */
        public void updateTotals(Object currentValue) {

            if (Converter.isNull(currentValue)) {
                // Если значение - NULL, то выходим (в агрегативных ф-циях не используем).
                // Пустая строка приравнена к NULL!
                return;
            }
            // Поставлю признак того, что промежуточные итоги пока не сброшены
            subtotalReset = false;
            // Поставлю признак того, что общие итоги пока не сброшены
            totalReset = false;

            // Во всех ситуациях кроме той, когда надо найти distinct-count, попробую получить decimal
            if (aggregationFunction != "distinctcount" && aggregationFunction != "count") {
                currentValue = Converter.toDecimal(currentValue.toString(), BigDecimal.valueOf(0));
            }

            switch (aggregationFunction) {

                case "max":
                    total = total.max(new BigDecimal(currentValue.toString()));
                    subTotal = subTotal.max(new BigDecimal(currentValue.toString()));
                    break;

                case "min":
                    total = total.min(new BigDecimal(currentValue.toString()));
                    subTotal = subTotal.min(new BigDecimal(currentValue.toString()));
                    break;

                case "sum":
                    total = total.add(new BigDecimal(currentValue.toString()));
                    subTotal = subTotal.add(new BigDecimal(currentValue.toString()));
                    break;

                case "count":
                    total = total.add(BigDecimal.valueOf(1));
                    subTotal = subTotal.add(BigDecimal.valueOf(1));
                    break;

                case "average":
                    totalSumForAverage = totalSumForAverage.add(new BigDecimal(currentValue.toString()));
                    totalCountForAverage++;
                    total = totalSumForAverage.divide(BigDecimal.valueOf(totalCountForAverage));

                    subTotalSumForAverage = subTotalSumForAverage.add(new BigDecimal(currentValue.toString()));
                    subTotalCountForAverage++;
                    subTotal = subTotalSumForAverage.divide(BigDecimal.valueOf(subTotalCountForAverage));
                    break;

                case "distinctcount":
                    /* TODO: найти замену SortedList
                    int ind = totalCollectionForDistinctCount.indexOfKey(CurrentValue);
                    if (ind >= 0)
                        m_TotalCollectionForDistinctCount.SetByIndex(ind, 1 + (int)m_TotalCollectionForDistinctCount
                            .GetByIndex(ind));
                    else
                        m_TotalCollectionForDistinctCount.Add(CurrentValue, 1);

                    m_Total = m_TotalCollectionForDistinctCount.Count;

                    ind = m_SubTotalCollectionForDistinctCount.IndexOfKey(CurrentValue);
                    if (ind >= 0)
                        m_SubTotalCollectionForDistinctCount.SetByIndex(ind,
                            1 + (int)m_SubTotalCollectionForDistinctCount.GetByIndex(ind));
                    else
                        m_SubTotalCollectionForDistinctCount.Add(CurrentValue, 1);

                    m_SubTotal = m_SubTotalCollectionForDistinctCount.Count;
                    */
                    break;

                default:
                    throw new AggregateFunctionUnknownException(aggregationFunction);
            }
        }

        //endregion

    }

    /**
     * класс, описывающий колонки таблицы лэйаута.
     */
    protected class LayoutColumns {

        /**
         * колонки.
         */
        protected ArrayList columns;

        /**
         * количество колонок.
         */
        protected int count;

        public int getCount() {
            return count;
        }

        /**
         * признак наличия хотя бы одного столбца с агрегативной ф-цией либо строкой.
         */
        private boolean hasAggregatedColumns;

        public boolean isHasAggregatedColumns() {
            return hasAggregatedColumns;
        }

        /**
         * признак наличия хотя бы одного столбца с rowspan'ом.
         */
        private boolean hasRospanedColumns;

        public boolean isHasRospanedColumns() {
            return hasRospanedColumns;
        }

        /**
         * возвращает массив названий колонок рекордсетам, по которым производится
         * группировка для последующего ровспана.
         * @return String [][]  - массив названий колонок RS
         */
        public String[][] getRowspannedFields() {
            String[][] rowSpans = new String[columns.size()][];

            for (int i = 0; i < columns.size(); i++) {
                rowSpans[i] = ((LayoutColumn) columns.get(i)).getRowspanByNames();
            }

            return rowSpans;
        }

        /**
         * конструктор.
         */
        public LayoutColumns() {
            columns = new ArrayList();
        }

        /**
         * добавляет колонку.
         * @param column - колонка
         */
        public void add(LayoutColumn column) {
            columns.add(column);
            count++;
            hasAggregatedColumns |= (StringUtils.isNotBlank(column.getAggregationFunction())
                    || StringUtils.isNotBlank(column.getAggregationString()));

            hasRospanedColumns |= (StringUtils.isNotBlank(column.getRowspanBy()));
        }

        /**
         * возвращает колонку по индексу.
         * @param index - индекс колонки
         * @return LayoutColumn - возвращает колонку
         */
        public LayoutColumn getColumnByIndex(int index) {
            return (LayoutColumn) columns.get(index);
        }

        /**
         * Возвращает колонку по её RSFieldName.
         * @param columnRsName - Имя колонки в виде Column1
         * @return LayoutColumn - возвращает колонку или null
         */
        public LayoutColumn getColumnByRsName(String columnRsName) {

            int index = getColumnOrdinal(columnRsName);
            if (-1 != index) {
                return (LayoutColumn) columns.get(index);
            }
            return null;
        }

        /**
         * Возвращает N колонки по её RSFieldName.
         * @param columnRsName - Имя колонки в виде Column1
         * @return ште  - Возвращает номер колонки или -1
         */
        public int getColumnOrdinal(String columnRsName) {

            String columnName = "{#" + columnRsName + "}";
            for (int i = 0; i < columns.size(); i++) {
                if (((LayoutColumn) columns.get(i)).getRsFileldName() == columnName) {
                    return i;
                }
            }

            return -1;
        }
    }


    /**
     * Глобальная таблица LayoutTable.
     */
    private LayoutTable layoutTable;

    public LayoutTable getLayoutTable() {
        return layoutTable;
    }

    /**
     * формирует визуальное представление отчета на основании описания.
     * @param layoutProfile - xml-профиль лэйаута
     * @param layoutData    - Данные лэйаута
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    protected void doMake(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) throws XMLStreamException {

        TableLayoutClass profile = (TableLayoutClass) layoutProfile;

        // Добавляем переменные лэйаута
        if (profile.getVar() != null) {
            for (VarClass var : profile.getVar()) {
                layoutData.getVars().put(var.getN(), null);
            }
        }

        // ридер по данным лэйаута
        IDataReader layoutDataReader;

        // колонки таблицы лэйаута
        LayoutColumns columns = new LayoutColumns();

        // получение данных
        // на самом деле LayoutDataReader - Это TableDataReader
        layoutDataReader = getData(profile, layoutData);

        // проверка данных на пустоту
        if (isNoData(layoutDataReader)) {
            // выводим сообщение об отсутствии данных
            writeNoDataMessage(layoutData, profile.getNoDataMessage());

            // закрывем ридер (одновременно закрывается коннекшн)
            layoutDataReader.close();

            return;
        }

        try {
            // рисуем лэйаут по полученным данным
            writeLayout(profile, layoutData, layoutDataReader, columns);
        } finally {
            // закрывем ридер (одновременно закрывается коннекшн)
            layoutDataReader.close();
        }
    }

    //region методы подготовки к формированию лэйаута

    /**
     * получает данные лэйаута в виде ридера.
     * @param layoutProfile - профиль лэйаута
     * @param layoutData    - данные лэйаута
     * @return IDataReader  - ридер с данными
     */
    protected IDataReader getData(TableLayoutClass layoutProfile, ReportLayoutData layoutData) {

        // наименование источника данных
        String dataSourceName = layoutProfile.getDataSourceName();
        // производим возможные подстановки из параметров
        dataSourceName = MacroProcessor.process(dataSourceName, layoutData);
        try {
            // получаем ридер от провайдера данных
            return layoutData.getDataProvider().getDataReader(dataSourceName, layoutData.getCustomData());
        } catch (Exception exc) {
            // генерим исключение
            throw new ReportException("Ошибка при получении данных для отображения лэйаута", exc);
        }
    }

    /**
     * проверяет наличие данных в ридере.
     * @param reader - ридер
     * @return boolean  - true, в случае пустоты ридера. иначе false
     */
    protected boolean isNoData(IDataReader reader) {

        // попытка считать первую строку
        return (!reader.read());
    }

    //endregion

    //region методы формирования лэйаута

    /**
     * отрисовка лэйаута.
     * @param layoutProfile - профиль лэйаута
     * @param layoutData    - Данные лэйаута
     * @param reader        - ридер с данными
     * @param columns       - колонки отчета
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    protected void writeLayout(TableLayoutClass layoutProfile, ReportLayoutData layoutData, IDataReader reader,
                               LayoutColumns columns) throws XMLStreamException {

        // признак непоказывания заголовков
        boolean showHeaders = true;
        if (null != layoutProfile.isOffHeaders()) {
            showHeaders = !layoutProfile.isOffHeaders();
        }

        // Признак транспонирования
        boolean isTransponed = false;
        if (null != layoutProfile.isTransposeTable()) {
            isTransponed = layoutProfile.isTransposeTable();
        }
        // начало таблицы
        layoutData.getRepGen().tableStart(showHeaders, layoutProfile.getStyleClass(), isTransponed);

        // ДатаТейбл с данными
        // !!! Reader уже на позиции not prior to the first record !!!
        IDataTable table = layoutData.getDataProvider().convertIDataReaderToDataTable(reader);

        // отрисовка заголовков
        writeColumns(layoutProfile, layoutData, table, columns);

        // словарь с данными о группировках
        Map<String, Object> groupings = new HashMap<>();
        if (layoutProfile.getGrouping() != null) {
            for (TableLayoutClass.GroupingClass grouping : layoutProfile.getGrouping()) {
                if (grouping.getGroupBy() != null) {
                    for (TableLayoutClass.GroupingClass.GroupByClass groupByNode : grouping.getGroupBy()) {
                        groupings.put(groupByNode.getN(), table.getRows().get(0).getItem(
                                table.getColumns().getColumnByName(groupByNode.getN()).getOrdinal()));
                    }
                }
            }
        }

        // если надо, рисуем нумерацию столбцов
        writeColumnCounter(layoutProfile, layoutData.getRepGen(), columns);

        // Инициализируем таблицу oLayoutTable
        layoutTable = new LayoutTable();

        // Отрисовываем строчек
        writeRows(layoutProfile, layoutData, reader, columns, groupings, table);

        // Прячет ячейки скрытых столбцов
        hideColumns(layoutProfile, layoutData, columns);

        // Отражаю LayoutTable в xsl-fo профиль
        flushLayoutTable(layoutData.getRepGen());

        // Очищаю таблицу
        layoutTable = null;

        // Закрываю ридер
        reader.close();
        // конец таблицы
        layoutData.getRepGen().tableEnd();
    }

    /**
     * Прячет ячейки скрытых столбцов.
     * @param layoutProfile - профиль отчета
     * @param layoutData    - данные лэйаута
     * @param columns       - описание колонок лэйаута
     */
    protected void hideColumns(TableLayoutClass layoutProfile, ReportLayoutData layoutData, LayoutColumns columns) {

        for (int i = 0; i < getLayoutTable().getRowCount(); i++) {
            int rowCellCount = getLayoutTable().getRow(i).getRowCells().size();
            for (int j = 0; j < rowCellCount; j++) {
                // не спрятанные столбцы пропускаем
                if (!columns.getColumnByIndex(j).isHidden()) {
                    continue;
                }

                LayoutCell cell = getLayoutTable().getCell(i, j);

                // для спрятанных столбцов rowspan не нужен
                cell.setRowspaned(false);
                cell.setStartsRowspanedCells(false);

                // если ячейка начинало colspan, то сдвинем начало colspan
                // на следующую ячейку в ряду
                if (cell.isStartsColumnspanedCells()) {
                    LayoutCell nextCell = getLayoutTable().getCell(i, j + 1);
                    if (nextCell != null) {
                        if (nextCell.isFakeCell()) {
                            nextCell.setFakeCell(false);
                            nextCell.setValue(cell.getValue());
                            nextCell.setCellStyle(cell.getCellStyle());
                            nextCell.setCellType(cell.getCellType());
                        }
                        if (cell.getColumnspanCount() > 2) {
                            nextCell.setStartsColumnspanedCells(true);
                            nextCell.setColumnspanCount(cell.getColumnspanCount() - 1);
                        }
                    }
                    cell.setStartsColumnspanedCells(false);
                }

                // проверим, не участвует ли текущая ячейка в colspan
                int hiddenCount = 0;    // кол-во спрятанных столбцов в colspan
                for (int k = j - 1; k >= 0; k--) {
                    if (columns.getColumnByIndex(k).isHidden()) {
                        hiddenCount++;
                        continue;
                    }

                    LayoutCell colspanCell = getLayoutTable().getCell(i, k);
                    if (colspanCell.isStartsColumnspanedCells()) {
                        // если текущая ячейка попадает в colspan, то
                        // уменьшим colspan
                        if (colspanCell.getColumnspanCount() >= j - k - hiddenCount + 1) {
                            if (colspanCell.getColumnspanCount() > 2) {
                                colspanCell.setColumnspanCount(colspanCell.getColumnspanCount() - 1);
                            } else {
                                colspanCell.setStartsColumnspanedCells(false);
                                colspanCell.setColumnspanCount(1);
                            }
                        }
                        break;
                    }
                }

                cell.setFakeCell(true);
            }
        }
    }

    /**
     * Функция на основе таблицы LayoutTable рисует в xsl-fo профиле отчета элемент fo:table.
     * @param repGen - FOWriter
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    protected void flushLayoutTable(XslFoProfileWriter repGen) throws XMLStreamException {

        for (LayoutRow row : getLayoutTable().getTableRows()) {
            repGen.tableRowStart(row.getRowStyle());

            for (LayoutCell cell : row.getRowCells()) {
                // Если зароуспанена, просто пропущу её
                if (cell.isRowspaned()) {
                    // здесь юзаем метод, который увеличивает счетчик колонок в TablePresentation(TP)
                    // Ведь колонка будет уже другая - мы не стали выводить ячейку этой колонки, т.к.
                    // её значение равно значению ячейки предыдущего ряда -
                    // а в TP мы по-прежнему будем в этом столбце.
                    repGen.tableRowOmitCell();
                } else {
                    if (!cell.isFakeCell()) {
                        String value = cell.getValue().toString();
                        RowCellBuilder rcb = RowCellBuilder.create(value)
                                .setType(cell.getCellType())
                                .setColSpan(cell.getColumnspanCount())
                                .setRowSpan(cell.getRowspanCount())
                                .setElementClass(cell.getCellStyle());
                        repGen.tableRowAddCell(rcb);
                    }
                }
            }

            repGen.tableRowEnd();
        }
    }

    /**
     * рисует колонки таблицы лэйаута.
     * @param layoutProfile - профиль лэйаута
     * @param layoutData    - данные лэйаута
     * @param table         - ридер с данными
     * @param columns       - описание колонок лэйаута
     */
    protected void writeColumns(TableLayoutClass layoutProfile, ReportLayoutData layoutData, IDataTable table,
                                LayoutColumns columns) {

        // строка с номерами скрытых колонок
        String hiddenColumns = StringUtils.EMPTY;

        if (!StringUtils.isBlank(layoutProfile.getHiddenColumnsParamName())) {
            hiddenColumns = layoutData.getParams().getParam(layoutProfile.getHiddenColumnsParamName()).toString();
        }

        // xml-узел с профилями дефолтных для лэйаута эвалуаторов/форматтеров
        List<AbstractFormatterClass> formattersNode = Converter.jaxbListToTypedList(layoutProfile.getFormatters()
                .getAbstractFormatter());

        if (layoutProfile.getCol() == null) {
            // если в профиле явно не описаны колонки отчета
            for (int i = 0; i < table.getColumns().getCount(); i++) {
                LayoutColumn column = new LayoutColumn(table.getColumns().getColumn(i).getColumnName(), formattersNode);
                // вызываем функцию получения хэндла колонки (вызывает добавление к списку колонок у репорт-райтера)
                column.getCalcColumnHandle(layoutData.getRepGen());
                // добавляем описание колонки в коллекцию
                columns.add(column);
            }
        } else {
            // если колонки описаны явно
            for (ColClass colNode : layoutProfile.getCol()) {
                // рекурсивно добавляем колонки
                insertColumn(colNode, null, layoutProfile, layoutData, columns, hiddenColumns, formattersNode);
            }
        }
    }

    /**
     * рекурсивно дабавляет колонки в отчет и в коллекцию колонок.
     * @param colNode        - профиль колонки
     * @param parentColumn   - Родительская колонка
     * @param layoutProfile  - профиль лэйаута
     * @param layoutData     - данные лэйаута
     * @param columns        - коллекция колонок
     * @param hiddenColumns  - строка с номерами скрытых колонок
     * @param formattersNode - описание вышестоящих эвалуаторов/форматтеров
     */
    protected void insertColumn(ColClass colNode, LayoutColumn parentColumn, TableLayoutClass layoutProfile,
                                ReportLayoutData layoutData, LayoutColumns columns, String hiddenColumns,
                                List<AbstractFormatterClass> formattersNode) {

        // заголовок колонки
        String title = (colNode.getT() != null ? MacroProcessor.process(colNode.getT(), layoutData)
                : StringUtils.EMPTY);

        // поле рекордсета
        String rsFieldName = StringUtils.defaultString(colNode.getData());

        // столбец скрытый
        boolean isHidden =
                (parentColumn != null && parentColumn.isHidden())
                || (rsFieldName.length() != 0 && hiddenColumns.indexOf(rsFieldName) >= 0)
                || (!StringUtils.isBlank(colNode.getHideIf())
                        && Converter.toBoolean(layoutData.getParams().getParam(colNode.getHideIf()).toString()))
                || (!StringUtils.isBlank(colNode.getHideIfNot())
                        && !Converter.toBoolean(layoutData.getParams().getParam(colNode.getHideIfNot()).toString()));

        // выравнивание
        AlignClass align = (null != colNode.getAlign() ? colNode.getAlign() : AlignClass.ALIGN_NONE);
        ValignClass valign = (null != colNode.getValign() ? colNode.getValign() : ValignClass.VALIGN_NONE);

        // ширина
        String width = StringUtils.defaultString(colNode.getW());

        // css-класс ячеек колонки
        // если явно задан для колонки, то использвем его
        String cellCssClass = StringUtils.defaultString(colNode.getStyleClass(),
                (StringUtils.defaultString(layoutProfile.getStyleClass(), XslFoProfileWriter.CELL_CLASS_NAME)));

        // css-класс заголовка колонки
        String headerCssClass = StringUtils.defaultString(colNode.getHeaderStyleClass(),
                XslFoProfileWriter.THEADER_CLASS_NAME);

        // css-класс итоговой строки колонки
        String totalCssClass = StringUtils.defaultString(colNode.getTotalStyleClass(),
                XslFoProfileWriter.SUBTOTAL_CLASS_NAME);

        // css-класс строки подзаголовка колонки
        String subTitleCssClass = StringUtils.defaultString(colNode.getSubtitleStyleClass(),
                XslFoProfileWriter.SUBTITLE_CLASS_NAME);

        // css-класс строки подитогов колонки
        String subTotalCssClass = StringUtils.defaultString(colNode.getSubtotalStyleClass(),
                XslFoProfileWriter.SUBTOTAL_CLASS_NAME);

        // признак того, что колонка является счетчиком строк
        boolean columnIsCounter = colNode.getCounterStart() > 0;

        // начальное значение и приращение счетчика
        int counterStart = 0;
        int counterIncrement = 0;
        if (columnIsCounter) {
            counterStart = colNode.getCounterStart();
            counterIncrement = colNode.getCounterIncrement() != 0 ? colNode.getCounterIncrement() : 1;
        }

        // Encoding ячейки
        EncodingTypeClass encoding = null != colNode.getEncoding() ? colNode.getEncoding() : EncodingTypeClass.TEXT;

        // Тип данных ячейки
        VarTypesClass type = null != colNode.getVt() ? colNode.getVt() : VarTypesClass.STRING;

        // функция-агрегат
        String aggregationFunction = null != colNode.getAggregationFunction()
                ? colNode.getAggregationFunction().toString() : StringUtils.EMPTY;

        // строка, выводимая в строке итогов
        String aggregationString = StringUtils.defaultString(colNode.getAggregationString());

        // строка, выводимая в строке подитогов
        String aggregationStringSubTotal = StringUtils.defaultString(colNode.getAggregationStringSubtotals());

        // строка, выводимая в подзаголовке
        String aggregationStringSubTitle = StringUtils.defaultString(colNode.getAggregationStringSubtitle());

        // количество объединяемых столбцов в строке итогов
        String aggregationColspan = StringUtils.defaultString(colNode.getAggregationColspan());

        // наименование колонки в рекордсете на основании
        // равенства последовательных данных в которой
        // производится rowspan соответствующей колонки таблицы лэйаута
        String rowspanBy = StringUtils.defaultString(colNode.getRowspanBy());

        // добавляем форматтеров текущего уровня
        List<AbstractFormatterClass> currentFormattersNode = Converter.jaxbListToTypedList(colNode.getFormatters()
                .getAbstractFormatter());

        LayoutColumn column = new LayoutColumn(parentColumn, title, rsFieldName, isHidden, align, valign, width,
                cellCssClass, headerCssClass, totalCssClass, subTitleCssClass, subTotalCssClass, columnIsCounter,
                counterStart, counterIncrement, aggregationFunction, aggregationString, aggregationStringSubTitle,
                aggregationStringSubTotal, aggregationColspan, rowspanBy, encoding, type, currentFormattersNode);

        // проверяем, есть ли у данной колонки вложенные
        if (colNode.getCol() != null) {
            // есть дочерние колонки, рекурсивно спускаемся к ним
            for (ColClass subColNode : colNode.getCol()) {
                insertColumn(subColNode, column, layoutProfile, layoutData, columns, hiddenColumns,
                        currentFormattersNode);
            }
            // выходим
            return;
        }
        // вызываем функцию получения хэндла колонки (вызывает добавление к списку колонок у репорт-райтера)
        column.getCalcColumnHandle(layoutData.getRepGen());
        // добавляем описание колонки в коллекцию
        columns.add(column);
    }

    /**
     * рисует строку таблицы лэйаута.
     * @param layoutProfile - профиль лэйаута
     * @param layoutData    - Данные лэйаута
     * @param reader        - дата-ридер для доступа к последовательным данным
     * @param columns       - описание колонок лэйаута
     * @param grouping      - коллекция с данными о группировках
     * @param table         - Таблица с данными рядов
     */
    protected void writeRows(TableLayoutClass layoutProfile, ReportLayoutData layoutData, IDataReader reader,
                             LayoutColumns columns, Map<String, Object> grouping, IDataTable table) {

        IDataRow previousRow = null; //предыдущий обрабатываемый ряд
        IDataRow currentRow; //текущий обрабатываемый ряд
        int[] rowspans; //таблица "объединителей колонок"

        rowspans = new int[columns.count]; //ровно столько, сколько у нас колонок в отчете

        for (int i = 0; i < table.getRows().size(); i++) {
            previousRow = table.getRows().get(i == 0 ? 0 : i - 1);
            currentRow = table.getRows().get(i);
            // Проверяю, и если надо, рисую границу группировки
            writeGroupBound(layoutProfile, layoutData, previousRow, currentRow, columns, grouping, table, rowspans,
                    false);

            // обновляем значения переменных
            setVarValues(layoutProfile, layoutData, currentRow, i);
            RowClass row = layoutProfile.getRow();
            if (row != null) {
                ReportFormatterData formatterData = new ReportFormatterData(layoutData, null, null,
                        currentRow, i, -1);
                MacroProcessor mp = new MacroProcessor(formatterData);
                /*TODO происходит вычислени c# кода
                // вычисляем значение признака того, что фрагмент показывать не надо
                boolean isHide = Converter.toBoolean(ExpressionEvaluator.evaluate(row.getHideIf(), formatterData)
                        .toString());

                if (isHide) {
                    continue;
                }*/
                // Добавляю новый ряд
                String rowStyle = mp.process(row.getStyleClass());
                getLayoutTable().addRow(rowStyle);
            } else {
                getLayoutTable().addRow();
            }

            // проходим по колонкам отчета
            for (int columnNum = 0; columnNum < columns.count; columnNum++) {
                // рисуем ячейку таблицы лэйаута
                if (columns.getColumnByIndex(columnNum).getRowspanBy().length() == 0) {
                    ReportFormatterData data = calculateCellValue(layoutData, columns, i, columnNum, currentRow, 1);
                    Object cellValue = null != data.getCurrentValue() ? data.getCurrentValue() : StringUtils.EMPTY;
                    String style = data.getClassName();
                    getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                            columns.getColumnByIndex(columnNum).getType(), 1, 1, style);
                } else {
                    // Если кол-во объединяемых рядов данной колонки не равно нулю...
                    if (rowspans[columnNum] != 0) {
                        // вставляю ячейку и говорю ей, что она зароуспанена
                        ReportFormatterData data = calculateCellValue(layoutData, columns, i, columnNum, currentRow,
                                rowspans[columnNum]);
                        Object cellValue = null != data.getCurrentValue() ? data.getCurrentValue() : StringUtils.EMPTY;
                        String style = data.getClassName();

                        getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                                columns.getColumnByIndex(columnNum).getType(), rowspans[columnNum], 1, style);
                        getLayoutTable().getCurrentRow().getCurrentCell().setRowspaned(true);
                    } else {
                        // В противном случае, просто рисую ячейку. Раньше непонятно зачем была дублирующая проверка
                        // но сначала надо пересчитать Rowspan
                        //(т.е. узнать кол-во ячеек последующих рядов, у которых значение равно значению
                        // текущей ячейки):
                        rowspans[columnNum] = calculateRowspan(table, i, columns.getColumnByIndex(columnNum));
                        ReportFormatterData data = calculateCellValue(layoutData, columns, i, columnNum,
                                currentRow, rowspans[columnNum]);
                        Object cellValue = null != data.getCurrentValue() ? data.getCurrentValue()
                                : StringUtils.EMPTY;
                        String style = data.getClassName();

                        getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                                columns.getColumnByIndex(columnNum).getType(), rowspans[columnNum], 1, style);

                        // Если Rowspan вдруг стал больше 1 - мы натолкнулись на новую последовательность
                        // зароуспаненных ячеек
                        if (rowspans[columnNum] > 1) {
                            // Говорим ячейке, что она начинает новую последовательность зароуспаненных ячеек.
                            getLayoutTable().getCurrentRow().getCurrentCell().setStartsRowspanedCells(true);
                        }

                        // Проверим, нет ли над текущей ячейкой подзаголовка
                        int startSubTitle = getLayoutTable().getRowCount() - 1;
                        for (int j = getLayoutTable().getRowCount() - 2; j >= 0; j--) {
                            LayoutCell cell = getLayoutTable().getCell(j, columnNum);
                            if (cell != null) {
                                if (!cell.isNotGroupSubTitle() || cell.isFakeCell()) {
                                    startSubTitle = j + 1;
                                    break;
                                }
                            }
                        }

                        // Если над текущей ячейкой есть подзаголовок, объединим их в rowspan
                        if (startSubTitle != getLayoutTable().getRowCount() - 1) {
                            LayoutCell currentCell = getLayoutTable().getCurrentRow().getCurrentCell();
                            LayoutCell startSubTitleCell = getLayoutTable().getCell(startSubTitle, columnNum);

                            startSubTitleCell.setStartsRowspanedCells(true);
                            startSubTitleCell.setValue(currentCell.getValue());
                            startSubTitleCell.setCellStyle(currentCell.getCellStyle());
                            startSubTitleCell.setCellType(currentCell.getCellType());

                            if (currentCell.isStartsRowspanedCells()) {
                                startSubTitleCell.setRowspanCount(currentCell.getRowspanCount()
                                        + getLayoutTable().getRowCount() - startSubTitle - 1);
                                currentCell.setStartsRowspanedCells(false);
                                currentCell.setRowspanCount(0);
                            } else {
                                startSubTitleCell.setRowspanCount(getLayoutTable().getRowCount() - startSubTitle);
                            }

                            for (int j = startSubTitle + 1; j < getLayoutTable().getRowCount(); j++) {
                                LayoutCell cell = getLayoutTable().getCell(j, columnNum);
                                if (cell != null) {
                                    cell.setRowspaned(true);
                                }
                            }
                        }
                    }

                    // Уменьшаю кол-во последующих рядов данной колонки, которые необходимо
                    //    объединить с текущим рядом
                    rowspans[columnNum]--;
                }
            }
            previousRow = currentRow;
        }
        // Здесь нужно вывести подытоги для последних сгруппированных строк.
        // Делаю это...
        writeGroupBound(layoutProfile, layoutData, previousRow, previousRow, columns, grouping, table, null, true);

        // проверка необходимости подведения итогов
        if (columns.isHasAggregatedColumns()) {
            writeTotalRow(layoutProfile, layoutData, columns, -1, -1, false, table, null, -1, previousRow);
        }
    }

    /**
     * Рассчитывает параметр Rowspan для заданной колонки в заданном ряду.
     * @param table         - Таблица из рядов - как правило, DataWindow
     * @param startRowIndex - Индекс ряда, в котором рассчитываем значение
     * @param column        - Описание колонки, для которой проводим рассчет
     * @return int  - возвращает параметр Rowspan для заданной колонки в заданном ряду
     */
    protected static int calculateRowspan(IDataTable table, int startRowIndex, LayoutColumn column) {

        if (column == null) {
            throw new ArgumentNullException("Column");
        }

        String[] arGroupNames = column.getRowspanByNames();
        int[] arGroupIndexes = new int[arGroupNames.length];    //индексы колонок по которым группируем
        int rowspanValue; //значение атрибута rowspan
        IDataRow currentRow;
        IDataRow startRow;

        for (int i = 0; i < arGroupIndexes.length; i++) {
            arGroupIndexes[i] = table.getColumns().getColumnByName(arGroupNames[i]).getOrdinal();
        }

        rowspanValue = 1;
        startRow = table.getRows().get(startRowIndex);

        // Пройдусь по всем следующим строкам
        for (int currentRowIndex = startRowIndex + 1; currentRowIndex < table.getRows().size(); currentRowIndex++) {
            //проверим по всем группирующим колонкам:
            currentRow = table.getRows().get(currentRowIndex);
            for (int j = 0; j < arGroupIndexes.length; j++) {
                // Если хотя бы в одной группирующей колонке значения не сошлись
                if (!(startRow.getItem(arGroupIndexes[j]).equals(currentRow.getItem(arGroupIndexes[j])))) {
                    return rowspanValue;
                }
            }
            //если этот ряд равен, увеличим параметр и перейдем к следующему
            rowspanValue++;
        }

        return rowspanValue;
    }

    /**
     * Вычисляет данные ячейки таблицы лэйаута.
     * @param layoutData - параметры лэйаута
     * @param columns    - описание колонок лэйаута
     * @param rowNum     - номер строки
     * @param columnNum  - номер колонки
     * @param currentRow - текщая строка данных
     * @param rowSpan    - кол-во объединяемых строк (включая текущую)
     * @return ReportFormatterData  - Структуру с расчитынами значениями
     */
    protected ReportFormatterData calculateCellValue(ReportLayoutData layoutData, LayoutColumns columns, int rowNum,
                                                     int columnNum, IDataRow currentRow, int rowSpan) {

        ReportFormatterData formatterData = calculateCellValueEx(layoutData, columns, rowNum, columnNum, currentRow,
                rowSpan, true);

        if (StringUtils.isBlank(formatterData.getClassName())) {
            formatterData.setClassName(columns.getColumnByIndex(columnNum).getCellCssClass());
        }

        return formatterData;
    }

    /**
     * Вычисляет данные ячейки подзаголовка таблицы лэйаута.
     * @param layoutData - параметры лэйаута
     * @param columns    - описание колонок лэйаута
     * @param rowNum     - номер строки
     * @param columnNum  - номер колонки
     * @param currentRow - текщая строка данных
     * @return ReportFormatterData  - Структуру с расчитынами значениями
     */
    protected ReportFormatterData calculateSubTitleCellValue(ReportLayoutData layoutData, LayoutColumns columns,
                                                             int rowNum, int columnNum, IDataRow currentRow) {

        ReportFormatterData formatterData = calculateCellValueEx(layoutData, columns, rowNum, columnNum, currentRow,
                0, false);

        if (StringUtils.isBlank(formatterData.getClassName())) {
            formatterData.setClassName(columns.getColumnByIndex(columnNum).getSubTitleCssClass());
        }

        return formatterData;
    }

    /**
     * Вычисляет данные ячейки таблицы лэйаута (расширенная версия).
     * @param layoutData     - параметры лэйаута
     * @param columns        - описание колонок лэйаута
     * @param rowNum         - номер строки
     * @param columnNum      - номер колонки
     * @param currentRow     - текщая строка данных
     * @param rowSpan        - кол-во объединяемых строк (включая текущую)
     * @param isUpdateTotals - признак, показывающий, надо ли обновлять подитоги
     * @return ReportFormatterData  - Структуру с расчитынами значениями
     */
    private static ReportFormatterData calculateCellValueEx(ReportLayoutData layoutData, LayoutColumns columns,
            int rowNum, int columnNum, IDataRow currentRow, int rowSpan, boolean isUpdateTotals) {

        // значение в ячейке
        Object currentValue = null;
        ReportFormatterData formatterData = new ReportFormatterData(
                layoutData,
                currentValue,
                null,
                currentRow,
                rowNum,
                columnNum);

        // если текущее значение не соответсвует никакой колонке рекордсета
        LayoutColumn column = columns.getColumnByIndex(columnNum);
        if (column.getRsFileldName().length() == 0) {
            // если колонка - счетчик
            if (column.isColumnIsCounter()) {
                // текущее значение счетчика
                currentValue = String.valueOf(column.getCounterCurrent());
                // инкрементируем счетчик (если не зароуспанен)
                if (rowSpan <= 1) {
                    column.incrementCounter();
                }
            }
        } else {
            // значение
            currentValue = new MacroProcessor(formatterData)
                    .process(column.getRsFileldName());
        }

        if (isUpdateTotals && column.getAggregationFunction().length() != 0) {
            column.updateTotals(currentValue);
        }

        formatterData.setRawCurrentValue(currentValue);
        // Encoding
        formatterData.setCurrentValue(encodeValue(columns.getColumnByIndex(columnNum), currentValue));

        // проходим по эвалуаторам и форматтерам
        executeFormatters(column, formatterData, UseTypeClass.TOTAL_CELL);

        return formatterData;
    }

    /**
     * Применяется для рассчета значения ячейки из строчки с аггрегированными значениями.
     * @param layoutProfile - профиль лэйаута
     * @param layoutData    - данные лэйаута
     * @param columns       - описание колонок лэйаута
     * @param rowNum        - номер строки
     * @param columnNum     - номер колонки
     * @param currentRow    - текщая строка данных
     * @param colspan       - колспан
     * @param isSubTotals   - признак вывода подитогов
     * @return ReportFormatterData  - Структуру с расчитаными данными
     */
    protected ReportFormatterData calculateTotalCell(TableLayoutClass layoutProfile, ReportLayoutData layoutData,
             LayoutColumns columns, int rowNum, int columnNum, IDataRow currentRow, int colspan, boolean isSubTotals) {

        // значение в ячейке
        Object currentValue;
        // получаем объект, с которым работают форматтеры и эвалуаторы
        ReportFormatterData formatterData = new ReportFormatterData(
                layoutData,
                null,
                null,
                currentRow,
                rowNum,
                columnNum);
        LayoutColumn column = columns.getColumnByIndex(columnNum);
        try {
            // если текущее значение не соответсвует никакой колонке рекордсета
            if (column.getRsFileldName().length() == 0) {
                // если колонка - счетчик
                if (column.isColumnIsCounter()) {
                    // Если для колонки заданы такие значения как aggregation-string,
                    // вместо счетчика пишем aggregation-string
                    if (!isSubTotals && column.getAggregationString().length() != 0 || isSubTotals
                            && column.getAggregationStringSubTotals().length() != 0) {
                        // Если выводятся подитоги
                        if (isSubTotals) {
                            currentValue = new MacroProcessor(formatterData)
                                    .process(column.getAggregationStringSubTotals());
                        } else  {
                            //выводятся итоги
                            currentValue = new MacroProcessor(formatterData).process(column.getAggregationString());
                        }
                    } else {
                        // текущее значение счетчика
                        currentValue = String.valueOf(column.getCounterCurrent());
                        // инкрементируем счетчик
                        column.incrementCounter();
                    }
                } else {
                    // null иначе
                    currentValue = null;
                }
            } else {
                // значение
                currentValue = new MacroProcessor(formatterData).process(column.getRsFileldName());
            }
        } catch (Exception e) {
            currentValue = null;
        }

        formatterData.setRawCurrentValue(currentValue);
        // Encoding
        formatterData.setCurrentValue(encodeValue(columns.getColumnByIndex(columnNum), currentValue));

        // проходим по эвалуаторам и форматтерам
        executeFormatters(column, formatterData, UseTypeClass.DATA_CELL);

        if (StringUtils.isBlank(formatterData.getClassName())) {
            formatterData.setClassName(isSubTotals
                    ? column.getSubTotalCssClass()
                    : column.getTotalCssClass());
        }

        return formatterData;
    }

    /**
     * Декодирует значение при необходимости.
     * @param column - описание колонки
     * @param value  - декодируемое значение
     * @return Object   - возвращает декодированное значение
     */
    private static Object encodeValue(LayoutColumn column, Object value) {

        // Encoding
        if (column.getEncoding() == EncodingTypeClass.TEXT && value != null) {
            value = StringEscapeUtils.escapeHtml4(value.toString());
        }

        return value;
    }

    /**
     * проходит по эвалуаторам и форматтерам.
     * @param column         - описание колонки
     * @param formatterData  - Набор данных передаваемых форматеру для обработки
     * @param checkedUseType - проверяемый тип
     */
    private static void executeFormatters(LayoutColumn column, ReportFormatterData formatterData,
                                          UseTypeClass checkedUseType) {

        // проходим по эвалуаторам и форматтерам
        if (column.getFormatters() != null) {
            for (AbstractFormatterClass formatterNode : column.getFormatters()) {
                if (null == formatterNode.getUse() || formatterNode.getUse() != checkedUseType) {
                    // просим объект у фабрики
                    IReportFormatter formatter = ReportObjectFactory.getFormatter(formatterNode);

                    // делаем что-то
                    formatter.execute(formatterNode, formatterData);
                }
            }
        }
    }

    /**
     * Функция проверяет нужно ли для какой-нибудь колонки подводить итог группировки и в случае необходимости
     * выводит либо промежуточный итог либо пустую строку-разделитель.
     * @param layoutProfile - Профиль лейаута
     * @param layoutData    - Данные лэйаута
     * @param previousRow   - Предыдущая строка
     * @param currentRow    - Текущая строка
     * @param columns       - Коллекция колонок
     * @param groupings     - какие-то группировки
     * @param dataTable     - таблица с данными
     * @param rowspans      - Текущие значения rowspan'ов для столбцов(см. {@link #writeRows}/>)
     * @param isLastRow     - Признак того, что текущая стока последняя и следовательно необходимо выводить подитог
     */
    protected void writeGroupBound(TableLayoutClass layoutProfile, ReportLayoutData layoutData, IDataRow previousRow,
                                   IDataRow currentRow, LayoutColumns columns, Map<String, Object> groupings,
                                   IDataTable dataTable, int[] rowspans, boolean isLastRow) {

        List<TableLayoutClass.GroupingClass> groupingList = layoutProfile.getGrouping();

        if (rowspans == null) {
            rowspans = new int[columns.count];
        }

        // проверяем необходимость вывода границы группировки
        if (groupingList == null) {
            return;
        }

        // Здесь надо сделать массив из tablelayoutGroupingGroupbyClass, отсортированный по boundpresentation,
        // т.е. с начала надо выводить группы с sub-total'ами, а потом с separator'ами

        // Список всех элементов r:group-by
        ArrayList groupedByArray = new ArrayList();

        //Соберу все r:group-by
        for (TableLayoutClass.GroupingClass grouping : groupingList) {
            if (grouping.getGroupBy() != null) {
                for (TableLayoutClass.GroupingClass.GroupByClass groupByNode : grouping.getGroupBy()) {
                    groupedByArray.add(groupByNode);
                }
            }
        }

        boolean isFirstRow = (getLayoutTable().getRowCount() == 0); // Признак того, что текущая стока - первая
        boolean isSeparatorDrawn = false; // Признак того, что сепаратор был уже нарисован
        boolean isWriteSimpleSeparator = false; // Признак того, что нужно будет отрисовать сепаратор
        boolean isWriteComplicateSeparator = false; // Признак того, что нужно будет отрисовать сложный сепаратор
        int minGroupingColumnIndex = Integer.MAX_VALUE; // Наименьший индекс колонки, по которой нужно группировать.

        // Это колонки с sub-total или sub-title по которым здесь надо будет осуществить группировку
        ArrayList currentlyGroupingColumns = new ArrayList();

        ///////////////////////////////////////////////////////////////////////////////////////
        // 1. Определение всех колонок, для которых нужно выводить подитоги или подзаголовки
        ///////////////////////////////////////////////////////////////////////////////////////
        // Прохожусь по всем r:group-by и проверяю надо ли выводить подитог(разделитель),
        // Если надо - делаю!
        for (int j = 0; j < groupedByArray.size(); j++) {
            TableLayoutClass.GroupingClass.GroupByClass groupByNode =
                    (TableLayoutClass.GroupingClass.GroupByClass)groupedByArray.get(j);

            // имя колонки, по которой нужно группировать данные
            String groupRsField = groupByNode.getN();

            int groupColumnIndex = columns.getColumnOrdinal(groupRsField);
            // колонка, по которой будем группировать
            LayoutColumn groupColumn = columns.getColumnByIndex(groupColumnIndex);

            // поставим флаг, что колонка является группирующей колонкой
            groupColumn.setGroupingColumn(true);

            // текущее значение
            Object currentValue;

            // Значение текущего ряда в группируемой колонке
            currentValue = currentRow.getItemByName(groupRsField);

            // Если значения перестали совпадать, значит в этом ряду пошли другие данные ,
            // (или указано, что мы должны вывести границу группировки для последней строки)
            // значит надо что-то сделать...
            if (!currentValue.equals(groupings.get(groupRsField)) || isLastRow || isFirstRow) {
                boolean isRowspannedCells = false;

                // Если до этой колонки уже есть столбец, у которого текущий Rowspans > 0,
                // то рисовать пустой новый ряд нельзя
                // Если после этой колонки есть столбец, у которого текущий Rowspans > 0,
                // то рисовать пустной новый ряд НУЖНО, только нужно пересчитать Rowspan
                // Проверка на это...
                for (int columnNum = 0; columnNum < columns.count; columnNum++) {
                    String fieldName = columns.getColumnByIndex(columnNum).getRsFileldName().replace("{#", "")
                            .replace("}", "");
                    // Определение минимального номера группируемой колонки (потребуется позже)
                    if (fieldName == groupRsField) {
                        if (columnNum < minGroupingColumnIndex) {
                            minGroupingColumnIndex = columnNum;
                        }
                    }

                    if (rowspans[columnNum] > 0) {
                        // Если это другая, не эта, колонка
                        if (fieldName != groupRsField) {
                            isRowspannedCells = true;
                        }
                    }
                }

                // Добавляем подзаголовок
                switch (groupByNode.getUpperBoundPresentation()) {
                    case SUB_TITLE:
                        // Добавляю колонку, для которой закончилась очередная группа
                        if (!currentlyGroupingColumns.contains(groupColumnIndex)) {
                            currentlyGroupingColumns.add(groupColumnIndex);
                        }
                        // Ставим флаг, что для колонки нужно выводить подзаголовок
                        groupColumn.setHasSubTitle(true);
                        break;

                    default:
                        throw new NotImplementedException("Dont implement for value:"
                                + groupByNode.getUpperBoundPresentation());
                }

                // Добавляем подитоги
                BoundPresentationEnumClass boundPresentation = groupByNode.getBoundPresentation();
                switch (boundPresentation) {
                    case SEPARATOR:
                        // Если сепаратор ещё ни для одной из групп не был отрисован...
                        if (!isSeparatorDrawn) {
                            //Если это не для последнй строки(А зачем для последней строки рисовать пробел???)
                            if (!isLastRow) {
                                if (!isRowspannedCells) {
                                    isWriteSimpleSeparator = true;
                                } else {
                                    // Сложный случай
                                    isWriteComplicateSeparator = true;
                                }

                                isSeparatorDrawn = true; //Отрисовали сепаратор.
                            }
                        }
                        break;

                    case SUB_TOTAL:
                        // Добавляю колонку, для которой закончилась очередная группа
                        if (!currentlyGroupingColumns.contains(groupColumnIndex)) {
                            currentlyGroupingColumns.add(groupColumnIndex);
                        }
                        // При добавлении в массив ставим флаг, что колонка поставлена на ожидание на вывод итогов
                        // по ней

                        groupColumn.setPendingForSubtotal(true);
                        // Ставим флаг, что для колонки нужно выводить подитоги
                        groupColumn.setHasSubTotal(true);
                        break;

                    default:
                        throw new NotImplementedException("Dont implement for value:" + boundPresentation);
                }

                // Поменяю текущее группирующее значения для этой колонки
                groupings.put(groupRsField, currentValue);
            } // if появилось новое значения для группируемой колонки
        } // Цикл по всем r:group-by


        ///////////////////////////////////////////////////////////////////////////////////////
        /// 2.    Добавление в список группируемых колонок тех, у которых индекс больше наибольшего максимального из
        ///         группируемых
        ///////////////////////////////////////////////////////////////////////////////////////
        // Снова пройдусь по всем заданным группируемым колонкам. Теперь цель - найти все колонки, по которым группируем
        // в принципе, однако не попали в список aCurrentlyGroupingColumns.
        // Если индекс такой колонки > nMinGroupedColumnIndex, то добавляю её в список aCurrentlyGroupingColumns
        // насильствено, при условии, что boundpresentation для этой группировки равен subtotal.
        for (int j = 0; j < groupedByArray.size(); j++) {
            TableLayoutClass.GroupingClass.GroupByClass groupByNode =
                    (TableLayoutClass.GroupingClass.GroupByClass)groupedByArray.get(j);

            String groupRsField = groupByNode.getN();

            for (int columnNum = 0; columnNum < columns.count; columnNum++) {
                // Если номер колонки больше nMinGroupingColumnIndex,
                // то добавляю её в список aGroupedColumns насильствено.
                if (columns.getColumnByIndex(columnNum).getRsFileldName().replace("{#", "").replace("}", "")
                        == groupRsField) {
                    if (!currentlyGroupingColumns.contains(columnNum)
                            && columnNum > minGroupingColumnIndex && minGroupingColumnIndex < Integer.MAX_VALUE) {
                        if (groupByNode.getBoundPresentation() == BoundPresentationEnumClass.SUB_TOTAL
                                || groupByNode.getUpperBoundPresentation()
                                == UpperBoundPresentationEnumClass.SUB_TITLE) {
                            currentlyGroupingColumns.add(columnNum);

                            // При добавлении в массив ставим флаг, что колонка поставлена на ожидание на вывод итогов
                            // по ней
                            columns.getColumnByIndex(columnNum).pendingForSubtotal = true;

                            // Если вдруг для колонки добавляемой насильственно на вывод результатов по ней
                            // ColumnsRowpsan > 1, тогда нужно его обнулить и пересчитать RowpsanCount для
                            // ячейки, которой Rowspan начинается.
                            if (rowspans[columnNum] > 0) {
                                // Обнуляем Rowspan
                                rowspans[columnNum] = 0;
                                // Самое важное не забыть пересчитать RowspanCount для той ячейки, которая его начинает
                                int layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                                while (true) {
                                    if (layoutTableRowNumber < 0) {
                                        break;
                                    }

                                    LayoutCell currentCell = getLayoutTable()
                                            .getRow(layoutTableRowNumber).getRowCells().get(columnNum);

                                    if (currentCell != null) {
                                        if (currentCell.isStartsRowspanedCells()) {
                                            // Поставлю правильное значение RowspanCount'a
                                            currentCell.setRowspanCount(
                                                    getLayoutTable().getRowCount() - layoutTableRowNumber
                                            );
                                            break;
                                        }
                                    }

                                    layoutTableRowNumber = layoutTableRowNumber - 1;
                                }
                            }
                        } else if (groupByNode.getBoundPresentation() == BoundPresentationEnumClass.SEPARATOR) {
                            // Если вдруг оказалось, что группирующая колонка, по которой нужно насильственно вывести
                            // итоги, должна выводить сепаратор, то и скажем ей выводить сепаратор.

                            boolean isRowspannedCells = false;

                            for (int colIdx = 0; colIdx < columns.getCount(); colIdx++) {
                                if (rowspans[colIdx] > 0) {
                                    // Если это другая, не эта, колонка
                                    if (columns.getColumnByIndex(colIdx).getRsFileldName() != groupRsField) {
                                        isRowspannedCells = true;
                                    }
                                }
                            }

                            // Если сепаратор ещё ни для одной из групп не был отрисован...
                            if (!isSeparatorDrawn) {
                                //Если это не для последнй строки(А зачем для последней строки рисовать пробел???)
                                if (!isLastRow) {
                                    if (!isRowspannedCells) {
                                        isWriteSimpleSeparator = true;
                                    } else {
                                        // Сложный случай
                                        isWriteComplicateSeparator = true;
                                    }

                                    isSeparatorDrawn = true; //Отрисовали сепаратор.
                                }
                            }
                        }
                    }
                }
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////
        /// 3.    Отсортирую массив с номерами колонок, по которым необходимо произвести
        ///     подсчет результатов. Сортиртирую в обратном порядке.
        ///////////////////////////////////////////////////////////////////////////////////////
        Collections.sort(currentlyGroupingColumns);
        Collections.reverse(currentlyGroupingColumns);

        /////////////////////////////////////////////////////////////////////////////////
        ///    4. Вывод итогов:
        /// 4.1. Выводим с начала результаты по колонке с наибольшим порядковым номером
        /// 4.2. Не возможна ситуация, когда у колонки с большим порядковым номером кол-во
        ///      группируемых по ней строк будет больше, чем кол-во группируемых строк по
        ///         колонке с меньшим порядковым номером.
        /////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < currentlyGroupingColumns.size(); i++) {
            // Номер колонки, по которой производится группировка
            int currentlyGroupingColumnIdx = (int)currentlyGroupingColumns.get(i);

            if (!columns.getColumnByIndex(currentlyGroupingColumnIdx).isHasSubTotal()) {
                continue;
            }

            // Номер текущей строкив  LayoutTable
            int rowNumber = getLayoutTable().getRowCount();

            // Количество группируемых ячеек для данного подытога
            int groupedCellsCount = 1;

            // Рассчет числа строк, по которым подсчитываются итоги по данной колонке
            while (true) {
                if (rowNumber < 0) {
                    // Если дошли до начала, не встретив ни одной Aggregated строки, значит группировать
                    // нужно по всем строкам
                    groupedCellsCount = getLayoutTable().getRowCount();

                    break;
                }

                LayoutCell currentCell = getLayoutTable().getCell(rowNumber, currentlyGroupingColumnIdx);

                if (currentCell != null) {

                    // Если натолкнулись на начало роуспана
                    if (currentCell.isStartsRowspanedCells()) {
                        // Надо получить oCurrentCell.RowspanCount
                        groupedCellsCount = currentCell.getRowspanCount();
                        break;
                    }

                    // Если натолкнулись на аггрегатную ячейку
                    if (currentCell.isAggregated()) {
                        groupedCellsCount = getLayoutTable().getRowCount() - rowNumber - 1;
                        break;
                    }
                }
                rowNumber = rowNumber - 1;
            } //while

            if (currentlyGroupingColumnIdx >= 0) {
                // Для данной колонки сбросим флаг ожидания вывода результатов
                columns.getColumnByIndex(currentlyGroupingColumnIdx).setPendingForSubtotal(false);
            }

            // Выведу подытог
            if (!isFirstRow) {
                writeTotalRow(layoutProfile, layoutData, columns, -1, currentlyGroupingColumnIdx, true, dataTable,
                        rowspans, groupedCellsCount, previousRow);
            }
        }
        // После отрисовки подитогов можно вывести разделитель...
        // Если надо выводить простую строку-разделитель...
        if (isWriteSimpleSeparator && !isFirstRow) {
            writeGroupBoundSeparator(columns);
        } else if (isWriteComplicateSeparator && !isFirstRow) {
            // Сложный случай(часть ячеек надо зароуспанить с предыдущими)
            writeGroupBoundSeparatorEx(columns, rowspans);
        }

        ///////////////////////////////////////////////////////////////////////////////////////
        /// 5.    Переверну массив с номерами колонок, по которым необходимо произвести
        ///     подсчет результатов.
        ///////////////////////////////////////////////////////////////////////////////////////
        Collections.reverse(currentlyGroupingColumns);

        // обновляем значения переменных
        setVarValues(layoutProfile, layoutData, currentRow, -1);

        /////////////////////////////////////////////////////////////////////////////////
        ///    6. Вывод подзаголовков.
        /////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < currentlyGroupingColumns.size(); i++) {
            // Номер колонки, по которой производится группировка
            int currentlyGroupingColumnIdx = (int)currentlyGroupingColumns.get(i);

            if (!columns.getColumnByIndex(currentlyGroupingColumnIdx).isHasSubTitle()) {
                continue;
            }

            // Выведу подзагловок
            if (!isLastRow) {
                writeSubTitleRow(layoutProfile, layoutData, columns, -1, currentlyGroupingColumnIdx, dataTable,
                        rowspans);
            }
        }
    }

    /**
     * Рисует строку подзаголовка.
     * @param layoutProfile    - профиль лэйаута
     * @param layoutData       - Данные лэйаута
     * @param columns          - описание колонок лэйаута
     * @param currentRowNum    - номер строки
     * @param currentColumnNum - Используется для указания функции номера колонки, по которой идет аггрегация данных
     * @param dataTable        - таблица с данными
     * @param columnsRowspan   - текущая величина Rowspana для ячеек строки
     */
    protected void writeSubTitleRow(TableLayoutClass layoutProfile, ReportLayoutData layoutData,
                                    LayoutColumns columns, int currentRowNum, int currentColumnNum,
                                    IDataTable dataTable, int[] columnsRowspan) {

        // формируем строку типа DataRow
        IDataTable table;
        Object[] rowData;

        // Если задана таблица с данными используем её
        table = dataTable.clone();
        rowData = new Object[dataTable.getColumns().getCount()];

        // TODO: странно, что новая строка запрашивается у переданной таблицы, а не у клонированной
        // М.б. это не на что и не влияет, но в WriteTotalRow сделано иначе.
        IDataRow currentRow = dataTable.newRow();

        //region Рассчет DataRow с результатами группировки

        // проходим по колонкам отчета и расчитываем значение каждой ячейки даннной строки
        for (int columnNum = 0; columnNum < columns.count; columnNum++) {
            // только для тех колонок, для которых задано поле ридера
            if (columns.getColumnByIndex(columnNum).getRsFileldName().length() != 0) {
                try {
                    // номер колонки в ридере
                    int readerColumnOrdinal = table.getColumns().getColumnByName(
                            columns.getColumnByIndex(columnNum).getRsFileldName().replace("{#", "").replace("}", ""))
                            .getOrdinal();
                    if (columns.getColumnByIndex(columnNum).getAggregationStringSubTitle().length() != 0
                            && columnNum == currentColumnNum) {
                        rowData[readerColumnOrdinal] = MacroProcessor.process(
                                columns.getColumnByIndex(columnNum).getAggregationStringSubTitle(), layoutData);
                    }
                } catch (Exception e) {
                    // В первоисточнике здесь не было никаких действий
                }
            }
        }

        //endregion

        // Здесь заложена огромная возможность для эксепшинов. Это бомба! - Изменить работу с данными из БД!!!!
        // Только что итоговая строка делалась как массив object. А теперь этот массив из object тупо
        // присваивается строке строготипизированной таблицы.
        currentRow.setItemArray(rowData);

        // Добавляю строку
        getLayoutTable().addRow();

        int currentColspan = 1; // Счетчик текущего для строки Colspan'a

        // инициализация null'евого Rowspan'a
        if (columnsRowspan == null) {
            columnsRowspan = new int[columns.count];
        }

        // проходим по колонкам отчета и добавляем ячейки в таблицу LayoutTable
        for (int columnNum = 0; columnNum < columns.count; columnNum++) {
            // количество объединяемых столбцов
            int colspan = 1;
            if (columns.getColumnByIndex(columnNum).getAggregationColspan().length() != 0) {
                colspan = Integer.parseInt(columns.getColumnByIndex(columnNum).getAggregationColspan());
            }
            if (colspan < 0) {
                colspan = 1;
            }

            // Вычисляем значение ячейки
            ReportFormatterData data = calculateSubTitleCellValue(layoutData, columns, currentRowNum, columnNum,
                    currentRow);
            Object cellValue = null != data.getCurrentValue() ? data.getCurrentValue() : StringUtils.EMPTY;
            String style = data.getClassName();

            // 1) Если номер текущей колонки равен номеру колонки по которой производиться
            // группировка, то просто выводим ячейку по стандартному алгоритму.
            if (columnNum == currentColumnNum) {
                getLayoutTable().getCurrentRow().addCell(cellValue, columns.getColumnByIndex(columnNum).getType(), 1,
                        colspan, style);

                // Если установлен флаг Colspan'a ячейка будет fake.
                if (currentColspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                    // Уменьшаю CurrentColspan
                    currentColspan--;
                } else if (colspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);
                    getLayoutTable().getCurrentRow().getCurrentCell().setColumnspanCount(colspan);
                    // Флаг начала колспана
                    currentColspan = colspan;
                }
                // признак промежуточных итогов
                getLayoutTable().getCurrentRow().getCurrentCell().setAggregated(true);
            } else if (columnNum > currentColumnNum) {
                // 2) Если колонка находится правее колонки, по которой производится группировка...
                getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, colspan, style);

                // Если установлен флаг Colspan'a ячейка будет fake.
                if (currentColspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                    // Уменьшаю CurrentColspan
                    currentColspan--;
                } else if (colspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);
                    getLayoutTable().getCurrentRow().getCurrentCell().setColumnspanCount(colspan);
                    // Флаг начала колспана
                    currentColspan = colspan;
                }
                // признак промежуточных итогов
                getLayoutTable().getCurrentRow().getCurrentCell().setAggregated(true);
                // признак подзаголовка без группировки
                getLayoutTable().getCurrentRow().getCurrentCell().setNotGroupSubTitle(true);
            } else if (columnNum < currentColumnNum) {
                // 3) Если колонка находится левее колонки, по которой производится группировка...
                if (columns.getColumnByIndex(columnNum).getRowspanBy().length() == 0
                        || columnsRowspan[columnNum] == 0) {
                    getLayoutTable().getCurrentRow()
                            .addCell(cellValue, columns.getColumnByIndex(columnNum).getType(), 1, 1, style);
                } else {
                    for (int i = getLayoutTable().getRowCount() - 1; i >= 0; i--) {
                        LayoutCell cell = getLayoutTable().getCell(i, columnNum);
                        if (cell != null) {
                            if (cell.isStartsRowspanedCells()) {
                                cell.setRowspanCount(cell.getRowspanCount() + 1);
                                break;
                            }
                        }
                    }
                    getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1, style);
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                    getLayoutTable().getCurrentRow().getCurrentCell().setRowspaned(true);
                }
                // признак подзаголовка без группировки
                getLayoutTable().getCurrentRow().getCurrentCell().setNotGroupSubTitle(true);
            }
        }
    }

    /**
     * Рисует строку подведения итогов. Это может быть как строка с общими итогами, так и с промежуточными.
     * @param layoutProfile     - профиль отчета
     * @param layoutData        - Данные лэйаута
     * @param columns           - описание колонок лэйаута
     * @param currentRowNum     - номер строки
     * @param currentColumnNum  - Используется для указания функции номера колонки, по которой идет аггрегация данных
     * @param subTotals         - признак вывода подитогов
     * @param dataTable         - таблица с данными
     * @param columnsRowspan    - текущая величина Rowspana для ячеек строки
     * @param groupedCellsCount - Кол-во сток для которого выводиться подитог
     * @param previousRow       - предыдущая строка в таблице
     */
    protected void writeTotalRow(TableLayoutClass layoutProfile, ReportLayoutData layoutData, LayoutColumns columns,
                                 int currentRowNum, int currentColumnNum, boolean subTotals, IDataTable dataTable,
                                 int[] columnsRowspan, int groupedCellsCount, IDataRow previousRow) {

        // формируем строку типа DataRow
        IDataTable table;
        Object[] rowData;

        // Если задана таблица с данными используем её
        table = dataTable.clone();
        rowData = new Object[dataTable.getColumns().getCount()];

        IDataRow currentRow = table.newRow();

        //region Расчет DataRow с результатами группировки

        // проходим по колонкам отчета и расчитываем значение каждой ячейки даннной строки
        for (int columnNum = 0; columnNum < columns.count; columnNum++) {
            // только для тех колонок, для которых задано поле ридера
            if (columns.getColumnByIndex(columnNum).getRsFileldName().length() != 0) {
                try {
                    // номер колонки в ридере
                    int readerColumnOrdinal = table.getColumns().getColumnByName(
                            columns.getColumnByIndex(columnNum).getRsFileldName().replace("{#", "").replace("}", ""))
                            .getOrdinal();

                    if (columns.getColumnByIndex(columnNum).getAggregationFunction().length() != 0) {
                        // !! в строке итогов в этом столбце значение, подсчитанное соответствующей ф-цией

                        // Если нужно вывести подитоги
                        if (subTotals) {
                            // Если промежуточные итоги еще не сброшены, возьму значение из колонок
                            if (!columns.getColumnByIndex(columnNum).isSubtotalReset()) {
                                rowData[readerColumnOrdinal] = columns.getColumnByIndex(columnNum).getSubTotal();
                            } else {
                                //Если уже сброшены, то придется расчитывать
                                int startRow = getLayoutTable().getRowCount() - groupedCellsCount;
                                int endRow = getLayoutTable().getRowCount();
                                rowData[readerColumnOrdinal] = getLayoutTable().getAggregatedValue(
                                        columnNum, columns.getColumnByIndex(columnNum).getAggregationFunction(),
                                        startRow, endRow);
                            }
                        } else {
                            // Если нужно вывести итоги
                            // Если итоги еще не сброшены...
                            if (!columns.getColumnByIndex(columnNum).isTotalReset()) {
                                rowData[readerColumnOrdinal] = columns.getColumnByIndex(columnNum).getTotal();
                            } else {
                                rowData[readerColumnOrdinal] = getLayoutTable().getAggregatedValue(
                                        columnNum, columns.getColumnByIndex(columnNum).getAggregationFunction());
                            }
                        }
                    } else if (subTotals
                            && columns.getColumnByIndex(columnNum).getAggregationStringSubTotals().length() != 0) {
                        // для группирующего столюца формируем строку подитогов
                        if (columnNum == currentColumnNum) {
                            rowData[readerColumnOrdinal] = MacroProcessor.process(
                                    columns.getColumnByIndex(columnNum).getAggregationStringSubTotals(), layoutData);
                        } else {
                            // для остальных столбцов берем значение из предыдущей строки
                            rowData[readerColumnOrdinal] = previousRow.getItem(readerColumnOrdinal);
                        }
                    } else if (!subTotals && columns.getColumnByIndex(columnNum).getAggregationString().length() != 0) {
                        // формируем строку итогов
                        rowData[readerColumnOrdinal] = MacroProcessor.process(
                                columns.getColumnByIndex(columnNum).getAggregationString(), layoutData);
                    } else {
                        // в строке итогов в этом столбце ничего нет
                        rowData[readerColumnOrdinal] = null;
                    }

                    columns.getColumnByIndex(columnNum).resetTotals(false);
                } catch (Exception e) {
                    // В первоисточнике здесь не было никаких действий
                }
            }
        }

        //endregion

        // Здесь заложена огромная возможность для эксепшинов. Это бомба! - Изменить работу с данными из БД!!!!
        // Только что итоговая строка делалась как массив object. А теперь этот массив из object тупо
        // присваивается строке строготипизированной таблицы.
        currentRow.setItemArray(rowData);

        // Обновляем значения переменных
        setVarValues(layoutProfile, layoutData, currentRow, currentRowNum);

        // Добавляю строку
        getLayoutTable().addRow();

        ///////////////////////////////////////////////////////////////////////////////////////
        ///            Секция формирования строки с итогами в LayoutTable
        ///////////////////////////////////////////////////////////////////////////////////////
        int currentColspan = 1; // Счетчик текущего для строки Colspan'a
        int layoutTableRowNumber; // Текущий номер строки в LayoutTable

        // инициализация null'евого Rowspan'a
        if (columnsRowspan == null) {
            columnsRowspan = new int[columns.count];
        }

        // Когда нам нужно вывести строчку с общими итогами, то нет никаких CurrentColumnNum и пр. вещей,
        // связанных с промежуточными итогами.
        if (currentColumnNum == -1) {
            // проходим по колонкам отчета и добавляем ячейки в таблицу LayoutTable по стандартному алгоритму
            for (int columnNum = 0; columnNum < columns.count; columnNum++) {
                // количество объединяемых столбцов
                int colspan = 1;
                if (columns.getColumnByIndex(columnNum).getAggregationColspan().length() != 0) {
                    colspan = Integer.valueOf(columns.getColumnByIndex(columnNum).getAggregationColspan());
                }
                if (colspan < 0) {
                    colspan = 1;
                }

                // Вычисляем значение ячейки
                ReportFormatterData data = calculateTotalCell(layoutProfile, layoutData, columns, currentRowNum,
                        columnNum, currentRow, colspan, subTotals);
                Object cellValue = null != data.getCurrentValue() ? data.getCurrentValue() : StringUtils.EMPTY;
                String style = data.getClassName();

                // Пока Colspan = 1 спокойно добавляем ячейки в LayoutTable. Как только наталкнулись на ячейку, у
                // которой Colspan > 1 выставляем флаг, что начался Colspan и все последующий ячейки, входящие в Colspn
                // добавляем с флагом  fake.
                getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                        columns.getColumnByIndex(columnNum).getType(), 1, colspan, style);

                // Если установлен флаг Colspan'a ячейка будет fake.
                if (currentColspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                    // Уменьшаю CurrentColspan
                    currentColspan -= 1;
                } else if (colspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);
                    // Флаг начала колспана
                    currentColspan = colspan;
                }
                // признак промежуточных итогов
                getLayoutTable().getCurrentRow().getCurrentCell().setAggregated(true);
            }

            // Дальше в WriteTotalRow делать нечего, выходим...
            return;
        }

        // проходим по колонкам отчета и добавляем ячейки в таблицу LayoutTable
        for (int columnNum = 0; columnNum < columns.count; columnNum++) {
            // количество объединяемых столбцов
            int colspan = 1;
            if (columns.getColumnByIndex(columnNum).getAggregationColspan().length() != 0) {
                colspan = Integer.valueOf(columns.getColumnByIndex(columnNum).getAggregationColspan());
            }
            if (colspan < 0) {
                colspan = 1;
            }

            // Вычисляем значение ячейки
            ReportFormatterData data = calculateTotalCell(layoutProfile, layoutData, columns, currentRowNum,
                    columnNum, currentRow, colspan, subTotals);
            Object cellValue = null != data.getCurrentValue() ? data.getCurrentValue() : StringUtils.EMPTY;
            String style = data.getClassName();

            // 1) Если номер текущей колонки равен номеру колонки по которой производиться
            // группировка, то просто выводим ячейку по стандартному алгоритму.
            if (columnNum == currentColumnNum) {
                // Пока Colspan = 1 спокойно добавляем ячейки в LayoutTable. Как только наталкнулись на ячейку, у
                // которой Colspan > 1 выставляем флаг, что начался Colspan и все последующий ячейки, входящие в Colspn
                // добавляем с флагом  fake.
                getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                        columns.getColumnByIndex(columnNum).getType(), 1, colspan, style);

                // Если установлен флаг Colspan'a ячейка будет fake.
                if (currentColspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                    // Уменьшаю CurrentColspan
                    currentColspan -= 1;
                } else if (colspan > 1) {
                    getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);
                    // Флаг начала колспана
                    currentColspan = colspan;
                }
                // признак промежуточных итогов
                getLayoutTable().getCurrentRow().getCurrentCell().setAggregated(true);
            } else if (columns.getColumnByIndex(columnNum).isPendingForSubtotal()) {
                // 2) Если имеем дело с колонкой, стоящей в очереди ожидания вывода результатов
                //    группировки по ней...
                // 2.1) Если колонка находится левее колонки, по которой производится текущая
                //        группировка...
                if (columnNum < currentColumnNum) {
                    // 2.1.1) Если для данной колонки Rowspan > 0, тогда нужно добавить fake-ячейку
                    //          и увеличить значение RowspanCount для ячейки, начинающий Rowspan.
                    //          Т.е. продолжаем рисовать Rowspan.
                    if (columnsRowspan[columnNum] > 0) {
                        layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                        // Увеличение значение RowspanCount
                        while (true) {
                            if (layoutTableRowNumber < 0) {
                                break;
                            }

                            LayoutCell currentCell = getLayoutTable().getCell(layoutTableRowNumber, columnNum);

                            if (currentCell != null) {
                                if (currentCell.isStartsRowspanedCells()) {
                                    //2. Увеличу для этой ячейки текущую величину Rowspan'a
                                    currentCell.setRowspanCount(currentCell.getRowspanCount() + 1);
                                    break;
                                }
                            }

                            layoutTableRowNumber = layoutTableRowNumber - 1;
                        }

                        // ... И вставить fake - ячейку, чтобы не нарушать стркутуру матрицы LayoutTable
                        getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                        getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                        // Если установлен флаг Colspan'a нужно уменьшить текущий Colspan.
                        if (currentColspan > 1) {
                            // Уменьшаю CurrentColspan
                            currentColspan -= 1;
                        }
                    }

                    // 2.1.2) Если для данной колонки Rowspan <= 1, тогда нужно добавить rowspaned-ячейку,
                    //      затем в предыдущих рядах этого столбца найти первую не fake-ячейку и не Rowspaned-ячейку
                    //      и сказать той ячейке, что она начинает Rowspan и поставить для неё значение RowpsanCount
                    //      равное количеству объединяемых рядов
                    if (columnsRowspan[columnNum] <= 1) {
                        // Добавление rowspaned-ячейки
                        getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                        getLayoutTable().getCurrentRow().getCurrentCell().setRowspaned(true);
                        // Если установлен флаг Colspan'a нужно уменьшить текущий Colspan.
                        if (currentColspan > 1) {
                            getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                            // Уменьшаю CurrentColspan
                            currentColspan -= 1;
                        }

                        layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                        // Найдем первую начиная с этого ряда не fake-ячейку и не rowspaned-ячейку...
                        while (true) {
                            if (layoutTableRowNumber < 0) {
                                break;
                            }

                            LayoutCell currentCell = getLayoutTable().getCell(layoutTableRowNumber, columnNum);

                            if (currentCell != null) {
                                if (!currentCell.isFakeCell() && !currentCell.isRowspaned()) {
                                    //Поставлю для этой ячейки правильное значение Rowspan'a
                                    currentCell.setRowspanCount(getLayoutTable().getRowCount() - layoutTableRowNumber);
                                    break;
                                }
                            }

                            layoutTableRowNumber = layoutTableRowNumber - 1;
                        }
                    }
                }

                // 2.2) Если колонка находится правее колонки, по которой производится
                //        группировка... Считаем, что такая ситуация не возможна.
            } else {
                // 3) Если это обычная колонка...

                // 3.1) Если колонка находится левее колонки, по которой производится
                //        группировка...
                if (columnNum < currentColumnNum) {
                    // 3.1.1) Если для данной колонки Rowspan > 0, тогда нужно добавить fake-ячейку
                    //          и увеличить значение RowspanCount для ячейки, начинающий Rowspan.
                    //          Т.е. продолжаем рисовать Rowspan.
                    if (columnsRowspan[columnNum] > 0) {
                        layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                        // Увеличение значение RowspanCount
                        while (true) {
                            if (layoutTableRowNumber < 0) {
                                break;
                            }

                            LayoutCell currentCell = getLayoutTable().getCell(layoutTableRowNumber, columnNum);

                            if (currentCell != null) {
                                if (currentCell.isStartsRowspanedCells()) {
                                    //2. Увеличу для этой ячейки текущую величину Rowspan'a
                                    currentCell.setRowspanCount(currentCell.getRowspanCount() + 1);
                                    break;
                                }
                            }

                            layoutTableRowNumber = layoutTableRowNumber - 1;
                        }

                        // ... И вставить fake - ячейку, чтобы не нарушать стркутуру матрицы LayoutTable
                        getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                        getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                        getLayoutTable().getCurrentRow().getCurrentCell().setRowspaned(true);
                        // Если установлен флаг Colspan'a нужно уменьшить текущий Colspan.
                        if (currentColspan > 1) {
                            // Уменьшаю CurrentColspan
                            currentColspan -= 1;
                        }
                    } else if (columnsRowspan[columnNum] <= 1) {
                        // 3.1.2) Если для данной колонки Rowspan <= 1...
                        // 3.1.2.1) Если для данной колонки Rowspan <= 1, тогда нужно добавить rowspaned-ячейку,
                        //      затем в предыдущих рядах этого столбца найти первую не fake-ячейку
                        //       и не Rowspaned-ячейку и сказать той ячейке, что она начинает Rowspan и поставить
                        //      для неё значение RowpsanCount равное количеству объединяемых рядов
                        if (columns.getColumnByIndex(columnNum).isGroupingColumn()) {
                            // Добавление rowspaned-ячейки
                            getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                            getLayoutTable().getCurrentRow().getCurrentCell().setRowspaned(true);
                            // Если установлен флаг Colspan'a нужно уменьшить текущий Colspan.
                            if (currentColspan > 1) {
                                getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                                // Уменьшаю CurrentColspan
                                currentColspan -= 1;
                            }

                            layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                            // Найдем первую начиная с этого ряда не fake-ячейку и не rowspaned-ячейку...
                            while (true) {
                                if (layoutTableRowNumber < 0) {
                                    break;
                                }

                                LayoutCell currentCell = getLayoutTable().getCell(layoutTableRowNumber, columnNum);

                                if (currentCell != null) {
                                    if (!currentCell.isFakeCell() && !currentCell.isRowspaned()) {
                                        //Поставлю для этой ячейки правильное значение Rowspan'a
                                        currentCell.setRowspanCount(
                                                getLayoutTable().getRowCount() - layoutTableRowNumber);
                                        break;
                                    }
                                }

                                layoutTableRowNumber = layoutTableRowNumber - 1;
                            }
                        } else {
                            // 3.1.2.2) Если колонка не является группирующей, просто выводим ячейку по
                            //          стандартному алгоритму.
                            // Пока Colspan = 1 спокойно добавляем ячейки в LayoutTable. Как только наталкнулись на
                            // ячейку, у которой Colspan > 1 выставляем флаг, что начался Colspan и все последующий
                            // ячейки, входящие в Colspn добавляем с флагом  fake.
                            getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                                    columns.getColumnByIndex(columnNum).getType(), 1, colspan, style);

                            // Если установлен флаг Colspan'a ячейка будет fake.
                            if (currentColspan > 1) {
                                getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                                // Уменьшаю CurrentColspan
                                currentColspan -= 1;
                            } else if (colspan > 1) {
                                getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);
                                // Флаг начала колспана
                                currentColspan = colspan;
                            }
                            // признак промежуточных итогов
                            getLayoutTable().getCurrentRow().getCurrentCell().setAggregated(true);
                        }
                    }
                }

                // 3.2) Если колонка находится правее колонки, по которой производится
                //        группировка...
                if (columnNum > currentColumnNum) {

                    // 3.2.1) Если для данной колонки Rowspan > 0...
                    if (columnsRowspan[columnNum] > 0) {
                        // 3.2.1.1) Если текущий ColspanCount > 1, то нужно прервать Rowspan. Для этого
                        //            устанавливаем для данной колонки Rowpsan=0, и пересчитываем значение
                        //          RowspanCount для ячейки, начинающей Rowspаn. Затем вставляем fake-ячейку.
                        if (currentColspan > 1) {
                            // Добавляем ячейку
                            getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                                    columns.getColumnByIndex(columnNum).getType(), 1, colspan, style);
                            // Говорим её, что она fake
                            getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                            // Уменьшаем CurrentColspan
                            currentColspan -= 1;

                            // Обнуляем Rowspan
                            columnsRowspan[columnNum] = 0;
                            // Самое важное не забыть пересчитать RowspanCount для той ячейки, которая его начинает
                            layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                            while (true) {
                                if (layoutTableRowNumber < 0) {
                                    break;
                                }

                                LayoutCell currentCell = getLayoutTable().getCell(layoutTableRowNumber, columnNum);

                                if (currentCell != null) {
                                    if (currentCell.isStartsRowspanedCells()) {
                                        // Поставлю правильное значение RowspanCount'a
                                        currentCell.setRowspanCount(
                                                getLayoutTable().getRowCount() - layoutTableRowNumber - 1);
                                        break;
                                    }
                                }

                                layoutTableRowNumber = layoutTableRowNumber - 1;
                            }
                        } else {
                            // 3.2.1.2) Если в строке нет текущего Colspan'a. Тогда нужно добавить fake-ячейку
                            //            и увеличить значение RowspanCount для ячейки, начинающий Rowspan.
                            //            Т.е. продолжаем рисовать Rowspan.
                            layoutTableRowNumber = getLayoutTable().getRowCount() - 1;

                            // Увеличение значение RowspanCount
                            while (true) {
                                if (layoutTableRowNumber < 0) {
                                    break;
                                }

                                LayoutCell currentCell = getLayoutTable().getCell(layoutTableRowNumber, columnNum);

                                if (currentCell != null) {
                                    if (currentCell.isStartsRowspanedCells()) {
                                        //2. Увеличу для этой ячейки текущую величину Rowspan'a
                                        currentCell.setRowspanCount(currentCell.getRowspanCount() + 1);
                                        break;
                                    }
                                }

                                layoutTableRowNumber = layoutTableRowNumber - 1;
                            }

                            // ... И вставить fake - ячейку, чтобы не нарушать стркутуру матрицы LayoutTable
                            getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                            getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                            getLayoutTable().getCurrentRow().getCurrentCell().setRowspaned(true);
                            // Если установлен флаг Colspan'a нужно уменьшить текущий Colspan.
                            if (currentColspan > 1) {
                                // Уменьшаю CurrentColspan
                                currentColspan -= 1;
                            }
                        }
                    } else if (columnsRowspan[columnNum] <= 1) {
                        // 3.2.2) Если для данной колонки Rowspan <= 1...

                        // 3.2.2.1) Если колонка является группирующей колонкой...
                        //            Прим.: Это ситуация не ожидающей вывода результатов по ней группирующей колонки,
                        //              находящийся справа от колонки, по которой в настоящий момент выводятся
                        //              результаты. При этом Rowspan для колонки равен нулю. Это означает, что
                        //            только что была добавлена строка результатов по данной колонке. А в текущей
                        //              строке мы должны либо объединить ячейку с текущим Colspan'ом, либо вставить
                        //              отдельную пустую ячейку.
                        if (columns.getColumnByIndex(columnNum).isGroupingColumn()) {
                            // Добавляем fake-ячейку
                            getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);

                            if (currentColspan > 1) {
                                getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);

                                // Уменьшаю CurrentColspan
                                currentColspan -= 1;
                            }

                        } else {
                            // 3.2.2.2) Если колонка не является группирующей колонкой, то просто выводим ячейку
                            //            по стандартному алгоритму.

                            // Пока Colspan = 1 спокойно добавляем ячейки в LayoutTable. Как только наталкнулись
                            // на ячейку, у которой Colspan > 1 выставляем флаг, что начался Colspan и все последующий
                            // ячейки, входящие в Colspn добавляем с флагом  fake.
                            getLayoutTable().getCurrentRow().addCell(cellValue, data.getRawCurrentValue(),
                                    columns.getColumnByIndex(columnNum).getType(), 1, colspan, style);

                            // Если установлен флаг Colspan'a ячейка будет fake.
                            if (currentColspan > 1) {
                                getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                                // Уменьшаю CurrentColspan
                                currentColspan -= 1;
                            } else if (colspan > 1) {
                                getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);
                                // Флаг начала колспана
                                currentColspan = colspan;
                            }
                            // признак промежуточных итогов
                            getLayoutTable().getCurrentRow().getCurrentCell().setAggregated(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * рисует границу группировки - строку-разделитель.
     * @param columns - описание колонок лэйаута
     */
    protected void writeGroupBoundSeparator(LayoutColumns columns) {

        // Просто добавляю пустую стоку
        getLayoutTable().addRow();
        getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, columns.count);

        // для симметрии таблицы добавляем пустые ячейки
        for (int n = 0; n < columns.count - 1; n++) {
            getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
            getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
        }
    }

    /**
     * рисует границу группировки - строку-разделитель в сложном случае.
     * @param columns         - значения rowspan'ов для текущей строки
     * @param columnsRowspans - описания rowspan'ов  для колонок
     */
    protected void writeGroupBoundSeparatorEx(LayoutColumns columns, int[] columnsRowspans) {

        int[] columnspan = new int[columns.count];

        // Новый ряд
        getLayoutTable().addRow();

        // Прохожусь по всем колонкам и расчитаю column-span'ы
        for (int columnNum = 0; columnNum < columns.count; columnNum++) {
            // Если Rowspan для ячейки >0, вставлю единарную ячейку
            if (columnsRowspans[columnNum] > 0) {
                // Колспан тупа 1
                columnspan[columnNum] = 1;

                // Увеличивая количество ячеек, надо позаботиться и об увеличении текущего
                // значения Rowspan для этой колонки:

                //1. Найду первую ячейку, с которой начинается rowspan этого столбца
                int rowNumber = getLayoutTable().getRowCount() - 1;
                while (true) {
                    if (rowNumber < 0) {
                        break;
                    }

                    LayoutCell currentCell = getLayoutTable().getCell(rowNumber, columnNum);

                    if (currentCell != null) {
                        if (currentCell.isStartsRowspanedCells()) {
                            //2. Увеличу для этой ячейки величину Rowspan'a
                            currentCell.setRowspanCount(currentCell.getRowspanCount() + 1);
                            break;
                        }
                    }

                    rowNumber = rowNumber - 1;
                }
            } else {
                // Установлю дефолтное значение колспана
                columnspan[columnNum] = columns.count - columnNum;

                // Вычисляю следующую ячейку у которой Rowspan > 0
                for (int nextColumn = columnNum; nextColumn < columns.count; nextColumn++) {
                    // Если нашли ячейку для которой Rowspan > 0, выхожу
                    if (columnsRowspans[nextColumn] > 0) {
                        // Установлю colspan
                        columnspan[columnNum] = nextColumn - columnNum;
                        break;
                    }
                }
            }
        }

        // Собственно отрисовка(вставка в LayoutTable)
        for (int columnNum = 0; columnNum < columns.count - 1; columnNum++) {
            if (columnspan[columnNum] > 1) {
                // Вставляю ячейку с которой начинается columnspan
                getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, columnspan[columnNum]);
                getLayoutTable().getCurrentRow().getCurrentCell().setStartsColumnspanedCells(true);

                // для симметрии таблицы добавляем пустые ячейки
                for (int n = 0; n < columnspan[columnNum] - 1; n++) {
                    getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                }
                // переставлю курсор на Columnspan[ColumnNum];
                columnNum = columnNum + columnspan[columnNum] - 1;
            } else {
                // если колонка - счетчик
                if (columns.getColumnByIndex(columnNum).isColumnIsCounter()) {
                    // текущее значение счетчика
                    String currentValue = String.valueOf(columns.getColumnByIndex(columnNum).getCounterCurrent());
                    // инкрементируем счетчик
                    columns.getColumnByIndex(columnNum).incrementCounter();
                    // выводим
                    getLayoutTable().getCurrentRow().addCell(currentValue, "string", 1, 1);
                } else {
                    // ничего не делаю. Эта ячейка все равно не будет выводиться, т.к. она зароуспаненна
                    // Добавляю ячейку для сохранения таблицы
                    getLayoutTable().getCurrentRow().addCell("&#160;", "string", 1, 1);
                    getLayoutTable().getCurrentRow().getCurrentCell().setFakeCell(true);
                }
            }
        }
    }

    /**
     * добавляет строку с нумерацией столбцов.
     * @param layoutProfile - профиль отчета
     * @param repGen        - репорт-райтер
     * @param columns       - описание колонок лэйаута
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    protected void writeColumnCounter(
            TableLayoutClass layoutProfile,
            XslFoProfileWriter repGen,
            LayoutColumns columns) throws XMLStreamException {

        final String columnNumberStyleClassName = "CAPTION_CLASS";

        // если надо, рисуем нумерацию столбцов
        if (null != layoutProfile.isColumnNumbers()) {
            if (layoutProfile.isColumnNumbers()) {
                String className = StringUtils.defaultString(layoutProfile.getColumnNumbersStyleClass(),
                        columnNumberStyleClassName);

                repGen.tableRowStart();
                int columnNum = 1;
                for (int j = 0; j < columns.count; j++) {
                    if (!columns.getColumnByIndex(j).isHidden()) {
                        RowCellBuilder rcb = RowCellBuilder.create(String.valueOf(columnNum))
                                .setType("i4")
                                .setElementClass(className);
                        repGen.tableRowAddCell(rcb);
                        columnNum++;
                    }
                }
                repGen.tableRowEnd();
            }
        }
    }

    /**
     * вычисляет значение на основе выражения.
     * @param expression        - выражение
     * @param formatterData     - Набор данных передаваемых форматеру для обработки
     * @return Object   - возвращает вычисленное значение
     */
    protected Object getVarValue(String expression, ReportFormatterData formatterData) {

        if (!StringUtils.isBlank(expression)) {
            return ExpressionEvaluator.evaluate(expression, formatterData, true);
        }

        return StringUtils.EMPTY;
    }

    /**
     * Присваивает значения переменным лэйаута.
     * @param layoutProfile - профиль лэйаута
     * @param layoutData    - данные лэйаута
     * @param currentRow    - текущая строка отчета
     * @param rowNum        - номер строки
     */
    protected void setVarValues(
            TableLayoutClass layoutProfile,
            ReportLayoutData layoutData,
            IDataRow currentRow,
            int rowNum) {

        // проходим по переменным уровня лэйаута
        if (layoutProfile.getVar() != null) {
            for (VarClass varNode : layoutProfile.getVar()) {
                // название параметра
                String paramName = StringUtils.defaultString(varNode.getNotEvaluateIfParam());

                // надо ли вычислять переменную
                if (layoutData.getParams().isParamExists(paramName)) {
                    if (Converter.toBoolean(layoutData.getParams().getParam(paramName).toString())) {
                        continue;
                    }
                }

                // получаем объект, с которым работают форматтеры и эвалуаторы
                ReportFormatterData formatterData = new ReportFormatterData(
                        layoutData,
                        layoutData.getVars().get(varNode.getN()),
                        null,
                        currentRow,
                        rowNum, -1);
                // определяем значение переменной
                String macros = varNode.getMacros();
                Object val = getVarValue(macros, formatterData);

                // тип переменной
                VarTypesClass type = varNode.getVt();
                // приводим значение к нужному типу
                switch (type) {
                    case DATE:
                    case DATE_TIME_TZ:
                    case TIME_TZ:
                        // даты оставляем в строковом виде, чтобы не терялась разница между
                        // датой, датой со временем и временем
                        val = Converter.toString(val, type);
                        break;
                    default:
                        val = Converter.toObject(val.toString(), type);
                        break;
                }
                layoutData.getVars().put(varNode.getN(), val);
                formatterData.setCurrentValue(val);
                if (varNode.getFormatters() != null) {
                    // по всем форматтерам
                    for (AbstractFormatterClass  formatterNode : Converter.jaxbListToTypedList(varNode.getFormatters()
                            .getAbstractFormatter())) {
                        // просим объект у фабрики
                        IReportFormatter formatter = ReportObjectFactory.getFormatter(formatterNode);
                        formatter.execute(formatterNode, formatterData);
                        // присваиваем значение переменной после форматирования
                        val = formatterData.getCurrentValue();
                        layoutData.getVars().put(varNode.getN(), val);
                    }
                }
            }
        }
    }

    //endregion
}
