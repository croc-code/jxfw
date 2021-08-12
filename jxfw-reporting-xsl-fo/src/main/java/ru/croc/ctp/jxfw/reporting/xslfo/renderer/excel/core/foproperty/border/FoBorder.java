package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.border;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.WHITE_SPACES;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BORDER_STYLE_DASHED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BORDER_STYLE_DOTTED;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BORDER_STYLE_DOUBLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BORDER_STYLE_MEDIUM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BORDER_STYLE_THICK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.BORDER_STYLE_THIN;

import com.aspose.cells.CellBorderType;
import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

/**
 * Класс обработки атрибутов border-color, border-style, border-width.
 * Created by vsavenkov on 26.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoBorder {

    /**
     * Созданный по умолчанию объект-бордюр, который де факто не должен отображать бордюр.
     * Стремный момент в том, что какой-нить криворукий программер может изменить значение этого объекта.
     * Отсюда мораль: парситься граница должна только в AttributeParser
     */
    public static FoBorder UNDEFINED = new FoBorder();

    /**
     * Переменные, содержащие значение цвета, ширины и стиля границы.
     */
    private Color color = FoColor.DEFAULT_COLOR;

    /**
     * тип бордюра.
     */
    private int borderType = CellBorderType.NONE;

    /**
     * стиль.
     */
    private String style;

    /**
     * ширина.
     */
    private int width = -1;

    /**
     * Свойство - цвет границы.
     * @return Color    - возвращает цвет границы
     */
    public Color getColor() {
        // Если цвет не задан, но задан стиль/ширина, то цвет границы берется по умолчанию черный
        return (color != FoColor.DEFAULT_COLOR || !(isStyleDefined() || isWidthDefined())) ? color : Color.BLACK;
    }

    /**
     * Свойство - цвет границы.
     * @param color - цвет границы
     */
    public void setColor(Color color) {
        this.color = color;
        changeStyle();
    }

    /**
     * Установка цвета границы.
     * @param colorString - цвет
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setColor(String colorString) {

        Color parsedColor = FoColor.parse(colorString);
        if (FoColor.isEmpty(parsedColor)) {
            return false;
        }
        color = parsedColor;
        changeStyle();
        return true;
    }

    /**
     * Установка толщины границы.
     * @param width - толщина
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setWidth(String width) {

        if (StringUtils.isBlank(width)) {
            return false;
        }

        switch (width) {
            case BORDER_STYLE_THIN:
                setWidth(1);
                break;
            case BORDER_STYLE_MEDIUM:
                setWidth(2);
                break;
            case BORDER_STYLE_THICK:
                setWidth(3);
                break;
            default:
                Integer value = HelpFuncs.getSizeInPixelsEx(width);
                if (value == null) {
                    return false;
                }
                setWidth(value);
                break;
        }
        return true;
    }

    /**
     * Свойство - толщина границы.
     * @param width - толщина границы
     */
    public void setWidth(int width) {
        this.width = width;
        changeStyle();
    }

    /**
     * Свойство - тип границы.
     * @return int  - возвращает тип границы
     */
    public int getBorderType() {
        return borderType;
    }

    /**
     * Свойство - стиль границы.
     * @return String   - возвращает стиль границы
     */
    public String getStyle() {
        // Если стиль не задан, но заданы цвет или ширина, то стиль границы берется по умолчанию solid
        return (style != null || !(isColorDefined() || isWidthDefined())) ? style : GlobalData.BORDER_STYLE_SOLID;
    }

    /**
     * Инициализирует стиль из строки.
     * @param style - строка, содержащая стиль
     */
    public void parseStyle(String style) {

        if (setStyle(style)) {
            return;
        }
        this.style = null;
        changeStyle();
    }

    /**
     * Свойство - толщина границы.
     * @return int  - вохвращает толщину границы
     */
    public int getWidth() {
        // Если ширина не задана, но заданы стиль или цвет, то ширина границы берется по умолчанию 1
        return (width >= 0 || !(isColorDefined() || isStyleDefined())) ? width : 1;
    }

    /**
     * Свойство - цвет определен ?.
     * @return boolean - возвращает true, если цвет определен и false в противном случае
     */
    public boolean isColorDefined() {
        return (color != FoColor.DEFAULT_COLOR);
    }

    /**
     * Свойство - ширина определена ?.
     * @return boolean - возвращает true, если ширина определена и false в противном случае
     */
    public boolean isWidthDefined() {
        return (width >= 0);
    }

    /**
     * Свойство - стиль определен ?.
     * @return boolean - возвращает true, если стиль определен и false в противном случае
     */
    public boolean isStyleDefined() {
        return (style != null);
    }

    /**
     * Свойство - хоть что нибудь определено ? Отображать границу надо ?.
     * @return boolean - возвращает true, если хоть что нибудь определено и false в противном случае
     */
    public boolean isDefined() {
        return borderType != CellBorderType.NONE;
    }

    /**
     * Установка окончательного стиля границы.
     */
    private void changeStyle() {

        if (width == 0 || GlobalData.BORDER_STYLE_NONE.equals(style) || !(isColorDefined() || isWidthDefined()
                || isStyleDefined())) {
            borderType = CellBorderType.NONE;
        } else {
            // Обработка стиля и ширины
            // признак заданности значения
            boolean isSetted = true;
            // если стиль задан, то
            if (null != style) {
                switch (style) {
                    case BORDER_STYLE_DOTTED:
                        borderType = CellBorderType.DOTTED;
                        break;
                    case BORDER_STYLE_DASHED:
                        borderType = width >= 2 ? CellBorderType.MEDIUM_DASHED : CellBorderType.DASHED;
                        break;
                    case BORDER_STYLE_DOUBLE:
                        borderType = CellBorderType.DOUBLE;
                        break;
                    default:
                        // снимем флаг обработанности
                        isSetted = false;
                        break;
                }
            } else {
                // иначе: снимем флаг обработанности
                isSetted = false;
            }
            // если значение не проставили, то
            if (!isSetted) {
                borderType = width >= 3 ? CellBorderType.THICK : width == 2 ? CellBorderType.MEDIUM
                        : CellBorderType.THIN;
            }
        }
    }

    /**
     * Установка стиля границы.
     * @param colorString - стиль
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setStyle(String colorString) {

        if (StringUtils.isBlank(colorString) || Arrays.asList(GlobalData.BORDER_STYLES).indexOf(colorString) < 0) {
            return false;
        }
        style = !GlobalData.BORDER_STYLE_HIDDEN.equals(colorString) ? colorString : GlobalData.BORDER_STYLE_NONE;
        changeStyle();
        return true;
    }

    /**
     * Разбор строкового значения границы (атрибуты border-top, border-right, border-top, border-bottom, border-left).
     * @param borderValue - строковое значение
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean parse(String borderValue) {

        if (StringUtils.isBlank(borderValue)) {
            return false;
        }
        for (String value : StringUtils.split(borderValue, String.valueOf(WHITE_SPACES))) {
            if (!setStyle(value) && !setWidth(value) && !setColor(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод получения состояния объекта в виде строки.
     * @return String   - возвращает строку состояния объекта
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        if (isWidthDefined()) {
            builder.append(width).append(GlobalData.UNIT_PX).append(GlobalData.SPACE_CHAR);
        }
        if (isStyleDefined()) {
            builder.append(style).append(GlobalData.SPACE_CHAR);
        }
        if (isColorDefined()) {
            builder.append(color.toString()).append(GlobalData.SPACE_CHAR);
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * Клонирование объекта.
     * @return Object   - возвращает копию объекта
     */
    @Override
    public Object clone() {
        return cloneMe();
    }

    /**
     * Клонирование объекта.
     * @return FoBorder   - возвращает копию бордюра
     */
    public FoBorder cloneMe() {

        FoBorder result = new FoBorder();
        result.color = color;
        result.borderType = borderType;
        result.width = width;
        result.style = style;
        return result;
    }

    /**
     * Проверяет эквивалентность объектов.
     * @param obj - сравниваемый объект
     * @return boolean  - возвращает true, если объекты эквивалентны и false в противном случае
     */
    @Override
    public boolean equals(Object obj) {

        FoBorder border = obj instanceof FoBorder ? (FoBorder)obj : null;
        if (border == null) {
            return false;
        }
        if (!(isDefined() && border.isDefined())) {
            return isDefined() ^ border.isDefined();
        }

        return getColor() == border.getColor()
            && Objects.equals(getStyle(), border.getStyle())
            && getWidth() == border.getWidth();
    }

    /**
     * Формирует хэш-код.
     * @return int  - возвращает хэш-код.
     */
    @Override
    public int hashCode() {

        return !isDefined() ? 0 : getColor().hashCode() ^ getStyle().hashCode() ^ getWidth();
    }
}
