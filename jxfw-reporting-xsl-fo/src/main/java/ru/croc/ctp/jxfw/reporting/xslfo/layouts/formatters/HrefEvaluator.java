package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.HrefEvaluatorClass;

/**
 * Эвалуатор для представления значения ячейки в виде ссылки.
 * Пример использования evaluator-а
 * <code>
 *     <!--
 *     <HrefEvaluator
 *          href="{@SomeParamWithoutUrlEncoding}{@!SomeParamWithUrlEncoding}
 *              {#SomeDbFieldWithoutUrlEncoding}{#!SomeDbFieldWithUrlEncoding}"
 *          [if-null="Some String Value"]
 *          [title="{@SomeParamWithoutUrlEncoding}{@!SomeParamWithUrlEncoding}
 *              {#SomeDbFieldWithoutUrlEncoding}{#!SomeDbFieldWithUrlEncoding}"]
 *          [target="{@SomeParamWithoutUrlEncoding}{@!SomeParamWithUrlEncoding}
 *              {#SomeDbFieldWithoutUrlEncoding}{#!SomeDbFieldWithUrlEncoding}"]
 *          [additional-xslfo="{@SomeParamWithoutUrlEncoding}{@!SomeParamWithUrlEncoding}
 *              {#SomeDbFieldWithoutUrlEncoding}{#!SomeDbFieldWithUrlEncoding}"]
 *          />
 *      -->
 *  </code>
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class HrefEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        HrefEvaluatorClass profile = (HrefEvaluatorClass) formatterProfile;

        // Создадим вспомогательный объект
        MacroProcessor processor = new MacroProcessor(formatterData, true);

        if (formatterData.isEmptyValue()) {
            if (StringUtils.isEmpty(profile.getIfNull())) {
                return;
            }
            formatterData.setCurrentValue(processor.process(profile.getIfNull()));
        }

        // если if-href-isnull=1, то в выводим только текст
        if (StringUtils.isNotEmpty(profile.getIfHrefIsNull())) {
            if (Converter.toBoolean(
                    processor.setAndProcess(profile.getIfHrefIsNull(), formatterData.getCurrentValue()).toString())) {
                return;
            }
        }

        // создадим объект, в который будем складывать стили
        ReportStyle rs = formatterData.getRepGen().getStylesCollection()
                .getReportStyle(processor.setAndProcess(profile.getStyleClass(), formatterData.getCurrentValue())) ;
        if (null == rs) {
            rs = new ReportStyle();
        }

        rs.put("external-destination", StringEscapeUtils.escapeHtml4(
                processor.setAndProcess(profile.getHref(), formatterData.getCurrentValue())));
        rs.put("show-destination", "new");

        if (profile.getTarget() != null) {
            rs.put("target", processor.setAndProcess(profile.getTarget(), formatterData.getCurrentValue()));
        }

        if (profile.getTitle() != null) {
            rs.put("title", StringEscapeUtils.escapeHtml4(
                    processor.setAndProcess(profile.getTitle(), formatterData.getCurrentValue())));
        }

        // Начнем заполнять буфер
        StringBuilder builder = new StringBuilder("<fo:basic-link ");
        builder.append(rs.toString());

        // это инц. 95025
        if (profile.getAdditionalXslfo() != null) {
            builder.append(processor.setAndProcess(profile.getAdditionalXslfo(), formatterData.getCurrentValue()));
        }

        builder.append(">");
        builder.append(StringEscapeUtils.escapeHtml4(formatterData.getCurrentValue().toString()));
        builder.append("</fo:basic-link>");

        formatterData.setCurrentValue(builder.toString());
    }
}
