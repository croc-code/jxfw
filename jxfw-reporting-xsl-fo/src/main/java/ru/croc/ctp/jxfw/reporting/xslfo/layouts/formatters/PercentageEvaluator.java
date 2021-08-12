package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.PercentageEvaluatorClass;

/**
 * Эвалуатор для подсчета процентов.
 * атрибуты:
 * value - текущее значение (либо явно, либо из параметра {@literal {@parameter-name}},
 * либо из RS {#rs-column-name})
 * total - значение 100% (либо явно, либо из параметра, либо из RS)
 * when-total-is-zero - необязательный атрибут, значение, возвращаемое в случае равенства делителя нулю (либо null)
 * when-value-is-zero - необязательный атрибут, значение, возвращаемое в случае равенства значения процента нулю
 *      (либо null)
 * rounded-digits - необязательный атрибут, количество цифр после запятой после округления
 * need-percent-symbol - необязательный атрибут, признак приписывания символа "%"
 * replace-substring - необязательный атрибут, значение подстроки подлежащее замене
 *      вычисленным процентом (для использования в заголовке отчета)
 * total - значение 100% (либо явно, либо из параметра, либо из RS)
 * when-total-is-zero - необязательный атрибут, значение, возвращаемое в случае равенства делителя нулю (либо null)
 * rounded-digits - необязательный атрибут, количество цифр после запятой после округления
 * need-percent-symbol - необязательный атрибут, признак приписывания символа "%"
 * replace-substring - необязательный атрибут, значение подстроки подлежащее замене вычисленным процентом
 *      (для использования в заголовке отчета)
 * Created by vsavenkov on 05.05.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class PercentageEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        PercentageEvaluatorClass profile = (PercentageEvaluatorClass) formatterProfile;

        // Создадим вспомогательный объект для обработки атрибутов профиля при помощи регулярного выражения
        MacroProcessor processor = new MacroProcessor(formatterData);

        // 2 значения для вычисления процента
        double val = Converter.toDouble(processor.process(profile.getValue()), 0F);
        double total = Converter.toDouble(processor.process(profile.getTotal()), 0F);

        // если делитель нулевой - берем значение из профиля
        if (total == 0) {
            // если этот случай обрабатывается в профиле
            if (profile.getWhenTotalIsZero() != null) {
                formatterData.setCurrentValue(profile.getWhenTotalIsZero());
                return;
            } else {
                throw new IllegalArgumentException("Деление на 0 при вычислении процента");
            }
        }

        // значение процента
        double percentageValue;
        StringBuilder res = new StringBuilder();

        // вычисляем
        try {
            /* TODO: пока не понял где это может задаваться
            if (oProfile.roundeddigitsSpecified)
            {
                int n = (int)oProfile.roundeddigits;
                PercentageValue = Math.Round(100 * val / total, n);
                sRes.Append(PercentageValue.ToString());
                double denom = Math.Round(PercentageValue - Math.Floor(PercentageValue), n);
                int k = 0;
                if (denom == 0)
                {
                    // добавляем десятичный разделитель
                    sRes.Append(System.Threading.Thread.CurrentThread.CurrentCulture.NumberFormat
                            .NumberDecimalSeparator);
                    k = 1;
                }
                for (int i = 0; i < 2 - k + n - (denom.ToString()).Length; i++)
                {
                    sRes.Append('0');
                }
            }
            else
            */
            {
                percentageValue = 100 * val / total;
                res.append(Double.toString(percentageValue));
            }
        } catch (Exception e) {
            // К сожалению проверка на 0 не всегда спасает.
            // В принципе в данном коде только и может произойти ошибка
            // переполнения - потому просто задавим её
            if (profile.getWhenTotalIsZero() != null) {
                formatterData.setCurrentValue(profile.getWhenTotalIsZero());
                return;
            } else {
                if (profile.getMessage() != null) {
                    throw new ReportException(profile.getMessage());
                } else {
                    throw new ReportException("Ошибка при определении значения процента");
                }
            }
        }

        if (profile.getWhenValueIsZero() != null && 0 == percentageValue) {
            formatterData.setCurrentValue(profile.getWhenValueIsZero());
            return;
        }

        // если в профиле эвалуатора есть атрибут replace-substring,
        // то значением эвалуатора будет являться строка
        // начало_строки_replace-substring_конец строки
        // (с подстановкой значения процента вместо значения атрибута
        // replace-substring)
        if (profile.getReplaceSubstring() != null) {
            /* TODO: пока не понял где это может задаваться
            if (oProfile.needpercentsymbolSpecified)
                data.CurrentValue = data.CurrentValue.ToString().Replace(oProfile.replacesubstring, sRes.Append('%')
                        .ToString());
            else
            */
            formatterData.setCurrentValue(formatterData.getCurrentValue().toString()
                .replace(profile.getReplaceSubstring(), res.toString()));
        } else {
            /* TODO: пока не понял где это может задаваться
            if (oProfile.needpercentsymbolSpecified)
                data.CurrentValue = sRes.Append('%').ToString();
            else
            */
            formatterData.setCurrentValue(res.toString());
        }
    }
}
