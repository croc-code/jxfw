package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.SimpleReportCellClassEvaluatorClass;

/**
 * Формирует имя CSS-класса для форматирования ячейки.
 * Пример использования
 * <code>
 *     <!--
 *     <CellClass template="{@SomeParamWithoutUrlEncoding}{@!SomeParamWithUrlEncoding}
 *          {#SomeDbFieldWithoutUrlEncoding}{#!SomeDbFieldWithUrlEncoding}" />
 *     -->
 * </code>
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class SimpleReportCellClass extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        SimpleReportCellClassEvaluatorClass profile = (SimpleReportCellClassEvaluatorClass) formatterProfile;
        if (profile.getTemplate() != null) {
            formatterData.setClassName(new MacroProcessor(formatterData, true).process(profile.getTemplate()));
        }
    }
}
