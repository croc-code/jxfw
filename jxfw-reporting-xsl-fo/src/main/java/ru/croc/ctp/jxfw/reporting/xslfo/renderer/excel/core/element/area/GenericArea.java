package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area;

import com.aspose.cells.TextAlignmentType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.AreaType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.AreaProgressionDirection;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.ColumnWidth;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.MarginProps;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.border.FoBorder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.font.FoFont;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.attribute.AttributeParser;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Абстрактный базовый класс области.
 * Инкапсулированы общее поведение и интерфейс областей.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public abstract class GenericArea implements IArea {

    /**
     * Ссылка на родительскую область.
     */
    protected IArea parentArea;

    /**
     * Список дочерних областей.
     */
    private List<IArea> childrenList;

    /**
     * Признак - область разбита на несколько областей - при построении разметки контента путем разбивки на колонки.
     */
    protected boolean splitted;

    /**
     * Признак - область имеет атрибут span - распространение области на всю ширину страницы при разбивке на колонки.
     */
    protected boolean isColumnLayoutSpan;

    /**
     * Контейнер свойств области.
     */
    protected EnumMap<FoPropertyType, Object> properties;

    /**
     * Прямоугольник - геометрическое расположение и размеры прямоугольника области.
     */
    private AreaRectangle borderRectangle;

    /**
     * Область ячеек Excel, занимаемая прямоугольником border области.
     */
    private Range borderRange;

    /**
     * Тип направления расположения области (ее дочерних областей)- progression direction.
     * По умолчанию тип направления - block
     */
    protected AreaProgressionDirection progressionDirection = AreaProgressionDirection.BLOCK;

    /**
     * Свойства - margins &amp; paddings.
     */
    private MarginProps margins;

    /**
     * Свойство - cсылка на родительскую область.
     * @return IArea    - возвращает cсылку на родительскую область
     */
    public IArea getParentArea() {
        return parentArea;
    }

    /**
     * Свойство - cсылка на родительскую область.
     * @param parentArea - cсылка на родительскую область
     */
    public void setParentArea(IArea parentArea) {
        this.parentArea = parentArea;
    }

    /**
     * Свойство - список дочерних областей.
     * @return List - возвращает список дочерних областей
     */
    public List<IArea> getChildrenList() {
        if (childrenList == null) {
            childrenList = new ArrayList<>();
        }
        return childrenList;
    }

    /**
     * Свойство - список дочерних областей.
     * @param childrenList - список дочерних областей
     */
    public void setChildrenList(List<IArea> childrenList) {
        this.childrenList = childrenList;
    }

    /**
     * Свойство - список атрибутов(свойств) области.
     * @return EnumMap  - возвращает список атрибутов(свойств) области
     */
    public EnumMap<FoPropertyType, Object> getProperties() {
        return properties;
    }

    /**
     * Свойство - область имеет дочерние области.
     * @return boolean - возвращает true, если область имеет дочерние области и false в противном случае
     */
    public boolean isHasChildren() {
        return (childrenList != null && childrenList.size() > 0);
    }

    /**
     * Свойство - область разбита на несколько областей - при построении разметки контента путем разбивки на колонки.
     * @return boolean - возвращает true, если область разбита на несколько областей и false в противном случае
     */
    public boolean isSplitted() {
        return splitted;
    }

    /**
     * Свойство - область разбита на несколько областей - при построении разметки контента путем разбивки на колонки.
     * @param isSplitted - область разбита на несколько областей?
     */
    public void setSplitted(boolean isSplitted) {
        this.splitted = isSplitted;
    }

    /**
     * Свойство - область имеет атрибут span - распространение области на всю ширину страницы при разбивке на колонки.
     * @return boolean - возвращает true, если область имеет атрибут span и false в противном случае
     */
    public boolean isColumnLayoutSpan() {
        return isColumnLayoutSpan;
    }

    /**
     * Свойство - область имеет атрибут span - распространение области на всю ширину страницы при разбивке на колонки.
     * @param isColumnLayoutSpan - область имеет атрибут span?
     */
    public void setColumnLayoutSpan(boolean isColumnLayoutSpan) {
        this.isColumnLayoutSpan = isColumnLayoutSpan;
    }

    /**
     * Свойство - область должна быть отрендерена - выведена в Excel.
     * @return boolean - возвращает true, если область должна быть отрендерена и false в противном случае
     */
    public boolean isNeedRendering() {
        return
                ///////////////////////////////////////////////////////////
                // 01.08.2006 DKL
                // При рендеринге XSL-FO в Excel, появляются не предусмотренные смердженые ячейки по строкам и столбцам:
                // ...
                // Согласно XSL-FO не должно быть мерджа столбцов T:U.

                // Следующая строка ( /* this.getAreaType() != AreaType.TABLE_ROW && */ ) была внесена 03.02.05 и
                // почему-то закомментирована 07.02.05. Я подозреваю, что это было сделано по ошибке, т.к. не была
                // удалена и не был написан комментарий. Тесты не выявили проблем из-за раскомментирования.

                getAreaType() != AreaType.TABLE_ROW && !isSplitted();

        //
        ////////////////////////////////////////////////////////////
    }

    /**
     * Свойство - прямоугольник - геометрическое расположение и размеры прямоугольника области.
     * @return AreaRectangle - возвращает геометрическое расположение и размеры прямоугольника области
     */
    public AreaRectangle getBorderRectangle() {
        if (borderRectangle == null) {
            borderRectangle = new AreaRectangle();
        }

        return borderRectangle;
    }

    /**
     * Свойство - прямоугольник - геометрическое расположение и размеры прямоугольника области.
     * @param borderRectangle - прямоугольник - геометрическое расположение и размеры прямоугольника области.
     */
    public void setBorderRectangle(AreaRectangle borderRectangle) {
        this.borderRectangle = borderRectangle;
    }

    /**
     * Свойство - область ячеек Excel, занимаемая прямоугольником области.
     * @return Range    - возвращает область ячеек Excel, занимаемую прямоугольником области
     */
    public Range getBorderRange() {
        if (borderRange == null) {
            borderRange = new Range();
        }

        return borderRange;
    }

    /**
     * Свойство - тип направления расположения области (ее дочерних областей)- progress direction.
     * @return AreaProgressionDirection - возвращает тип направления расположения области (ее дочерних областей)
     */
    public AreaProgressionDirection getProgressionDirection() {
        return progressionDirection;
    }

    /**
     * Свойство - тип направления расположения области (ее дочерних областей)- progress direction.
     * @param progressionDirection - тип направления расположения области
     */
    public void setProgressionDirection(AreaProgressionDirection progressionDirection) {
        this.progressionDirection = progressionDirection;
    }

    /**
     * Инициализирующий конструктор.
     * @param parentArea    - Родительская область
     * @param attributeList - Словарь свойств элемента XSL-FO соответствующего области
     */
    protected GenericArea(IArea parentArea, Map<String, String> attributeList) {

        this.parentArea = parentArea;

        // Если не задан список атрибутов - выходим
        if (attributeList == null || attributeList.size() == 0) {
            return;
        }

        // Разбираем атрибуты
        AttributeParser.parseAttributes(this, attributeList);

        // Пост-обработка установленных свойств
        preProcessProperties();
    }

    /**
     * Абстрактный метод - получение типа области AreaType.
     * @return AreaType - возвращает тип области AreaType
     */
    public abstract AreaType getAreaType();

    //region Чтение и установка свойств области

    /**
     * Возвращает отступы. Если их ещё нет, то создаёт новый объект.
     * @return MarginProps возвращает отступы
     */
    public MarginProps getOrCreateMargins() {

        if (null == margins) {
            margins = new MarginProps();
        }

        return margins;
    }

    public MarginProps getMargins() {
        return margins;
    }

    public void setMargins(MarginProps margins) {
        this.margins = margins;
    }

    /**
     * Метод получения значения свойства.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства, если свойство задано ИЛИ null
     */
    public Object getProperty(FoPropertyType propertyType) {

        return properties != null && properties.containsKey(propertyType)
                ? properties.get(propertyType) : null;
    }

    /**
     * Метод для получения значения свойства области.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства если свойство задано ИЛИ значение по умолчанию
     */
    public Object getPropertyValue(FoPropertyType propertyType) {

        // Если значение задано, то возвращаем его, иначе значение по умолчанию
        Object property = getProperty(propertyType);
        return null != property ? property : getDefaultPropertyValue(propertyType);
    }

    /**
     * Метод для получения значения свойства с учетом наследования значения свойства от родительской области.
     * @param propertyType - Тип свойства
     * @param deep         - Счетчик глубины рекурсии. При 1м вызове = 0
     * @return Object   - возвращает значение свойства, если свойство задано у объекта или кого-нибудь из родителей
     *                      по цепочке ИЛИ null
     */
    @Override
    public Object getInheritedProperty(FoPropertyType propertyType, int deep) {

        // Ищем свойство у себя, если не нашли, то ищем у родителей, поднимаясь вверх.
        Object property = getProperty(propertyType);
        return null != property ? property :
                (getParentArea() != null ? getParentArea().getInheritedProperty(propertyType, deep + 1) : null);
    }

    /**
     * Метод для получения значения свойства с учетом наследования значения свойства от родительской области.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства, если свойство задано у себя или родителей,
     *                      ИЛИ значение по умолчанию
     */
    public Object getInheritedPropertyValue(FoPropertyType propertyType) {

        Object property = getInheritedProperty(propertyType, 0);
        return null != property ? property : getDefaultPropertyValue(propertyType);
    }

    /**
     * Метод установки значения свойства области.
     * @param propertyType  - Тип свойства
     * @param propertyValue - Значение свойства
     */
    public void setPropertyValue(FoPropertyType propertyType, Object propertyValue) {

        // Если значение свойства указано
        if (null != propertyValue) {
            if (properties == null) {
                properties = new EnumMap<>(FoPropertyType.class);
            }
            properties.put(propertyType, propertyValue);
        } else if (properties != null) {
            // Значение = null эквивалентно удалению из словаря
            properties.remove(propertyType);
        }
    }

    /**
     * Получение значение свойства-измерения или значение по умолчанию, или Dimension.UNDEFINED, если нет значения
     * по умолчанию.
     * @param propertyType - Тип свойства
     * @return Dimension    - возвращает значение свойства-измерения или значение по умолчанию,
     *                          или Dimension.UNDEFINED, если нет значения по умолчанию
     */
    public Dimension getDimensionValue(FoPropertyType propertyType) {

        Object property = getPropertyValue(propertyType);
        return null != property ? (Dimension)property : Dimension.UNDEFINED;
    }

    /**
     * Получение значения свойства по умолчанию.
     * @param propertyType - Тип свойства
     * @return Object   - возвращает значение свойства
     */
    protected Object getDefaultPropertyValue(FoPropertyType propertyType) {

        // В списке значений по умолчанию не должно быть шрифта! Он обрабатывается более хитроумно
        switch (propertyType) {
            case FONT:
                return FoFont.getDefaultFont();

            case TEXT_ALIGN:
                return TextAlignmentType.LEFT;

            case VERTICAL_ALIGN:
                return TextAlignmentType.CENTER;

            case COLUMN_WIDTH:
                return ColumnWidth.getInstance(GlobalData.DEFAULT_COLUMN_WIDTH);

            case NUMBER_COLUMNS_SPANNED:
                return GlobalData.DEFAULT_COLUMNS_SPAN_VALUE;

            case NUMBER_ROWS_SPANNED:
                return GlobalData.DEFAULT_ROWS_SPAN_VALUE;

            case COLUMN_COUNT:
                return GlobalData.DEFAULT_COLUMN_COUNT;

            case COLUMN_GAP:
                return Dimension.getInstance(GlobalData.DEFAULT_COLUMN_GAP);

            case WRAP_OPTION:
                return GlobalData.DEFAULT_WRAP_OPTION;

            case REFERENCE_ORIENTATION:
                return GlobalData.DEFAULT_REFERENCE_ORIENTATION;

            case BORDER_TOP:
            case BORDER_LEFT:
            case BORDER_RIGHT:
            case BORDER_BOTTOM:
                // Стремный момент в том, что какой-нить криворукий программер может изменить значение этого объекта.
                // Отсюда мораль: парситься граница должна только в AttributeParser
                return FoBorder.UNDEFINED;

            case PADDING_TOP:
            case PADDING_LEFT:
            case PADDING_RIGHT:
            case PADDING_BOTTOM:
            case MARGIN_TOP:
            case MARGIN_LEFT:
            case MARGIN_RIGHT:
            case MARGIN_BOTTOM:
            case EXTENT:
            case TEXT_INDENT:
                return Dimension.ZERO;

            case PAGE_HEIGHT:
                return GlobalData.DEFAULT_PAGE_HEIGHT;

            case PAGE_WIDTH:
                return GlobalData.DEFAULT_PAGE_WIDTH;

            default:
                // В импортруемом коде ничего не было
        }

        return null;
    }

    //endregion

    //region Обработка свойств области

    /**
     * Обработка свойств области. Все свойства должны быть установлены.
     * Общая реализация.
     */
    private void preProcessProperties() {

        if (properties == null || properties.size() == 0) {
            return;
        }
        Integer startIndent = null;
        Integer endIndent = null;

        // Сперва попробуем разрешить ширину и высоту
        processWidth();
        processHeight();

        // Проходим по всему списку свойств
        for (Map.Entry<FoPropertyType, Object> entry : properties.entrySet()) {
            Object property = entry.getValue();
            Integer resolvedValue;
            switch (entry.getKey()) {
                case MIN_WIDTH:
                    processMinWidth((Dimension)property);
                    break;
                case MAX_WIDTH:
                    processMaxWidth((Dimension)property);
                    break;
                case MIN_HEIGHT:
                    processMinHeight((Dimension)property);
                    break;
                case MAX_HEIGHT:
                    processMaxHeight((Dimension)property);
                    break;

                // Все процентные значения рассчитываются почему-то (по стандарту!) по ШИРИНЕ
                case PADDING_TOP:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setPaddingTop(null != resolvedValue ? resolvedValue : 0);
                    break;
                case PADDING_BOTTOM:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setPaddingBottom(null != resolvedValue ? resolvedValue : 0);
                    break;
                case PADDING_LEFT:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setPaddingLeft(null != resolvedValue ? resolvedValue : 0);
                    break;
                case PADDING_RIGHT:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setPaddingRight(null != resolvedValue ? resolvedValue : 0);
                    break;
                case MARGIN_TOP:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setMarginTop(null != resolvedValue ? resolvedValue : 0);
                    break;
                case MARGIN_BOTTOM:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setMarginBottom(null != resolvedValue ? resolvedValue : 0);
                    break;
                case MARGIN_LEFT:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setMarginLeft(null != resolvedValue ? resolvedValue : 0);
                    break;
                case MARGIN_RIGHT:
                    resolvedValue = resolveDimensionPercentValue((Dimension)property, true, true);
                    getOrCreateMargins().setMarginRight(null != resolvedValue ? resolvedValue : 0);
                    break;
                case START_INDENT:
                    startIndent = resolveDimensionPercentValue((Dimension)property, true, true);
                    break;
                case END_INDENT:
                    endIndent = resolveDimensionPercentValue((Dimension)property, true, true);
                    break;

                case SPAN:
                    if (parentArea.getAreaType() == AreaType.FLOW) {
                        if (GlobalData.SPAN_ALL.equals(property)) {
                            isColumnLayoutSpan = true;
                        }
                    }
                    break;

                default:
                    // В импортруемом коде ничего не было
            }
        }

        // Обрабатываем отступы слева и справа
        if (null != startIndent && startIndent > 0) {
            processStartIndent(startIndent);
        }
        if (null != endIndent && endIndent > 0) {
            processEndIndent(endIndent);
        }
    }

    /**
     * Расчет процентных измерений по ширине/высоте блока/родительского блока.
     * @param value            - Измерение
     * @param isWidth          - Признак расчета по ширине = true или по высоте = false
     * @param isCalcFromParent - Признак расчета по родителю = true или по себе = false
     * @return Integer  - возвращает абсолютное значение или null, если расчет не удался
     */
    protected Integer resolveDimensionPercentValue(
            Dimension value, boolean isWidth, boolean isCalcFromParent) {

        if (!value.isDefined()) {
            return null;
        }
        if (!value.isPercentage()) {
            return Float.valueOf(value.getValue()).intValue();
        }
        AreaRectangle rectangle = !isCalcFromParent
                ? getBorderRectangle()
                : (getParentArea() != null ? getParentArea().getBorderRectangle() : null);

        if (rectangle == null
                || (isWidth && !rectangle.isWidthDefined())
                || (!isWidth && !rectangle.isHeightDefined())) {
            return null;
        }

        return Double.valueOf(
                Math.ceil(value.getValue() * (isWidth ? rectangle.getWidth() : rectangle.getHeight()) / 100F))
                .intValue();
    }

    /**
     * Обработка свойства ширины области.
     */
    private void processWidth() {

        Integer width = resolveDimensionPercentValue(
                getDimensionValue(FoPropertyType.WIDTH), true, true);
        if (null != width) {
            getBorderRectangle().setWidth(width);
        }
    }

    /**
     * Обработка свойства высоты области.
     */
    private void processHeight() {

        Integer height = resolveDimensionPercentValue(getDimensionValue(FoPropertyType.HEIGHT), false, true);
        if (null != height) {
            getBorderRectangle().setHeight(height);
        }
    }

    /**
     * Обработка свойства минимальной ширины.
     * @param dimension - свойство минимальной ширины
     */
    private void processMinWidth(Dimension dimension) {

        Integer minWidth = resolveDimensionPercentValue(dimension, true, true);

        if (null != minWidth && minWidth <= getBorderRectangle().getMaxWidth()) {
            getBorderRectangle().setMinWidth(minWidth);
            if (getBorderRectangle().isWidthDefined() && getBorderRectangle().getWidth() < minWidth) {
                getBorderRectangle().setWidth(minWidth);
            }
        }
    }

    /**
     * Обработка свойства максимальной ширины.
     * @param dimension - свойство максимальной ширины
     */
    private void processMaxWidth(Dimension dimension) {

        Integer maxWidth = resolveDimensionPercentValue(dimension, true, true);
        if (null != maxWidth && maxWidth > 0 && maxWidth >= getBorderRectangle().getMinWidth()) {
            getBorderRectangle().setMaxWidth(maxWidth);
            if (getBorderRectangle().isWidthDefined() && getBorderRectangle().getWidth() > maxWidth) {
                getBorderRectangle().setWidth(maxWidth);
            }
        }
    }

    /**
     * Обработка свойства минимальной высоты.
     * @param dimension - свойство минимальной высоты
     */
    private void processMinHeight(Dimension dimension) {

        Integer minHeight = resolveDimensionPercentValue(dimension, false, true);
        if (null != minHeight && minHeight <= getBorderRectangle().getMaxHeight()) {
            getBorderRectangle().setMinHeight(minHeight);
            if (getBorderRectangle().isHeightDefined() && getBorderRectangle().getHeight() < minHeight) {
                getBorderRectangle().setHeight(minHeight);
            }
        }
    }

    /**
     * Обработка свойства максимальной высоты.
     * @param dimension - свойство максимальной высоты
     */
    private void processMaxHeight(Dimension dimension) {

        Integer maxHeight = resolveDimensionPercentValue(dimension, false, true);
        if (null != maxHeight && maxHeight > 0 && maxHeight >= getBorderRectangle().getMinHeight()) {
            getBorderRectangle().setMaxHeight(maxHeight);
            if (getBorderRectangle().isHeightDefined() && getBorderRectangle().getHeight() > maxHeight) {
                getBorderRectangle().setHeight(maxHeight);
            }
        }
    }

    /**
     * Обработка атрибута start-indent.
     * @param startIndent - Значение размерности атрибута
     */
    private void processStartIndent(int startIndent) {

        // Если уже заданы и margin и padding, то игнорируем start-indent
        if (margins.getMarginLeft() > 0 && margins.getPaddingLeft() > 0) {
            return;
        }
        int value = startIndent - margins.getMarginLeft() - margins.getPaddingLeft();
        if (value <= 0) {
            return;
        }
        if (margins.getMarginLeft() == 0) {
            margins.setMarginLeft(value);
        } else {
            margins.setPaddingLeft(value);
        }
    }

    /**
     * Обработка атрибута end-indent.
     * @param endIndent - атрибут end-indent
     */
    private void processEndIndent(int endIndent) {

        // Если уже заданы и margin и padding, то игнорируем start-indent
        if (margins.getMarginRight() > 0 && margins.getPaddingRight() > 0) {
            return;
        }
        int value = endIndent - margins.getMarginRight() - margins.getPaddingRight();
        if (value <= 0) {
            return;
        }
        if (margins.getMarginRight() == 0) {
            margins.setMarginRight(value);
        } else {
            margins.setPaddingRight(value);
        }
    }

    /**
     * Вызов обработки свойств после добавления всех детей и вызова для них метода postProcessProperties.
     * То есть подымаемся снизу вверх
     */
    @Override
    public void postProcessProperties() {

        AreaType areaType = getAreaType();
        switch (areaType) {
            case TABLE_COLUMN:
            case TABLE_ROW:
            case TABLE_HEADER:
            case TABLE_BODY:
            case TABLE_FOOTER:
                // Для этих элементов не поддерживаем ни margin, ни padding
                margins = new MarginProps();
                break;
            case TABLE_CELL:
                // Для ячеек не поддерживаем margin
                if (null != margins) {
                    margins.setMarginTop(0);
                    margins.setMarginBottom(0);
                    margins.setMarginLeft(0);
                    margins.setMarginRight(0);
                }
                break;
            case TABLE:
                // Для таблиц не поддерживаем padding
                if (null != margins) {
                    margins.setPaddingTop(0);
                    margins.setPaddingBottom(0);
                    margins.setPaddingLeft(0);
                    margins.setPaddingRight(0);
                }
                break;

            case BASIC_LINK:
            case BLOCK:
            case CHARACTER:
            case EXTERNAL_GRAPHIC:
            case INLINE:
            case INSTREAM_FOREIGN_OBJECT:
            case LIST_BLOCK:
            case LIST_ITEM:
            case LIST_ITEM_BODY:
            case LIST_ITEM_LABEL:
            case PAGE_NUMBER:
            case PAGE_NUMBER_CITATION:
                moveChildMarginsToParentPaddings();
                if (getProgressionDirection() != AreaProgressionDirection.INLINE && null != margins) {
                    adjustPaddings();
                }
                break;

            default:
                // В импортруемом коде ничего не было
        }
    }

    /**
     * Перенос общих значений margin у детей к родителю в padding.
     */
    private void moveChildMarginsToParentPaddings() {

        switch (getProgressionDirection()) {
            case BLOCK:
                // Переносим общее значение внешних отступов у детей (для BLOCK, очевидно, это левые и правые отступы)
                // к родителю
                int left = Integer.MAX_VALUE;
                int right = Integer.MAX_VALUE;
                if (isHasChildren()) {
                    for (IArea childArea : getChildrenList()) {
                        MarginProps childrenMargins = ((GenericArea)childArea).margins;
                        if (null != childrenMargins) {
                            left = Math.min(left, childrenMargins.getMarginLeft());
                            right = Math.min(right, childrenMargins.getMarginRight());
                        }
                    }
                    if ((Integer.MAX_VALUE != left || Integer.MAX_VALUE != right) && (left > 0 || right > 0)) {
                        for (IArea childArea : getChildrenList()) {
                            // Вычитаем у детей
                            MarginProps childrenMargins = ((GenericArea)childArea).margins;
                            if (null != childrenMargins) {
                                childrenMargins.setMarginLeft(childrenMargins.getMarginLeft() - left);
                                childrenMargins.setMarginRight(childrenMargins.getMarginRight() - right);
                            }
                        }
                        // Добавляем себе
                        margins.setPaddingLeft(margins.getPaddingLeft() + left);
                        margins.setPaddingRight(margins.getPaddingRight() + right);
                    }
                }
                break;

            case ROW:
                // Переносим общее значение внешних отступов у детей (для ROW, очевидно, это верхние и нижние отступы)
                // к родителю
                int top = Integer.MAX_VALUE;
                int bottom = Integer.MAX_VALUE;
                if (isHasChildren()) {
                    for (IArea childArea : getChildrenList()) {
                        top = Math.min(top, ((GenericArea)childArea).margins.getMarginTop());
                        bottom = Math.min(bottom, ((GenericArea)childArea).margins.getMarginBottom());
                    }
                    if (top > 0 || bottom > 0) {
                        for (IArea childArea : getChildrenList()) {
                            // Вычитаем у детей
                            MarginProps childrenMargins = ((GenericArea)childArea).margins;
                            childrenMargins.setMarginTop(childrenMargins.getMarginTop() - top);
                            childrenMargins.setMarginBottom(childrenMargins.getMarginBottom() - bottom);
                        }
                        // Добавляем себе
                        margins.setPaddingTop(margins.getPaddingTop() + top);
                        margins.setPaddingBottom(margins.getPaddingBottom() + bottom);
                    }
                }
                break;

            case INLINE:
                // Очищаем все возможно установленные отступы, ибо для INLINE элементов они не обрабатываются!
                margins = new MarginProps();
                break;

            default:
                // В импортруемом коде ничего не было
        }
    }

    /**
     * Переносим padding в соответствующий margin, если граница не мешает.
     */
    private void adjustPaddings() {

        // Если нет соответствующей границы, то перекладываем внутренний отступ во внешний
        if (margins.getPaddingTop() > 0) {
            FoBorder borderTop = (FoBorder) getPropertyValue(FoPropertyType.BORDER_TOP);
            if (!borderTop.isDefined()) {
                margins.setMarginTop(margins.getMarginTop() + margins.getPaddingTop());
                margins.setPaddingTop(0);
            }
        }
        if (margins.getPaddingBottom() > 0) {
            FoBorder borderBottom = (FoBorder) getPropertyValue(FoPropertyType.BORDER_BOTTOM);
            if (!borderBottom.isDefined()) {
                margins.setMarginBottom(margins.getMarginBottom() + margins.getPaddingBottom());
                margins.setPaddingBottom(0);
            }
        }
        if (margins.getPaddingLeft() > 0) {
            FoBorder borderLeft = (FoBorder) getPropertyValue(FoPropertyType.BORDER_LEFT);
            if (!borderLeft.isDefined()) {
                margins.setMarginLeft(margins.getMarginLeft() + margins.getPaddingLeft());
                margins.setPaddingLeft(0);
            }
        }
        if (margins.getPaddingRight() > 0) {
            FoBorder borderRight = (FoBorder) getPropertyValue(FoPropertyType.BORDER_RIGHT);
            if (!borderRight.isDefined()) {
                margins.setMarginRight(margins.getMarginRight() + margins.getPaddingRight());
                margins.setPaddingRight(0);
            }
        }
    }

    //endregion
}
