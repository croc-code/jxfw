package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.impl;

import com.aspose.cells.Cell;
import com.aspose.cells.Cells;
import com.aspose.cells.Row;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.ExcelRowHeightsSetter;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Установление высоты строк книги excel по умолчанию.
 *
 */
public class DefaultExcelRowHeightsSetter implements ExcelRowHeightsSetter {

    @Override
    public void setHeightsOfRows(Workbook excel) throws Exception {
        if (excel == null) {
            return;
        }

        for (Worksheet sheet : (Iterable<Worksheet>) excel.getWorksheets()) {
            // устанавливаем автоматическую высоту строк
            sheet.autoFitRows();
            Cells cells = sheet.getCells();
            // пройдемся по рядам и для каждого ряда, установим высоту ряда равной максимальной из ячеек в ряду,
            // на случай если есть слитые ячейки, т.к. для них autofit не работает
            for (Row row : (Iterable<Row>) cells.getRows()) {
                setRowHeightToMax(row, cells);
            }
        }
    }

    private void setRowHeightToMax(Row row, Cells cells) throws Exception {
        int indexRow = row.getIndex();
        int colCount = cells.getColumns().getCount();
        Set<Double> heights = new TreeSet<>();
        // добавим текущую высоту ряда
        heights.add(row.getHeight());
        for (int indexColumn = 0; indexColumn < colCount; indexColumn++) {
            Cell cell = cells.get(indexRow, indexColumn);
            heights.add((double) cell.getHeightOfValue());
        }
        row.setHeight(Collections.max(heights));
    }
}
