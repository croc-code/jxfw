package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.processing.attribute;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BACKGROUND_IMAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BASELINE_SHIFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_COLOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_STYLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BORDER_WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BREAK_AFTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.BREAK_BEFORE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.EXTERNAL_GRAPHIC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.FLOW_NAME;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.FONT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.INITIAL_PAGE_NUMBER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MARGIN_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MASTER_NAME;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.MASTER_REFERENCE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.NUMBER_COLUMNS_SPANNED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.NUMBER_ROWS_SPANNED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PADDING_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PAGE_HEIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.PAGE_WIDTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REFERENCE_ORIENTATION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.REGION_NAME;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.SCALING;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.TEXT_ALIGN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.VERTICAL_ALIGN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.VISIBILITY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.VT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType.WRAP_OPTION;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_BOTTOM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.ALIGNMENT_TYPE_TOP;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.WHITE_SPACES;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BREAK_EVEN_PAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BREAK_ODD_PAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BREAK_PAGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_COLUMNS_SPAN_VALUE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_ROWS_SPAN_VALUE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.PAGE_BREAK_ALWAYS;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.PAGE_BREAK_AVOID;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.PAGE_BREAK_LEFT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.PAGE_BREAK_RIGHT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.SPACE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TRANSPARENT_COLOR;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.ParseBooleanResult;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.RenderType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.foimage.FoImage;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.ColumnWidth;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Dimension;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.border.FoBorder;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.font.FoFont;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text.FoAlignmentType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text.FoBaseLineShift;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.text.FoWrapOption;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.visibility.FoVisibility;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.vt.ValueType;

import java.awt.Color;
import java.util.Map;

/**
 * Класс, инкапсулирующий логику обработки атрибутов.
 * Created by vsavenkov on 21.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class AttributeParser {

    private static Logger logger = LoggerFactory.getLogger(AttributeParser.class);

    /**
     * Разбор атрибутов и занесение их значений в свойства.
     * @param area          - Область
     * @param attributeList - Словарь свойств XSL-FO элемента
     */
    public static void parseAttributes(IArea area, Map<String, String> attributeList) {

        logger.debug("Trying parse attributes: " + attributeList + " for area: " + area.getAreaType());

        if (attributeList != null && attributeList.size() > 0) {

            // Обрабатываем shorthand-свойства
            processShorthandProperties(area, attributeList);

            // Проходим по всему списку атрибутов
            for (String key : attributeList.keySet()) {
                String attributeValue = attributeList.get(key);   // Значение атрибута
                if (StringUtils.isBlank(attributeValue)) {
                    continue;
                }
                attributeValue = StringUtils.strip(attributeValue, String.valueOf(WHITE_SPACES));
                if (StringUtils.isBlank(attributeValue)) {
                    continue;
                }
                FoPropertyType propertyType = FoPropertyType.parseValue(key, RenderType.EXCEL);
                logger.debug("Trying parse property: " + propertyType + " value: " + attributeValue);
                processProperties(propertyType, area, attributeValue);
            }
        }
    }

    /**
     * Обработка значений свойств, которые устанавливают значения сразу по всем 4м сторонам. Например, border-*,
     * padding, margin
     * @param area           - Область
     * @param attributeName  - Наименование атрибута
     * @param attributeValue - Значение атрибута
     */
    private static void processFourSidesAttribute(IArea area, String attributeName, String attributeValue) {

        String[] arValues = attributeValue.split(String.valueOf(WHITE_SPACES));
        int length = arValues.length;
        if (length == 0 || length > 4) {
            return;
        }

        String attributeSuffix = null;
        int position = attributeName.indexOf(GlobalData.MINUS_CHAR);
        if (position < 0) {
            attributeName += GlobalData.MINUS_CHAR;
        } else {
            attributeSuffix = attributeName.substring(position);
            attributeName = attributeName.substring(0, position) + GlobalData.MINUS_CHAR;
        }
        processProperties(FoPropertyType.parseValue(
                attributeName + ALIGNMENT_TYPE_TOP + attributeSuffix, RenderType.EXCEL),
                area, arValues[0]);
        processProperties(FoPropertyType.parseValue(
                attributeName + ALIGNMENT_TYPE_RIGHT + attributeSuffix, RenderType.EXCEL),
                area, arValues[length == 1 ? 0 : 1]);
        processProperties(FoPropertyType.parseValue(
                attributeName + ALIGNMENT_TYPE_BOTTOM + attributeSuffix, RenderType.EXCEL),
                area, arValues[length <= 2 ? 0 : 2]);
        processProperties(FoPropertyType.parseValue(
                attributeName + ALIGNMENT_TYPE_LEFT + attributeSuffix, RenderType.EXCEL),
                area, arValues[length == 1 ? 0 : length == 4 ? 3 : 1]);
    }

    /**
     * Обработка атрибута border.
     * @param area   - Область
     * @param parsed - Значение атрибута
     */
    private static void processBorderAttribute(IArea area, String parsed) {

        FoBorder[] arBorders = new FoBorder[4];
        int len = 0;
        arBorders[len] = new FoBorder();
        for (String value : StringUtils.split(parsed, String.valueOf(WHITE_SPACES))) {
            FoBorder border = arBorders[len];
            FoBorder temp = new FoBorder();
            if (temp.setStyle(value)) {
                if (!border.isStyleDefined()) {
                    border.setStyle(temp.getStyle());
                    continue;
                }
            } else if (temp.setWidth(value)) {
                if (!border.isWidthDefined()) {
                    border.setWidth(temp.getWidth());
                    continue;
                }
            } else if (temp.setColor(value)) {
                if (!border.isColorDefined()) {
                    border.setColor(temp.getColor());
                    continue;
                }
            } else {
                len = 3;
            }

            if (++len > 3) {

                return; // Чересчур много границ. Не устанавливаем ничего!
            }
            arBorders[len] = temp;
        }
        // Если ничего так и не установили для отображения границы
        if (len == 0 && !arBorders[0].isDefined()) {
            return;
        }
        area.setPropertyValue(BORDER_TOP, arBorders[0]);
        area.setPropertyValue(BORDER_RIGHT,
                null != arBorders[1] ? arBorders[1] : arBorders[0].cloneMe());
        area.setPropertyValue(BORDER_BOTTOM,
                null != arBorders[2] ? arBorders[2] : arBorders[0].cloneMe());
        area.setPropertyValue(BORDER_LEFT,
                null != arBorders[3] ? arBorders[3] : arBorders[len == 0 ? 0 : 1].cloneMe());
    }

    /**
     * Обработка атрибута background-color.
     * @param area           - Область
     * @param attributeValue - Значение атрибута
     * @return boolean возвращает true, в случае успешной обработки атрибута и false, если не удалось распарсить цвет
     */
    private static boolean processBackgroundColorAttribute(IArea area, String attributeValue) {

        if (TRANSPARENT_COLOR.equals(attributeValue)) {
            area.setPropertyValue(FoPropertyType.BACKGROUND_COLOR, null);
            return true;
        }
        Color color = FoColor.parse(attributeValue);
        if (FoColor.isEmpty(color)) {
            return false;
        }
        area.setPropertyValue(FoPropertyType.BACKGROUND_COLOR, color);
        return true;
    }

    /**
     * Обработка атрибута background-image.
     * @param area           - Область
     * @param attributeValue - Значение атрибута
     */
    private static void processBackgroundImageAttribute(IArea area, String attributeValue) {

        FoImage foImage = new FoImage(attributeValue);
        if (foImage.getImage() != null) {
            area.setPropertyValue(BACKGROUND_IMAGE, foImage);
            area.setPropertyValue(FoPropertyType.WIDTH, Dimension.getInstance(foImage.getWidth()));
            area.setPropertyValue(FoPropertyType.HEIGHT, Dimension.getInstance(foImage.getHeight()));
        }
    }

    /**
     * Обработка атрибута background.
     * @param area           - Область
     * @param attributeValue - Значение атрибута
     */
    private static void processBackgroundAttribute(IArea area, String attributeValue) {

        String[] arValues = attributeValue.split(String.valueOf(WHITE_SPACES));
        for (int i = 0; i < arValues.length; i++) {
            if (processBackgroundColorAttribute(area, arValues[i])) {
                arValues[i] = null;
                break;
            }
        }
        String newAttributeValue = String.join(SPACE, arValues);
        if (!StringUtils.isBlank(newAttributeValue)) {
            processBackgroundImageAttribute(area, StringUtils.strip(newAttributeValue, String.valueOf(WHITE_SPACES)));
        }
    }

    /**
     * Обработка первоочередных свойств, для которых важен порядок обработки.
     * @param area          - Область
     * @param attributeList - Список атрибутов
     */
    private static void processShorthandProperties(IArea area, Map<String, String> attributeList) {

        FoPropertyType[] arPropSeq = new FoPropertyType[] {
            BORDER, BORDER_WIDTH, BORDER_STYLE, BORDER_COLOR,
            BORDER_TOP, BORDER_RIGHT, BORDER_BOTTOM, BORDER_LEFT,
            PADDING, MARGIN, FONT, BACKGROUND, BACKGROUND_IMAGE
        };

        for (FoPropertyType propertyType : arPropSeq) {

            String attributeValue = attributeList.get(propertyType.getPropertyName());

            if (StringUtils.isBlank(attributeValue)) {
                continue;
            }
            switch (propertyType) {
                case BORDER:
                    processBorderAttribute(area, attributeValue);
                    break;
                case BORDER_TOP:
                case BORDER_RIGHT:
                case BORDER_BOTTOM:
                case BORDER_LEFT:
                    FoBorder property = (FoBorder)area.getProperty(propertyType);
                    FoBorder foBorder = null != property ? property : new FoBorder();
                    if (foBorder.parse(attributeValue)) {
                        // Устанавливаем в свойство области
                        area.setPropertyValue(propertyType, foBorder);
                    }
                    break;
                case BORDER_WIDTH:
                case BORDER_STYLE:
                case BORDER_COLOR:
                case PADDING:
                case MARGIN:
                    processFourSidesAttribute(area, propertyType.getPropertyName(), attributeValue);
                    break;
                case FONT:
                    FoFont font = new FoFont();
                    if (font.parse(attributeValue)) {
                        area.setPropertyValue(FONT, font);
                    }
                    break;
                case BACKGROUND:
                    processBackgroundAttribute(area, attributeValue);
                    break;
                case BACKGROUND_IMAGE:
                    processBackgroundImageAttribute(area, attributeValue);
                    break;

                default:
                    // В импортруемом коде ничего не было
            }
        }
    }

    /**
     * Обработка базовых элементов.
     * @param propertyType   - Тип свойства
     * @param area           - Область
     * @param attributeValue - Значение атрибута - строка
     */
    private static void processProperties(FoPropertyType propertyType, IArea area, String attributeValue) {

        FoFont foFont;
        switch (propertyType) {
            case FONT_SIZE:
                foFont = getAreaFont(area);
                if (foFont.setFontSize(attributeValue)) {
                    area.setPropertyValue(FONT, foFont);
                }
                break;

            case FONT_FAMILY:
                foFont = getAreaFont(area);
                if (foFont.setFontFamily(attributeValue)) {
                    area.setPropertyValue(FONT, foFont);
                }
                break;

            case FONT_WEIGHT:
                foFont = getAreaFont(area);
                if (foFont.setFontWeight(attributeValue)) {
                    area.setPropertyValue(FONT, foFont);
                }
                break;

            case FONT_STYLE:
                foFont = getAreaFont(area);
                if (foFont.setFontStyle(attributeValue)) {
                    area.setPropertyValue(FONT, foFont);
                }
                break;

            case COLOR:
                Color color = FoColor.parse(attributeValue);
                if (!FoColor.isEmpty(color)) {
                    area.setPropertyValue(propertyType, color);
                }
                break;
            case BACKGROUND_COLOR:
                processBackgroundColorAttribute(area, attributeValue);
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
                handleDimensionProperty(area, propertyType, attributeValue);
                break;

            case COLUMN_WIDTH:
                area.setPropertyValue(propertyType, ColumnWidth.getInstance(attributeValue));
                break;

            case COLUMN_NUMBER:
            case COLUMN_COUNT:
                int value = (int)HelpFuncs.getValueFromString(attributeValue);
                if (value > 0) {
                    area.setPropertyValue(propertyType, value);
                }
                break;

            case NUMBER_COLUMNS_SPANNED:
                int numberColumnsSpanned = (int) HelpFuncs.getValueFromString(attributeValue);
                if (numberColumnsSpanned > DEFAULT_COLUMNS_SPAN_VALUE) {
                    area.setPropertyValue(NUMBER_COLUMNS_SPANNED, numberColumnsSpanned);
                }
                break;

            case NUMBER_ROWS_SPANNED:
                int numberRowsSpanned = (int)HelpFuncs.getValueFromString(attributeValue);
                if (numberRowsSpanned > DEFAULT_ROWS_SPAN_VALUE) {
                    area.setPropertyValue(NUMBER_ROWS_SPANNED, numberRowsSpanned);
                }
                break;

            case TABLE_LAYOUT:
                // Поддерживается только fixed-layout
                break;

            case TEXT_ALIGN:
                area.setPropertyValue(TEXT_ALIGN, FoAlignmentType.parse(attributeValue));
                break;

            case DISPLAY_ALIGN:
            case VERTICAL_ALIGN:
                area.setPropertyValue(VERTICAL_ALIGN, FoAlignmentType.parse(attributeValue));
                break;

            case WRAP_OPTION:
                ParseBooleanResult result = FoWrapOption.parse(attributeValue);
                if (result.isParsed()) {
                    area.setPropertyValue(WRAP_OPTION, result.getParsedValue());
                }
                break;

            // Цвет границы
            case BORDER_BEFORE_COLOR:
            case BORDER_TOP_COLOR:
                handleBorderColor(area, BORDER_TOP, attributeValue);
                break;
            case BORDER_AFTER_COLOR:
            case BORDER_BOTTOM_COLOR:
                handleBorderColor(area, BORDER_BOTTOM, attributeValue);
                break;
            case BORDER_START_COLOR:
            case BORDER_LEFT_COLOR:
                handleBorderColor(area, BORDER_LEFT, attributeValue);
                break;
            case BORDER_END_COLOR:
            case BORDER_RIGHT_COLOR:
                handleBorderColor(area, BORDER_RIGHT, attributeValue);
                break;

            // Стиль границы
            case BORDER_BEFORE_STYLE:
            case BORDER_TOP_STYLE:
                handleBorderStyle(area, BORDER_TOP, attributeValue);
                break;
            case BORDER_AFTER_STYLE:
            case BORDER_BOTTOM_STYLE:
                handleBorderStyle(area, BORDER_BOTTOM, attributeValue);
                break;
            case BORDER_START_STYLE:
            case BORDER_LEFT_STYLE:
                handleBorderStyle(area, BORDER_LEFT, attributeValue);
                break;
            case BORDER_END_STYLE:
            case BORDER_RIGHT_STYLE:
                handleBorderStyle(area, BORDER_RIGHT, attributeValue);
                break;

            // Ширина границы
            case BORDER_BEFORE_WIDTH:
            case BORDER_TOP_WIDTH:
                handleBorderWidth(area, BORDER_TOP, attributeValue);
                break;
            case BORDER_AFTER_WIDTH:
            case BORDER_BOTTOM_WIDTH:
                handleBorderWidth(area, BORDER_BOTTOM, attributeValue);
                break;
            case BORDER_START_WIDTH:
            case BORDER_LEFT_WIDTH:
                handleBorderWidth(area, BORDER_LEFT, attributeValue);
                break;
            case BORDER_END_WIDTH:
            case BORDER_RIGHT_WIDTH:
                handleBorderWidth(area, BORDER_RIGHT, attributeValue);
                break;

            // Пустые области - в рамках границы области
            case PADDING_BEFORE:
                handleDimensionProperty(area, PADDING_TOP, attributeValue);
                break;
            case PADDING_AFTER:
                handleDimensionProperty(area, PADDING_BOTTOM, attributeValue);
                break;
            case PADDING_START:
                handleDimensionProperty(area, PADDING_LEFT, attributeValue);
                break;
            case PADDING_END:
                handleDimensionProperty(area, PADDING_RIGHT, attributeValue);
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
                handleDimensionProperty(area, propertyType, attributeValue);
                break;

            case SPACE_BEFORE:
                handleDimensionProperty(area, MARGIN_TOP, attributeValue);
                break;
            case SPACE_AFTER:
                handleDimensionProperty(area, MARGIN_BOTTOM, attributeValue);
                break;
            case SPACE_START:
                handleDimensionProperty(area, MARGIN_LEFT, attributeValue);
                break;
            case SPACE_END:
                handleDimensionProperty(area, MARGIN_RIGHT, attributeValue);
                break;

            // Устанавливаем имя региона
            case REGION_NAME:
                area.setPropertyValue(REGION_NAME, attributeValue);
                break;

            // Используется для обработки external-graphic
            case SRC:
                handleSrc(area, attributeValue);
                break;

            // Обработка глобальных идентификаторов, используются для internal-destination & page-number-citation
            case ID:
            case REF_ID:
                area.setPropertyValue(FoPropertyType.REF_ID, attributeValue);
                break;

            case INTERNAL_DESTINATION:  // Внутренняя ссылка
            case EXTERNAL_DESTINATION:  // Внешняя ссылка HyperLink
                area.setPropertyValue(propertyType, attributeValue);
                break;

            case SPAN:
                area.setPropertyValue(FoPropertyType.SPAN, attributeValue);
                break;

            // Обработка атрибута text-decoration
            case TEXT_DECORATION:
                foFont = getAreaFont(area);
                if (foFont.setTextDecoration(attributeValue)) {
                    area.setPropertyValue(FONT, foFont);
                }
                break;

            // Обработка атрибута baseline-shift
            case BASELINE_SHIFT:
                area.setPropertyValue(BASELINE_SHIFT, FoBaseLineShift.parseFoBaseLineShift(attributeValue));
                break;

            // Обработка атрибута типа данных ячейки vt
            case VT:
                area.setPropertyValue(VT, new ValueType(attributeValue));
                break;

            // Обработка атрибута flow-name
            case FLOW_NAME:
                area.setPropertyValue(FLOW_NAME, attributeValue);
                break;

            // Обработка атрибута master-name;
            case MASTER_NAME:
                area.setPropertyValue(MASTER_NAME, attributeValue);
                break;

            // Обработка атрибута reference-orientation
            case REFERENCE_ORIENTATION:
                Double referenceOrientation = HelpFuncs.parseDoubleValue(attributeValue);
                if (referenceOrientation != null) {
                    // 360 градусов = полный круг. 180 = полкруга. Приводим к минимально возможному углу
                    int rerefenceOrientation = (referenceOrientation.intValue()) % 360;
                    if (rerefenceOrientation > 180) {
                        rerefenceOrientation -= 360;
                    }
                    if (rerefenceOrientation <= -180) {
                        rerefenceOrientation += 360;
                    }
                    area.setPropertyValue(REFERENCE_ORIENTATION, rerefenceOrientation);
                }
                break;

            // Обработка атрибута master-reference
            case MASTER_REFERENCE:
                area.setPropertyValue(MASTER_REFERENCE, attributeValue);
                break;

            // Обработка атрибута page-height
            case PAGE_HEIGHT:
                area.setPropertyValue(PAGE_HEIGHT, HelpFuncs.getSizeInPixels(attributeValue));
                break;

            // Обработка атрибута page-width
            case PAGE_WIDTH:
                area.setPropertyValue(PAGE_WIDTH, HelpFuncs.getSizeInPixels(attributeValue));
                break;

            // Обработка атрибута visibility
            case VISIBILITY:
                ParseBooleanResult parsingResult = FoVisibility.parse(attributeValue);
                if (parsingResult.isParsed()) {
                    area.setPropertyValue(VISIBILITY, parsingResult.getParsedValue());
                }
                break;

            // Обработка атрибута initial-page-number
            case INITIAL_PAGE_NUMBER:
                area.setPropertyValue(INITIAL_PAGE_NUMBER, attributeValue);
                break;

            // Масштабирование
            case SCALING:
                area.setPropertyValue(SCALING, attributeValue);
                break;

            case PAGE_BREAK_AFTER:
                if (!PAGE_BREAK_AVOID.equals(attributeValue)) {
                    area.setPropertyValue(BREAK_AFTER, convertPageBreakToBreak(attributeValue));
                }
                break;

            case PAGE_BREAK_BEFORE:
                if (!PAGE_BREAK_AVOID.equals(attributeValue)) {
                    area.setPropertyValue(BREAK_BEFORE, convertPageBreakToBreak(attributeValue));
                }
                break;

            case BREAK_AFTER:
                area.setPropertyValue(BREAK_AFTER, attributeValue);
                break;

            case BREAK_BEFORE:
                area.setPropertyValue(BREAK_BEFORE, attributeValue);
                break;

            default:
                logger.error("PropetyType: " + propertyType + " cannot be parsed. Value: " + attributeValue);
                // В импортруемом коде ничего не было
        }
    }

    /**
     * Метод конвертирует значения атрибутов break-before/break-after в аналоги для
     * атрибутов page-break-before/page-break-after.
     * @param attributeValue - Значение атрибута break-before/break-after
     * @return String Значение атрибуты page-break-before/page-break-after
     */
    private static String convertPageBreakToBreak(String attributeValue) {

        String pageAttributeValue = attributeValue;
        switch (attributeValue) {
            case PAGE_BREAK_LEFT:
                pageAttributeValue = BREAK_EVEN_PAGE;
                break;
            case PAGE_BREAK_RIGHT:
                pageAttributeValue = BREAK_ODD_PAGE;
                break;
            case PAGE_BREAK_ALWAYS:
                pageAttributeValue = BREAK_PAGE;
                break;

            default:
                logger.error("PageBreak cannot be parsed to Break. Value:" + attributeValue);
                // В импортруемом коде ничего не было
        }
        return pageAttributeValue;
    }

    /**
     * Обработка свойства-измерения.
     * @param area           - Область
     * @param propertyType   - Тип свойства
     * @param attributeValue - Значение атрибута - строка
     */
    private static void handleDimensionProperty(IArea area, FoPropertyType propertyType, String attributeValue) {

        Dimension dimension = Dimension.getInstance(attributeValue);
        if (dimension.isDefined()) {
            area.setPropertyValue(propertyType, dimension);
        }
    }

    /**
     * Обработка атрибута src ( для обработки external-graphic ).
     * @param area           - Область
     * @param attributeValue - Значение атрибута
     */
    private static void handleSrc(IArea area, String attributeValue) {

        FoImage foImage = new FoImage(attributeValue);
        if (foImage.getImage() != null) {
            area.setPropertyValue(EXTERNAL_GRAPHIC, foImage);
        }
    }

    /**
     * Обработка атрибута border-...-color.
     * @param area           - Область
     * @param propertyType   - Тип свойства
     * @param attributeValue - Значение атрибута - строка
     */
    private static void handleBorderColor(IArea area, FoPropertyType propertyType, String attributeValue) {

        // Получаем объект
        FoBorder foBorder = getOrCreateProperty(area, propertyType);
        // Устанавливаем свойство
        if (foBorder.setColor(attributeValue)) {
            // Устанавливаем в свойство области
            area.setPropertyValue(propertyType, foBorder);
        }
    }

    /**
     * Обработка атрибута border-...-width.
     * @param area           - Область
     * @param propertyType   - Тип свойства
     * @param attributeValue - Значение атрибута
     */
    private static void handleBorderWidth(IArea area, FoPropertyType propertyType, String attributeValue) {

        // Получаем объект
        FoBorder foBorder = getOrCreateProperty(area, propertyType);
        // Устанавливаем свойство
        if (foBorder.setWidth(attributeValue)) {
            // Устанавливаем в свойство области
            area.setPropertyValue(propertyType, foBorder);
        }
    }

    /**
     * Обработка атрибута border-...-style.
     * @param area           - Область
     * @param propertyType   - Тип свойства
     * @param attributeValue - Значение атрибута
     */
    private static void handleBorderStyle(IArea area, FoPropertyType propertyType, String attributeValue) {

        // Получаем объект
        FoBorder foBorder = getOrCreateProperty(area, propertyType);
        // Устанавливаем свойство
        if (foBorder.setStyle(attributeValue)) {
            // Устанавливаем в свойство области
            area.setPropertyValue(propertyType, foBorder);
        }
    }

    /**
     * Получение св-ва из описания области или создание нового.
     * @param area           - Область
     * @param propertyType   - Тип свойства
     * @return FoBorder возвращает св-во из описания области или новое.
     */
    private static FoBorder getOrCreateProperty(IArea area, FoPropertyType propertyType) {

        // Получаем объект
        FoBorder property = (FoBorder)area.getProperty(propertyType);
        return null != property ? property : new FoBorder();
    }

    /**
     * Получение шрифта для области.
     * @param area - Область
     * @return FoFont возвращает шрифт
     */
    private static FoFont getAreaFont(IArea area) {

        FoFont result = (FoFont)area.getProperty(FONT);
        if (result != null) {
            return result;
        }

        area = area.getParentArea();
        while (area != null) {
            result = (FoFont)area.getProperty(FONT);
            if (result != null) {
                return result.cloneMe();
            }
            area = area.getParentArea();
        }
        return new FoFont();
    }
}
