package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.ColumnWidth;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:table&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableArea extends GenericArea {

    /**
     * Список колонок.
     */
    private List<IArea> columnList = new ArrayList<>();

    /**
     * Свойство - список колонок.
     * @return List - возвращает список колонок
     */
    public List<IArea> getColumnList() {
        return columnList;
    }

    /**
     * Свойство - список колонок.
     * @param columnList - список колонок
     */
    public void setColumnList(List<IArea> columnList) {
        this.columnList = null != columnList ? columnList : new ArrayList<>();
    }

    /**
     * Свойство - сумма значений пропорциональных ширин всех колонок.
     * @return float    - возвращает сумму значений пропорциональных ширин всех колонок
     */
    public float getProportionalColumnsSum() {

        float proportionalColumnsSum = 0f;
        for (IArea column : columnList) {
            ColumnWidth columnWidth = ((TableColumnArea)column).width;
            if (columnWidth.isProportional() && columnWidth.hasValue()) {
                proportionalColumnsSum += columnWidth.getValue();
            }
        }
        return proportionalColumnsSum;
    }

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public TableArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.TABLE;
    }
}
