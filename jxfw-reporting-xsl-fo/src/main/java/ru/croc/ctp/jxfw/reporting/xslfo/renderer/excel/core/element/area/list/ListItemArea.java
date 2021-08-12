package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.list;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область элемента fo:list-item.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ListItemArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public ListItemArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);

        // Задаем по умолчанию тип направления расположения области (ее дочерних областей)
        setProgressionDirection(AreaProgressionDirection.ROW);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.LIST_ITEM;
    }

    @Override
    public void postProcessProperties() {
    
        super.postProcessProperties();
        GenericArea listItemLabelArea = (GenericArea) HelpFuncs.findArea(this, AreaType.LIST_ITEM_LABEL);
        GenericArea listItemBodyArea = (GenericArea)HelpFuncs.findArea(this, AreaType.LIST_ITEM_BODY);
        // Если у нас одновременно не указаны метка и тело, то регулировать нечего
        if (listItemLabelArea == null || listItemBodyArea == null) {
            return;
        }

        // Фишка в том, что все отступы и у метки и у тела заданы относительно list-item, а не друг друга..
        // Поэтому надо высчитать правильные размеры и откорректировать отступы так, чтобы они были последовательны
        Integer labelSeparation = null;
        Integer distanceBetweenStarts = null;

        // Получаем значение provisional-label-separation
        Dimension dimension = getDimensionValue(FoPropertyType.PROVISIONAL_LABEL_SEPARATION);
        if (dimension.isDefined()) {
            // У list-item есть, если это значение процентное, то сперва пытаемся разрешить от своей ширины
            labelSeparation = resolveDimensionPercentValue(dimension, true, false);
            if (null == labelSeparation) {
                // если не получилось, то от ширины list-block
                labelSeparation = resolveDimensionPercentValue(dimension, true, true);
            }
        }
        if (null == labelSeparation) {
            // Пытаемся получить значение, указанное в list-block
            dimension = ((GenericArea)getParentArea()).getDimensionValue(FoPropertyType.PROVISIONAL_LABEL_SEPARATION);
            labelSeparation = resolveDimensionPercentValue(dimension, true, true);
        }
        if (null == labelSeparation) {
            labelSeparation = 0;
        }

        // Получаем значение provisional-distance-between-starts
        dimension = getDimensionValue(FoPropertyType.PROVISIONAL_DISTANCE_BETWEEN_STARTS);
        if (dimension.isDefined()) {
            // У list-item есть, если это значение процентное, то сперва пытаемся разрешить от своей ширины
            distanceBetweenStarts = resolveDimensionPercentValue(dimension, true, false);
            if (null == distanceBetweenStarts) {
                // если не получилось, то от ширины list-block
                distanceBetweenStarts = resolveDimensionPercentValue(dimension, true, true);
            }
        }
        if (null == distanceBetweenStarts) {
            // Пытаемся получить значение, указанное в list-block
            dimension = ((GenericArea)getParentArea())
                    .getDimensionValue(FoPropertyType.PROVISIONAL_DISTANCE_BETWEEN_STARTS);
            distanceBetweenStarts = resolveDimensionPercentValue(dimension, true, true);
        }

        int labelMarginLeft = listItemLabelArea.getMargins().getMarginLeft();
        int labelWidth = listItemLabelArea.getBorderRectangle().getWidth();
        int labelMarginRight = listItemLabelArea.getMargins().getMarginRight();
        int bodyMarginLeft = listItemBodyArea.getMargins().getMarginLeft();
        int bodyWidth = listItemBodyArea.getBorderRectangle().isWidthDefined()
                ? listItemBodyArea.getBorderRectangle().getWidth() : 0;
        int bodyMarginRight = listItemBodyArea.getMargins().getMarginRight();
        int listItemWidth = getBorderRectangle().isWidthDefined()
                 ? getBorderRectangle().getWidth()
                 : getParentArea().getBorderRectangle().isWidthDefined()
                    ? getParentArea().getBorderRectangle().getWidth() : 0;
        if (labelWidth < 0) {
            int distance = bodyMarginLeft - labelMarginLeft;
            if (null != distanceBetweenStarts) {
                distance = Math.max(distance, distanceBetweenStarts);
            } else if (distance <= 0) {
                distance = GlobalData.DEFAULT_PROVISIONAL_DISTANCE_BETWEEN_STARTS;
            }

            labelWidth = distance - labelSeparation;

            if (listItemWidth > 0) {
                int labelWidth1 = labelMarginRight > 0 ? listItemWidth - labelMarginRight - labelMarginLeft
                        : listItemWidth;
                int labelWidth2 = bodyWidth > 0 ? listItemWidth - bodyMarginRight - bodyWidth
                        - labelSeparation - labelMarginLeft : listItemWidth;

                labelWidth1 = Math.min(labelWidth1, labelWidth2);
                if (labelWidth1 < listItemWidth && labelWidth1 > labelWidth) {
                    labelWidth = labelWidth1;
                }
            }
            if (labelWidth < 0) {
                labelWidth = 0;
            } else {
                // Устанавливаем расчетную ширину для метки
                AreaRectangle rect = listItemLabelArea.getBorderRectangle();
                rect.setWidth(labelWidth);
                // Считываем, чтобы считать установившееся значение с учетом min-width & max-width
                labelWidth = rect.getWidth();
                // Устанавливаем данное значение как максимальное, чтобы зафиксировать размер ширины метки!
                rect.setMaxWidth(labelWidth);
            }
        }

        if (null != distanceBetweenStarts) {
            labelSeparation = Math.max(distanceBetweenStarts - labelWidth, labelSeparation);
        }

        int marginLeft = Math.max(bodyMarginLeft - labelMarginLeft - labelWidth, 0);
        if (labelMarginRight > 0 && bodyWidth > 0) {
            marginLeft = Math.max(marginLeft, labelMarginRight - bodyWidth - bodyMarginRight);
        }

        if (marginLeft <= 0 || marginLeft < labelSeparation) {
            marginLeft = labelSeparation;
        }

        listItemLabelArea.getMargins().setMarginRight(0);
        listItemBodyArea.getMargins().setMarginLeft(marginLeft);
    }
}
