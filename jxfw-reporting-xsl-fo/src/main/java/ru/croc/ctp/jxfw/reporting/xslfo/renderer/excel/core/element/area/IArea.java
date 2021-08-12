package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range;

import java.util.EnumMap;
import java.util.List;

/**
 * Интерфейс, задающий общее поведение области.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public interface IArea {
    
    /**
     * Свойство - список атрибутов(свойств) элемента.
     * @return EnumMap  - возвращает список атрибутов
     */
    EnumMap<FoPropertyType, Object> getProperties();
    
    /**
     * Свойство - cсылка на родительскую область.
     * @return IArea    - возвращает cсылку на родительскую область
     */
    IArea getParentArea();

    /**
     * Свойство - cсылка на родительскую область.
     * @param parentArea - cсылка на родительскую область
     */
    void setParentArea(IArea parentArea);

    /**
     * Свойство - список дочерних областей.
     * @return List - вовзращает список дочерних областей
     */
    List<IArea> getChildrenList();

    /**
     * Свойство - список дочерних областей.
     * @param childrenList - список дочерних областей
     */
    void setChildrenList(List<IArea> childrenList);
    
    /**
     * Свойство - область имеет дочерние области.
     * @return boolean  - возвращает true, если область имеет дочерние области и false в противном случае
     */
    boolean isHasChildren();

    /**
     * Свойство - область разбита на несколько областей - при построении разметки контента путем разбивки на колонки.
     * @return boolean  - возвращает true, если область разбита на несколько областей и false в противном случае
     */
    boolean isSplitted();

    /**
     * Свойство - область разбита на несколько областей - при построении разметки контента путем разбивки на колонки.
     * @param splitted - true, если область разбита на несколько областей и false в противном случае
     */
    void setSplitted(boolean splitted);

    /**
     * Свойство - область имеет атрибут span - распространение области на всю ширину страницы при разбивке на колонки.
     * @return boolean  - возвращает true, если область имеет атрибут span и false в противном случае
     */
    boolean isColumnLayoutSpan();

    /**
     * Свойство - область имеет атрибут span - распространение области на всю ширину страницы при разбивке на колонки.
     * @param columnLayoutSpan - true, если область имеет атрибут span и false в противном случае
     */
    void setColumnLayoutSpan(boolean columnLayoutSpan);

    /**
     * Свойство - область должна быть отрендерена - выведена в Excel.
     * @return boolean  - возвращает true, если область должна быть отрендерена и false в противном случае
     */
    boolean isNeedRendering();

    /**
     * Свойство - прямоугольник - геометрическое расположение и размеры прямоугольника области.
     * @return AreaRectangle    - возвращает прямоугольник
     */
    AreaRectangle getBorderRectangle();

    /**
     * Свойство - прямоугольник - геометрическое расположение и размеры прямоугольника области.
     * @param borderRectangle - прямоугольник - геометрическое расположение и размеры прямоугольника области.
     */
    void setBorderRectangle(AreaRectangle borderRectangle);
    
    /**
     * Свойство - область ячеек Excel, занимаемая прямоугольником области.
     * @return Range    - возвращает область ячеек Excel
     */
    Range getBorderRange();

    /**
     * Свойство - тип направления расположения области (ее дочерних областей)- progress direction.
     * @return AreaProgressionDirection - возвращает тип направления расположения области
     */
    AreaProgressionDirection getProgressionDirection();

    /**
     * Свойство - тип направления расположения области (ее дочерних областей)- progress direction.
     * @param progressionDirection - тип направления расположения области
     */
    void setProgressionDirection(AreaProgressionDirection progressionDirection);

    /**
     * Метод получения значения свойства.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства, если свойство задано ИЛИ null
     */
    Object getProperty(FoPropertyType propertyType);

    /**
     * Метод для получения значения свойства элемента.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства, если свойство задано ИЛИ значение по умолчанию
     */
    Object getPropertyValue(FoPropertyType propertyType);

    /**
     * Метод для получения значения свойства с учетом наследования значения свойства от родительской области.
     * @param propertyType - Тип свойства
     * @param deep         - Счетчик глубины рекурсии. При 1м вызове = 0
     * @return Object   - возвращает значение свойства, если свойство задано у объекта или кого-нибудь из родителей 
     *                      по цепочке ИЛИ null
     */
    Object getInheritedProperty(FoPropertyType propertyType, int deep);

    /**
     * Метод для получения значения свойства с учетом наследования значения свойства от родительской области.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства, если свойство задано ИЛИ значение по умолчанию
     */
    Object getInheritedPropertyValue(FoPropertyType propertyType);

    /**
     * Метод установки значения свойства элемента.
     * @param propertyType  - Тип свойства
     * @param propertyValue - Значение свойства
     */
    void setPropertyValue(FoPropertyType propertyType, Object propertyValue);

    /**
     * Метод для получения типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    AreaType getAreaType();

    /**
     * Вызов обработки свойств после добавления всех детей и вызова для них метода postProcessProperties.
     * То есть подымаемся снизу вверх
     */
    void postProcessProperties();
}
