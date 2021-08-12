package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.root;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.GenericArea;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Класс, инкапсулирующий элемент &lt;fo:root&gt;.
 * Created by vsavenkov on 27.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
@SuppressWarnings("rawtypes")
public class RootArea extends GenericArea {

    /**
     *  Отображение, хранящее набор SimplePageMasterArea соответствующих ключам - master-name.
     */
    private Hashtable layoutMasterSet = new Hashtable();

    /**
     *  Значение - количество колонок на которое призводить разбиение контента области элемента &lt;fo:flow&gt;.
     */
    private int columnCount = GlobalData.DEFAULT_COLUMN_COUNT;

    /**
     *  Значение - расстояние между колонками.
     */
    private int columnGap = GlobalData.DEFAULT_COLUMN_GAP;

    /**
     * Свойство - количество колонок на которое призводить разбиение контента области элемента &lt;fo:flow&gt;.
     * @return int  - возвращает количество колонок на которое призводить разбиение контента области элемента
     *                      &lt;fo:flow&gt;
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Свойство - количество колонок на которое призводить разбиение контента области элемента &lt;fo:flow&gt;.
     * @param columnCount - количество колонок
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * Свойство - расстояние между колонками.
     * @return int  - возвращает расстояние между колонками
     */
    public int getColumnGap() {
        return columnGap;
    }

    /**
     * Свойство - расстояние между колонками.
     * @param columnGap - расстояние между колонками
     */
    public void setColumnGap(int columnGap) {
        this.columnGap = columnGap;
    }

    /**
     * Инициализирующий конструктор.
     */
    public RootArea() {
        super(null, new HashMap<>());

        setBorderRectangle(new AreaRectangle(0, 0, -1, -1));
    }

    /**
     * Получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    @Override
    public AreaType getAreaType() {
        return AreaType.ROOT;
    }

    /**
     * Свойство - отображение, хранящее набор SimplePageMasterArea соотвествующих ключам - master-name.
     * @return Hashtable    - возвращает отображение, хранящее набор SimplePageMasterAre
     *                          соответствующих ключам - master-name
     */
    public Hashtable getLayoutMasterSet() {
        return layoutMasterSet;
    }
}
