package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND_COLOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND_IMAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_COLOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_STYLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.DISPLAY_ALIGN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.FONT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REFERENCE_ORIENTATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.TEXT_ALIGN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.UNDEFINED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.MINUS_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.SPACE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.TRANSPARENT_COLOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.WHITE_SPACES;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_AFTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_BEFORE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_CENTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_END;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_JUSTIFY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_START;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.ALIGNMENT_TYPE_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.VERTICAL_ALIGN_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.VERTICAL_ALIGN_MIDDLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.VERTICAL_ALIGN_TEXT_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.VERTICAL_ALIGN_TEXT_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.VERTICAL_ALIGN_TOP;

import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.ParagraphAlignment;
import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.RenderType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

/**
 * Класс, инкапсулирующий логику обработки атрибутов.
 * Created by vsavenkov on 27.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class AttributeParser {

    /**
     * Список аттрибутов.
     */
    private final Map<String, String> attributeList;
    /**
     * Св-ва.
     */
    private final EnumMap<FoPropertyType, Object> properties;
    /**
     * Родительский шрифт.
     */
    private final FoFont parentFont;
    /**
     * Шрифт.
     */
    private FoFont font;

    /**
     * Получение шрифта области. Если его еще нет, то берем копию родительского. Если и его нет, то шрифт по умолчанию.
     * @return FoFont   - возвращает шрифт области
     */
    private FoFont getFont() {
        return null != font ? font : (parentFont != null ? parentFont.cloneMe() : FoFont.getDefaultFont());
    }

    /**
     * Приватный конструктор.
     * @param area          - Область
     * @param attributeList - Словарь свойств XSL-FO элемента
     */
    private AttributeParser(GenericArea area, Map<String, String> attributeList) {
        
        this.attributeList = attributeList;
        properties = new EnumMap<>(FoPropertyType.class);
        parentFont = getAreaFont(area.getParentArea());
    }

    /**
     * Разбор атрибутов и занесение их значений в свойства.
     * @param area          - Область
     * @param attributeList - Словарь свойств XSL-FO элемента
     * @return Словарь распознанных свойств XSL-FO
     */
    public static EnumMap<FoPropertyType, Object> parse(GenericArea area, Map<String, String> attributeList) {
        
        if (attributeList == null || attributeList.size() == 0) {
            return null;
        }
        AttributeParser attributeParser = new AttributeParser(area, attributeList);
        attributeParser.execute();
        return attributeParser.properties.size() != 0 ? attributeParser.properties : null;
    }

    /**
     * Разбор атрибутов и занесение их значений в свойства.
     */
    private void execute() {
        
        // Разбор первоочередных свойств, для которых важен порядок обработки
        processShorthandProperties();


        // Проходим по всему списку атрибутов
        for (Map.Entry entry : attributeList.entrySet()) {
            
            String attributeValue = (String)entry.getValue();   // Значение атрибута
            if (StringUtils.isBlank(attributeValue)) {
                continue;
            }
            FoPropertyType propertyType = FoPropertyType.parseValue((String)entry.getKey(), RenderType.WORD);
            if (propertyType == UNDEFINED) {
                continue;
            }
            attributeValue = StringUtils.strip(attributeValue, String.valueOf(WHITE_SPACES));
            if (!StringUtils.isBlank(attributeValue)) {
                processProperty(propertyType, attributeValue);
            }
        }

        // Если у нас в результате обработки установили шрифт в значение, отличное от родителя, то сохраняем его
        // в свойствах
        if (font != null && !font.isEquals(parentFont)) {
            properties.put(FONT, font);
        }
    }

    /**
     * Нахождения шрифта области (если у области не найдено, то ищем у родителя и т.д.).
     * @param area - Область
     * @return FoFont   - возвращает шрифт области
     */
    private static FoFont getAreaFont(GenericArea area) {
        
        while (area != null) {
            FoFont result = (FoFont)area.getPropertyValue(FONT);
            if (result != null) {
                return result;
            }
            area = area.getParentArea();
        }
        return null;
    }

    /**
     * Обработка значений свойств, которые устанавливают значения сразу по всем 4м сторонам. Например, border-*,
     * padding, margin.
     * @param attrName  - Наименование атрибута
     * @param attrValue - Значение атрибута
     */
    private void processFourSidesAttribute(String attrName, String attrValue) {
        String[] values = attrValue.split("[" + String.valueOf(WHITE_SPACES) + "]");
        int length = values.length;
        if (length == 0 || length > 4) {
            return;
        }

        String attrSuffix = StringUtils.EMPTY;
        int position = attrName.indexOf(MINUS_CHAR);
        if (position < 0) {
            attrName += MINUS_CHAR;
        } else {
            attrSuffix = attrName.substring(position);
            attrName = attrName.substring(0, position) + MINUS_CHAR;
        }
        processProperty(FoPropertyType.parseValue(
                attrName + ALIGNMENT_TYPE_TOP + attrSuffix, RenderType.WORD),
                        values[0]);
        processProperty(FoPropertyType.parseValue(
                attrName + ALIGNMENT_TYPE_RIGHT + attrSuffix, RenderType.WORD),
                        values[length == 1 ? 0 : 1]);
        processProperty(FoPropertyType.parseValue(
                attrName + ALIGNMENT_TYPE_BOTTOM + attrSuffix, RenderType.WORD),
                        values[length <= 2 ? 0 : 2]);
        processProperty(FoPropertyType.parseValue(
                attrName + ALIGNMENT_TYPE_LEFT + attrSuffix, RenderType.WORD),
                        values[length == 1 ? 0 : length == 4 ? 3 : 1]);
    }

    /**
     * Обработка атрибута border.
     * @param attrValue - Значение атрибута
     */
    private void processBorderAttribute(String attrValue) {
        FoBorder[] arBorders = new FoBorder[4];
        int length = 0;
        arBorders[length] = new FoBorder();
        for (String value : StringUtils.split(attrValue, String.valueOf(WHITE_SPACES))) {
            FoBorder border = arBorders[length];
            FoBorder temp = new FoBorder();
            if (temp.parseStyle(value)) {
                if (!border.isStyleDefined()) {
                    border.setStyle(temp.getStyle());
                    continue;
                }
            } else if (temp.parseWidth(value)) {
                if (!border.isWidthDefined()) {
                    border.setWidth(temp.getWidth());
                    continue;
                }
            } else if (temp.parseColor(value)) {
                if (!border.isColorDefined()) {
                    border.setColor(temp.getColor());
                    continue;
                }
            } else {
                length = 3;
            }

            if (++length > 3) {
                return; // Чересчур много границ. Не устанавливаем ничего!
            }
            arBorders[length] = temp;
        }
        // Если ничего так и не установили для отображения границы
        if (length == 0 && !arBorders[0].isDefined()) {
            return;
        }

        properties.put(BORDER_TOP, arBorders[0]);
        properties.put(BORDER_RIGHT, null != arBorders[1] ? arBorders[1] : arBorders[0].cloneMe());
        properties.put(BORDER_BOTTOM, null != arBorders[2] ? arBorders[2] : arBorders[0].cloneMe());
        properties.put(BORDER_LEFT, null != arBorders[3] ? arBorders[3] : arBorders[length == 0 ? 0 : 1].cloneMe());
    }

    /**
     * Обработка первоочередных свойств, для которых важен порядок обработки.
     */
    private void processShorthandProperties() {

        FoPropertyType[] arPropSeq = new FoPropertyType[] {
            BORDER, BORDER_WIDTH, BORDER_STYLE, BORDER_COLOR,
            BORDER_TOP, BORDER_RIGHT, BORDER_BOTTOM, BORDER_LEFT,
            PADDING, MARGIN, FONT, BACKGROUND };

        for (FoPropertyType propertyType : arPropSeq) {
            String attrValue = attributeList.get(propertyType.getPropertyName());
            if (StringUtils.isEmpty(attrValue)) {
                continue;
            }
            // Удаляем отрабатываемый атрибут, чтобы он не мешался при вызова processProperty
            attributeList.remove(propertyType.getPropertyName());

            switch (propertyType) {
                case BORDER:
                    processBorderAttribute(attrValue);
                    break;
                case BORDER_TOP:
                case BORDER_RIGHT:
                case BORDER_BOTTOM:
                case BORDER_LEFT:
                    handleBorder(propertyType, attrValue);
                    break;
                case BORDER_WIDTH:
                case BORDER_STYLE:
                case BORDER_COLOR:
                case PADDING:
                case MARGIN:
                    processFourSidesAttribute(propertyType.getPropertyName(), attrValue);
                    break;
                case FONT:
                    FoFont font = FoFont.getDefaultFont();
                    if (font.parse(attrValue)) {
                        this.font = font;
                    }
                    break;
                case BACKGROUND:
                    processBackgroundAttribute(attrValue);
                    break;

                default:
                    // В импортруемом коде ничего не было
            }
        }
    }

    /**
     * Обработка базовых элементов.
     * @param propertyType - Тип свойства
     * @param attrValue    - Значение атрибута - строка
     */
    private void processProperty(FoPropertyType propertyType, String attrValue) {

        FoFont font;
        switch (propertyType) {
            case FONT_SIZE:
                font = getFont();
                if (font.setFontSize(attrValue)) {
                    this.font = font;
                }
                break;
            case FONT_FAMILY:
                font = getFont();
                if (font.setFontFamily(attrValue)) {
                    this.font = font;
                }
                break;
            case FONT_WEIGHT:
                font = getFont();
                if (font.setFontWeight(attrValue)) {
                    this.font = font;
                }
                break;
            case FONT_STYLE:
                font = getFont();
                if (font.setFontStyle(attrValue)) {
                    this.font = font;
                }
                break;
            case TEXT_DECORATION:
                font = getFont();
                if (font.setTextDecoration(attrValue)) {
                    this.font = font;
                }
                break;
            case BASELINE_SHIFT:
                font = getFont();
                if (font.setBaselineShift(attrValue)) {
                    this.font = font;
                }
                break;

            case COLOR:
                Color color = FoColor.parse(attrValue);
                if (!FoColor.isEmpty(color)) {
                    properties.put(propertyType, color);
                }
                break;

            case BACKGROUND_COLOR:
                processBackgroundColorAttribute(attrValue);
                break;

            // Цвет границы
            case BORDER_BEFORE_COLOR:
            case BORDER_TOP_COLOR:
                handleBorderColor(BORDER_TOP, attrValue);
                break;
            case BORDER_AFTER_COLOR:
            case BORDER_BOTTOM_COLOR:
                handleBorderColor(BORDER_BOTTOM, attrValue);
                break;
            case BORDER_START_COLOR:
            case BORDER_LEFT_COLOR:
                handleBorderColor(BORDER_LEFT, attrValue);
                break;
            case BORDER_END_COLOR:
            case BORDER_RIGHT_COLOR:
                handleBorderColor(BORDER_RIGHT, attrValue);
                break;

            // Стиль границы
            case BORDER_BEFORE_STYLE:
            case BORDER_TOP_STYLE:
                handleBorderStyle(BORDER_TOP, attrValue);
                break;
            case BORDER_AFTER_STYLE:
            case BORDER_BOTTOM_STYLE:
                handleBorderStyle(BORDER_BOTTOM, attrValue);
                break;
            case BORDER_START_STYLE:
            case BORDER_LEFT_STYLE:
                handleBorderStyle(BORDER_LEFT, attrValue);
                break;
            case BORDER_END_STYLE:
            case BORDER_RIGHT_STYLE:
                handleBorderStyle(BORDER_RIGHT, attrValue);
                break;

            // Ширина границы
            case BORDER_BEFORE_WIDTH:
            case BORDER_TOP_WIDTH:
                handleBorderWidth(BORDER_TOP, attrValue);
                break;
            case BORDER_AFTER_WIDTH:
            case BORDER_BOTTOM_WIDTH:
                handleBorderWidth(BORDER_BOTTOM, attrValue);
                break;
            case BORDER_START_WIDTH:
            case BORDER_LEFT_WIDTH:
                handleBorderWidth(BORDER_LEFT, attrValue);
                break;
            case BORDER_END_WIDTH:
            case BORDER_RIGHT_WIDTH:
                handleBorderWidth(BORDER_RIGHT, attrValue);
                break;

            case WIDTH:
            case HEIGHT:
            case MIN_WIDTH:
            case MAX_WIDTH:
            case MIN_HEIGHT:
            case MAX_HEIGHT:
            case START_INDENT:
            case END_INDENT:
            case EXTENT:
            case PROVISIONAL_DISTANCE_BETWEEN_STARTS:
            case PROVISIONAL_LABEL_SEPARATION:
            case TEXT_INDENT:
            case COLUMN_GAP:
                handleDimensionProperty(propertyType, attrValue);
                break;

            // Пустые области - в рамках границы области
            case PADDING_BEFORE:
                handleDimensionProperty(PADDING_TOP, attrValue);
                break;
            case PADDING_AFTER:
                handleDimensionProperty(PADDING_BOTTOM, attrValue);
                break;
            case PADDING_START:
                handleDimensionProperty(PADDING_LEFT, attrValue);
                break;
            case PADDING_END:
                handleDimensionProperty(PADDING_RIGHT, attrValue);
                break;

            case PADDING_TOP:
            case PADDING_BOTTOM:
            case PADDING_LEFT:
            case PADDING_RIGHT:
            // Пустые области - вне рамок границы области
            case MARGIN_TOP:
            case MARGIN_BOTTOM:
            case MARGIN_LEFT:
            case MARGIN_RIGHT:
                handleDimensionProperty(propertyType, attrValue);
                break;

            case SPACE_BEFORE:
                handleDimensionProperty(MARGIN_TOP, attrValue);
                break;
            case SPACE_AFTER:
                handleDimensionProperty(MARGIN_BOTTOM, attrValue);
                break;
            case SPACE_START:
                handleDimensionProperty(MARGIN_LEFT, attrValue);
                break;
            case SPACE_END:
                handleDimensionProperty(MARGIN_RIGHT, attrValue);
                break;

            case REFERENCE_ORIENTATION:
                handleReferenceOrientation(attrValue);
                break;

            case TEXT_ALIGN:
                handleTextAlign(attrValue);
                break;

            case DISPLAY_ALIGN:
            case VERTICAL_ALIGN:
                handleVerticalAlign(attrValue);
                break;

            case NUMBER_COLUMNS_SPANNED:
            case NUMBER_ROWS_SPANNED:
                int spanned;
                try {
                    spanned = Integer.parseInt(attrValue);
                    if (spanned > 1) {
                        properties.put(propertyType, spanned);
                    }
                } catch (NumberFormatException e) {
                    // Так я заменил int.TryParse
                }
                break;
            default:
                properties.put(propertyType, attrValue);
                break;
        }
    }

    /**
     * Обработка свойства-измерения.
     * @param propertyType - Тип свойства
     * @param attrValue    - Значение атрибута - строка
     */
    private void handleDimensionProperty(FoPropertyType propertyType, String attrValue) {
        
        Double value = HelpFuncs.getSizeInPointsEx(attrValue);
        if (null != value) {
            properties.put(propertyType, value);
        }
    }

    /**
     * Обработка атрибута background-color.
     * @param attrValue - Значение атрибута
     * @return boolean возвращает true, если удалось распарсить цвет и false в противном случае
     */
    private boolean processBackgroundColorAttribute(String attrValue) {
        if (TRANSPARENT_COLOR.equals(attrValue)) {
            properties.remove(BACKGROUND_COLOR);
            return true;
        }
        Color color = FoColor.parse(attrValue);
        if (FoColor.isEmpty(color)) {
            return false;
        }
        properties.put(BACKGROUND_COLOR, color);
        return true;
    }

    /**
     * Обработка атрибута background.
     * @param attrValue - Значение атрибута
     */
    private void processBackgroundAttribute(String attrValue) {
        
        String[] arValues = StringUtils.split(attrValue, String.valueOf(WHITE_SPACES));
        for (int i = 0; i < arValues.length; i++) {
            if (processBackgroundColorAttribute(arValues[i])) {
                arValues[i] = null;
                break;
            }
        }
        String newAttrValue = StringUtils.strip(String.join(SPACE, arValues), String.valueOf(WHITE_SPACES));
        if (!StringUtils.isBlank(newAttrValue)) {
            processProperty(BACKGROUND_IMAGE, newAttrValue);
        }
    }

    /**
     * Обработка атрибута border-...
     * @param propertyType   - Тип свойства
     * @param attributeValue - Значение атрибута - строка
     */
    private void handleBorder(FoPropertyType propertyType, String attributeValue) {
        
        Object value = null;
        boolean isCreate = !properties.containsKey(propertyType);
        if (!isCreate) {
            value = properties.get(propertyType);
        }
        FoBorder border = null != value ? (FoBorder)value : new FoBorder();
        if (border.parse(attributeValue) && isCreate) {
            properties.put(propertyType, border);
        }
    }

    /**
     * Обработка атрибута border-...-color.
     * @param propertyType - Тип свойства
     * @param attrValue    - Значение атрибута - строка
     */
    private void handleBorderColor(FoPropertyType propertyType, String attrValue) {

        Object value = null;
        boolean isCreate = !properties.containsKey(propertyType);
        if (!isCreate) {
            value = properties.get(propertyType);
        }
        FoBorder foBorder = null != value ? (FoBorder)value : new FoBorder();
        if (foBorder.parseColor(attrValue) && isCreate) {
            properties.put(propertyType, foBorder);
        }
    }

    /**
     * Обработка атрибута border-...-width.
     * @param propertyType - Тип свойства
     * @param attrValue    - Значение атрибута
     */
    private void handleBorderWidth(FoPropertyType propertyType, String attrValue) {
        
        Object value = null;
        boolean isCreate = !properties.containsKey(propertyType);
        if (!isCreate) {
            value = properties.get(propertyType);
        }
        FoBorder foBorder = null != value ? (FoBorder)value : new FoBorder();

        if (foBorder.parseWidth(attrValue) && isCreate) {
            properties.put(propertyType, foBorder);
        }
    }

    /**
     * Обработка атрибута border-...-style.
     * @param propertyType - Тип свойства
     * @param attrValue    - Значение атрибута
     */
    private void handleBorderStyle(FoPropertyType propertyType, String attrValue) {
        
        Object value = null;
        boolean isCreate = !properties.containsKey(propertyType);
        if (!isCreate) {
            value = properties.get(propertyType);
        }
        FoBorder foBorder = null != value ? (FoBorder)value : new FoBorder();
        if (foBorder.parseStyle(attrValue) && isCreate) {
            properties.put(propertyType, foBorder);
        }
    }
    
    /**
     * Обработка атрибута reference-orientation.
     * @param attrValue - Значение атрибута
     */
    private void handleReferenceOrientation(String attrValue) {
        
        Double parsedReferenceOrientation = HelpFuncs.parseDoubleValue(attrValue);
        if (parsedReferenceOrientation != null) {
            // 360 градусов = полный круг. 180 = полкруга. Приводим к минимально возможному углу
            int rerefenceOrientation = ((int)parsedReferenceOrientation.doubleValue()) % 360;
            if (rerefenceOrientation > 180) {
                rerefenceOrientation -= 360;
            }
            if (rerefenceOrientation <= -180) {
                rerefenceOrientation += 360;
            }
            properties.put(REFERENCE_ORIENTATION, rerefenceOrientation);
        }
    }

    /**
     * Обработка атрибута text-align.
     * @param attrValue - Значение атрибута
     */
    private void handleTextAlign(String attrValue) {
        
        int enAlignment;
        switch (attrValue) {
            case ALIGNMENT_TYPE_START:
            case ALIGNMENT_TYPE_LEFT:
                enAlignment = ParagraphAlignment.LEFT;
                break;
            case ALIGNMENT_TYPE_END:
            case ALIGNMENT_TYPE_RIGHT:
                enAlignment = ParagraphAlignment.RIGHT;
                break;
            case ALIGNMENT_TYPE_JUSTIFY:
                enAlignment = ParagraphAlignment.JUSTIFY;
                break;
            case ALIGNMENT_TYPE_CENTER:
                enAlignment = ParagraphAlignment.CENTER;
                break;
            default:
                return;
        }
        properties.put(TEXT_ALIGN, enAlignment);
    }

    /**
     * Обработка атрибута display-align или vertical-align.
     * @param attrValue - Значение атрибута
     */
    private void handleVerticalAlign(String attrValue) {
        
        int enAlignment;
        switch (attrValue) {
            case ALIGNMENT_TYPE_BEFORE:
            case VERTICAL_ALIGN_TEXT_TOP:
            case VERTICAL_ALIGN_TOP:
                enAlignment = CellVerticalAlignment.TOP;
                break;
            case ALIGNMENT_TYPE_CENTER:
            case VERTICAL_ALIGN_MIDDLE:
                enAlignment = CellVerticalAlignment.CENTER;
                break;
            case ALIGNMENT_TYPE_AFTER:
            case VERTICAL_ALIGN_TEXT_BOTTOM:
            case VERTICAL_ALIGN_BOTTOM:
                enAlignment = CellVerticalAlignment.BOTTOM;
                break;
            default:
                return;
        }
        properties.put(DISPLAY_ALIGN, enAlignment);
    }
}
