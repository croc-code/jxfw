package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.logging;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.FoPropertyType;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.AreaRectangle;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout.Range;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.IArea;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.core.element.area.text.InlineArea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Хелпер для подготовки диагностической информации для IArea.
 */
public class AreaLogHelper {

    /**
     * Рекурсивный метод для записи в журнал всех свойств области.
     *
     * @param area - Область
     * @return запись для лога
     */
    public static String getAreaPropertiesForLog(IArea area) {
        StringBuilder sb = new StringBuilder();
        InlineArea inlineArea = area instanceof InlineArea ? (InlineArea) area : null;
        sb.append("Свойства области: ");
        sb.append(area.getAreaType().getFoName()
                + (inlineArea == null ? StringUtils.EMPTY : " Text='" + inlineArea.getText() + "'"));
        sb.append(SystemUtils.LINE_SEPARATOR);

        if (area.getProperties() == null) {
            return sb.toString();
        }

        for (Map.Entry<FoPropertyType, Object> entry : area.getProperties().entrySet()) {
            sb.append("-- Имя: ");
            sb.append(entry.getKey());
            sb.append(" Значение: ");
            sb.append(entry.getValue());
            sb.append(SystemUtils.LINE_SEPARATOR);
        }

        return sb.toString();
    }

    /**
     * Метод записи в журнал строки пары данных в виде: '[ВРЕМЯ]   :ЗНАЧЕНИЕ'.
     *
     * @param value - Строка названия пары данных
     * @return запись для лога
     */
    public static String getValueWithTimestampForLog(String value) {
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
        String formattedTime = format.format(time);
        return "[" + formattedTime + "]   :" + value;
    }

    /**
     * Метод для записи в журнал координат всех областей и подобластей.
     *
     * @param area - Область
     * @return запись для лога
     */
    public static String getAreaRangesForLog(IArea area) {
        StringBuilder sb = new StringBuilder();
        fillAreaRanges(area, sb);
        return sb.toString();
    }

    /**
     * Метод для записи в журнал координат всех областей и подобластей.
     *
     * @param area {@link IArea}
     * @return запись для лога
     */
    public static String getAreaCoordinatesForLog(IArea area) {
        StringBuilder sb = new StringBuilder();
        fillAreaCoordinates(area, sb);
        return sb.toString();
    }

    /**
     * Рекурсивный метод для записи в журнал координат всех областей и подобластей.
     *
     * @param area - Область
     */
    private static void fillAreaCoordinates(IArea area, final StringBuilder sb) {

        AreaRectangle rectangle = area.getBorderRectangle();
        InlineArea inlineArea = area instanceof InlineArea ? (InlineArea) area : null;

        sb.append("Область: ");
        sb.append(area.getAreaType().getFoName());
        sb.append(" -- X=");
        sb.append(rectangle.getX());
        sb.append(", Y=");
        sb.append(rectangle.getY());
        sb.append(", Width=");
        sb.append(rectangle.getWidth());
        sb.append(", Height=");
        sb.append(rectangle.getHeight());
        sb.append((inlineArea == null ? " " : " Text='" + inlineArea.getText() + "' "));
        sb.append(area.getProgressionDirection());
        sb.append(SystemUtils.LINE_SEPARATOR);

        if (area.getChildrenList() != null) {
            for (IArea childArea : area.getChildrenList()) {
                fillAreaCoordinates(childArea, sb);
            }
        }
    }

    /**
     * Рекурсивный метод для записи в журнал координат всех областей и подобластей.
     *
     * @param area - Область
     */
    private static void fillAreaRanges(IArea area, final StringBuilder sb) {

        Range range = area.getBorderRange();

        sb.append("Область: ");
        sb.append(area.getAreaType().getFoName());
        if (range != null) {
            sb.append(" -- X=");
            sb.append(range.getX());
            sb.append(", Y=");
            sb.append(range.getY());
            sb.append(", Width=");
            sb.append(range.getWidth());
            sb.append(", Height=");
            sb.append(range.getHeight());
        }

        InlineArea inlineArea = area instanceof InlineArea ? (InlineArea) area : null;
        sb.append((inlineArea == null ? StringUtils.EMPTY : " Text='" + inlineArea.getText() + "'"));
        sb.append(SystemUtils.LINE_SEPARATOR);

        if (area.getChildrenList() != null) {
            for (IArea childArea : area.getChildrenList()) {
                fillAreaRanges(childArea, sb);
            }
        }
    }


}
