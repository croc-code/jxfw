package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.AggregateFunctionUnknownException;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс LayoutTable. Служит для отрисовки матрицы таблицы в памяти. Затем по ней рисуется xsl-fo.
 * Created by vsavenkov on 26.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class LayoutTable {

    /**
     * Массив строк таблицы.
     */
    private List<LayoutRow> tableRows;

    public List<LayoutRow> getTableRows() {
        return tableRows;
    }

    /**
     * Конструктор по умолчанию.
     */
    public LayoutTable() {
        tableRows = new ArrayList<>();
    }

    /**
     * Добавляет новую строку в таблицу данных.
     */
    public void addRow() {
        tableRows.add(new LayoutRow());
    }

    /**
     * Добавляет новую строку с заданным стилем в таблицу данных.
     * @param style - Стиль строки
     */
    public void addRow(String style) {
        tableRows.add(new LayoutRow(style));
    }

    /**
     * Возвращает текущую (последнюю) строку.
     * @return LayoutRow    - Возвращает текущую (последнюю) строку
     */
    public LayoutRow getCurrentRow() {
        if (tableRows.size() > 0) {
            return  tableRows.get(tableRows.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Возвращает количество строк данной таблицы данных.
     * @return int  - Возвращает количество строк данной таблицы данных
     */
    public int getRowCount() {
        return tableRows.size();
    }

    /**
     * Возвращает строку по индексу.
     * @param rowIndex - индекс ряда
     * @return LayoutRow    - В случае некоректного индекса возвращает null, иначе LayoutRow
     */
    public LayoutRow getRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tableRows.size()) {
            return null;
        }
        return tableRows.get(rowIndex);
    }

    /**
     * Возвращает ячейку таблицы данных по индексам.
     * @param rowIndex      - индекс ряда
     * @param cellIndex     - индекс ячейки
     * @return LayoutCell   - В случае некоректных индексов возвращает null, иначе LayoutCell
     */
    public LayoutCell getCell(int rowIndex, int cellIndex) {
        if (rowIndex < 0 || rowIndex >= tableRows.size()) {
            return null;
        }

        LayoutRow row = tableRows.get(rowIndex);

        if (row != null) {
            return row.getRowCells().get(cellIndex);
        }

        return null;
    }

    /**
     * Возвращает аггрегатное значение для колонки таблицы.
     * @param columnOrdinal         - Номер колонки
     * @param aggregateFunction     - Функция-аггрегат
     * @return Object   - Результат аггрегации
     */
    public Object getAggregatedValue(int columnOrdinal, String aggregateFunction) {
        return getAggregatedValue(columnOrdinal, aggregateFunction, 0, tableRows.size() - 1);
    }

    /**
     * Возвращает аггрегатное значение для колонки таблицы для диапазона строк.
     * @param columnOrdinal         - Номер колонки
     * @param aggregationFunction   - Функция-аггрегат
     * @param startRow              - Начальная строка
     * @param endRow                - Конечная строка
     * @return BigDecimal   - Результат аггрегации
     */
    public BigDecimal getAggregatedValue(int columnOrdinal, String aggregationFunction, int startRow, int endRow) {

        Object currentValue;
        BigDecimal totalValue = BigDecimal.valueOf(0);
        BigDecimal totalSumForAverage = BigDecimal.valueOf(0);
        int totalCountForAverage = 0;
        Map<Object, Object> totalCollectionForDistinctCount = new HashMap<>();

        // Пройдусь по всем аггрегируемым строкам этого столбца...
        for (int row = (startRow - 1 >= 0 ? startRow - 1 : 0); row < endRow; row++) {
            LayoutCell cell = getCell(row, columnOrdinal);

            // Если ячейка вдруг оказалась равна нулю...
            if (cell == null) {
                continue;
            }

            // Если это не fake-ячейка и не аггрегированная ячейка(вобщем не искуственная),
            // тогда займемся подстчетом...
            if (!cell.isFakeCell() && !cell.isAggregated()) {
                currentValue = getCell(row, columnOrdinal).getRawValue();
                Boolean isEmptyCurrentValue = (currentValue == null)
                        /*  || TODO: при необходимости раскомментировать и отредактировать условие
                         (CurrentValue == DBNull.Value) */
                        || (currentValue.toString().length() == 0);

                // Во всех ситуациях кроме той, когда надо найти distinct-count, попробую получить decimal
                if (aggregationFunction != "distinctcount" && aggregationFunction != "count") {
                    if (isEmptyCurrentValue) {
                        currentValue = 0;
                    } else {
                        currentValue = Converter.toDecimal(currentValue.toString(), BigDecimal.valueOf(0));
                    }
                }

                switch (aggregationFunction) {
                    case "max":
                        totalValue = totalValue.max(new BigDecimal(currentValue.toString()));
                        break;

                    case "min":
                        totalValue = totalValue.min(new BigDecimal(currentValue.toString()));
                        break;

                    case "sum":
                        totalValue = totalValue.add(new BigDecimal(currentValue.toString()));
                        break;

                    case "count":
                        if (!(currentValue instanceof String)) {
                            totalValue = totalValue.add(new BigDecimal(1));
                        } else {
                            //Для строк не буду счтитать null'ы и пустоты
                            if (!isEmptyCurrentValue) {
                                totalValue = totalValue.add(new BigDecimal(1));
                            }
                        }
                        break;

                    case "average":
                        totalSumForAverage = totalSumForAverage.add(new BigDecimal(currentValue.toString()));
                        totalCountForAverage++;
                        totalValue = totalSumForAverage.divide(new BigDecimal(totalCountForAverage));

                        break;

                    case "distinctcount":
                        if (!isEmptyCurrentValue) {
                            // null и пустые строки не считаем
                            if (!totalCollectionForDistinctCount.containsValue(currentValue)) {
                                totalCollectionForDistinctCount.put(currentValue, currentValue);
                            }
                            totalValue = new BigDecimal(totalCollectionForDistinctCount.size());
                        }
                        break;

                    default:
                        throw new AggregateFunctionUnknownException(aggregationFunction);
                }
            }
        }

        return totalValue;
    }
}
