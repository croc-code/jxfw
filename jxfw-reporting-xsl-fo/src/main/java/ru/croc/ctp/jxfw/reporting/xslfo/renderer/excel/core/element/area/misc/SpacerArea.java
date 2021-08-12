package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.misc;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область, представляющую собой пустое пространство,
 * получающееся при наличии свойств FO-элемента:
 * 1) padding-before, padding-after, padding-top, padding-bottom,  
 *    padding-start, padding-end, padding-left, padding-right  
 *    В данном случае пустое пространство эмулируется в зависимости от наличия атрибутов padding:
 *      - Если присутствует атрибут top/before/bottom/after:
 *        - Текущей области устанавливается тип расположения block;
 *        - Вставляется SpacerArea (если присутствует атрибут top/before);
 *        - Вставляется новая область CommonArea - область контента(если нет start/end/left/right),
 *          иначе вставляем CommonArea с типом расположения inline, в которую вставляем:
 *          - SpacerArea (если присутствует атрибут start/left);
 *          - Область контента - CommonArea;
 *          - SpacerArea (если присутствует атрибут end/right);
 *        - Вставляется SpacerArea (если присутствует атрибут bottom/after);
 *      - Если отсутствуют атрибуты top/before/bottom/after и присутствуют start/end/left/right:
 *        - Текущей области устанавливается тип расположения inline;
 *        - Вставляем в эту область:
 *          - SpacerArea (если присутствует атрибут start/left);
 *          - Область контента - CommonArea;
 *          - SpacerArea (если присутствует атрибут end/right);
 *     Во всех случаях эмуляции пустых пространств, корректируются некоторые свойства
 *     этих областей с учетом того, что эти пустые пространства лежат внутри border прямоугольника
 *     текущей области
 * 2) space-before, space-after, space-start, space-end
 *       Аналогично padding-свойствам, за исключением того, что учитывается, что эти пустые пространства
 *       находятся вне border прямоугольника текущей области
 *    !!!!!!!
 *    ПУСТЫЕ ПРОСТРАНСТВА С РАЗМЕРАМИ МЕНЬШЕ ЧЕМ ПОРОГОВОЕ - GlobalData.PADDING_SPACE_THRESHOLD - 
 *    НЕ ОБРАБАТЫВАЮТСЯ, Т.К. В EXCEL СРЕДНИЕ РАЗМЕРЫ ЭЛЕМЕНТОВ ИМЕЮТ ЗНАЧЕНИЕ ~ ПОРОГОВОМУ
 *    !!!!!!!
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class SpacerArea extends GenericArea {
    
    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public SpacerArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.SPACER;
    }
}
