package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:flow&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FlowArea extends GenericArea {

    /**
     * Конструктор с параметрами.
     * @param parentArea    - Родительская область
     * @param attributeList - Список атрибутов
     */
    public FlowArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.FLOW;
    }
}
