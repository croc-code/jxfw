package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:simple-page-master.
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class SimplePageMasterArea extends GenericArea {

    /**
     * Ссылка на область RegionBody.
     */
    private RegionBody regionBody;

    /**
     * Ссылка на область RegionBefore.
     */
    private RegionBefore regionBefore;

    /**
     * Ссылка на область RegionAfter.
     */
    private RegionAfter regionAfter;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public SimplePageMasterArea(GenericArea parentArea, Map<String, String> attributeList) {
        super(AreaType.SIMPLE_PAGE_MASTER, parentArea, attributeList);
    }

    /**
     * Свойство - ссылка на область fo:region-body.
     * @return RegionBody   - возвращает ссылку на область fo:region-body
     */
    public RegionBody getRegionBody() {
        return regionBody;
    }

    public void setRegionBody(RegionBody regionBody) {
        this.regionBody = regionBody;
    }

    /**
     * Свойство - ссылка на область fo:region-before.
     * @return RegionBefore   - возвращает ссылку на область fo:region-before
     */
    public RegionBefore getRegionBefore() {
        return regionBefore;
    }

    public void setRegionBefore(RegionBefore regionBefore) {
        this.regionBefore = regionBefore;
    }

    /**
     * Свойство - ссылка на область fo:region-after.
     * @return RegionAfter   - возвращает ссылку на область fo:region-after
     */
    public RegionAfter getRegionAfter() {
        return regionAfter;
    }

    public void setRegionAfter(RegionAfter regionAfter) {
        this.regionAfter = regionAfter;
    }

    /**
     * возвращает запас сверху.
     * @return double   - возвращает запас сверху.
     */
    public double getMarginTop() {
        
        return getMargin(FoPropertyType.MARGIN_TOP);
    }

    /**
     * возвращает запас снизу.
     * @return double   - возвращает запас снизу.
     */
    public double getMarginBottom() {
        
        return getMargin(FoPropertyType.MARGIN_BOTTOM);
    }

    /**
     * возвращает запас слева.
     * @return double   - возвращает запас слева.
     */
    public double getMarginLeft() {

        return getMargin(FoPropertyType.MARGIN_LEFT);
    }

    /**
     * возвращает запас справа.
     * @return double   - возвращает запас справа.
     */
    public double getMarginRight() {
        
        return getMargin(FoPropertyType.MARGIN_RIGHT);
    }

    /**
     * Рассчитывает и возвращает запас для переданного типа.
     * @param marginType    - один из типов (MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM. MARGIN_LEFT)
     * @return double   - Возвращает запас для переданного типа
     **/
    private double getMargin(FoPropertyType marginType) {
        
        double result = HelpFuncs.nvl2(getPropertyValue(marginType), HelpFuncs.ZERO);
        if (regionBody != null) {
            result += HelpFuncs.nvl2(regionBody.getPropertyValue(marginType), HelpFuncs.ZERO);
        }

        return result;
    }

    /**
     * Возвращает размер экстента перед.
     * @return double   - Возвращает размер экстента перед.
     */
    public double getRegionBeforeExtent() {
        
        // Если экстент больше, чем граница сверху, то ограничиваем его размер
        return Math.min(getMarginTop(),
            regionBefore != null
                   ? HelpFuncs.nvl2(regionBefore.getPropertyValue(FoPropertyType.EXTENT), HelpFuncs.ZERO)
                   : HelpFuncs.ZERO);
    }

    /**
     * Возвращает размер экстента после.
     * @return double   - Возвращает размер экстента после.
     */
    public double getRegionAfterExtent() {

        // Если экстент больше, чем граница снизу, то ограничиваем его размер
        return Math.min(getMarginBottom(),
            regionAfter != null
                   ? HelpFuncs.nvl2(regionAfter.getPropertyValue(FoPropertyType.EXTENT), HelpFuncs.ZERO)
                   : HelpFuncs.ZERO);
    }
}
