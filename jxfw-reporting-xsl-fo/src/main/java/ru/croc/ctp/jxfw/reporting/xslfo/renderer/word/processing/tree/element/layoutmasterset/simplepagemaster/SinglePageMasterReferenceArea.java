package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.layoutmasterset.simplepagemaster;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.util.Hashtable;
import java.util.Map;

/**
 * Класс, инкапсулирующий обработку элемента fo:single-page-master-reference
 * Created by vsavenkov on 06.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("rawtypes")
public class SinglePageMasterReferenceArea extends GenericArea {

    /**
     * Ссылка на контейнер m_oLayoutMasterSet.
     */
    private Hashtable layoutMasterSet;

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Ссылка на родительскую область
     * @param attributeList - Список атрибутов
     */
    public SinglePageMasterReferenceArea(GenericArea parentArea, Map<String, String> attributeList) {
        super(AreaType.SINGLE_PAGE_MASTER_REFERENCE, parentArea, attributeList);
    }

    /**
     * Свойство - отображение, хранящее набор SimplePageMasterArea соотвествующих ключам - master-name.
     * @return Hashtable    - возвращает набор SimplePageMasterArea
     */
    public Hashtable getLayoutMasterSet() {
        return layoutMasterSet;
    }

    public void setLayoutMasterSet(Hashtable layoutMasterSet) {
        this.layoutMasterSet = layoutMasterSet;
    }
}
