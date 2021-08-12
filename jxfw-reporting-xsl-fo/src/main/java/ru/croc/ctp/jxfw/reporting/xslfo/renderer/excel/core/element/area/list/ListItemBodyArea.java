package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list;

import com.aspose.cells.TextAlignmentType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область элемента fo:list-item-body.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ListItemBodyArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public ListItemBodyArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.LIST_ITEM_BODY;
    }

    /**
     * Получение значения свойства по умолчанию.
     * @param propertyType - Тип свойства
     * @return Object- возвращает значение свойства
     */
    @Override
    protected Object getDefaultPropertyValue(FoPropertyType propertyType) {
    
        switch (propertyType) {
            case TEXT_ALIGN:
                return TextAlignmentType.LEFT;

            case VERTICAL_ALIGN:
                // Выравнивание вверх
                return TextAlignmentType.TOP;

            default:
                // В импортруемом коде ничего не было
        }
        return super.getDefaultPropertyValue(propertyType);
    }
}
