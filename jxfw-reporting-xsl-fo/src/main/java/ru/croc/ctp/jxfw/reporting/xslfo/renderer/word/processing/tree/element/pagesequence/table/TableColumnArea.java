package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.NUMBER_COLUMNS_REPEATED;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.ColumnWidthType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:table-column.
 * Created by vsavenkov on 26.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableColumnArea extends GenericArea {

    /**
     * ширина колонки в поинтах.
     */
    private double columnWidthInPoints;

    /**
     * тип св-ва ширины колонки.
     */
    private ColumnWidthType widthType;

    /**
     * ширина колонки.
     */
    private double width;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public TableColumnArea(GenericArea parentArea, Map<String, String> attributeList) {

        super(AreaType.TABLE_COLUMN, parentArea, attributeList);
        handleColumnWidthAttr();
    }

    /**
     * Создание колонок (с учетом атрибутам "number-columns-repeated").
     * @param tableArea     - зона таблицы
     * @param attributeList - список атрибутов
     */
    @SuppressWarnings("unchecked")
    public static void createTableColumns(TableArea tableArea, Map<String, String> attributeList) {

        int columnNumber = 0;
        if (null != attributeList) {
            try {
                columnNumber = Integer.parseInt(
                        String.valueOf(attributeList.get(NUMBER_COLUMNS_REPEATED.getPropertyName())));
            } catch (NumberFormatException e) {
                // Так я заменил int.TryParse
            }
        }
        if (columnNumber < 1) {
            columnNumber = 1;
        }
        for (; columnNumber > 0; columnNumber--) {
            tableArea.getColumnList().add(new TableColumnArea(tableArea, attributeList));
        }
    }

    public double getColumnWidthInPoints() {
        return columnWidthInPoints;
    }

    public void setColumnWidthInPoints(double columnWidthInPoints) {
        this.columnWidthInPoints = columnWidthInPoints;
    }

    public ColumnWidthType getWidthType() {
        return widthType;
    }

    public double getWidth() {
        return width;
    }

    /**
     * Разбор атрибута ширины колонки column-width на отдельные свойства widthType и width.
     */
    private void handleColumnWidthAttr() {
        
        // Стандартная ширина для пропорциональных столбцов
        final double defaultProportionalWidth = 1;

        String columnWidth = (String) getPropertyValue(FoPropertyType.COLUMN_WIDTH);

        if (StringUtils.isBlank(columnWidth)) {
            widthType = ColumnWidthType.PROPORTIONAL;
            width = defaultProportionalWidth;
            return;
        }
        if (HelpFuncs.isPercentValue(columnWidth)) {
            widthType = ColumnWidthType.PERCENT;
            width = HelpFuncs.getPercentValue(columnWidth);
            return;
        }
        int position = columnWidth.indexOf(GlobalData.PROPORTIONAL_COLUMN_WIDTH);
        if (position < 0) {
            widthType = ColumnWidthType.POINT;
            width = HelpFuncs.getSizeInPoints(columnWidth);
            columnWidthInPoints = width;
            return;
        }

        widthType = ColumnWidthType.PROPORTIONAL;
        width = HelpFuncs.ZERO;
        position = columnWidth.indexOf('(', position);
        if (position < 0) {
            return;
        }
        int endPosition = columnWidth.indexOf(')', position);
        if (endPosition < 0) {
            return;
        }
        Double parsed = HelpFuncs.parseDoubleValue(columnWidth.substring(position + 1, endPosition));
        width = null != parsed ? parsed : defaultProportionalWidth;
        if (width < HelpFuncs.ZERO) {
            width = HelpFuncs.ZERO;
        }
    }
}
