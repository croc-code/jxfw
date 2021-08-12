package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.EmptyValueEvaluatorClass;

/**
 * Преобразует пустое значение в то, что надо.
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class EmptyValueEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        EmptyValueEvaluatorClass profile = (EmptyValueEvaluatorClass) formatterProfile;

        if (formatterData.isEmptyValue()) {
            formatterData.setCurrentValue(new MacroProcessor(formatterData).process(profile.getValue()));
        }
    }
}
