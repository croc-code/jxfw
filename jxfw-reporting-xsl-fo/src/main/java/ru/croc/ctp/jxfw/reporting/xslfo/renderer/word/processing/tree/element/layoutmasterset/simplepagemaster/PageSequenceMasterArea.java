package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.ArrayList;
import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:single-page-master-reference
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("rawtypes")
public class PageSequenceMasterArea extends GenericArea {

    /**
     * контейнер мастеров страниц.
     */
    private ArrayList singlePageMasterReferences;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public PageSequenceMasterArea(GenericArea parentArea, Map<String, String> attributeList) {
        super(AreaType.PAGE_SEQUENCE_MASTER, parentArea, attributeList);
        singlePageMasterReferences = new ArrayList();
    }

    /**
     * свойство контейнер мастеров страниц.
     * @return ArrayList    - возвращает контейнер мастеров страниц
     */
    public ArrayList getSinglePageMasterReferences() {
        return singlePageMasterReferences;
    }

    public void setSinglePageMasterReferences(ArrayList singlePageMasterReferences) {
        this.singlePageMasterReferences = singlePageMasterReferences;
    }
}
