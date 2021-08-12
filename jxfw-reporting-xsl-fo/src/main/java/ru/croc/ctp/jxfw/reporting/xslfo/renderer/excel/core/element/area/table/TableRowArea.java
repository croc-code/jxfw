package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:table-row&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableRowArea extends GenericArea {

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    public TableRowArea(IArea parentArea, Map<String, String> attributeList) {
        super(parentArea, attributeList);

        // Задаем по умолчанию тип направления расположения области (ее дочерних областей)
        progressionDirection = AreaProgressionDirection.ROW;
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.TABLE_ROW;
    }

    /**
     * Свойство - Контейнер - секция таблицы, которой принадлежит строка.
     * @return CellGridContainer - возвращает контейнер - секцию таблицы
     */
    public CellGridContainer getGridContainer() {
        return (CellGridContainer) parentArea;
    }
}
