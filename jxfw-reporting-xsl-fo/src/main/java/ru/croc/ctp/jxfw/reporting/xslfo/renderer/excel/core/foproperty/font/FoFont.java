package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.foproperty.font;

import static java.awt.Font.MONOSPACED;
import static java.awt.Font.SANS_SERIF;
import static java.awt.Font.SERIF;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FontStyle.Bold;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FontStyle.Italic;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FontStyle.Regular;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FontStyle.Strikeout;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FontStyle.Underline;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.GraphicsUnit.Inch;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.GraphicsUnit.Millimeter;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.GraphicsUnit.Pixel;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.GraphicsUnit.Point;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.COMMA_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.WHITE_SPACES;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_FONT_NAME;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_FONT_SCALING_FACTOR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DEFAULT_FONT_SIZE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.DQUOTE_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_FAMILY_SANS_CURSIVE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_FAMILY_SANS_FANTASY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_FAMILY_SANS_MONOSPACE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_FAMILY_SANS_SERIF;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_FAMILY_SANS_SERIF2;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_FAMILY_SERIF;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_LARGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_LARGER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_MEDIUM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_SMALER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_SMALL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_XX_LARGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_XX_SMALL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_X_LARGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_SIZE_X_SMALL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_STYLE_BACKSLANT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_STYLE_ITALIC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_STYLE_NORMAL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_STYLE_OBLIQUE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_100;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_200;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_300;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_400;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_500;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_600;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_700;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_800;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_900;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_BOLD;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_BOLDER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_LIGHTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.FONT_WEIGHT_NORMAL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.IN_MM_CONVERT_RATIO;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.IN_PIXELS_CONVERT_RATIO;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.IN_POINTS_CONVERT_RATIO;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.MIN_SIZE_VALUE_LENGTH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.PC_PIXELS_CONVERT_RATIO;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.QUOTE_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.SPACE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.SPACE_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_BLINK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_LINE_THROUGH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_NONE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_NO_BLINK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_NO_LINE_THROUGH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_NO_OVERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_NO_UNDERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_OVERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.TEXT_DECORATION_UNDERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_CM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_EM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_IN;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_MM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_PC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_PT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData.UNIT_PX;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.GraphicsUnit;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

import java.awt.Font;

/**
 * Класс инкапсулирующий обработку атрибутов font-size, font-weight, font-family, font-style.
 * Created by vsavenkov on 24.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoFont {

    /**
     * шрифт по умолчанию.
     */
    private static final Font DEFAULT_FONT_FAMILY = Font.decode(DEFAULT_FONT_NAME);

    /**
     * Наименование шрифта.
     */
    private Font fontFamily;

    /**
     * Pазмер шрифта.
     */
    private float fontSize = DEFAULT_FONT_SIZE;

    /**
     * Единица измерения шрифта (по умолчанию - pt).
     */
    private GraphicsUnit fontUnit = Point;

    /**
     * Стиль шрифта.
     */
    private int fontStyle = Regular.value();

    /**
     * Вес шрифта (по умолчанию - 400 = normal).
     */
    private int fontWeight = 400;

    /**
     * Обработка названия шрифта (могут быть заданы несколько наименований от наиболее конкретного к наиболее общему,
     * разделенные запятыми).
     * @param fontFamily - получает значение атрибута font-family
     * @return boolean  - возвращает true, если удалось установить шрифт и false в противном случае
     */
    public boolean setFontFamily(String fontFamily) {

        if (StringUtils.isBlank(fontFamily)) {
            return false;
        }

        String family = null;
        for (String value : StringUtils.split(fontFamily, COMMA_CHAR)) {
            family = StringUtils.strip(value,
                    String.valueOf(WHITE_SPACES) + QUOTE_CHAR + DQUOTE_CHAR);
            if (StringUtils.isBlank(family)) {
                continue;
            }
            switch (family) {
                case FONT_FAMILY_SERIF:
                    this.fontFamily = Font.decode(SERIF);
                    return true;
                case FONT_FAMILY_SANS_SERIF:
                case FONT_FAMILY_SANS_SERIF2:
                    this.fontFamily = Font.decode(SANS_SERIF);
                    return true;
                case FONT_FAMILY_SANS_MONOSPACE:
                    this.fontFamily = Font.decode(MONOSPACED);
                    return true;
                // Обобщенные семейства шрифтов, для которых нет аналога по умолчанию.
                // Будем использовать стандартный шрифт
                case FONT_FAMILY_SANS_CURSIVE:
                case FONT_FAMILY_SANS_FANTASY:
                    this.fontFamily = null;
                    return true;
                default:
                    // не нашли соответствия шаблону - будем парсить
            }
            this.fontFamily = Font.decode(family);
            return true;
        }

        if (!StringUtils.isBlank(family)) {
            family = family.toLowerCase();
            if (family.endsWith(SPACE + FONT_FAMILY_SANS_SERIF)
                    || family.endsWith(SPACE + FONT_FAMILY_SANS_SERIF2)) {
                this.fontFamily = Font.decode(SANS_SERIF);
                return true;
            }
            if (family.endsWith(SPACE + FONT_FAMILY_SERIF)) {
                this.fontFamily = Font.decode(SERIF);
                return true;
            }
        }
        return false;
    }

    /**
     * Обработка размера шрифта.
     * @param fontSize - получает значение атрибута font-size
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setFontSize(String fontSize) {

        if (StringUtils.isBlank(fontSize)) {
            return false;
        }

        switch (fontSize) {
            case FONT_SIZE_XX_SMALL:
                this.fontSize = (float)(DEFAULT_FONT_SIZE
                        * Math.pow(DEFAULT_FONT_SCALING_FACTOR, -3));
                fontUnit = Point;
                return true;
            case FONT_SIZE_X_SMALL:
                this.fontSize = (float)(DEFAULT_FONT_SIZE
                        * Math.pow(DEFAULT_FONT_SCALING_FACTOR, -2));
                fontUnit = Point;
                return true;
            case FONT_SIZE_SMALL:
                this.fontSize = (float)(DEFAULT_FONT_SIZE
                        * Math.pow(DEFAULT_FONT_SCALING_FACTOR, -1));
                fontUnit = Point;
                return true;
            case FONT_SIZE_MEDIUM:
                this.fontSize = DEFAULT_FONT_SIZE;
                fontUnit = Point;
                return true;
            case FONT_SIZE_LARGE:
                this.fontSize = (float)(DEFAULT_FONT_SIZE
                        * Math.pow(DEFAULT_FONT_SCALING_FACTOR, 1));
                fontUnit = Point;
                return true;
            case FONT_SIZE_X_LARGE:
                this.fontSize = (float)(DEFAULT_FONT_SIZE
                        * Math.pow(DEFAULT_FONT_SCALING_FACTOR, 2));
                fontUnit = Point;
                return true;
            case FONT_SIZE_XX_LARGE:
                this.fontSize = (float)(DEFAULT_FONT_SIZE
                        * Math.pow(DEFAULT_FONT_SCALING_FACTOR, 3));
                fontUnit = Point;
                return true;
            //*** Относительный размер ***
            case FONT_SIZE_SMALER:
                this.fontSize = (float)(this.fontSize * Math.pow(DEFAULT_FONT_SCALING_FACTOR, -1));
                return true;
            //*** Относительный размер ***
            case FONT_SIZE_LARGER:
                this.fontSize = (float)(this.fontSize * Math.pow(DEFAULT_FONT_SCALING_FACTOR, 1));
                return true;
            default:
                // не нашли соответствия шаблону - будем разбирать значение
        }

        float newFontSize;
        GraphicsUnit newFontUnit = fontUnit;

        // Проверяем заканчивается ли размер шрифта на %
        if (HelpFuncs.isPercentValue(fontSize)) {
            // Изменяем значение шрифта
            newFontSize = this.fontSize * HelpFuncs.getPercentValueAsFloat(fontSize) / 100;
        } else {
            String unitName;
            if (fontSize.length() <= MIN_SIZE_VALUE_LENGTH) {
                unitName = UNIT_PX;
            } else {
                // Отрезаем хвост со наименованием единицы измерения
                unitName = fontSize.substring(fontSize.length() - MIN_SIZE_VALUE_LENGTH);
                fontSize = fontSize.substring(0, fontSize.length() - MIN_SIZE_VALUE_LENGTH);
            }
            newFontSize = HelpFuncs.getValueFromString(fontSize);
            switch (unitName) {
                case UNIT_IN:
                    newFontUnit = Inch;
                    break;
                case UNIT_CM:
                    newFontUnit = Millimeter;
                    newFontSize *= 10; // В одном см 10 мм
                    break;
                case UNIT_MM:
                    newFontUnit = Millimeter;
                    break;
                case UNIT_PT:
                    newFontUnit = Point;
                    break;
                case UNIT_PX:
                    newFontUnit = Pixel;
                    break;
                case UNIT_PC:
                    newFontUnit = Pixel;
                    newFontSize = (float)(newFontSize * PC_PIXELS_CONVERT_RATIO);
                    break;
                case UNIT_EM:
                    newFontSize *= this.fontSize;
                    break;
                default:
                    // возможно, единица измерения не указана
                    newFontSize = HelpFuncs.getValueFromString(fontSize + unitName);
                    break;
            }
        }

        if (newFontSize <= 0) {
            return false;
        }
        // Устанавливаем новый размер шрифта
        this.fontSize = newFontSize;
        fontUnit = newFontUnit;
        return true;
    }

    /**
     * Обработка веса шрифта.
     * @param fontWeight - получает значение атрибута font-weight
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setFontWeight(String fontWeight) {

        if (StringUtils.isBlank(fontWeight)) {
            return false;
        }

        // Определяем возможный вес шрифта и устанавливаем
        switch (fontWeight) {
            case FONT_WEIGHT_LIGHTER:
            case FONT_WEIGHT_100:
                this.fontWeight = 100;
                break;
            case FONT_WEIGHT_200:
                this.fontWeight = 200;
                break;
            case FONT_WEIGHT_300:
                this.fontWeight = 300;
                break;
            case FONT_WEIGHT_400:
            case FONT_WEIGHT_NORMAL:
                this.fontWeight = 400;
                break;
            case FONT_WEIGHT_500:
                this.fontWeight = 500;
                break;
            case FONT_WEIGHT_600:
                this.fontWeight = 600;
                break;
            case FONT_WEIGHT_700:
            case FONT_WEIGHT_BOLD:
                this.fontWeight = 700;
                break;
            case FONT_WEIGHT_800:
                this.fontWeight = 800;
                break;
            case FONT_WEIGHT_900:
            case FONT_WEIGHT_BOLDER:
                this.fontWeight = 900;
                break;
            default:
                return false;
        }
        if (this.fontWeight >= 700) {
            fontStyle &= ~Regular.value();
            fontStyle |= Bold.value();
        } else {
            fontStyle &= ~Bold.value();
            fontStyle |= Regular.value();
        }
        return true;
    }

    /**
     * Обработка стиля шрифта.
     * @param fontStyle - получает значение атрибута font-style
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setFontStyle(String fontStyle) {

        if (StringUtils.isBlank(fontStyle)) {
            return false;
        }

        // Определяем возможный стиль шрифта
        switch (fontStyle) {
            case FONT_STYLE_NORMAL:
                this.fontStyle &= ~Italic.value();
                this.fontStyle |= Regular.value();
                break;
            case FONT_STYLE_ITALIC:
            case FONT_STYLE_OBLIQUE:
                this.fontStyle &= ~Regular.value();
                this.fontStyle |= Italic.value();
                break;
            case FONT_STYLE_BACKSLANT:
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Обработка декоративных настроек шрифта.
     * @param decoration - получает значение атрибута text-decoration
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean setTextDecoration(String decoration) {

        if (StringUtils.isBlank(decoration)) {
            return false;
        }

        switch (decoration) {
            case TEXT_DECORATION_NONE:
                fontStyle &= ~(Underline.value() | Strikeout.value());
                break;
            case TEXT_DECORATION_UNDERLINE:
                fontStyle |= Underline.value();
                break;
            case TEXT_DECORATION_LINE_THROUGH:
                fontStyle |= Strikeout.value();
                break;
            case TEXT_DECORATION_NO_UNDERLINE:
                fontStyle &= ~Underline.value();
                break;
            case TEXT_DECORATION_NO_LINE_THROUGH:
                fontStyle &= ~Strikeout.value();
                break;
            case TEXT_DECORATION_OVERLINE:
            case TEXT_DECORATION_NO_OVERLINE:
            case TEXT_DECORATION_BLINK:
            case TEXT_DECORATION_NO_BLINK:
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Разбор строкового значения шрифта.
     * @param font - строковое значение
     * @return boolean  - возвращает успешность установки нового значения
     */
    public boolean parse(String font) {

        if (StringUtils.isBlank(font)) {
            return false;
        }
        StringBuilder fontFamilyBuilder = new StringBuilder();
        boolean isFontStyle = false;
        boolean isFontWeight = false;
        for (String value : StringUtils.split(font, String.valueOf(WHITE_SPACES))) {
            if (fontFamilyBuilder.length() > 0) {
                fontFamilyBuilder.append(SPACE_CHAR).append(value);
            } else if (!isFontStyle && setFontStyle(value)) {
                isFontStyle = true;
            } else if (!isFontWeight && setFontWeight(value)) {
                isFontWeight = true;
            } else if (setFontSize(value)) {
                // Если уже установлен размер шрифта, то назад дороги нет
                isFontStyle = true;
                isFontWeight = true;
            } else {
                fontFamilyBuilder.append(value);
            }
        }
        return fontFamilyBuilder.length() > 0
                ? setFontFamily(fontFamilyBuilder.toString())
                : isFontStyle || isFontWeight;
    }

    /**
     * Свойство - Название шрифта.
     * @return String   - возвращает название шрифта
     */
    public String getName() {
        return (fontFamily != null) ? fontFamily.getName() : DEFAULT_FONT_NAME;
    }

    /**
     * Свойство - Размер шрифта в пунктах !!!.
     * @return float    - возвращает размер шрифта в пунктах
     */
    public float getSize() {
        switch (fontUnit) {
            case Pixel:
                return (float)(fontSize * IN_POINTS_CONVERT_RATIO / IN_PIXELS_CONVERT_RATIO);
            case Inch:
                return (float)(fontSize * IN_POINTS_CONVERT_RATIO);
            case Millimeter:
                return (float)(fontSize * IN_POINTS_CONVERT_RATIO / IN_MM_CONVERT_RATIO);
            default:
                return fontSize;
        }
    }

    /**
     * Свойство - Вес шрифта.
     * @return int  - возвращает вес шрифта
     */
    public int getWeight() {
        return fontWeight;
    }

    /**
     * Свойство - Шрифт наклонный ?.
     * @return boolean  - возвращает true, если шрифт наклонный и false в противном случае
     */
    public boolean isItalic() {
        return ((fontStyle & Italic.value()) != 0);
    }

    /**
     * Свойство - Шрифт жирный ?.
     * @return boolean  - возвращает true, если шрифт жирный и false в противном случае
     */
    public boolean isBold() {
        return ((fontStyle & Bold.value()) != 0);
    }

    /**
     * Свойство - Шрифт перечеркнутый ?.
     * @return boolean  - возвращает true, если шрифт перечеркнутый и false в противном случае
     */
    public boolean isStrikeout() {
        return ((fontStyle & Strikeout.value()) != 0);
    }

    /**
     * Свойство - Шрифт подчеркнут снизу ?.
     * @return boolean  - возвращает true, если шрифт подчеркнут снизу и false в противном случае
     */
    public boolean isUnderline() {
        return ((fontStyle & Underline.value()) != 0);
    }

    /**
     * Возвращает значение установленного шрифта.
     * @return Font -  возвращает значение установленного шрифта
     */
    public Font getFont() {

        String fontName = null != fontFamily ? fontFamily.getName() : DEFAULT_FONT_FAMILY.getName();

        return new Font(fontName, fontStyle, getSizeInPoints());
    }

    /**
     * Возвращает размер в поинтах.
     * @return int  - возвращает размер в поинтах
     */
    private int getSizeInPoints() {
        // после перерасчёта в поинты привоу к целому значению
        return Float.valueOf(getSize()).intValue();
    }

    /**
     * значение шрифта по умолчанию.
     */
    private static final FoFont DEFAULT_FONT = new FoFont();

    /**
     * Возвращает значение шрифта по умолчанию.
     * @return FoFont   - возвращает значение шрифта по умолчанию
     */
    public static FoFont getDefaultFont() {
        return DEFAULT_FONT;
    }

    /**
     * Метод получения состояния объекта в виде строки.
     * @return String   - возвращает строку состояния объекта
     */
    @Override
    public String toString() {
        return String.format(GlobalData.getCultureInfo(),
                "FontSize = '%1$f', FontFamily = '%2$s', FontStyle = '%3$d', FontWeight = '%4$d', FontUnit = '%5$s'",
                fontSize, getName(), fontStyle, fontWeight, fontUnit);
    }

    /**
     * Метод реализующий интерфейс Cloneable.
     * @return Object   - возвращает  копию объекта
     */
    @Override
    public Object clone() {
        return cloneMe();
    }

    /**
     * Создание клона.
     * @return FoFont   - возвращает копию шрифта
     */
    public FoFont cloneMe() {

        FoFont foFont = new FoFont();
        foFont.fontStyle = fontStyle;
        foFont.fontUnit = fontUnit;
        foFont.fontWeight = fontWeight;
        foFont.fontFamily = fontFamily;
        foFont.fontSize = fontSize;
        return foFont;
    }
}
