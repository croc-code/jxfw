package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;

import java.util.Map;

/**
 * Абстрактный класс, инкапсулирующий обработку элементов fo:region-body, fo:region-before, fo:region-after.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public abstract class GenericRegion extends GenericArea {

    /**
     * Инициализирующий конструктор.
     * @param attributeList - Список атрибутов
     */
    protected GenericRegion(Map<String, String> attributeList) {
        super(null, attributeList);
    }

    /**
     * Свойство - высота региона.
     * @return float    - возвращает высоту региона
     */
    public float getExtent() {
        return getDimensionValue(FoPropertyType.EXTENT).getValue();
    }

    /**
     * Свойство - название региона.
     * @return String   - возвращает название региона
     */
    public String getRegionName() {

        String regionName = (String) getPropertyValue(FoPropertyType.REGION_NAME);
        if (!StringUtils.isBlank(regionName)) {
            return regionName;
        }
        switch (getAreaType()) {
            case REGION_BEFORE:
                return GlobalData.XSL_REGION_BEFORE;
            case REGION_AFTER:
                return GlobalData.XSL_REGION_AFTER;
            default:
                return GlobalData.XSL_REGION_BODY;
        }
    }
}
