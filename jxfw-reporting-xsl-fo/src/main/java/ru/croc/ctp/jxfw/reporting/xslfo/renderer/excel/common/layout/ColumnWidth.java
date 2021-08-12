package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.layout;

import ru.croc.ctp.jxfw.reporting.xslfo.renderer.core.common.HelpFuncs;
import ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel.common.GlobalData;

/**
 * Класс, инкапсулирующий ширину колонки таблицы.
 * Immutable
 * Created by vsavenkov on 25.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class ColumnWidth extends Dimension {

    /**
     * Признак - ширина колонки - пропорциональная величина.
     */
    private boolean proportional;

    /**
     * Свойство - признак пропорциональности ширины колонки.
     * @return boolean  - возвращает true, если ширины колонки пропорциональны и false в противном случае
     */
    public boolean isProportional() {
        return proportional;
    }

    /**
     * Инициализирующий конструктор.
     * @param value          - Значение ширины колонки
     * @param percentage   - Признак - задана ли ширина колонки в процентах
     * @param proportional - Признак - ширина колонки - пропорциональная величина
     */
    private ColumnWidth(float value, boolean percentage, boolean proportional) {
        super(value, percentage);
        this.proportional = proportional;
    }

    /**
     * Инициализирующий конструктор.
     * @param value - Значение ширины колонки
     */
    private ColumnWidth(String value) {
        super(value);
    }

    /**
     * Возвращает новый экземпляр объекта ширины колонки.
     * Значение ширины колонки задано в виде строки
     * @param value - Значение ширины колонки в виде строки
     * @return ColumnWidth - возвращает новый экземпляр объекта ширины колонки.
     */
    public static ColumnWidth getInstance(String value) {
        // Проверяем наличие функции proportional-column-width в значении ширины колонки
        return value.indexOf(GlobalData.PROPORTIONAL_COLUMN_WIDTH) >= 0
                ? new ColumnWidth(HelpFuncs.getProportionalWidth(value), false, true)
                : new ColumnWidth(value);
    }

    /**
     * Сравнение двух ширин колонок.
     * @param width - Сравниваемая ширина колонки
     * @return boolean - возвращает true, если ширины эквивалентны и false в противном случае
     */
    public boolean isEquals(ColumnWidth width) {

        if (null == width) {
            return false;
        }

        if (!(isDefined() && width.isDefined())) {
            return isDefined() ^ width.isDefined();
        }

        return width.value == value
                && width.isPercentage() == isPercentage()
                && width.proportional == proportional;
    }

    /**
     * Сравнение размерности с объектом, предположительно являющимся тоже шириной колонки.
     * @param obj - сравниваемый объект
     * @return boolean - возвращает true, если объекты эквивалентны и false в противном случае
     */
    @Override
    public boolean equals(Object obj) {
        return isEquals(obj instanceof ColumnWidth ? (ColumnWidth)obj : null);
    }

    /**
     *  Хэш-код объекта.
     * @return int  - возвращает хэш-код объекта
     */
    @Override
    public int hashCode() {
        // TODO: нафига это надо? Оно и так вызовется...
        return super.hashCode();
    }

    /* TODO: по-моему это лишнее
    /// <summary>
    /// Перегруженный оператор эквивалентности
    /// </summary>
    /// <param name="oDim1"></param>
    /// <param name="oDim2"></param>
    /// <returns></returns>
    public static boolean operator ==(ColumnWidth oDim1, ColumnWidth oDim2)
    {
        // Если это 2 null или один и тот же объект, сразу выходим с успехом
        if (ReferenceEquals(oDim1, oDim2))
            return true;
        // Проверяем, что 1й объект не является null
        if (ReferenceEquals(oDim1, null))
            return false;
        return oDim1.isEquals(oDim2);
    }

    /// <summary>
    /// Перегруженный оператор неэквивалентности
    /// </summary>
    /// <param name="oDim1"></param>
    /// <param name="oDim2"></param>
    /// <returns></returns>
    public static boolean operator !=(ColumnWidth oDim1, ColumnWidth oDim2)
    {
        return !(oDim1 == oDim2);
    }
    */
}
