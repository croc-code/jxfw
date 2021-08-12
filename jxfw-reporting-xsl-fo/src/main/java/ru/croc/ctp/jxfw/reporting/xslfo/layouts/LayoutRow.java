package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import java.util.ArrayList;
import java.util.List;

/**
 * Строка в таблице.
 * Created by vsavenkov on 26.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class LayoutRow {

    /**
     * Массив ячеек.
     */
    private final List<LayoutCell> rowCells = new ArrayList();

    protected List<LayoutCell> getRowCells() {
        return rowCells;
    }

    /**
     * Конструктор по умолчанию.
     */
    public LayoutRow() {
    }

    /**
     * Конструктор.
     * @param style    - Стиль строки
     */
    public LayoutRow(String style) {
        this();
        rowStyle = style;
    }

    /**
     * Стиль строки.
     */
    private String rowStyle;

    public String getRowStyle() {
        return rowStyle;
    }

    public void setRowStyle(String rowStyle) {
        this.rowStyle = rowStyle;
    }

    /**
     * Добавляет в строку ячейку.
     * @param cellValue     - Значение ячейки
     * @param cellType      - Тип значения ячейки
     * @param rowspan       - Rowspan для ячейки
     * @param columnspan    - Columnspan для ячейки
     */
    public void addCell(Object cellValue, String cellType, int rowspan, int columnspan) {
        rowCells.add(new LayoutCell(cellValue, cellType, rowspan, columnspan));
    }

    /**
     * Добавляет в строку ячейку.
     * @param cellValue         - Значение ячейки
     * @param rawCurrentValue   - Чистые данные ячейки (без обработки форматтерами)
     * @param cellType          - Тип значения ячейки
     * @param rowspan           - Rowspan для ячейки
     * @param columnspan        - Columnspan для ячейки
     */
    protected void addCell(Object cellValue, Object rawCurrentValue, String cellType, int rowspan, int columnspan) {
        rowCells.add(new LayoutCell(cellValue, rawCurrentValue, cellType, rowspan, columnspan));
    }

    /**
     * Добавляет в строку ячейку.
     * @param cellValue  - Значение ячейки
     * @param cellType   - Тип значения ячейки
     * @param rowspan    - Rowspna для ячейки
     * @param columnspan - Columnspan для ячейки
     * @param cellStyle  - Строка с описанием стиля ячейки
     */
    public void addCell(Object cellValue, String cellType, int rowspan, int columnspan, String cellStyle) {
        rowCells.add(new LayoutCell(cellValue, cellValue, cellType, rowspan, columnspan, cellStyle));
    }

    /**
     * Добавляет в строку ячейку.
     * @param cellValue     - Значение ячейки
     * @param rawCellValue  - Чистые данные ячейки (без обработки форматтерами)
     * @param cellType      - Тип значения ячейки
     * @param rowspan       - Rowspna для ячейки
     * @param columnspan    - Columnspan для ячейки
     * @param cellStyle     - Строка с описанием стиля ячейки
     */
    protected void addCell(Object cellValue, Object rawCellValue, String cellType, int rowspan, int columnspan,
                           String cellStyle) {
        rowCells.add(new LayoutCell(cellValue, rawCellValue, cellType, rowspan, columnspan, cellStyle));
    }

    /**
     * Возвращает текущую (последнюю) ячейку ряда.
     * @return LayoutCell   - Возвращает текущую (последнюю) ячейку ряда.
     */
    public LayoutCell getCurrentCell() {
        return rowCells.size() > 0 ? rowCells.get(rowCells.size() - 1) : null;
    }
}
