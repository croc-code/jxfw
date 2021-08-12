package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region.RegionAfter;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region.RegionBefore;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.region.RegionBody;

import java.util.Map;

/**
 * Класс, инкапсулирующий область fo:simple-page-master.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class SimplePageMasterArea extends GenericArea {

    /**
     * Значение элемента fo:region-body.
     */
    private RegionBody regionBody;

    /**
     * Значение элемента fo:region-before.
     */
    private RegionBefore regionBefore;

    /**
     * Значение элемента fo:region-after.
     */
    private RegionAfter regionAfter;

    /**
     * Свойство - значение элемента fo:region-body.
     * @return RegionBody   - возвращает значение элемента fo:region-body
     */
    public RegionBody getRegionBody() {
        return regionBody;
    }

    /**
     * Свойство - значение элемента fo:region-body.
     * @param regionBody - значение элемента fo:region-body
     */
    public void setRegionBody(RegionBody regionBody) {
        this.regionBody = regionBody ;
    }

    /**
     * Свойство - значение элемента fo:region-before.
     * @return RegionBefore - возвращает значение элемента fo:region-before
     */
    public RegionBefore getRegionBefore() {
        return regionBefore;
    }

    /**
     * Свойство - значение элемента fo:region-before.
     * @param regionBefore - значение элемента fo:region-before
     */
    public void setRegionBefore(RegionBefore regionBefore) {
        this.regionBefore = regionBefore;
    }

    /**
     * Свойство - значение элемента fo:region-after.
     * @return RegionAfter  - возвращает значение элемента fo:region-after
     */
    public RegionAfter getRegionAfter() {
        return regionAfter;
    }

    /**
     * Свойство - значение элемента fo:region-after.
     * @param regionAfter - значение элемента fo:region-after
     */
    public void setRegionAfter(RegionAfter regionAfter) {
        this.regionAfter = regionAfter;
    }

    /**
     * Свойство - значение расположения страницы.
     * @return int  - возвращает значение расположения страницы
     */
    public int getReferenceOrientation() {
        return (int) getPropertyValue(FoPropertyType.REFERENCE_ORIENTATION);
    }

    /**
     * Свойство - значение отступа слева для страницы.
     * @return int  - возвращает значение отступа слева для страницы
     */
    public int getMarginLeft() {
        return getOrCreateMargins().getMarginLeft()
                + (regionBody != null ? regionBody.getOrCreateMargins().getMarginLeft() : 0);
    }

    /**
     * Свойство - значение отступа справа для страницы.
     * @return int  - возвращает значение отступа справа для страницы
     */
    public int getMarginRight() {
        return getOrCreateMargins().getMarginRight()
                + (regionBody != null ? regionBody.getOrCreateMargins().getMarginRight() : 0);
    }

    /**
     * Свойство - значение отступа сверху для страницы.
     * @return int  - возвращает значение отступа сверху для страницы
     */
    public int getMarginTop() {

        int margin = getOrCreateMargins().getMarginTop()
                + (regionBody != null ? regionBody.getOrCreateMargins().getMarginTop() : 0);
        int extent = (regionBefore != null) ? (int) regionBefore.getExtent() : 0;
        return Math.max(margin, extent);
    }

    /**
     * Свойство - значение отступа снизу для страницы.
     * @return int  - возвращает значение отступа снизу для страницы
     */
    public int getMarginBottom() {

        int margin = getOrCreateMargins().getMarginBottom()
                + (regionBody != null ? regionBody.getOrCreateMargins().getMarginBottom() : 0);
        int extent = (regionAfter != null) ? (int) regionAfter.getExtent() : 0;
        return Math.max(margin, extent);
    }

    /**
     * Свойство - значение ширины контента страницы.
     * @return int  - возвращает значение ширины контента страницы
     */
    public int getPageContentWidth() {
        return (int) getPropertyValue(FoPropertyType.PAGE_WIDTH) - getMarginLeft() - getMarginRight();
    }

    /**
     * Свойство - значение высоты контента страницы.
     * @return int  - возвращает значение высоты контента страницы
     */
    public int getPageContentHeight() {
        return (int) getPropertyValue(FoPropertyType.PAGE_HEIGHT) - getMarginTop() - getMarginBottom();
    }

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public SimplePageMasterArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.SIMPLE_PAGE_MASTER;
    }
}
