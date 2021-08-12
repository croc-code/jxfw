package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import org.apache.commons.lang3.StringUtils;

/**
 * Представляет ячейку в таблице.
 * Created by vsavenkov on 26.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class LayoutCell {

    /**
     * rowspan для ячейки.
     */
    private int rowspanCount;

    /**
     * columnspan для ячейки.
     */
    private int columnspanCount;

    /**
     * тип значения ячейки.
     */
    private String cellType;

    /**
     * значение ячейки.
     */
    private Object cellValue;

    /**
     * стиль ячейки.
     */
    private String cellStyle;

    /**
     * Признак того, что с данной ячейки начинается последовательность зароуспаннных ячеек
     * длинной в {@link #rowspanCount}/>.
     */
    private boolean startsRowspanedCells;

    /**
     * Признак того, что с данной ячейки начинается последовательность объединенных в строке ячеек
     * длинной в {@link #columnspanCount}/>.
     */
    private boolean startsColumnspanedCells;

    /**
     * Признак того, что даннная ячейка зароуспанена с предыдущей.
     */
    private boolean rowspanedCell;

    /**
     * Признак того, что значение в ячейке - аггрегат по предыдущим ячейкам.
     */
    private boolean isAggregatedValue;

    /**
     * Признак того, что ячейка не должна выводиться в отчет.
     */
    private boolean isFakeCell;

    /**
     * Признак того, что ячейка относится к подзаголовку, но не является группирующей.
     */
    private boolean isNotGroupSubTitle;

    /**
     * Чистые данные ячейки (без обработки форматтерами).
     */
    private Object cellRawValue;


    /**
     * Конструктор по умолчанию.
     */
    public LayoutCell() {
        this(null, null, "string", 1, 1, StringUtils.EMPTY);
    }

    /**
     * Конструктор.
     * @param cellValue       - Значение ячейки
     * @param cellType        - Тип значения ячейки
     * @param rowspanCount    - Rowspan для ячейки
     * @param columnspanCount - Columnspan для ячейки
     */
    public LayoutCell(Object cellValue, String cellType, int rowspanCount, int columnspanCount) {
        this(cellValue, cellValue, cellType, rowspanCount, columnspanCount, StringUtils.EMPTY);
    }

    /**
     * Конструктор.
     * @param cellValue       - Значение ячейки
     * @param cellRawValue    - Чистые данные ячейки (без обработки форматтерами)
     * @param cellType        - Тип значения ячейки
     * @param rowspanCount    - Rowspan для ячейки
     * @param columnspanCount - Columnspan для ячейки
     */
    protected LayoutCell(Object cellValue, Object cellRawValue, String cellType, int rowspanCount,
                         int columnspanCount) {
        this(cellValue, cellRawValue, cellType, rowspanCount, columnspanCount, StringUtils.EMPTY);
    }

    /**
     * Самый сложный конструктор.
     * @param cellValue         - Значение ячейки
     * @param cellRawValue      - Чистые данные ячейки (без обработки форматтерами)
     * @param cellType          - Тип значения ячейки
     * @param rowspanCount      - Rowspan для ячейки
     * @param columnspanCount   - Columnspan для ячейки
     * @param cellStyle         - Строка с описанием стиля ячейки
     */
    protected LayoutCell(Object cellValue, Object cellRawValue, String cellType, int rowspanCount, int columnspanCount,
                         String cellStyle) {
        this.rowspanCount = rowspanCount;
        this.columnspanCount = columnspanCount;
        this.cellValue = cellValue;
        this.cellRawValue = cellRawValue;
        this.cellType = cellType;
        this.cellStyle = cellStyle;
        startsRowspanedCells = false;
        startsColumnspanedCells = false;
        rowspanedCell = false;
        isAggregatedValue = false;
        isFakeCell = false;
    }

    /**
     * Самый сложный конструктор.
     * @param cellValue         - Значение ячейки
     * @param cellType          - Тип значения ячейки
     * @param rowspanCount      - Rowspan для ячейки
     * @param columnspanCount   - Columnspan для ячейки
     * @param cellStyle         - Строка с описанием стиля ячейки
     */
    public LayoutCell(Object cellValue, String cellType, int rowspanCount, int columnspanCount, String cellStyle) {
        this.rowspanCount = rowspanCount;
        this.columnspanCount = columnspanCount;
        this.cellValue = cellValue;
        cellRawValue = cellValue;
        this.cellType = cellType;
        this.cellStyle = cellStyle;
        startsRowspanedCells = false;
        startsColumnspanedCells = false;
        rowspanedCell = false;
        isAggregatedValue = false;
        isFakeCell = false;
    }

    /**
     * Получить значение ячейки.
     * @return значение ячейки
     */
    public Object getValue() {
        return cellValue;
    }

    /**
     * Установить значение ячейки.
     * @param cellValue значение ячейки
     */
    public void setValue(Object cellValue) {
        this.cellValue = cellValue;
    }

    /**
     * Чистые данные ячейки (без обработки форматтерами).
     * @return чистые данные ячейки
     */
    public Object getRawValue() {
        return cellRawValue;
    }

    /**
     * Чистые данные ячейки (без обработки форматтерами).
     * @param cellRawValue данные ячейки
     */
    public void setRawValue(Object cellRawValue) {
        this.cellRawValue = cellRawValue;
    }

    /**
     * Получить rowspan для ячейки.
     * @return rowspan для ячейки
     */
    public int getRowspanCount() {
        return rowspanCount;
    }

    /**
     * Установить rowspan для ячейки.
     * @param rowspanCount rowspan для ячейки
     */
    public void setRowspanCount(int rowspanCount) {
        this.rowspanCount = rowspanCount;
    }

    /**
     * Получить columnspan для ячейки.
     * @return columnspan для ячейки
     */
    public int getColumnspanCount() {
        return columnspanCount;
    }

    /**
     * Установить columnspan для ячейки.
     * @param columnspanCount для ячейки
     */
    public void setColumnspanCount(int columnspanCount) {
        this.columnspanCount = columnspanCount;
    }

    /**
     * Получить тип значения ячейки.
     * @return тип значения ячейки
     */
    public String getCellType() {
        return cellType;
    }

    /**
     * Установить тип значения ячейки.
     * @param cellType тип значения ячейки
     */
    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    /**
     * Получить стиль ячейки.
     * @return стиль ячейки
     */
    public String getCellStyle() {
        return cellStyle;
    }

    /**
     * Установить стиль ячейки.
     * @param cellStyle стиль ячейки
     */
    public void setCellStyle(String cellStyle) {
        this.cellStyle = cellStyle;
    }

    /**
     * Поучить признак того, что с данной ячейки начинается последовательность зароуспаннных ячеек
     * длинной в {@link #rowspanCount}/>.
     * @return true если стартовая ячейка
     */
    public boolean isStartsRowspanedCells() {
        return startsRowspanedCells;
    }

    /**
     * Установить признак того, что с данной ячейки начинается последовательность зароуспаннных ячеек
     * длинной в {@link #rowspanCount}/>.
     * @param startsRowspanedCells признак того, что ячейка стартовая
     */
    public void setStartsRowspanedCells(boolean startsRowspanedCells) {
        this.startsRowspanedCells = startsRowspanedCells;
    }

    /**
     * Установить признак того, что с данной ячейки начинается последовательность объединенных в строке ячеек
     * длинной в {@link #columnspanCount}/>.
     * @return true если ячейка стартовая
     */
    public boolean isStartsColumnspanedCells() {
        return startsColumnspanedCells;
    }

    /**
     * Установить признак того, что с данной ячейки начинается последовательность объединенных в строке ячеек
     * длинной в {@link #columnspanCount}/>.
     * @param startsColumnspanedCells признак того, что с данной
     *                               ячейки начинается последовательность объединенных в строке ячеек
     */
    public void setStartsColumnspanedCells(boolean startsColumnspanedCells) {
        this.startsColumnspanedCells = startsColumnspanedCells;
    }

    /**
     * Получить признак того, что даннная ячейка зароуспанена с предыдущей.
     * @return true если даннная ячейка зароуспанена с предыдущей
     */
    public boolean isRowspaned() {
        return rowspanedCell;
    }

    /**
     * Установить признак того, что даннная ячейка зароуспанена с предыдущей.
     * @param  rowspanedCell признак того, что даннная ячейка зароуспанена с предыдущей
     */
    public void setRowspaned(boolean rowspanedCell) {
        this.rowspanedCell = rowspanedCell;
    }

    /**
     * Получить признак того, что значение в ячейке - аггрегат по предыдущим ячейкам.
     * @return признак того, что значение в ячейке - аггрегат по предыдущим ячейкам
     */
    public boolean isAggregated() {
        return isAggregatedValue;
    }

    /**
     * Установить признак того, что значение в ячейке - аггрегат по предыдущим ячейкам.
     * @param aggregatedValue признак того, что значение в ячейке - аггрегат по предыдущим ячейкам
     */
    public void setAggregated(boolean aggregatedValue) {
        isAggregatedValue = aggregatedValue;
    }

    /**
     * Получить признак того, что ячейка не должна выводиться в отчет.
     * @return true если данная ячейка не выводиться в отчёт
     */
    public boolean isFakeCell() {
        return isFakeCell;
    }

    /**
     * Установить признак того, что ячейка не должна выводиться в отчет.
     * @param fakeCell признак того, что ячейка не должна выводиться в отчет
     */
    public void setFakeCell(boolean fakeCell) {
        isFakeCell = fakeCell;
    }

    /**
     * Получить признак того, что ячейка относится к подзаголовку, но не является группирующей.
     * @return true если ячейка относится к подзаголовку, но не является группирующей
     */
    public boolean isNotGroupSubTitle() {
        return isNotGroupSubTitle;
    }

    /**
     * Установить признак того, что ячейка относится к подзаголовку, но не является группирующей.
     * @param notGroupSubTitle признак того, что ячейка относится к подзаголовку, но не является группирующей
     */
    public void setNotGroupSubTitle(boolean notGroupSubTitle) {
        isNotGroupSubTitle = notGroupSubTitle;
    }
}
