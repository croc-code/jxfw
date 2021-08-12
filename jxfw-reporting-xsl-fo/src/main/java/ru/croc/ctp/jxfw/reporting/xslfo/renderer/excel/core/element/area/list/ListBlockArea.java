package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область элемента fo:list-block.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ListBlockArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public ListBlockArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.LIST_BLOCK;
    }
}
