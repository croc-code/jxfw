package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Map;

/**
 * Абстрактный базовый класс области.
 * Инкапсулированы общее поведение и интерфейс областей region.
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public abstract class GenericRegion extends GenericArea {

    /**
     * Инициализирующий конструктор.
     * @param areaType      - Тип создаваемой области
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public GenericRegion(AreaType areaType, GenericArea parentArea, Map<String, String> attributeList) {
        super(areaType, parentArea, attributeList);
    }

    /**
     * Свойство - высота региона.
     * @return double   - возвращает высоту региона
     */
    public double getExtent() {
        
        return HelpFuncs.nvl2(getPropertyValue(FoPropertyType.EXTENT), HelpFuncs.ZERO);
    }

    /**
     * Свойство - название региона.
     * @return String   - возвращает название региона
     */
    public String getRegionName() {
        
        String regionName = (String) getPropertyValue(FoPropertyType.REGION_NAME);
        if ((regionName != null) && (regionName.length() != 0)) {
            return regionName;
        } else if (this instanceof RegionAfter) {
            return GlobalData.XSL_REGION_AFTER;
        } else if (this instanceof RegionBefore) {
            return GlobalData.XSL_REGION_BEFORE;
        } else {
            return GlobalData.XSL_REGION_BODY;
        }
    }
}
