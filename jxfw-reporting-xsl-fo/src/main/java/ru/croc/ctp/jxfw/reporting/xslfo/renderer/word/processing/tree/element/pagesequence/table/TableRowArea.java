package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:table-row.
 * Created by vsavenkov on 28.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class TableRowArea extends GenericArea {

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public TableRowArea(GenericArea parentArea, Map<String, String> attributeList) {
        super(AreaType.TABLE_ROW, parentArea, attributeList);
    }
}
