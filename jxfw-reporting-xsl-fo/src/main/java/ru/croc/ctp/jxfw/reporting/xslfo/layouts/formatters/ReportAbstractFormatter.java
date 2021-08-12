package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;

/**
 * Абстрактный класс.
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public abstract class ReportAbstractFormatter implements IReportFormatter {

    /**
     * Производит форматирование ячейки таблицы.
     * @param formatterProfile  - Профиль
     * @param formatterData     - Набор исходных данных для форматировщика
     */
    public void execute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {
        doExecute(formatterProfile, formatterData);
    }

    /**
     * Производит форматирование ячейки таблицы.
     * @param formatterProfile  - Профиль
     * @param formatterData     - Набор исходных данных для форматировщика
     */
    protected abstract void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData);
}
