package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.List;
import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:table-cell&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableCellArea extends GenericArea {

    /**
     * Порядковый номер позиции ячейки в контейнере, в котором располагаются ячейки в виде матрицы ячеек.
     */
    private int position = -1;

    /**
     * Свойство - позиция ячейки в контейнере, в котором располагаются ячейки в виде матрицы ячеек.
     * @return int  - возвращает позицию ячейки в контейнере
     */
    protected int getPosition() {
        return position;
    }

    /**
     * Свойство - позиция ячейки в контейнере, в котором располагаются ячейки в виде матрицы ячеек.
     * @param position - позиция ячейки в контейнере, в котором располагаются ячейки в виде матрицы ячеек
     */
    protected void setPosition(int position) {
        this.position = position;
    }

    /**
     * Свойство - Контейнер - секция таблицы, которой принадлежит ячейка.
     * @return CellGridContainer - возвращает контейнер - секцию таблицы, которой принадлежит ячейка
     */
    public CellGridContainer getGridContainer() {
        return (CellGridContainer)getParentArea().getParentArea();
    }

    /**
     * Конструктор по умолчанию.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public TableCellArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.TABLE_CELL;
    }

    /**
     * Начальный столбец ячейки.
     * @return int  - возвращает начальный столбец ячейки
     */
    public int getColumn() {
        return getGridContainer().getColumnNumber(position);
    }

    /**
     * Начальная строка ячейки.
     * @return int  - возвращает начальную строку ячейки
     */
    public int getRow() {
        return getGridContainer().getRowNumber(position);
    }

    /**
     * Кол-во столбцов, занимаемых ячейкой.
     * @return int  - возвращает кол-во столбцов, занимаемых ячейкой
     */
    public int getNumberColumnsSpanned() {
        Object property = getProperty(FoPropertyType.NUMBER_COLUMNS_SPANNED);
        return (int)(null != property ? property : GlobalData.DEFAULT_COLUMNS_SPAN_VALUE);
    }

    /**
     * Кол-во строк, занимаемых ячейкой.
     * @return int  - возвращает кол-во строк, занимаемых ячейкой
     */
    public int getNumberRowsSpanned() {
        Object property = getProperty(FoPropertyType.NUMBER_ROWS_SPANNED);
        return (int)(null != property ? property : GlobalData.DEFAULT_ROWS_SPAN_VALUE);
    }

    /**
     * Получение списка колонок для ячейки.
     * Ячейка может 'покрывать' несколько колонок - атрибут number-columns-spanned
     * @return TableColumnArea[] - возвращает список колонок для ячейки
     */
    public TableColumnArea[] getColumns() {

        if (position < 0) {
            return null;
        }
        int colCount = getNumberColumnsSpanned();
        TableColumnArea[] tableColumnAreas = new TableColumnArea[colCount];
        CellGridContainer container = getGridContainer();
        List<IArea> columnList = container.getParentTableArea().getColumnList();
        int columnNumber = container.getColumnNumber(position);
        // Добавляем те колонки, на которые распространяется ячейка
        for (int i = 0; i < colCount; i++) {
            tableColumnAreas[i] = (TableColumnArea)columnList.get(columnNumber + i);
        }
        return tableColumnAreas;
    }

    /**
     * Расчет ширины и высоты ячейки по ширине столбца(ов) и высоте строк(и).
     */
    public void recalculateWidthAndHeight() {

        CellGridContainer container = getGridContainer();
        List<IArea> columnList = container.getParentTableArea().getColumnList();
        int columnNumber = container.getColumnNumber(position);
        int rowNumber = container.getRowNumber(position);

        // Находим ширину
        int width = 0;
        for (int i = 0, colCount = getNumberColumnsSpanned(); i < colCount; i++) {
            width += ((TableColumnArea)columnList.get(columnNumber + i)).getBorderRectangle().getWidth();
        }
        getBorderRectangle().setWidth(width);

        // Находим высоту
        int height = 0;
        for (int i = 0, rowCount = getNumberRowsSpanned(); i < rowCount; i++) {
            height += container.getChildrenList().get(rowNumber + i).getBorderRectangle().getHeight();
        }
        getBorderRectangle().setHeight(height);
    }

    /**
     * Метод для получения значения свойства с учетом наследования значения свойства от родительской области
     * Для ячейки следующее поведение: ищется в строке, дальше в столбце, дальше в секции таблицы и т.д.
     * @param propertyType - Тип свойства
     * @param deep         - Счетчик глубины рекурсии. При 1м вызове = 0
     * @return Object   - возвращает значение свойства, если свойство задано у объекта или кого-нибудь из родителей
     *                      по цепочке или null
     */
    @Override
    public Object getInheritedProperty(FoPropertyType propertyType, int deep) {

        TableColumnArea[] columns = getColumns();
        if (columns == null || columns.length == 0) {
            return super.getInheritedProperty(propertyType, deep);
        }

        Object result = getProperty(propertyType);
        if (null == result) {
            // Ищем в строке
            result = getParentArea().getProperty(propertyType);
            if (null == result) {
                // Ищем в столбце
                result = columns[0].getProperty(propertyType);
                if (null == result) {
                    // Ищем в секции и выше
                    result = getParentArea().getParentArea().getInheritedProperty(propertyType, deep + 2);
                }
            }
        }

        return result;
    }
}
