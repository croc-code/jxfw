package ru.croc.ctp.jxfw.core.export;

import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.Locale;
import java.util.TimeZone;


/**
 * Сервис форматирования данных для экспорта.
 *
 * @author OKrutova
 * @since 1.6
 */
public interface ExportFormatter {

    /**
     * Отформатировать Boolean свойство.
     * @param value значение свойства
     * @param xfwStructuralFeature метаданные свойства
     * @param locale локаль клиентского запроса
     * @return форматированное значение
     */
    Object formatBoolean(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale);

    /**
     * Отформатировать целочисленное свойство.
     * @param value значение свойства
     * @param xfwStructuralFeature метаданные свойства
    * @param locale локаль клиентского запроса
     * @return форматированное значение
     */
    Object formatInteger(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale);

    /**
     * Отформатировать числовое свойство со знаками после запятой.
     * @param value значение свойства
     * @param xfwStructuralFeature метаданные свойства
     * @param locale локаль клиентского запроса
     * @return форматированное значение
     */
    Object formatFloat(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale);

    /**
     * Отформатировать свойство- перечисление.
     * @param value значение свойства
     * @param xfwStructuralFeature метаданные свойства
    * @param locale локаль клиентского запроса
     * @return форматированное значение
     */
    Object formatEnum(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale);

    /**
     * Отформатировать свойство - дату и\или время.
     * @param value значение свойства
     * @param xfwStructuralFeature метаданные свойства
     * @param locale локаль клиентского запроса
     * @param timeZone часовой пояс клиентского запроса
     * @return форматированное значение
     */
    Object formatDateTime(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale, TimeZone timeZone);

    /**
     * Отформатировать свойство - период.
     * @param value значение свойства
     * @param xfwStructuralFeature метаданные свойства
     * @param locale локаль клиентского запроса
     * @return форматированное значение
     */
    Object formatDuration(Object value, XfwStructuralFeature xfwStructuralFeature, Locale locale);


    /**
     * Поддерживается ли данный выходной формат данным форматтером.
     * @param  format format
     * @return да\нет
     */
    boolean supportsFormat(OutputFormat format);


}
