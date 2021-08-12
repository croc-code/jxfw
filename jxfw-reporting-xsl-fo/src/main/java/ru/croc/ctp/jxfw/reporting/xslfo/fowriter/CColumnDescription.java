//
// Translated by CS2J (http://www.cs2j.com): 15.02.2017 0:36:14
//

package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AlignClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ValignClass;

import java.util.ArrayList;
import java.util.Comparator;

/**
* Класс, содержащий описание колонки отчета.
* Дерево таких классов описывает набор колонок для отрисовки.
*/
@SuppressWarnings({"unchecked", "rawtypes"})
public final class CColumnDescription {
    /**
    * Конструктор объекта с информацией о колонке отчета.
    * 
    *  @param strCaption Заголовок
    *  @param enAlign Горизонтальное выравнивание
    *  @param enVAlign Вертикальное выравнивание
    *  @param reportStyle Стиль колонки
    *  @param headerStyle Стиль ячеек заголовка колонки
    *  @param strColumnWidth Ширина колонки
    *  @param enHeaderAlign Горизонтальное выравнивание заголовка
    *  @param enHeaderVAlign Вертикальное выравнивание заголовка
    *  @param parentRef Ссылка на описание родительской колонки (или null для верхнего уровня)
    */
    public CColumnDescription(String strCaption,
                              AlignClass enAlign,
                              ValignClass enVAlign,
                              ReportStyle reportStyle,
                              ReportStyle headerStyle,
                              String strColumnWidth,
                              AlignClass enHeaderAlign,
                              ValignClass enHeaderVAlign,
                              CColumnDescription parentRef) {
        //Сохраняем переданные свойства:
        if (strCaption != null) {
            caption = strCaption;
        } else {
            caption = StringUtils.EMPTY;
        }
        columnStyle = reportStyle;
        this.headerStyle = headerStyle;
        if (strColumnWidth != null) {
            this.strColumnWidth = strColumnWidth;
        } else {
            this.strColumnWidth = StringUtils.EMPTY;
        }
        //проставляем ссылку на родителя:
        parentRef.children.add(this);
        align = enAlign;
        vertAlign = enVAlign;
        headerAlign = enHeaderAlign;
        headerVAlign = enHeaderVAlign;
        //Оформляем колоку для включения в дерево
        id = lastId++;
    }

    /**
    * Закрытый конструктор для создания вершины-заглушки, представляющей корень.
    */
    private CColumnDescription() {
        id = lastId++;
    }

    /**
    * Формирует элемент-заглушку для представления корня в иерархии.
    * 
    *  @return {@code new CColumnDescription()}
    */
    public static CColumnDescription getRoot() {
        return new CColumnDescription();
    }

    /**
    * Добавление подчиненной колонки.
    * 
    *  @param child Ссылка на подчиненную колонку
    */
    public void addChild(CColumnDescription child) {
        children.add(child);
    }

    /**
    * Проводит разметку дерева.
    */
    public void treeMarkUp() {
        for (Object dummyForeachVar0 : children) {
            //Прежде всего вызываем разметку для поддерева
            CColumnDescription columnDescription = (CColumnDescription)dummyForeachVar0;
            //проставим для детей уровень в иерархии:
            columnDescription.setTreeLevel(getTreeLevel() + 1);
            //разметим нижестоящих
            columnDescription.treeMarkUp();
            //Прибавим ширину вычисленного поддерева
            subTreeWidth += columnDescription.getTreeWidth();
        }
        //Если это просто лист, то у него ширина будет 1.
        if (getIsLeaf()) {
            subTreeWidth = 1;
        }
         
    }

    //Свойства:
    /**
    * Заголовок колонки.
    * @return заголовок колонки
    */
    public String getCaption() {
        return caption;
    }

    /**
    * Горизонтальное выравнивание.
    * @return  горизонтальное выравнивание
    */
    public AlignClass getAlign() {
        return align;
    }

    /**
    * Вертикальное выравнивание.
    * @return вертикальное выравнивание.
    */
    public ValignClass getVAlign() {
        return vertAlign;
    }

    /**
    * Горизонтальное выравнивание заголовка.
    * @return горизонтальное выравнивание заголовка
    */
    public AlignClass getHeaderAlign() {
        return headerAlign;
    }

    /**
    * Вертикальное выравнивание заголовка.
    * @return вертикальное выравнивание заголовка.
    */
    public ValignClass getHeaderVAlign() {
        return headerVAlign;
    }

    /**
    * Стиль ячеек заголовка колонки.
    * @return cтиль ячеек заголовка колонки.
    */
    public ReportStyle getHeaderCellStyle() {
        return headerStyle;
    }

    /**
    * Стиль колонки.
    * @return cтиль колонки.
    */
    public ReportStyle getColumnStyle() {
        return columnStyle;
    }

    /**
    * Ширина колонки.
    * @return ширина колонки.
    */
    public String getColumnWidth() {
        return strColumnWidth;
    }

    /**
    * Уровень в дереве.
    * Корректное значение будет возвращаться только после
    * предварительного вызова TreeMarkUp.
    * @return уровень в дереве.
    */
    public int getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(int value) {
        treeLevel = value;
    }

    /**
    * Ширина поддерева с корнем в данном узле.
    * @return ширина поддерева с корнем в данном узле
    */
    public int getTreeWidth() {
        return subTreeWidth;
    }

    /**
    * Является ли вершина листовой.
    * @return является ли вершина листовой.
    */
    public boolean getIsLeaf() {
        return children.size() == 0;
    }

    public int getId() {
        return id;
    }

    //Закрытые переменные:
    /**
    * Заголовок колонки храним здесь.
    */
    private String caption = new String();
    /**
    * Горизонтальное выравнивание храним здесь.
    */
    private AlignClass align = AlignClass.ALIGN_NONE;
    /**
    * Вертикальное выравнивание храним здесь.
    */
    private ValignClass vertAlign = ValignClass.VALIGN_NONE;
    /**
    * Стиль колонки.
    */
    private ReportStyle columnStyle;
    /**
    * Стиль ячейки заголовка колонки.
    */
    private ReportStyle headerStyle;
    /**
    * Ширину колонки храним здесь.
    */
    private String strColumnWidth = new String();
    /**
    * Горизонтальное выравнивание заголовка храним здесь.
    */
    private AlignClass headerAlign = AlignClass.ALIGN_NONE;
    /**
    * Вертикальное выравнивание заголовка храним здесь.
    */
    private ValignClass headerVAlign = ValignClass.VALIGN_NONE;
    /**
    * Ширина поддерева - кол-во листовых колонок для данного узла.
    * Учитываются только видимые колонки
    */
    private int subTreeWidth;
    /**
    * Уровень элемента в дереве.
    */
    private int treeLevel;
    /**
    * Внутренний индентификатор.
    */
    private int id;
    /**
    * Глобальный счетчик для назначения идентификаторов.
    */
    private static int lastId;
    //Вспомогательные значения - для организации дерева.
    /**
    * Ссылки на дочерние колонки.
    */
    private ArrayList children = new ArrayList();

    /**
     * Сортировка по уровням.
     */
    protected static final class SortByLevel implements Comparator<CColumnDescription> {

        /**
         * Сравнение по уровням в дереве.
         * @param col1     - Колонка1
         * @param col2     - Колонка2
         * @return int - Разность между уровнями
         */
        public int compare(CColumnDescription col1, CColumnDescription col2) {
            //Колонки с одинаковым уровнем в дереве упорядочиваются по "возрасту".
            if (col1.getTreeLevel() == col2.getTreeLevel()) {
                return col1.getId() - col2.getId();
            } else {
                return col1.getTreeLevel() - col2.getTreeLevel();
            }
        }
    }
}


