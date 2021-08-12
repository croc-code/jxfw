package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentException;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractParamProcessorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AndClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AndNotClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.CheckParamClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.OrClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ParamDefinitionProcessorClass;

/**
 * Import from Croc.XmlFramework.ReportService .Net.2.0
 *  Обработчик параметров.
 * Проверяет заданность комбинаций параметров. Набор допустимых комбинаций
 * описывается в профиле отчета в виде логического выражения в нормальной форме
 * (ИЛИ, И, НЕ). В случае нарушения выводится сообщение об ошибке, приведенное в
 * атрибуте message узла профиля обработчика параметров. В случае отсутствия
 * атрибута выводится стандартное сообщение об ошибке: "Комбинация заданных
 * параметров недопустима!".
 */
public class ReportParamDefintionProcessor extends ReportAbstractParamProcessor {
    /**
     * Обработка параметров отчёта.
     * @param processorProfile профиль процессора
     * @param processorData данные
     */
    @Override
    protected void doProcess(AbstractParamProcessorClass processorProfile, ReportLayoutData processorData) {
        ParamDefinitionProcessorClass processor = (ParamDefinitionProcessorClass) processorProfile;
        // цикл по всем комбинациям условий на параметры
        if (processor.getCheckParams() != null) {
            // признак облома проверки комбинации параметров
            boolean failed = false;
            for (OrClass checkCondition : processor.getCheckParams().getOr()) {
                failed = processAnd(checkCondition, processorData, failed);
                failed = processAndNot(checkCondition, processorData, failed);
            }
            if (!failed) {
                return;
            }
        }
        String exceptionMessage = getExceptionMessage(processor, processorData);
        throw new ArgumentException(exceptionMessage);

    }

    private boolean processAnd(OrClass checkCondition, ReportLayoutData processorData, boolean failed) {
        // для каждой комбинации условий проходим по обязательным параметрам
        if (checkCondition.getAnd() != null) {
            for (AndClass andCondition : checkCondition.getAnd()) {
                if (andCondition.getCheckParam() != null) {
                    for (CheckParamClass node : andCondition.getCheckParam()) {
                        String paramName = node.getN() == null ? StringUtils.EMPTY : node.getN();
                        // если параметр не задан - ошибка
                        if (processorData.getParams().isEmptyParam(paramName)) {
                            failed = true;
                        }
                    }
                }
            }
        }
        return failed;
    }

    private boolean processAndNot(OrClass checkCondition, ReportLayoutData processorData, boolean failed) {
        // для каждой комбинации условий проходим по параметрам,
        // которых не должно быть
        if (checkCondition.getAndNot() != null) {
            for (AndNotClass andNot : checkCondition.getAndNot()) {
                if (andNot.getCheckParam() != null) {
                    for (CheckParamClass node : andNot.getCheckParam()) {
                        String paramName = node.getN() == null ? StringUtils.EMPTY  : node.getN();
                        // если параметр задан - ошибка
                        if (!processorData.getParams().isEmptyParam(paramName)) {
                            failed = true;
                        }
                    }
                }
            }
        }
        return failed;
    }

    private String getExceptionMessage(ParamDefinitionProcessorClass processor, ReportLayoutData processorData) {
        String exceptionMessage;
        if (processor.getMessage() != null && processor.getMessage().length() != 0) {
            exceptionMessage = MacroProcessor.process(processor.getMessage(), processorData);
        } else {
            exceptionMessage = "Комбинация заданных параметров недопустима!";
        }
        return exceptionMessage;
    }
}

