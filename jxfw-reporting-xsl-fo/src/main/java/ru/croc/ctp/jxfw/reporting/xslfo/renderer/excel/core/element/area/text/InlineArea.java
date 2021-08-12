package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:inline&gt;.
 * Все элементы #PCDATA XML файла по умолчанию будут заменяться этой областью
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class InlineArea extends GenericArea {

    /**
     * Текст элемента.
     */
    protected String text = StringUtils.EMPTY;

    /**
     * Текст элемента.
     * @return String   - возвращает текст элемента.
     */
    public String getText() {
        return text;
    }

    /**
     * Текст элемента.
     * @param text - Текст элемента.
     */
    public void setText(String text) {
        this.text = StringUtils.defaultString(text);
    }

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public InlineArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);
        // Задаем по умолчанию тип направления расположения области (ее дочерних областей)
        progressionDirection = AreaProgressionDirection.INLINE;
    }

    /**
     * Инициализирующий конструктор.
     * @param text          - Текст элемента
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public InlineArea(String text, IArea parentArea, Map<String, String> attributeList) {
        this(parentArea, attributeList);
        this.text = text;
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.INLINE;
    }
}
