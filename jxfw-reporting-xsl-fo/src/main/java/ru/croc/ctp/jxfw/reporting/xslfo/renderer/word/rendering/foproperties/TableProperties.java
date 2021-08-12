package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import static com.aspose.words.CellMerge.PREVIOUS;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.COLUMN_NUMBER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.NUMBER_COLUMNS_SPANNED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.NUMBER_ROWS_SPANNED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.PROPORTIONAL_COLUMN_TABLE_WIDTH_PERCENT;

import com.aspose.words.CellMerge;
import org.apache.commons.math.util.MathUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.SideValues;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableCellArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableColumnArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table.TableRowArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.ArrayList;

/**
 * Класс, хранящий функции для обработки свойств таблицы.
 * Created by vsavenkov on 26.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableProperties {
    
    /**
     * Устанавливает фиксированную ширину колонки в Point.
     * @param columnList - Список колонок
     * @param tableWidth - Ширина таблицы
     */
    public static void setColumnWidth(ArrayList<TableColumnArea> columnList, double tableWidth) {
        
        final double prc100 = 100d;
        double proportionalSum = HelpFuncs.ZERO;
        double proportionalPercents = HelpFuncs.ZERO;
        double percentsSum = HelpFuncs.ZERO;
        double sumInPoints = HelpFuncs.ZERO;

        // Пройдем по всем колонкам таблицы
        for (TableColumnArea tableColumn : columnList) {
            
            switch (tableColumn.getWidthType()) {
                case PROPORTIONAL:
                    proportionalSum += tableColumn.getWidth();
                    break;
                case PERCENT:
                    percentsSum += tableColumn.getWidth();
                    break;
                case POINT:
                    sumInPoints += tableColumn.getWidth();
                    break;

                default:
                    // В импортруемом коде ничего не было
            }
        }
        // Считаем сколько процентов надо отдать пропорциональным столбцам.. Если сумма столбцов с процентами < 100,
        // то, соответственно, остальное пропорциональным столбцам.. иначе (дебильный случай) берем значение
        // по умолчанию
        if (proportionalSum > HelpFuncs.ZERO) {
            proportionalPercents = (percentsSum < prc100)
                                        ? prc100 - percentsSum
                                        : PROPORTIONAL_COLUMN_TABLE_WIDTH_PERCENT;
        }
        
        // Добавляем в общие проценты проценты от пропорциональных столбцов
        percentsSum += proportionalPercents;
        // Если у нас только фиксированные столбцы, то их ширины нельзя пересчитать
        if (percentsSum == HelpFuncs.ZERO) {
            return;
        }
        
        // Вычисляем размер, который мы распределяем между процентными и пропорциональными столбцами
        double widthGap = tableWidth - sumInPoints;

        // Обрабатываем ситуацию, когда сумма значений ширин фиксированных колонок превысила ширину таблицы
        if (widthGap <= HelpFuncs.ZERO) {
            // Резервируем для рассчитываемых колонок место, равное проценту от ширины таблицы, заданному константой
            widthGap = tableWidth * PROPORTIONAL_COLUMN_TABLE_WIDTH_PERCENT / prc100;
        }

        // Размер, распределяемый между пропорциональными столбцами
        double proportionalsWidth = widthGap * proportionalPercents / percentsSum;

        //Рассчитываем ширину пропорциональных и процентных столбцов
        for (TableColumnArea tableColumn : columnList) {
            if (tableColumn.getWidth() == HelpFuncs.ZERO) {
                continue;
            }
            switch (tableColumn.getWidthType()) {
                case PROPORTIONAL:
                    tableColumn.setColumnWidthInPoints(
                        MathUtils.round(tableColumn.getWidth() * proportionalsWidth / proportionalSum, 1));
                    break;
                case PERCENT:
                    tableColumn.setColumnWidthInPoints(
                        MathUtils.round(tableColumn.getWidth() * widthGap / percentsSum, 1));
                    break;

                default:
                    // В импортруемом коде ничего не было
            }
        }
    }

    /**
     * Сортировка колонок по свойству column-number.
     * @param columnList - Список колонок
     */
    public static void setColumnNumber(ArrayList columnList) {
        
        for (int i = 0; i < columnList.size(); i++) {
            String columnNumberString;
            columnNumberString = (String)((TableColumnArea)columnList.get(i)).getPropertyValue(COLUMN_NUMBER);
            if (columnNumberString != null) {
                int columnNumber;
                columnNumber = Integer.valueOf(columnNumberString);
                if (columnNumber <= columnList.size()) {
                    columnNumber = columnNumber - 1;
                    if (i != columnNumber) {
                        if (!columnNumberString.equals(((TableColumnArea)columnList.get(columnNumber))
                                .getPropertyValue(COLUMN_NUMBER))) {
                            Object tableColumn;
                            tableColumn = columnList.get(columnNumber);
                            columnList.set(columnNumber, columnList.get(i));
                            columnList.set(i, tableColumn);
                            i--;
                        }
                    }
                }
            }
        }
    }

    /**
     * Расстановка свойств row-spanned и column-spanned в TableCellArea.
     * @param area - Область
     * @throws Exception генерирует в случае нарушения разметки таблицы
     */
    public static void setSpannedForCell(GenericArea area) throws Exception {
        if (!area.hasChildren()) {
            return;
        }

        int rowCount = area.getChildrenList().size();
        int colCount = ((TableArea)area.getParentArea()).getColumnList().size();
        if (colCount == 0) {
            throw new Exception("Таблица не имеет колонок!");
        }
        TableCellArea[] mergedCells = new TableCellArea[colCount];

        // цикл по строкам
        for (int i = 0; i < rowCount; i++) {
            TableRowArea rowArea = (TableRowArea)area.getChildrenList().get(i);
            // цикл по столбцам
            for (int j = 0; j < colCount; j++) {
                TableCellArea cellArea = mergedCells[j];
                if (cellArea != null) {
                    if (!rowArea.hasChildren()) {
                        rowArea.setChildrenList(new ArrayList<>());
                    }
                    if (j > rowArea.getChildrenList().size()) {
                        throw new Exception(
                                "Неправильная разметка таблицы! Найдена область таблицы, которая не покрыта элементом"
                                + " <fo:table-cell>!");
                    }
                    cellArea.setParentArea(rowArea);
                    cellArea.setVerticalMerge(PREVIOUS);
                    rowArea.getChildrenList().add(j, cellArea);
                } else {
                    if (!rowArea.hasChildren() || j >= rowArea.getChildrenList().size()) {
                        throw new Exception(
                                "Неправильная разметка таблицы! Найдена область таблицы, которая не покрыта элементом"
                                + " <fo:table-cell>!");
                    }
                    cellArea = (TableCellArea)rowArea.getChildrenList().get(j);
                }

                int numberColumnSpanned = HelpFuncs.nvl2(cellArea.getPropertyValue(NUMBER_COLUMNS_SPANNED), 1);
                int numberRowSpanned = HelpFuncs.nvl2(cellArea.getPropertyValue(NUMBER_ROWS_SPANNED), 1);

                if (numberColumnSpanned > 1 && j + numberColumnSpanned > colCount) {
                    throw new Exception(String.format(
                            "Неправильная разметка таблицы! Для ячейки ряда = %1$d, колонки = %2$d задано неправильное"
                            + " значение number-columns-spanned=%3$d. Ширина ряда превышает ширину таблицы.",
                            i, j, numberColumnSpanned));
                }
                if (numberRowSpanned > 1 && i + numberRowSpanned > rowCount) {
                    throw new Exception(String.format(
                            "Неправильная разметка таблицы! Для ячейки ряда = %1$d, колонки = %2$d задано неправильное"
                            + " значение number-rows-spanned=%3$d. Высота колонки превышает высоту таблицы.",
                            i, j, numberRowSpanned));
                }

                // Обработка свойства number-rows-spanned
                if (numberRowSpanned <= 1) {
                    mergedCells[j] = null;
                } else {
                    if (cellArea.getVerticalMerge() != PREVIOUS) {
                        cellArea.setVerticalMerge(CellMerge.FIRST);
                    }

                    TableCellArea cellMerged = new TableCellArea(cellArea);
                    // Уменьшаем на 1 кол-во сливаемых строк
                    cellMerged.setPropertyValue(NUMBER_ROWS_SPANNED, --numberRowSpanned);
                    // А атрибут слияния по столбцам оставляем. Пригодится для развертывания при обработке следующей
                    // строки
                    mergedCells[j] = cellMerged;
                    // Убираем верхнюю границу у добавляемой ячейки
                    cellMerged.setPropertyValue(BORDER_TOP, null);
                    // Убираем нижнюю границу у ячейки из ПРЕДЫДУЩЕЙ строки
                    cellArea.setPropertyValue(BORDER_BOTTOM, null);
                }


                // Обработка свойства number-columns-spanned
                if (numberColumnSpanned > 1) {
                    cellArea.setHorizontalMerge(CellMerge.FIRST);
                    for (int k = 1; k < numberColumnSpanned; k++) {
                        if (mergedCells[j + k] != null) {
                            throw new Exception(String.format(
                                    "Неправильная разметка таблицы! Для ячейки ряда = %1$d, колонки = %2$d задано"
                                    + " неправильное значение number-columns-spanned=%3$d. Пересекается c областью"
                                    + " number-rows-spanned в колонке=%4$d.",
                                     i, j, numberColumnSpanned, j + k));
                        }
                        TableCellArea cellMerged = new TableCellArea(cellArea);
                        // Убираем атрибут слияния и по строкам тоже!
                        cellMerged.setPropertyValue(NUMBER_COLUMNS_SPANNED, null);
                        cellMerged.setPropertyValue(NUMBER_ROWS_SPANNED, null);
                        cellMerged.setHorizontalMerge(PREVIOUS);
                        cellMerged.setVerticalMerge(cellArea.getVerticalMerge());
                        rowArea.getChildrenList().add(j + k, cellMerged);
                    }
                }
            }

            if (rowArea.getChildrenList().size() > colCount) {
                throw new Exception(String.format(
                        "Неправильная разметка таблицы! Ширина ряда = %1$d превышает ширину таблицы.", i));
            }
        }
    }

    /**
     * функция устанавливает свойства padding.
     * @param paddings     - поля содержимого элемента
     * @param propertyType - Тип свойства
     * @param value        - Значение свойства
     * @return SideValues возвращает установленные размеры
     */
    public static SideValues setPaddingProperties(SideValues paddings, FoPropertyType propertyType, Object value) {
        
        switch (propertyType) {

            //padding-bottom
            case PADDING_BOTTOM:
                paddings.setBottom((double)value);
                break;

            //padding-left
            case PADDING_LEFT:
                paddings.setLeft((double)value);
                break;
            //padding-right
            case PADDING_RIGHT:
                paddings.setRight((double)value);
                break;

            //padding-top
            case PADDING_TOP:
                paddings.setTop((double)value);
                break;

            default:
                // В импортруемом коде ничего не было
        }

        return paddings;
    }

    /**
     * функция устанавливает свойства margin.
     * @param margins      - отступы
     * @param propertyType - Тип свойства
     * @param value        - Значение свойства
     * @return SideValues возвращает установленные размеры
     */
    public static SideValues setMarginProperties(SideValues margins, FoPropertyType propertyType, Object value) {
        
        switch (propertyType) {
            //margin-top
            case MARGIN_TOP:
                margins.setTop((double)value);
                break;
            //margin-bottom
            case MARGIN_BOTTOM:
                margins.setBottom((double)value);
                break;
            //margin-left
            case MARGIN_LEFT:
                margins.setLeft((double)value);
                break;
            //margin-right
            case MARGIN_RIGHT:
                margins.setRight((double)value);
                break;

            default:
                // В импортруемом коде ничего не было
        }
        
        return margins;
    }
}
