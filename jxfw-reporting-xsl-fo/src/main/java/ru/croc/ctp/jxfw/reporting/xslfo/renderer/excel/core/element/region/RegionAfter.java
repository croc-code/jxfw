package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:region-after.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class RegionAfter extends GenericRegion {
    
    /**
     * Инициализирующий конструктор.
     * @param attributeList - Список атрибутов
     */
    public RegionAfter(Map<String, String> attributeList) {
        super(attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.REGION_AFTER;
    }
}
