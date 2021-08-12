package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.pagesequence.table;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.ArrayList;
import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:table.
 * Created by vsavenkov on 28.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("rawtypes")
public class TableArea extends GenericArea {

    /**
     * Список колонок.
     */
    private ArrayList columnList;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public TableArea(GenericArea parentArea, Map<String, String> attributeList) {
        
        super(AreaType.TABLE, parentArea, attributeList);
        columnList = new ArrayList();
    }

    /**
     * Свойство - список колонок.
     * @return ArrayList    - возвращает список колонок
     */
    public ArrayList getColumnList() {
        return columnList;
    }

    /**
     * Свойство - список колонок.
     * @param columnList - список колонок
     */
    public void setColumnList(ArrayList columnList) {
        if (columnList != null) {
            this.columnList = columnList;
        } else {
            this.columnList = new ArrayList();
        }
    }  
}
