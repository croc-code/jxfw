package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.table;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.XslFoException;

import java.util.Map;

/**
 * Класс, инкапсулирующий область XSL-FO элемента &lt;fo:table-body&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableBodyArea extends CellGridContainer {

    /**
     * Конструктор по умолчанию.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     * @throws XslFoException   - генерирует при нарушении структуры
     */
    public TableBodyArea(IArea parentArea, Map<String, String> attributeList) throws XslFoException {
        super(parentArea, attributeList);
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.TABLE_BODY;
    }
}
