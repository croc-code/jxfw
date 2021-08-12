package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Класс, инкапсулирующий обработку элемента fo:root.
 * Created by vsavenkov on 23.06.2017.
 */
@SuppressWarnings("rawtypes")
public class RootArea extends GenericArea {

    /**
     * Отображение, хранящее набор SimplePageMasterArea соответствующих ключам - master-name.
     */
    private Hashtable layoutMasterSet;

    /**
     * Инициализирующий конструктор.
     */
    public RootArea() {
        super(AreaType.ROOT, null, new HashMap<>());
        layoutMasterSet = new Hashtable();
    }

    /**
     * Свойство - отображение, хранящее набор SimplePageMasterArea соотвествующих ключам - master-name.
     * @return Hashtable    - возвращает набор мастер страниц.
     */
    public Hashtable getLayoutMasterSet() {
        return layoutMasterSet;
    }
    
    public void setLayoutMasterSet(Hashtable layoutMasterSet) { 
        this.layoutMasterSet = layoutMasterSet;
    }

    /**
     * Количество мастер страниц.
     * @return int  - возвращает количество мастер страниц
     */
    public int getCount() {
        return (layoutMasterSet == null) ? 0 : layoutMasterSet.size();
    }
}
