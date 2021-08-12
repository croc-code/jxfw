package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.TemplateEvaluatorClass;

/**
 * Вставляет произвольную строку по шаблону.
 * Возможно использование полей из источника данных {#field-name},
 * значений параметров {@literal {@param-name}} и значений переменных фрагмента отчета {%variable-name}
 * Created by vsavenkov on 05.05.2017.Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class TemplateEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        String templateString;    // Строка шаблона

        TemplateEvaluatorClass profile = (TemplateEvaluatorClass) formatterProfile;

        if (formatterData.isEmptyValue() && profile.getNullTemplate() != null) {
            templateString = profile.getNullTemplate();
        } else {
            templateString = profile.getTemplate();
        }

        formatterData.setCurrentValue(new MacroProcessor(formatterData).process(templateString));
    }
}
