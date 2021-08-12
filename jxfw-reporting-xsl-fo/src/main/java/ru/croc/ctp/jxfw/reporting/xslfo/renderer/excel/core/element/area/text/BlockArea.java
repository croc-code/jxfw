package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.REFERENCE_ORIENTATION_0_ANGLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.REFERENCE_ORIENTATION_90_ANGLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.REFERENCE_ORIENTATION_MINUS_90_ANGLE;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:block&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class BlockArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public BlockArea(IArea parentArea, Map<String, String> attributeList) {

        super(parentArea, attributeList);
    
        preProcessProperties();
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.BLOCK;
    }

    /**
     * Обработка свойств области. Все свойства должны быть установлены.
     * Общая реализация.
     */
    private void preProcessProperties() {
    
        // Установка угла поворота текста
        Integer rotation = (Integer) getProperty(FoPropertyType.REFERENCE_ORIENTATION);
        if (rotation == null || rotation == GlobalData.DEFAULT_REFERENCE_ORIENTATION) {
            return;
        }

        if (getAreaType() == AreaType.BLOCK) {
            switch (rotation) {
                case REFERENCE_ORIENTATION_0_ANGLE:
                case REFERENCE_ORIENTATION_90_ANGLE:
                case REFERENCE_ORIENTATION_MINUS_90_ANGLE:
                    return;
                default:
                    setPropertyValue(FoPropertyType.REFERENCE_ORIENTATION, GlobalData.DEFAULT_REFERENCE_ORIENTATION);
            }
        }
    }
}
