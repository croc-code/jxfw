package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.listblock;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.BLOCK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.LIST_ITEM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.LIST_ITEM_BODY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType.LIST_ITEM_LABEL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PROVISIONAL_DISTANCE_BETWEEN_STARTS;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PROVISIONAL_LABEL_SEPARATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs.ZERO;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute.FoBorder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:list-item.
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ListItemArea extends GenericArea {

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public ListItemArea(GenericArea parentArea, Map<String, String> attributeList) {
        super(LIST_ITEM, parentArea, attributeList);
    }
    
    /**
     * Попытка макисмально вынести на верхний уровеь (в list-item-label или list-item-block) отступы слева/справа.
     * @param area - область
     */
    private static void adjustPaddings(GenericArea area) {
        
        double paddingLeft = HelpFuncs.nvl2(area.getPropertyValue(PADDING_LEFT), ZERO);
        double paddingRight = HelpFuncs.nvl2(area.getPropertyValue(PADDING_RIGHT), ZERO);

        if (area.hasChildren()) {
            double[][] arChildMargins = new double[area.getChildrenList().size()][2];
            int count = 0;
            // Переносим общее значение внешних отступов у детей BLOCK-ов родителю
            double left = Double.MAX_VALUE;
            double right = Double.MAX_VALUE;
            for (GenericArea childArea : area.getChildrenList()) {
                // Если среди детей попался не блок, то отменяем процесс
                if (childArea.getType() != BLOCK) {
                    left = ZERO;
                    right = ZERO;
                    break;
                }
                // рекурсивно вызываем себя для вложенного блока до обработки значения
                adjustPaddings(childArea);
                arChildMargins[count][0] = HelpFuncs.nvl2(childArea.getPropertyValue(MARGIN_LEFT), ZERO);
                arChildMargins[count][1] = HelpFuncs.nvl2(childArea.getPropertyValue(MARGIN_RIGHT), ZERO);
                left = Math.min(left, arChildMargins[count][0]);
                right = Math.min(right, arChildMargins[count][1]);
                count++;
            }

            if (left > ZERO || right > ZERO) {
                count = 0;
                for (GenericArea childArea : area.getChildrenList()) {
                    // Вычитаем у детей
                    childArea.setPropertyValue(MARGIN_LEFT, arChildMargins[count][0] - left);
                    childArea.setPropertyValue(MARGIN_RIGHT, arChildMargins[count][1] - right);
                }
                // Добавляем себе
                paddingLeft += left;
                paddingRight += right;
            }
        }
        // Если есть падинг и нет границы, то падинг перекидывается в маргин
        if (paddingLeft > ZERO) {
            FoBorder borderLeft = (FoBorder)area.getPropertyValue(BORDER_LEFT);
            if (!borderLeft.isDefined()) {
                area.setPropertyValue(MARGIN_LEFT, paddingLeft
                        + HelpFuncs.nvl2(area.getPropertyValue(MARGIN_LEFT), ZERO));
                paddingLeft = ZERO;
            }
            area.setPropertyValue(PADDING_LEFT, paddingLeft);
        }

        if (paddingRight > ZERO) {
            FoBorder borderRight = (FoBorder)area.getPropertyValue(BORDER_RIGHT);
            if (!borderRight.isDefined()) {
                area.setPropertyValue(MARGIN_RIGHT, paddingRight
                        + HelpFuncs.nvl2(area.getPropertyValue(MARGIN_RIGHT), ZERO));
                paddingRight = ZERO;
            }
            area.setPropertyValue(PADDING_RIGHT, paddingRight);
        }
    }

    /**
     * Расчет ширины метки с учетом всяких атрибутов типа provisional-label-distance и provisional-label-separation.
     * @param labelArea - Xsl-fo область метки
     * @param bodyArea  - Xsl-fo область тела
     */
    private void calculateMargins(GenericArea labelArea, GenericArea bodyArea) {
        
        final double undefined = -1d;
        // Если у нас одновременно не указаны метка и тело, то регулировать нечего
        if (labelArea == null || bodyArea == null) {
            return;
        }
        double labelSeparation = HelpFuncs.nvl2(getPropertyValue(PROVISIONAL_LABEL_SEPARATION), undefined);
        if (labelSeparation <= ZERO) {
            // Пытаемся получить значение, указанное в list-block
            labelSeparation = HelpFuncs.nvl2(getParentArea().getPropertyValue(PROVISIONAL_LABEL_SEPARATION), ZERO);
        }
        double distanceBetweenStarts = HelpFuncs.nvl2(getPropertyValue(PROVISIONAL_DISTANCE_BETWEEN_STARTS), undefined);
        if (distanceBetweenStarts <= ZERO) {
            // Пытаемся получить значение, указанное в list-block
            distanceBetweenStarts = HelpFuncs.nvl2(
                    getParentArea().getPropertyValue(PROVISIONAL_DISTANCE_BETWEEN_STARTS), ZERO);
        }

        double labelMarginLeft = HelpFuncs.nvl2(labelArea.getPropertyValue(MARGIN_LEFT), ZERO);
        double labelWidth = HelpFuncs.nvl2(labelArea.getPropertyValue(WIDTH), undefined);
        double labelMarginRight = HelpFuncs.nvl2(labelArea.getPropertyValue(MARGIN_RIGHT), ZERO);
        double bodyMarginLeft = HelpFuncs.nvl2(bodyArea.getPropertyValue(MARGIN_LEFT), ZERO);
        double bodyWidth = HelpFuncs.nvl2(bodyArea.getPropertyValue(WIDTH), ZERO);
        double bodyMarginRight = HelpFuncs.nvl2(bodyArea.getPropertyValue(MARGIN_RIGHT), ZERO);
        double listItemWidth = HelpFuncs.nvl2(getPropertyValue(WIDTH), undefined);
        if (listItemWidth < ZERO) {
            // Пытаемся получить значение, указанное в list-block
            listItemWidth = HelpFuncs.nvl2(getParentArea().getPropertyValue(WIDTH), ZERO);
        }

        if (labelWidth < ZERO) {
            double distance = bodyMarginLeft - labelMarginLeft;
            if (distanceBetweenStarts > ZERO) {
                distance = Math.max(distance, distanceBetweenStarts);
            } else if (distance <= ZERO) {
                distance = GlobalData.DEFAULT_PROVISIONAL_DISTANCE_BETWEEN_STARTS;
            }

            labelWidth = distance - labelSeparation;

            if (listItemWidth > ZERO) {
                double labelWidth1 = labelMarginRight > ZERO
                    ? listItemWidth - labelMarginRight - labelMarginLeft
                    : listItemWidth;
                double labelWidth2 = bodyWidth > ZERO
                    ? listItemWidth - bodyMarginRight - bodyWidth - labelSeparation - labelMarginLeft
                    : listItemWidth;

                labelWidth1 = Math.min(labelWidth1, labelWidth2);
                if (labelWidth1 < listItemWidth && labelWidth1 > labelWidth) {
                    labelWidth = labelWidth1;
                }
            }
            if (labelWidth < ZERO) {
                labelWidth = ZERO;
            } else {
                // Устанавливаем расчетную ширину для метки
                labelArea.setPropertyValue(WIDTH, labelWidth);
            }
        }

        if (distanceBetweenStarts > ZERO) {
            labelSeparation = Math.max(distanceBetweenStarts - labelWidth, labelSeparation);
        }

        double marginLeft = Math.max(bodyMarginLeft - labelMarginLeft - labelWidth, ZERO);
        if (labelMarginRight > ZERO && bodyWidth > ZERO) {
            marginLeft = Math.max(marginLeft, labelMarginRight - bodyWidth - bodyMarginRight);
        }

        if (marginLeft <= ZERO || marginLeft < labelSeparation) {
            marginLeft = labelSeparation;
        }

        labelArea.setPropertyValue(MARGIN_RIGHT, ZERO);
        bodyArea.setPropertyValue(MARGIN_LEFT, marginLeft);
    }

    /**
     * Вызов обработки свойств после добавления всех детей и вызова для них метода postProcessProperties.
     * То есть подымаемся снизу вверх
     */
    @Override
    public void postProcessProperties() {
        
        if (hasChildren()) {
            GenericArea labelArea = null;
            GenericArea bodyArea = null;
            for (GenericArea childArea : getChildrenList()) {
                if (childArea.getType() == LIST_ITEM_LABEL && labelArea == null) {
                    labelArea = childArea;
                }
                if (childArea.getType() == LIST_ITEM_BODY && bodyArea == null) {
                    bodyArea = childArea;
                }
                adjustPaddings(childArea);
            }
            calculateMargins(labelArea, bodyArea);
        }
    }
}