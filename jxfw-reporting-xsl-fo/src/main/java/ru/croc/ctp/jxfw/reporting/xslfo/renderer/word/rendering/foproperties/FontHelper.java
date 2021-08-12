package ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.rendering.foproperties;

import com.aspose.words.Font;
import com.aspose.words.Underline;
import org.apache.commons.lang3.NotImplementedException;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoColor;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.common.XslFoCulture;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.attribute.FoFont;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.word.processing.tree.element.root.GenericArea;

import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Вспомогательные методы
 * Created by vsavenkov on 28.06.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class FontHelper {

    /**
     * Соответствия "Language ID" и "Language tag" из таблицы "https://msdn.microsoft.com/en-us/library/cc233982.aspx".
     * Шестнадцатиричные кода переведены в десятичную систему.
     * Используется при задании св-ва LocaleId объекта com.aspose.words.Font.
     * Перенесён минимум. В случае необходимости дополнить.
     */
    private static final Map<String, Integer> LANGUAGE_ID = new HashMap<String, Integer>() {
        private static final long serialVersionUID = 1L;

        {
            put("ru", 25);
            put("ru-RU", 1049);
            put("en", 9);
            put("en-GB", 2057);
            put("en-US", 1033);
        }
    };

    /**
     * Возвращает идентификатор языка по имени локализации.
     * Данные соответствуют таблице "https://msdn.microsoft.com/en-us/library/cc233982.aspx".
     * @param locale    - локализация
     * @return int  - возвращает идентификатор языка.
     * @throws NotImplementedException  - генерирует исключение в случае отсутствия данных для переданной локализации.
     */
    private static int getLcid(Locale locale) {
        
        if (!LANGUAGE_ID.containsKey(locale.toString())) {
            throw new NotImplementedException("Need add language ID for locale:" + locale.getDisplayName());
        }
        return LANGUAGE_ID.get(locale.toString());
    }

    /**
     * Метод присваивает шрифт, указанный в классе FOFont приемнику Aspose.Words.Font.
     * @param fontTo   - целевой шрифт
     * @param fontFrom - шрифт - источник
     */
    public static void assign(Font fontTo, FoFont fontFrom) {
        
        if (fontFrom == null) {
            return;
        }
        fontTo.setName(fontFrom.getName());
        fontTo.setSize(fontFrom.getSize());
        fontTo.setBold(fontFrom.isBold());
        fontTo.setItalic(fontFrom.isItalic());
        fontTo.setUnderline(fontFrom.isUnderline() ? Underline.SINGLE : Underline.NONE);
        fontTo.setStrikeThrough(fontFrom.isStrikeout());
        fontTo.setSubscript(fontFrom.isSubscript());
        fontTo.setSuperscript(fontFrom.isSuperscript());
        if (XslFoCulture.getCultureInfo() != null) {
            fontTo.setLocaleId(getLcid(XslFoCulture.getCultureInfo()));
        }
    }

    /**
     * Метод присваивает цвет шрифту.
     * @param fontTo      - шрифт
     * @param colorObject - цвет
     */
    public static void assignFontColor(Font fontTo, Object colorObject) {
        
        if (colorObject == null) {
            return;
        }
        Color color = (Color)colorObject;
        if (!FoColor.isEmpty(color)) {
            fontTo.setColor(color);
        }
    }

    /**
     * Метод присваивает цвет фона шрифту.
     * @param fontTo              - Целевой шрифт
     * @param colorObject         - цвет фона
     * @param skipBackgroundColor - При совпадении с данным цветом, не устанавливать цвет фона
     */
    public static void assignFontHighlightColor(Font fontTo, Object colorObject, Color skipBackgroundColor) {
        
        if (colorObject == null) {
            return;
        }
        Color color = (Color)colorObject;
        if (!FoColor.isEmpty(color) && color.getRGB() != skipBackgroundColor.getRGB()) {
            fontTo.setHighlightColor(color);
        }
    }

    /**
     * Вытаскивание и присваивание шрифта из свойств области.
     * @param fontTo              - Целевой шрифт
     * @param areaFrom            - Область xsl-fo
     * @param skipBackgroundColor - При совпадении с данным цветом, не устанавливать цвет фона
     */
    public static void assignFontArea(Font fontTo, GenericArea areaFrom, Color skipBackgroundColor) {
        
        if (areaFrom == null) {
            return;
        }
        assign(fontTo, (FoFont)areaFrom.getInheritablePropertyValue(FoPropertyType.FONT));
        assignFontColor(fontTo, areaFrom.getInheritablePropertyValue(FoPropertyType.COLOR));
        assignFontHighlightColor(fontTo, areaFrom.getInheritablePropertyValue(FoPropertyType.BACKGROUND_COLOR),
                                 skipBackgroundColor);
    }
}
