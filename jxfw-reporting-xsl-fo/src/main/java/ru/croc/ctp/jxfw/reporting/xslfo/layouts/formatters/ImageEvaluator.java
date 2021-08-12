package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import org.apache.commons.lang3.StringEscapeUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.style.ReportStyle;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ImageEvaluatorClass;

/**
 * Вставляет тэг img для отображения "картиночного" свойства объекта.
 * Атрибуты:
 * hide-if-null     -    признак неформирования в случае постого значения колонки
 * src              -    значение атрибута src элемента <img/>
 * height           -    значение атрибута height элемента <img/>
 * width            -    значение атрибута width элемента <img/>
 * border           -    значение атрибута border элемента <img/>
 * alt              -    значение атрибута alt элемента <img/>
 * title            -    значение атрибута title элемента <img/>
 * additional-xslfo -    дополнительные атрибуты тега <img/>
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class ImageEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        ImageEvaluatorClass profile = (ImageEvaluatorClass) formatterProfile;

        if (profile.isHideIfNull() && formatterData.isEmptyValue()) {
            return;
        }

        // Создадим вспомогательный объект
        MacroProcessor processor = new MacroProcessor(formatterData, true);

        // создадим объект, в который будем складывать стили
        ReportStyle rs = formatterData.getRepGen().getStylesCollection()
                .getReportStyle(processor.process(profile.getStyleClass()));
        if (null == rs) {
            rs = new ReportStyle();
        }

        rs.put("src", StringEscapeUtils.escapeHtml4(processor
                .setAndProcess(profile.getSrc(), formatterData.getCurrentValue())));

        if (profile.getHeight() != null) {
            rs.put("content-height", processor.setAndProcess(profile.getHeight(), formatterData.getCurrentValue()));
        }

        if (profile.getWidth() != null) {
            rs.put("content-width", processor.setAndProcess(profile.getWidth(), formatterData.getCurrentValue()));
        }

        if (profile.getBorder() != null) {
            rs.put("border", processor.setAndProcess(profile.getBorder().toString(), formatterData.getCurrentValue()));
        }

        if (profile.getAlt() != null) {
            rs.put("alt", processor.setAndProcess(profile.getAlt(), formatterData.getCurrentValue()));
        }

        if (profile.getTitle() != null) {
            rs.put("title", StringEscapeUtils.escapeHtml4(
                    processor.setAndProcess(profile.getTitle(), formatterData.getCurrentValue())));
        }

        // Начнем заполнять буфер
        StringBuilder builder = new StringBuilder("<fo:external-graphic ");
        builder.append(rs.toString());

        // DEBUG: это инц. 95025
        if (profile.getAdditionalXslfo() != null) {
            builder.append(processor.setAndProcess(profile.getAdditionalXslfo(), formatterData.getCurrentValue()));
        }

        builder.append("/>");
        formatterData.setCurrentValue(builder.toString());
    }
}
