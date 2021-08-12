package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.utility;

import com.aspose.cells.Color;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

import java.math.BigDecimal;

/**
 * Утилитный класс. Основное назначение - это упрощение чтения значений аттрибутов.
 * Created by vsavenkov on 22.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class Utils {

    /**
     * Конвертация из точек в см.
     * @param pixelsCount - Количество точек
     * @return double   - возвращает размер в см
     */
    public static double pixelsToCm(float pixelsCount) {
        return round(pixelsCount / GlobalData.CM_PIXELS_CONVERT_RATIO, 2);
    }

    /**
     * Округление дробного.
     * Содрано с
     * "https://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java"
     * @param doubleValue      - округляемое значение
     * @param numberOfDecimals - кол-во цифр
     * @return double   - возвращает округлённое дробное
     */
    private static double round(double doubleValue, int numberOfDecimals) {

        if ( doubleValue > 0) {
            return BigDecimal.valueOf(doubleValue).setScale(numberOfDecimals, BigDecimal.ROUND_FLOOR).doubleValue();
        } else {
            return BigDecimal.valueOf(doubleValue).setScale(numberOfDecimals, BigDecimal.ROUND_CEILING).doubleValue();
        }
    }

    /**
     * Переводит цвет из объекта Aspose в системный объект.
     * @param asposeColor - цвет Aspose
     * @return Color    - возвращает системный цвет
     */
    public static java.awt.Color encodeColor(Color asposeColor) {
        return new java.awt.Color(asposeColor.toArgb());
    }

    /**
     * Переводит цвет из системного в объект Aspose.
     * @param systemColor - системный цвет
     * @return Color    - возвращает цвет Aspose
     */
    public static Color decodeColor(java.awt.Color systemColor) {
        return Color.fromArgb(systemColor.getRGB());
    }
}
