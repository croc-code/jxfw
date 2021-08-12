package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table;

import static com.aspose.words.CellMerge.NONE;
import static com.aspose.words.CellMerge.PREVIOUS;

import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:table-cell.
 * Created by vsavenkov on 28.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableCellArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public TableCellArea(GenericArea parentArea, Map<String, String> attributeList) {
        super(AreaType.TABLE_CELL, parentArea, attributeList);
    }

    /**
     * Инициализирующий конструктор.
     * @param patternCell - Ячейка, копию которой мы создаем
     */
    public TableCellArea(TableCellArea patternCell) {

        super(AreaType.TABLE_CELL, patternCell.getParentArea(), patternCell.getProperties());
    }

    /**
     * Признак объединения ячейки по горизонтали.
     */
    private int horizontalMerge;
    /**
     * Признак объединения ячейки по вертикали.
     */
    private int verticalMerge;

    public int getHorizontalMerge() {
        return horizontalMerge;
    }

    public void setHorizontalMerge(int horizontalMerge) {
        this.horizontalMerge = horizontalMerge;
    }

    public int getVerticalMerge() {
        return verticalMerge;
    }

    public void setVerticalMerge(int verticalMerge) {
        this.verticalMerge = verticalMerge;
    }

    /**
     * Возвращает индекс колонки, в которой находится ячейка.
     * Работает правильно только после добавления ячеек-пустышек для number-columns-spanned
     * @return int возвращает индекс колонки, в которой находится ячейка.
     */
    private int getColumnIndex() {

        Assert.isTrue(getParentArea().hasChildren(), "getParentArea().hasChildren()");
        int result = getParentArea().getChildrenList().indexOf(this);
        Assert.isTrue(result >= 0, "nResult >= 0");
        return result;
    }

    /**
     * Возвращает колонку, в которой находится ячейка.
     * Работает правильно только после добавления ячеек-пустышек для number-columns-spanned
     * @return TableColumnArea возвращает колонку, в которой находится ячейка
     */
    public TableColumnArea getColumn() {
        
        int columnIndex = getColumnIndex();
        // последовательность взбирания по иерархии: Ячейка->Строка->Секция->Таблица
        TableArea table = (TableArea)getParentArea().getParentArea().getParentArea();
        return table.getColumnList().size() > columnIndex
                   ? (TableColumnArea)table.getColumnList().get(columnIndex)
                   : null;
    }

    /**
     * Рассчитывает ширину колонки с учетом смерженных колонок.
     * @return double возвращает ширину колонки с учетом смерженных колонок
     */
    public double getCellWidthInPoints() {
        
        switch (horizontalMerge) {
            case PREVIOUS:
                // Если это добавленная фейковая ячейка, то она не имеет ширины
                return HelpFuncs.ZERO;
            case NONE:
                // Если ячейка не распространяется на несколько столбцов, то всё просто
                return getColumn().getColumnWidthInPoints();

            default:
                // В импортруемом коде ничего не было
        }
        double result = HelpFuncs.ZERO;
        int columnIndex = getColumnIndex();
        // последовательность взбирания по иерархии: Ячейка->Строка->Секция->Таблица
        TableArea table = (TableArea)getParentArea().getParentArea().getParentArea();
        // Кол-во смерженных столбцов
        int intNumberColumnSpanned = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.NUMBER_COLUMNS_SPANNED), 1);
        for (int i = 0; i < intNumberColumnSpanned; i++) {
            result += ((TableColumnArea)table.getColumnList().get(i + columnIndex)).getColumnWidthInPoints();
        }
        return result;
    }

    /**
     * Метод для получения унаследованного значения свойства, рекурсивно вызывая родителей, пока не найдем значение или
     * не дойдем до вершины.
     * Для ячейки следующее поведение: ищется в строке, дальше в столбце, дальше в секции таблицы и т.д.
     * @param propertyType - Тип свойства
     * @param deep         - Счетчик глубины рекурсии. При 1м вызове = 0
     * @return Значение свойства, если свойство задано у объекта или кого-нибудь из родителей по цепочке или null
     */
    @Override
    protected Object getInheritedPropertyValue(FoPropertyType propertyType, int deep) {
        
        // Не возвращаем background-color для рекурсивного вызова (ибо ячейка сама отлично у себя его выставит)
        if (deep > 0 && propertyType == FoPropertyType.BACKGROUND_COLOR) {
            return null;
        }

        TableColumnArea column = getColumn();
        if (column == null) {
            return super.getInheritedPropertyValue(propertyType, deep);
        }

        Object result = getPropertyValue(propertyType);
        if (null == result) {
            // Ищем в строке
            result = getParentArea().getPropertyValue(propertyType);
            
            if (null == result) {
                // Ищем в столбце
                column.getPropertyValue(propertyType);
            
                if (null == result) {
                    // Ищем в секции и выше
                    getParentArea().getParentArea().getInheritablePropertyValue(propertyType);
                    
                }
            }
        }
        return result;
    }
}
