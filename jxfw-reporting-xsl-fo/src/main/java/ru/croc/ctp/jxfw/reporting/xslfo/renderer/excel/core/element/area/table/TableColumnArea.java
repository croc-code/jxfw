package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table;

import org.springframework.util.Assert;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.ColumnWidth;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий элемент &lt;fo:table-column/&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableColumnArea extends GenericArea {

    /**
     * Ширина столбца (передается в конструкторе в словаре свойств в виде одноименного свойства).
     */
    public final ColumnWidth width;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public TableColumnArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);

        width = (ColumnWidth) getPropertyValue(FoPropertyType.COLUMN_WIDTH);
        Assert.isTrue(width != null, "width != null");
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.TABLE_COLUMN;
    }

    /**
     * Ширину можно поменять только у столбцов, имеющих размер, отличный от 0, и являющихся пропорциональными
     * или процентными.
     * @return boolean - возвращает true, если Ширину можно поменять и false в противном случае
     */
    public boolean getSizeCanBeRedistributed() {
        return (width.isPercentage() || width.isProportional()) && width.hasValue();
    }
}
