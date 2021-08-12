package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.image;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.foimage.FoImage;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:external-graphic&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ExternalGraphicArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public ExternalGraphicArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
        
        preProcessProperties();
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.EXTERNAL_GRAPHIC;
    }

    /**
     * Обработка свойств области. Все свойства должны быть установлены.
     * Общая реализация.
     */
    private void preProcessProperties() {
        
        FoImage image = (FoImage) getPropertyValue(FoPropertyType.EXTERNAL_GRAPHIC);
        if (image != null) {
            // Устанавливаем размер области равный размеру картинки
            // Корректируем размеры изображения из-за коэффициентов, которымы Excel приходится корректировать
            getBorderRectangle().setFixedWidth((int)Math.round((image.getWidth() + 1) / GlobalData.EXCEL_WIDTH_RATIO));
            getBorderRectangle().setFixedHeight(
                    (int)Math.round((image.getHeight() + 1) / GlobalData.EXCEL_HEIGHT_RATIO));
        }
    }
}
