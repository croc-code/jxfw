package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text.InlineArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.PageSequenceArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.RootArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root.SimplePageMasterArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:page-number&gt;.
 * Created by vsavenkov on 27.07.2017.
 */
public class PageNumberArea extends InlineArea {

    /**
     * Корневая область.
     */
    private final RootArea rootArea;

    /**
     * Свойство - текст элемента.
     * @return String   - возвращает текст элемента
     */
    @Override
    public String getText() {
        return getPageNumber();
    }

    /**
     * Свойство - текст элемента.
     * @param text - Текст элемента.
     */
    public void setText(String text) {
    }

    /**
     * Инициализирующий конструктор.
     * @param rootArea      - Корневая область
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public PageNumberArea(RootArea rootArea, IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);

        this.rootArea = rootArea;
        text = GlobalData.PAGE_NUMBER_EXCEL_ENTITY;
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.PAGE_NUMBER;
    }

    /**
     * Получение номера страницы.
     * Возвращает значение > 0, если координаты заданы, иначе -1
     * @return String   - возвращает номер страницы
     */
    public String getPageNumber() {

        int borderY = getBorderRectangle().getY();
        if (getBorderRectangle().getY() < 0) {
            return GlobalData.SPACE;
        }

        // Получаем первое описание свойств страницы
        PageSequenceArea pageSequenceArea = (PageSequenceArea) rootArea.getChildrenList().get(0);

        // Получаем ссылку на класс, содержащий параметры страницы
        SimplePageMasterArea simplePageMasterArea = (SimplePageMasterArea) rootArea.getLayoutMasterSet()
                .get(pageSequenceArea.getPropertyValue(FoPropertyType.MASTER_REFERENCE));

        int pageHeight = simplePageMasterArea.getPageContentHeight();

        IArea staticContentArea = getParentStaticContent();
        if (staticContentArea != null && staticContentArea.getProperties() != null
                && simplePageMasterArea.getRegionBefore() != null) {
            String flowName = (String)staticContentArea.getPropertyValue(FoPropertyType.FLOW_NAME);
            if (flowName.equals(simplePageMasterArea.getRegionBefore().getRegionName())) {
                return GlobalData.DEFAULT_PAGE_NUMBER;
            }
        } else {
            return String.valueOf(borderY / pageHeight + 1);
        }
        return GlobalData.DEFAULT_PAGE_NUMBER;
    }

    /**
     * Метод для получения родительской области элемента &lt;fo:static-content&gt;.
     * @return IArea    - возвращает родительскую область элемента &lt;fo:static-content&gt;
     */
    private IArea getParentStaticContent() {

        IArea parentArea = this.parentArea;
        while (parentArea != null) {
            if (parentArea.getAreaType() == AreaType.STATIC_CONTENT) {
                return parentArea;
            }

            parentArea = parentArea.getParentArea();
        }
        return parentArea;
    }
}
