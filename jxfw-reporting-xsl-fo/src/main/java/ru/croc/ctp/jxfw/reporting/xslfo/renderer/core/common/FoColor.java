package ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common;

import org.apache.commons.lang.StringUtils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс инкапсулирующий обработку аттрибута color в класс Windows.Drawing.Color.
 * Created by vsavenkov on 28.07.2017.
 */
public class FoColor {

    /**
     * Значение цвета по умолчанию.
     */
    public static Color DEFAULT_COLOR = new Color(0, true);

    /**
     * длина полной формы записи цвета в шестнадцатиричном формате - 7 символов.
     */
    private static final int COLOR_HEX_FULL_LEN = 7;
    /**
     * длина краткой формы записи цвета в шестнадцатиричном формате  - 4 символов.
     */
    private static final int COLOR_HEX_MIN_LEN = 4;
    /**
     * длина краткой формы записи цвета в формате RGB - 10 символов.
     */
    private static final int COLOR_RGB_MIN_LEN = 10;

    /**
     * Кэш цветов.
     */
    private static final Map<String, Color> COLOR_CACHE = new HashMap<>();

    /**
     * объект для блокировки.
     */
    private static final ReentrantReadWriteLock REENTRANT_LOCK = new ReentrantReadWriteLock();

    /**
     * Проверка на то, что указанный цвет является пустым.
     * К сожалению, ТОЛЬКО ТАК нужно проверять, ибо в Aspose ухитряются инициировать так, 
     * что сравнение с Color.Empty == false, oColor.isEmpty == false
     * @param color - Сравниваемый цвет
     * @return boolean  - возвращает true, если цвет пустой и false в противном случае
     */
    public static boolean isEmpty(Color color) {
        if (color == null) {
            return true;
        }
        return color.getRGB() == 0;
    }

    /**
     * Преобразование строкового значения цвета в системное.
     * @param colorValue - принимает в качестве параметра значение атрибута color
     * @return Color    - возвращает системное значение цвета
     */
    public static Color parse(String colorValue) {
        
        if (StringUtils.isBlank(colorValue)) {
            return DEFAULT_COLOR;
        }

        Color color;
        // Пытаемся сначала получить значение из кэша
        REENTRANT_LOCK.readLock().lock();
        try {
            if (COLOR_CACHE.containsKey(colorValue)) {
                return COLOR_CACHE.get(colorValue);
            }
        } finally {
            REENTRANT_LOCK.readLock().unlock();
        }

        color = getColorFromString(colorValue);
        if (isEmpty(color)) {
            return color;
        }

        REENTRANT_LOCK.writeLock().lock();
        try {
            COLOR_CACHE.put(colorValue, color);
        } finally {
            REENTRANT_LOCK.writeLock().unlock();
        }
        return color;
    }

    /**
     * Метод для получения цвета из строки.
     * @param color - Цвет, заданный в виде строки
     * @return Color    - возвращает цвет
     */
    private static Color getColorFromString(String color) {
        if (StringUtils.isEmpty(color) || StringUtils.equalsIgnoreCase(color, "null" )) {
            return DEFAULT_COLOR;
        }
        // основание для шестандцатиричной системы исчислений
        final int hexRadix = 16;

        int length = color.length();

        //если первый символ '#' - значит цвет представлен в виде #rgb либо #rrggbb
        if (length > 0 && color.charAt(0) == GlobalData.NUMBER_CHAR) {
            //цвет представлен в виде #rgb переведем это представление в #rrggbb
            if (length == COLOR_HEX_MIN_LEN) {
                // просто дублируем каждую составляющую цвета
                color = new String(
                        new char[] { GlobalData.NUMBER_CHAR, color.charAt(1), color.charAt(1), color.charAt(2),
                                color.charAt(2), color.charAt(3), color.charAt(3) });
                length = COLOR_HEX_FULL_LEN;
            }
            //цвет представлен в виде #rrggbb
            if (length == COLOR_HEX_FULL_LEN) {
                // получаем красную составляющую
                int red = Integer.valueOf(color.substring(1, 3), hexRadix);
                // получаем зеленую составляющую
                int green = Integer.valueOf(color.substring(3, 5), hexRadix);
                // получаем синюю составляющую
                int blue = Integer.valueOf(color.substring(5, 7), hexRadix);

                // теперь остается только вернуть значение цвета
                return new Color(red, green, blue);
            }
            length = 0;
        }
        if (length >= COLOR_RGB_MIN_LEN && color.startsWith(GlobalData.RGB)) {
            //если значение начинается с "rgb" - значит цвет представлен в виде rgb(0,0,0) или rgb(0%,0%,0%)
            color = color.substring(4, color.length() - 5); // удаление rgb( и )
            String[] rgb = StringUtils.split(color, GlobalData.COMMA_CHAR);
            if (rgb.length == 3) {
                // получаем красную составляющую
                float red = HelpFuncs.isPercentValue(rgb[0])
                        ? HelpFuncs.getPercentValueAsFloat(rgb[0]) * 255 / 100
                        : HelpFuncs.getValueFromString(rgb[0]);
                // получаем зеленую составляющую
                float green = HelpFuncs.isPercentValue(rgb[1])
                        ? HelpFuncs.getPercentValueAsFloat(rgb[1]) * 255 / 100
                        : HelpFuncs.getValueFromString(rgb[1]);
                // получаем синюю составляющую
                float blue = HelpFuncs.isPercentValue(rgb[2])
                        ? HelpFuncs.getPercentValueAsFloat(rgb[2]) * 255 / 100
                        : HelpFuncs.getValueFromString(rgb[2]);
                // составляем цвет
                return new Color((int)red, (int)green, (int)blue);
            }
            length = 0;
        }
        if (length > 0) {
            // теперь можно попробовать узнать совпадает ли значение цвета со значениями определенными в системе
            Color returnValue = Color.decode(color);
            if (null != returnValue) {
                return returnValue;
            }
        }
        // если мы добрались до этой строки - значит цвет был неверно задан.
        return DEFAULT_COLOR;
    }
}
