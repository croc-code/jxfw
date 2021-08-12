package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors;

import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractParamProcessorClass;

/**
 * Абстрактный обработчик параметров.
 */
public abstract class ReportAbstractParamProcessor implements IReportParamProcessor {
    @Override
    public void process(AbstractParamProcessorClass processorProfile, ReportLayoutData processorData) {
        doProcess(processorProfile, processorData);
    }

    /**
     * Абстрактная реализация.
     * @param processorProfile профиль процессора
     * @param processorData данные
     */
    protected abstract void doProcess(AbstractParamProcessorClass processorProfile, ReportLayoutData processorData);
}
