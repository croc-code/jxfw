package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;

/**
 * Интерфейс объектов, реалзующих форматировние ячеек таблицы и их значений.
 * Created by vsavenkov on 25.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public interface IReportFormatter {

    /**
     * Производит форматирование ячейки таблицы.
     * @param formatterProfile  - Профиль
     * @param formatterData     - Набор исходных данных для форматировщика
     */
    void execute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData);
}
