package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors;

import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractParamProcessorClass;

/**
 * Обработчик параметров: логические проверки и дополнительная обработка.
 * Created by vsavenkov on 13.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public interface IReportParamProcessor {
    /**
     * Собственно обработка параметров отчета.
     * @param processorProfile  - Профиль процессора
     * @param processorData     - Данные лэйаута
     */
    void process(AbstractParamProcessorClass processorProfile, ReportLayoutData processorData);
}
