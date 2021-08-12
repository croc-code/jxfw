package ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.meta.ReportObjectThreadSafe;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractFormatterClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.StringEvaluatorClass;

/**
 * Форматирование строки.
 * Реализовано:
 * 1) замена подстроки новым значением (в т.ч. значением параметра
 * {@literal {@param-name}} и значением из рекордсета {#rs-column-name})
 * 2) добавление подстроки к исходной спереди / сзади
 * 3) проставление значения в случае пустоты исходной строки
 * атрибуты:
 * message - текст ошибки
 * original-value - подстрока, подлежащая замене
 * replace-with - либо новое значение заменяемой подстроки,
 *      либо наименование параметра, значение которого будет им являться
 *      либо наименование колонки рекордсета, значение которой (в переданной эвалуатору строке) будет им являться
 *      что именно - определяется наличем префиксов @, #, ! (см. MacroProcessor)
 * append-before - значение строки (либо наименование параметра, либо наименование столбца рекордсета)
 *      добавляемое к исходной спереди
 *      что именно - определяется наличем префиксов @, #, ! (см. MacroProcessor)
 * append-after - значение строки (либо наименование параметра, либо наименование столбца рекордсета)
 *      добавляемое к исходной сзади
 *      что именно - определяется наличем префиксов @, #, ! (см. MacroProcessor)
 * if-empty - значение строки (либо наименование параметра, либо наименование столбца рекордсета)
 *      в случае пустоты исходной строки (либо равенста null или DbNull)
 *      что именно - определяется наличем префиксов @, #, ! (см. MacroProcessor)
 * Created by vsavenkov on 05.05.2017.Import from Croc.XmlFramework.ReportService .Net.2.0
 */
@ReportObjectThreadSafe
public class StringEvaluator extends ReportAbstractFormatter {

    @Override
    protected void doExecute(AbstractFormatterClass formatterProfile, ReportFormatterData formatterData) {

        StringEvaluatorClass profile = (StringEvaluatorClass) formatterProfile;

        /*
            Создадим вспомогательный объект для обработки атрибутов
            профиля при помощи регулярного выражения
        */
        MacroProcessor processor = new MacroProcessor(formatterData, true);

        if (formatterData.isEmptyValue()) {
            if (profile.getIfEmpty() != null) {
                formatterData.setCurrentValue(processor.process(profile.getIfEmpty()));
            }
            return;
        }

        // значение, на которое заменяется подстрока
        String replaceWith = profile.getReplaceWith() != null ? processor.process(profile.getReplaceWith())
                : StringUtils.EMPTY;

        // значение строки после реплэйса
        String newValue = formatterData.getCurrentValue().toString();
        if (profile.getOriginalValue() != null) {
            newValue = newValue.replace(profile.getOriginalValue(), replaceWith);
        }

        // строка приписываемая спереди
        String appendBefore = profile.getAppendBefore() != null ? processor.process(profile.getAppendBefore())
                : StringUtils.EMPTY;

        // строка приписываемая сзади
        String appendAfter = profile.getAppendAfter() != null ? processor.process(profile.getAppendAfter())
                : StringUtils.EMPTY;

        formatterData.setCurrentValue(appendBefore + newValue + appendAfter);
    }
}
