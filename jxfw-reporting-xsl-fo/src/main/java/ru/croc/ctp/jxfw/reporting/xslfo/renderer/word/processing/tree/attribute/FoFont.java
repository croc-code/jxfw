package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute;

import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.DQUOTE_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.QUOTE_CHAR;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.GlobalData.WHITE_SPACES;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.BASELINE_SHIFT_BASELINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.BASELINE_SHIFT_SUB;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.BASELINE_SHIFT_SUPER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_FAMILY_SANS_CURSIVE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_FAMILY_SANS_FANTASY;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_FAMILY_SANS_MONOSPACE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_FAMILY_SANS_SERIF;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_FAMILY_SANS_SERIF2;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_FAMILY_SERIF;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_LARGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_LARGER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_MEDIUM;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_SMALER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_SMALL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_XX_LARGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_XX_SMALL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_X_LARGE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_SIZE_X_SMALL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_STYLE_BACKSLANT;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_STYLE_ITALIC;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_STYLE_NORMAL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_STYLE_OBLIQUE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_100;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_200;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_300;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_400;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_500;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_600;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_700;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_800;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_900;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_BOLD;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_BOLDER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_LIGHTER;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.FONT_WEIGHT_NORMAL;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_BLINK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_LINE_THROUGH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_NONE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_NO_BLINK;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_NO_LINE_THROUGH;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_NO_OVERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_NO_UNDERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_OVERLINE;
import static ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData.TEXT_DECORATION_UNDERLINE;

import org.apache.commons.lang.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.FontStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.GlobalData;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.XslFoCulture;

import java.awt.Font;

/**
 * Класс инкапсулирующий обработку атрибутов font-size, font-weight, font-family, font-style.
 * А также text-decoration и baseline-shift
 * Created by vsavenkov on 28.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FoFont {

    /**
     * Наименование шрифта.
     */
    private Font fontFamily;
    /**
     * Pазмер шрифта.
     */
    private double fontSize = GlobalData.DEFAULT_FONT_SIZE;
    /**
     * Стиль шрифта.
     */
    private int fontStyle = FontStyle.Regular.value();
    /**
     * Вес шрифта (по умолчанию - 400 = normal).
     */
    private int fontWeight = 400;
    /**
     * Нижний индекс шрифта.
     */
    private boolean isSubscript;
    /**
     * Верхний индекс шрифта.
     */
    private boolean isSuperscript;

    /**
     * Обработка названия шрифта (могут быть заданы несколько наименований от наиболее конкретного к наиболее
     * общему, разделенные запятыми).
     * @param fontFamily - получает значение атрибута font-family
     * @return boolean возвращает true, если удалось разобрать шрифт и false в противном случае
     */
    public boolean setFontFamily(String fontFamily) {
        
        if (StringUtils.isBlank(fontFamily)) {
            return false;
        }

        String family = null;
        for (String value : StringUtils.split(fontFamily, GlobalData.COMMA_CHAR)) {
            family = StringUtils.strip(value, String.valueOf(WHITE_SPACES) + QUOTE_CHAR + DQUOTE_CHAR);
            if (StringUtils.isBlank(family)) {
                continue;
            }
            switch (family) {
                case FONT_FAMILY_SERIF:
                    this.fontFamily = Font.decode(Font.SERIF);
                    return true;
                case FONT_FAMILY_SANS_SERIF:
                case FONT_FAMILY_SANS_SERIF2:
                    this.fontFamily = Font.decode(Font.SANS_SERIF);
                    return true;
                case FONT_FAMILY_SANS_MONOSPACE:
                    this.fontFamily = Font.decode(Font.MONOSPACED);
                    return true;
                // Обобщенные семейства шрифтов, для которых нет аналога по умолчанию. 
                // Будем использовать стандартный шрифт
                case FONT_FAMILY_SANS_CURSIVE:
                case FONT_FAMILY_SANS_FANTASY:
                    this.fontFamily = null;
                    return true;

                default:
                    // В импортруемом коде ничего не было
            }
            this.fontFamily = Font.decode(family);
            return true;
        }

        if (!StringUtils.isBlank(family)) {
            family = family.toLowerCase();
            if (family.endsWith(GlobalData.SPACE + FONT_FAMILY_SANS_SERIF)
                    || family.endsWith(GlobalData.SPACE + FONT_FAMILY_SANS_SERIF2)) {
                this.fontFamily = Font.decode(Font.SANS_SERIF);
                return true;
            }
            if (family.endsWith(GlobalData.SPACE + FONT_FAMILY_SERIF)) {
                this.fontFamily = Font.decode(Font.SERIF);
                return true;
            }
        }
        return false;
    }

    /**
     * Обработка размера шрифта.
     * @param fontSize - получает значение атрибута font-size
     * @return успешность установки нового значения
     */
    public boolean setFontSize(String fontSize) {
        
        if (StringUtils.isBlank(fontSize)) {
            return false;
        }

        switch (fontSize) {
            case FONT_SIZE_XX_SMALL:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, -3);
                return true;
            case FONT_SIZE_X_SMALL:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, -2);
                return true;
            case FONT_SIZE_SMALL:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, -1);
                return true;
            case FONT_SIZE_MEDIUM:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE;
                return true;
            case FONT_SIZE_LARGE:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, 1);
                return true;
            case FONT_SIZE_X_LARGE:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, 2);
                return true;
            case FONT_SIZE_XX_LARGE:
                this.fontSize = GlobalData.DEFAULT_FONT_SIZE * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, 3);
                return true;
            //*** Относительный размер ***
            case FONT_SIZE_SMALER:
                this.fontSize = this.fontSize * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, -1);
                return true;
            //*** Относительный размер ***
            case FONT_SIZE_LARGER:
                this.fontSize = this.fontSize * Math.pow(GlobalData.DEFAULT_FONT_SCALING_FACTOR, 1);
                return true;

            default:
                // В импортруемом коде ничего не было
        }

        Double newFontSize;
        // Проверяем заканчивается ли размер шрифта на %
        if (HelpFuncs.isPercentValue(fontSize)) {
            newFontSize = this.fontSize * HelpFuncs.getPercentValue(fontSize) / 100d;
        } else {
            newFontSize = HelpFuncs.getSizeInPointsEx(fontSize);
        }

        if (null == newFontSize || newFontSize <= HelpFuncs.ZERO) {
            return false;
        }
        // Устанавливаем новый размер шрифта
        this.fontSize = newFontSize;
        return true;
    }

    /**
     * Обработка веса шрифта.
     * @param fontWeight - получает значение атрибута font-weight
     * @return успешность установки нового значения
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
            fontStyle &= ~FontStyle.Regular.value();
            fontStyle |= FontStyle.Bold.value();
        } else {
            fontStyle &= ~FontStyle.Bold.value();
            fontStyle |= FontStyle.Regular.value();
        }
        return true;
    }

    /**
     * Обработка стиля шрифта.
     * @param fontStyle - получает значение атрибута font-style
     * @return успешность установки нового значения
     */
    public boolean setFontStyle(String fontStyle) {
        
        if (StringUtils.isBlank(fontStyle)) {
            return false;
        }

        // Определяем возможный стиль шрифта
        switch (fontStyle) {
            case FONT_STYLE_NORMAL:
                this.fontStyle &= ~FontStyle.Italic.value();
                this.fontStyle |= FontStyle.Regular.value();
                break;
            case FONT_STYLE_ITALIC:
            case FONT_STYLE_OBLIQUE:
                this.fontStyle &= ~FontStyle.Regular.value();
                this.fontStyle |= FontStyle.Italic.value();
                break;
            case FONT_STYLE_BACKSLANT:
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Обработка декоративных настроек шрифта (атрибут text-decoration).
     * @param decoration - получает значение атрибута text-decoration
     * @return успешность установки нового значения
     */
    public boolean setTextDecoration(String decoration) {
        
        if (StringUtils.isBlank(decoration)) {
            return false;
        }

        switch (decoration) {
            case TEXT_DECORATION_NONE:
                fontStyle &= ~(FontStyle.Underline.value() | FontStyle.Strikeout.value());
                break;
            case TEXT_DECORATION_UNDERLINE:
                fontStyle |= FontStyle.Underline.value();
                break;
            case TEXT_DECORATION_LINE_THROUGH:
                fontStyle |= FontStyle.Strikeout.value();
                break;
            case TEXT_DECORATION_NO_UNDERLINE:
                fontStyle &= ~FontStyle.Underline.value();
                break;
            case TEXT_DECORATION_NO_LINE_THROUGH:
                fontStyle &= ~FontStyle.Strikeout.value();
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
     * Обработка декоративных настроек шрифта (атрибут baseline-shift).
     * @param baselineShift - получает значение атрибута baseline-shift
     * @return успешность установки нового значения
     */
    public boolean setBaselineShift(String baselineShift) {
        
        if (StringUtils.isBlank(baselineShift)) {
            return false;
        }
        switch (baselineShift) {
            case BASELINE_SHIFT_BASELINE:
                isSubscript = false;
                isSuperscript = false;
                return true;
            case BASELINE_SHIFT_SUB:
                isSubscript = true;
                isSuperscript = false;
                return true;
            case BASELINE_SHIFT_SUPER:
                isSubscript = false;
                isSuperscript = true;
                return true;
            default:
                return false;
        }
    }

    /**
     * Разбор строкового значения шрифта.
     * @param font - строковое значение
     * @return успешность установки нового значения
     */
    public boolean parse(String font) {
        
        if (StringUtils.isBlank(font)) {
            return false;
        }
        StringBuilder fontFamily = new StringBuilder();
        boolean isFontStyle = false;
        boolean isFontWeight = false;
        for (String value : StringUtils.split(font, String.valueOf(WHITE_SPACES))) {
            if (fontFamily.length() > 0) {
                fontFamily.append(GlobalData.SPACE_CHAR).append(value);
            } else if (!isFontStyle && setFontStyle(value)) {
                isFontStyle = true;
            } else if (!isFontWeight && setFontWeight(value)) {
                isFontWeight = true;
            } else if (setFontSize(value)) {
                // Если уже установлен размер шрифта, то назад дороги нет
                isFontStyle = true;
                isFontWeight = true;
            } else {
                fontFamily.append(value);
            }
        }
        return fontFamily.length() > 0
            ? setFontFamily(fontFamily.toString())
            : isFontStyle || isFontWeight;
    }

    /**
     * Свойство - Название шрифта.
     * @return String возвращает название шрифта
     */
    public String getName() {
        return (fontFamily != null) ? fontFamily.getName() : GlobalData.DEFAULT_FONT_NAME;
    }

    /**
     * Свойство - Размер шрифта в пунктах !!!.
     * @return int возвращает размер шрифта в пунктах
     */
    public int getSize() {
        return (int) fontSize;
    }

    /**
     * Свойство - Вес шрифта.
     * @return int возвращает вес шрифта
     */
    public int getWeight() {
        return fontWeight;
    }

    /**
     * Свойство - Шрифт наклонный ?.
     * @return boolean возвращает true, если шрифт наклонный и false в противном случае
     */
    public boolean isItalic() {
        return ((fontStyle & FontStyle.Italic.value()) != 0);
    }

    /**
     * Свойство - Шрифт жирный ?.
     * @return boolean возвращает true, если шрифт жирный и false в противном случае
     */
    public boolean isBold() {
        return ((fontStyle & FontStyle.Bold.value()) != 0);
    }

    /**
     * Свойство - Шрифт перечеркнутый ?.
     * @return boolean возвращает true, если шрифт перечеркнутый и false в противном случае
     */
    public boolean isStrikeout() {
        return ((fontStyle & FontStyle.Strikeout.value()) != 0);
    }

    /**
     * Свойство - Шрифт подчеркнут снизу ?.
     * @return boolean возвращает true, если шрифт подчеркнут снизу и false в противном случае
     */
    public boolean isUnderline() {
        return ((fontStyle & FontStyle.Underline.value()) != 0);
    }

    /**
     * Свойство - Нижний индекс шрифта.
     * @return boolean возвращает true, если нижний индекс шрифта и false в противном случае
     */
    public boolean isSubscript() {
        return isSubscript;
    }

    /**
     * Свойство - Верхний индекс шрифта.
     * @return boolean возвращает true, если верхний индекс шрифта и false в противном случае
     */
    public boolean isSuperscript() {
        return isSuperscript;
    }

    /**
     * Возвращает значение шрифта по умолчанию.
     * @return FoFont Возвращает значение шрифта по умолчанию
     */
    public static FoFont getDefaultFont() {
        return new FoFont();
    }

    /**
     * Метод получения состояния объекта в виде строки.
     * @return Строка состояния объекта
     */
    @Override
    public String toString() {
        
        return String.format(XslFoCulture.getCultureInfo(),
                 "FontSize = %f, FontFamily = %s, FontStyle = %d, FontWeight = %d, isSubscript = %b, "
                         + "isSuperscript = %b",
                fontSize, getName(), fontStyle, fontWeight, isSubscript, isSuperscript);
    }

    /**
     * Метод реализующий интерфейс ICloneable.
     * @return возвращает клон объекта
     */
    @Override
    public Object clone() {
        return cloneMe();
    }

    /**
     * Создание клона.
     * @return возвращает клон объекта
     */
    public FoFont cloneMe() {
        
        FoFont foFont = new FoFont();
        foFont.fontStyle = this.fontStyle;
        foFont.fontWeight = this.fontWeight;
        foFont.fontFamily = this.fontFamily;
        foFont.fontSize = this.fontSize;
        return foFont;
    }

    /**
     * Сравнение шрифтов на эквивалентность.
     * @param font - Сравниваемый шрифт
     * @return boolean возвращает true, если объекты эквивалентны и false в противном случае
     */
    public boolean isEquals(FoFont font) {
        
        if (font == null) {
            return false;
        }
        return getName().equals(font.getName()) && getSize() == font.getSize()
                && this.fontWeight == font.fontWeight && this.fontStyle == font.fontStyle
                && this.isSuperscript == font.isSuperscript && this.isSuperscript == font.isSuperscript;
    }
}
