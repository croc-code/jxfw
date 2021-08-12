package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.DateTimeEvaluatorClass;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Эвалуатор для представления даты/времени в виде строки.
 * Если подлежащее форматированию значение значение null то возвращается значение аттрибута
 * if-null профиля целевой формат задается аттрибутом format профиля документацию на
 * формат аттрибута format см. MSDN "DateTimeFormatInfo class, about DateTimeFormatInfo class"
 * Пример использования:
 * <date-time-evaluator format="G" if-null="-"/>
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class DateTimeEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        DateTimeEvaluatorClass profile = (DateTimeEvaluatorClass) formatterProfile;

        if (formatterData.isEmptyValue()) {
            if (profile.getIfNull() != null) {
                formatterData.setCurrentValue(profile.getIfNull());
            } else {
                formatterData.setCurrentValue(StringUtils.EMPTY);
            }
        } else {
            try {
                Locale ci = Locale.getDefault();
                String localizedValue = String.format(ci, "%s", formatterData.getCurrentValue());
                if (StringUtils.isEmpty(profile.getFormat())) {
                    formatterData.setCurrentValue(localizedValue);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(profile.getFormat(), ci);
                    formatterData.setCurrentValue(dateFormat.format(localizedValue));
                }

            } catch (Exception e) {
                formatterData.setCurrentValue(formatterData.getCurrentValue().toString());
            }
        }
    }
}
