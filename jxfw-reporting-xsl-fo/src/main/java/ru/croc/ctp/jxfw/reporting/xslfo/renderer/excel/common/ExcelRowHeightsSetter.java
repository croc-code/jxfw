package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common;

import com.aspose.cells.Workbook;

/**
 * Контракт на установление высоты строк книги excel.
 *
 * @author SMufazzalov
 * @since 1.6
 */
public interface ExcelRowHeightsSetter {

    /**
     * Устанавливаем высоту строк.
     *
     * @param excel книга.
     */
    void setHeightsOfRows(Workbook excel) throws Exception;

}
