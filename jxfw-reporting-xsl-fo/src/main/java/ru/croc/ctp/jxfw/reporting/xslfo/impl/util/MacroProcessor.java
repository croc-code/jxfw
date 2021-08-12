package ru.croc.ctp.jxfw.reporting.xslfo.impl.util;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.formatters.ReportFormatterData;

/**
 * Класс выполняет макроподстановку значений параметров отчета, столбцов из БД, переменных отчета, псевдопеременных.
 * Created by vsavenkov on 06.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class MacroProcessor {

    /**
     * Набор обрабатываемых данных.
     */
    private ReportFormatterData reportFormatterData;

    /// <summary>
    /// Текущее значение
    /// </summary>
    public Object getCurrentValue() {
        return reportFormatterData.getCurrentValue();
    }

    /**
     * Устаавливает текущее значение.
     * @param currentValue устанавливаемое значение
     */
    public void setCurrentValue(Object currentValue) {
        reportFormatterData.setCurrentValue(currentValue);
    }

    /**
     * Конструктор.
     *
     * @param reportParams - Коллекция параметров отчета
     */
    public MacroProcessor(ReportParams reportParams) {
        this(new ReportLayoutData(null, reportParams, null, null, null), null);
    }

    /**
     * Конструктор.
     *
     * @param layoutData - Данные лэйаута
     * @param value      - Текущее значение
     */
    public MacroProcessor(ReportLayoutData layoutData, Object value) {
        this(new ReportFormatterData(layoutData, value));
    }

    /**
     * Конструктор. При обработке меняется data.CurrentValue!
     *
     * @param data - Набор обрабатываемых данных
     */
    public MacroProcessor(ReportFormatterData data) {
        this.reportFormatterData = data;
    }

    /**
     * Конструктор.
     * @param data          - Набор обрабатываемых данных
     * @param isCloneData   - При true делается копия данных, чтобы при обработке не менялось значение CurrentValue у
     *                      исходных данных
     */
    public MacroProcessor(ReportFormatterData data, boolean isCloneData) {

        if (isCloneData) {
            this.reportFormatterData = data.clone();
        } else {
            this.reportFormatterData = data;
        }
    }

    /**
     * Инициализирует CurrentValue и выполняет необходимые макроподстановки.
     * Пример шаблона:
     * "{xml @SomeParam}{html format() #SomeDbFiels}{url $Now}"
     * @param source    - Исходная строка
     * @param curValue  - Текущее значение
     * @return String   - Результат замены
     */
    public String setAndProcess(String source, Object curValue) {
        setCurrentValue(curValue);
        return process(source);
    }

    /**
     * Выполняет необходимые макроподстановки.
     * Пример шаблона:
     * "{xml @SomeParam}{html format() #SomeDbFiels}{url $Now}"
     * @param source исходная строка
     * @return результат замены
     */
    public String process(String source) {
        if (source == null || source.length() == 0) {
            setCurrentValue(null);
            return source;
        }
        // TODO: переделать
        return source; //m_RegExp.Replace(source, new MatchEvaluator(MatchEvaluatorMethodImplementation));
    }

    /**
     * Выполняет необходимые макроподстановки.
     * Потокобезопасен.
     * @param source        - Исходная строка
     * @param layoutData    - Данные лэйаута
     * @return String   - Результат замены
     */
    public static String process(String source, ReportLayoutData layoutData) {
        return new MacroProcessor(layoutData, null).process(source);
    }
}
