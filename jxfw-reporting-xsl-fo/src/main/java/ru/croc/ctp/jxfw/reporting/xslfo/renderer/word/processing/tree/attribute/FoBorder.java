package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute;

import static com.aspose.words.LineStyle.DASH_LARGE_GAP;
import static com.aspose.words.LineStyle.DOT;
import static com.aspose.words.LineStyle.DOUBLE;
import static com.aspose.words.LineStyle.EMBOSS_3_D;
import static com.aspose.words.LineStyle.ENGRAVE_3_D;
import static com.aspose.words.LineStyle.INSET;
import static com.aspose.words.LineStyle.NONE;
import static com.aspose.words.LineStyle.OUTSET;
import static com.aspose.words.LineStyle.SINGLE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.WHITE_SPACES;

import com.aspose.words.LineStyle;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.util.MathUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;

import java.awt.Color;

/**
 * Класс обработки атрибутов border-color, border-style, border-width.
 * Created by vsavenkov on 27.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoBorder {

    /**
     * Значение, применяемое при незаданности св-ва.
     */
    private final int lineStyleNotSet = -1;

    //region Переменные, содержащие значение цвета, ширины и стиля границы
    /**
     * Цвет.
     */
    private Color color = FoColor.DEFAULT_COLOR;
    /**
     * Стиль линии.
     */
    private int lineStyle = lineStyleNotSet;
    /**
     * Ширина. По-умолчанию = 1
     */
    private double width = -1d;
    //endregion

    /**
     * Свойство - цвет границы.
     * @return Color возвращает цвет границы
     */
    public Color getColor() {
        // Если цвет не задан, но задан стиль/ширина, то цвет границы берется по умолчанию черный
        return (color != FoColor.DEFAULT_COLOR || !(isStyleDefined() || isWidthDefined())) ? color : Color.BLACK;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Свойство - стиль линии границы. Если границу по какой-то причине не надо рисовать, то возвращает LineStyle.None.
     * @return int возвращает код стиля линии границы
     */
    public int getLineStyle() {
        return width == 0d ? LineStyle.NONE
            : lineStyleNotSet != lineStyle
            ? lineStyle
            : (isColorDefined() || isWidthDefined())
            // Если цвет или ширина заданы, а стиль границы - нет, то по умолчанию - solid
            ? SINGLE : LineStyle.NONE;
    }

    /**
     * Свойство - стиль границы.
     * @return String возвращает стиль
     */
    public String getStyle() {
        // Если стиль не задан, но заданы цвет или ширина, то стиль границы берется по умолчанию solid
        if (lineStyleNotSet == lineStyle) {
            return (isColorDefined() || isWidthDefined()) ? GlobalData.BORDER_STYLE_SOLID : null;
        }

        switch (lineStyle) {
            case NONE:
                return GlobalData.BORDER_STYLE_NONE;
            case DASH_LARGE_GAP:
                return GlobalData.BORDER_STYLE_DASHED;
            case DOT:
                return GlobalData.BORDER_STYLE_DOTTED;
            case DOUBLE:
                return GlobalData.BORDER_STYLE_DOUBLE;
            case ENGRAVE_3_D:
                return GlobalData.BORDER_STYLE_GROOVE;
            case INSET:
                return GlobalData.BORDER_STYLE_INSET;
            case OUTSET:
                return GlobalData.BORDER_STYLE_OUTSET;
            case EMBOSS_3_D:
                return GlobalData.BORDER_STYLE_RIDGE;
            case SINGLE:
                return GlobalData.BORDER_STYLE_SOLID;
            default:
                return null;
        }
    }

    /**
     * Установка стиля.
     * @param style - стиль
     */
    public void setStyle(String style) {
        if (parseStyle(style)) {
            return;
        }
        lineStyle = lineStyleNotSet;
    }

    /**
     * Свойство - толщина границы.
     * @return double возвращает толщину границы
     */
    public double getWidth() {
        // Если ширина не задана, но заданы стиль или цвет, то ширина границы берется по умолчанию 1
        return (width >= 0 || !(isColorDefined() || isStyleDefined())) ? width : 1d;
    }

    /**
     * Свойство - толщина границы.
     * @param width - толщина границы
     */
    public void setWidth(double width) {
        this.width = MathUtils.round(width, 2);
    }

    /**
     * Свойство - цвет определен ?.
     * @return boolean возвращает true, если цвет определен и false в противном случае
     */
    public boolean isColorDefined() {
        return color != FoColor.DEFAULT_COLOR;
    }

    /**
     * Свойство - ширина определена ?.
     * @return boolean возвращает true, если ширина определена и false в противном случае
     */
    public boolean isWidthDefined() {
        return width >= 0;
    }

    /**
     *Свойство - стиль определен ?.
     * @return boolean возвращает true, если стиль определен и false в противном случае
     */
    public boolean isStyleDefined() {
        return lineStyleNotSet != lineStyle;
    }

    /**
     * Свойство - хоть что нибудь определено ?.
     * @return boolean возвращает true, если хоть что нибудь определено и false в противном случае
     */
    public boolean isDefined() {
        return isColorDefined() || isWidthDefined() || isStyleDefined();
    }

    /**
     * Свойство - хоть что нибудь определено ? Отображать границу надо ?.
     * @return boolean возвращает true, если надо отображать границу и false в противном случае
     */
    public boolean isVisible() {
        return getLineStyle() != LineStyle.NONE;
    }

    /**
     * Установка цвета границы.
     * @param color - цвет
     * @return успешность установки нового значения
     */
    public boolean parseColor(String color) {

        Color parsedColor = FoColor.parse(color);
        if (FoColor.isEmpty(parsedColor)) {
            return false;
        }
        this.color = parsedColor;
        return true;
    }

    /**
     * Установка стиля границы.
     * @param style - стиль
     * @return успешность установки нового значения
     */
    public boolean parseStyle(String style) {

        if (StringUtils.isBlank(style)) {
            return false;
        }
        switch (style) {
            case GlobalData.BORDER_STYLE_NONE:
            case GlobalData.BORDER_STYLE_HIDDEN:
                lineStyle = LineStyle.NONE;
                break;
            case GlobalData.BORDER_STYLE_DASHED:
                lineStyle = LineStyle.DASH_LARGE_GAP;
                break;
            case GlobalData.BORDER_STYLE_DOTTED:
                lineStyle = DOT;
                break;
            case GlobalData.BORDER_STYLE_DOUBLE:
                lineStyle = DOUBLE;
                break;
            case GlobalData.BORDER_STYLE_GROOVE:
                lineStyle = LineStyle.ENGRAVE_3_D;
                break;
            case GlobalData.BORDER_STYLE_INSET:
                lineStyle = INSET;
                break;
            case GlobalData.BORDER_STYLE_OUTSET:
                lineStyle = OUTSET;
                break;
            case GlobalData.BORDER_STYLE_RIDGE:
                lineStyle = LineStyle.EMBOSS_3_D;
                break;
            case GlobalData.BORDER_STYLE_SOLID:
                lineStyle = SINGLE;
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Установка толщины границы.
     * @param width - толщина
     * @return успешность установки нового значения
     */
    public boolean parseWidth(String width) {

        if (StringUtils.isBlank(width)) {
            return false;
        }

        switch (width) {
            case GlobalData.BORDER_STYLE_THIN:
                setWidth(1d);
                break;
            case GlobalData.BORDER_STYLE_MEDIUM:
                setWidth(2d);
                break;
            case GlobalData.BORDER_STYLE_THICK:
                setWidth(3d);
                break;
            default:
                Double value = HelpFuncs.getSizeInPointsEx(width);
                if (value == null) {
                    return false;
                }
                setWidth(value);
                break;
        }
        return true;
    }

    /**
     * Разбор строкового значения границы (атрибуты border-top, border-right, border-top, border-bottom, border-left).
     * @param borderValue - строковое значение
     * @return успешность установки нового значения
     */
    public boolean parse(String borderValue) {

        if (StringUtils.isBlank(borderValue)) {
            return false;
        }
        for (String value : StringUtils.split(borderValue, String.valueOf(WHITE_SPACES))) {
            if (!parseStyle(value) && !parseWidth(value) && !parseColor(value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод получения состояния объекта в виде строки.
     * @return Строка состояния объекта
     */
    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        if (isWidthDefined()) {
            builder.append(width).append(ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.UNIT_PT)
                    .append(ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.SPACE_CHAR);
        }
        if (isStyleDefined()) {
            builder.append(
                    getStyle()).append(ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.SPACE_CHAR
            );
        }
        if (isColorDefined()) {
            builder.append(color.toString())
                    .append(ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.SPACE_CHAR);
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * Клонирование объекта.
     * @return возвращает клон объекта
     */
    @Override
    public Object clone() {
        return cloneMe();
    }

    /**
     * Клонирование объекта.
     * @return возвращает клон объекта
     */
    public FoBorder cloneMe() {
        
        FoBorder result = new FoBorder();
        result.color =  this.color;
        result.lineStyle = this.lineStyle;
        result.width = this.width;
        return result;
    }

    /**
     * Проверка эквивалентности.
     * @param border - проверяемый объект
     * @return boolean возвращает true, если объект эквивалентен и false в противном случае
     */
    public boolean isEquals(FoBorder border) {
        
        if (border == null) {
            return false;
        }
        boolean isMyIsDefined = isDefined();
        boolean isCmpIsDefined = border.isDefined();
        if (!(isMyIsDefined && isCmpIsDefined)) {
            return isMyIsDefined ^ isCmpIsDefined;
        }

        return getColor() == border.getColor() && getStyle().equals(border.getStyle())
                && getWidth() == border.getWidth();
    }

    @Override 
    public boolean equals(Object obj) {
        return isEquals(obj instanceof FoBorder ? (FoBorder)obj : null);
    }

    @Override
    public int hashCode() {
        return !isDefined() ? 0 : getColor().hashCode() ^ getStyle().hashCode() ^ Double.hashCode(getWidth());
    }
}
