package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute.AttributeParser;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Абстрактный базовый класс области.
 * Инкапсулированы общее поведение и интерфейс областей.
 * Created by vsavenkov on 23.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public abstract class GenericArea {

    /**
     * Тип области.
     */
    private final AreaType type;

    /**
     * Котейнер свойств.
     */
    protected EnumMap<FoPropertyType, Object> properties;

    /**
     * Ссылка на родительскую область.
     */
    private GenericArea parentArea;

    /**
     * Список дочерних областей.
     */
    private List<GenericArea> childrenList;

    /**
     * Текст элемента.
     */
    private String text;

    /**
     * Свойство для установки/проверки признака того, что для области XSL-FO был сформирован объект Aspose.Words.
     */
    private boolean isOutputObjectCreated;

    /**
     * Свойство - тип области.
     * @return AreaType возвращает тип области
     */
    public AreaType getType() {
        return type;
    }

    /**
     * Свойство - cсылка на родительскую область.
     * @return GenericArea возвращает cсылку на родительскую область
     */
    public GenericArea getParentArea() {
        return parentArea;
    }

    public void setParentArea(GenericArea parentArea) {
        this.parentArea = parentArea;
    }

    /**
     * Свойство - список дочерних областей.
     * @return List&lt;GenericArea&gt; возвращает список дочерних областей
     */
    public List<GenericArea> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<GenericArea> childrenList) {
        this.childrenList = childrenList;
    }

    /**
     * Свойство - область имеет дочерние области.
     * @return  boolean возвращает true, если область имеет дочерние области и false в противном случае
     */
    public boolean hasChildren() {
        return (childrenList != null && childrenList.size() > 0);
    }

    /**
     * Признак наличия свойств у области.
     * @return boolean возвращает признак наличия свойств у области
     */
    public boolean hasProperties() {
        return properties != null && properties.size() > 0;
    }

    /**
     * Свойство - текст элемента.
     * @return String   - возвращает текст элемента
     */
    public String getText() {
        if (text == null) {
            text = StringUtils.EMPTY;
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Свойство - список атрибутов(свойств) области.
     * @return EnumMap  - возвращает список атрибутов
     */
    public EnumMap<FoPropertyType, Object> getProperties() {
        return properties;
    }

    public boolean isOutputObjectCreated() {
        return isOutputObjectCreated;
    }

    public void setOutputObjectCreated(boolean outputObjectCreated) {
        isOutputObjectCreated = outputObjectCreated;
    }

    /**
     * Инициализирующий конструктор.
     * @param areaType      - Тип создаваемой области
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    protected GenericArea(AreaType areaType, GenericArea parentArea, Map<String, String> attributeList) {

        type = areaType;
        this.parentArea = parentArea;

        // Разбираем заданные атрибуты
        properties = AttributeParser.parse(this, attributeList);

        preProcessProperties();
    }

    /**
     * Обработка свойств области. Все свойства должны быть установлены.
     */
    private void preProcessProperties() {
        
        if (properties == null || properties.size() == 0) {
            return;
        }
        processStartIndent();
        processEndIndent();
    }

    /**
     * Обработка атрибута start-indent.
     */
    private void processStartIndent() {
        
        // Атрибут start-indent превращается в соответствующий отступ слева
        double startIndent = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.START_INDENT), HelpFuncs.ZERO);
        if (startIndent <= HelpFuncs.ZERO) {
            return;
        }
        double marginLeft = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.MARGIN_LEFT), HelpFuncs.ZERO);
        double paddingLeft = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.PADDING_LEFT), HelpFuncs.ZERO);
        // Если уже заданы и margin и padding, то игнорируем start-indent
        if (marginLeft > HelpFuncs.ZERO && paddingLeft > HelpFuncs.ZERO) {
            return;
        }
        double value = startIndent - marginLeft - paddingLeft;
        if (value <= HelpFuncs.ZERO) {
            return;
        }
        if (marginLeft == HelpFuncs.ZERO) {
            setPropertyValue(FoPropertyType.MARGIN_LEFT, value);
        } else {
            setPropertyValue(FoPropertyType.PADDING_LEFT, value);
        }
    }

    /**
     * Обработка атрибута end-indent.
     */
    private void processEndIndent() {
        
        // Атрибут end-indent превращается в соответствующий отступ справа
        double endIndent = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.END_INDENT), HelpFuncs.ZERO);
        if (endIndent <= HelpFuncs.ZERO) {
            return;
        }
        double marginRight = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.MARGIN_RIGHT), HelpFuncs.ZERO);
        double paddingRight = HelpFuncs.nvl2(getPropertyValue(FoPropertyType.PADDING_RIGHT), HelpFuncs.ZERO);
        // Если уже заданы и margin и padding, то игнорируем end-indent
        if (marginRight > HelpFuncs.ZERO && paddingRight > HelpFuncs.ZERO) {
            return;
        }
        double value = endIndent - marginRight - paddingRight;
        if (value <= HelpFuncs.ZERO) {
            return;
        }
        if (marginRight == HelpFuncs.ZERO) {
            setPropertyValue(FoPropertyType.MARGIN_RIGHT, value);
        } else {
            setPropertyValue(FoPropertyType.PADDING_RIGHT, value);
        }
    }

    /**
     * Инициализирующий конструктор.
     * @param areaType   - Тип создаваемой области
     * @param parentArea - Родительская область
     * @param properties - Словарь свойств, с которого надо сделать копию для использования
     */
    protected GenericArea(AreaType areaType, GenericArea parentArea, EnumMap<FoPropertyType, Object> properties) {
        
        type = areaType;
        this.parentArea = parentArea;

        if (properties != null && properties.size() > 0) {
            this.properties = new EnumMap<>(properties);
        }
    }

    /**
     * Метод для получения значения свойства области.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства, если свойство задано или null
     */
    public Object getPropertyValue(FoPropertyType propertyType) {

        Object result = null;
        if (properties != null) {
            result = properties.get(propertyType);
        }
        
        return result;
    }

    /**
     * Метод для получения унаследованного значения свойства, рекурсивно вызывая родителей, пока не найдем значение
     * или не дойдем до вершины.
     * @param propertyType - Тип свойства
     * @param deep         - Счетчик глубины рекурсии. При 1м вызове = 0
     * @return Object   - возвращает значение свойства, если свойство задано у объекта
     *                      или кого-нибудь из родителей по цепочке или null
     */
    protected Object getInheritedPropertyValue(FoPropertyType propertyType, int deep) {
        
        Object returnValue = getPropertyValue(propertyType);
        return null != returnValue ? returnValue :
            (getParentArea() != null ? getParentArea().getInheritedPropertyValue(propertyType, deep + 1) : null);
    }

    /**
     * Метод для получения унаследованного значения свойства, рекурсивно вызывая родителей, пока не найдем значение 
     * или не дойдем до вершины.
     * @param propertyType - тип св-ва
     * @return Object   - возвращает значение свойства, если свойство задано у объекта 
     *                      или кого-нибудь из родителей по цепочке или null
     */
    public Object getInheritablePropertyValue(FoPropertyType propertyType) {
        return getInheritedPropertyValue(propertyType, 0);
    }

    /**
     * Метод для установки/удаления значения свойства области.
     * @param propertyType - Тип свойства
     * @param value        - Если значение = null, то происходит удаление свойства, иначе присваивание нового значения
     */
    public void setPropertyValue(FoPropertyType propertyType, Object value) {
        
        if (null == value) {
            if (properties != null) {
                properties.remove(propertyType);
            }
        } else {
            if (properties == null) {
                properties = new EnumMap<>(FoPropertyType.class);
            }
            properties.put(propertyType, value);
        }
    }

    /**
     * Вызов обработки свойств после добавления всех детей и вызова для них метода postProcessProperties.
     * То есть подымаемся снизу вверх
     */
    public void postProcessProperties() {
    }
}
