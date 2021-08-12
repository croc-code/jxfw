package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors;

import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.Converter;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractParamProcessorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ParamByParamProcessorClass;

import java.util.Objects;

/**
 * Производит подстановку значения параметру на основании значения (или его отсутствия)
 * другого параметра. Реализовано 4 варианта поведения:
 * 1) проставление занчения параметру, в том случае, если некоторый параметр не инициализирован (is null)
 * 2) проставление занчения параметру, в том случае, если некоторый параметр инициализирован (not is null)
 * 3) проставление занчения параметру, в том случае, если некоторый параметр равен некоторому значению (в т.ч.
 *  сравнение по подстроке)
 * 4) проставление значения параметру, в том случае, если некоторый
 *  параметр НЕ равен некоторому значению (в т.ч. сравнение по подстроке)
 * */
public class ParamByParamProcessor extends ReportAbstractParamProcessor {
    @Override
    protected void doProcess(AbstractParamProcessorClass processorProfile, ReportLayoutData processorData) {
        // param-by-param processor
        ParamByParamProcessorClass processor = (ParamByParamProcessorClass) processorProfile;
        ReportParams params = processorData.getParams();
        // проход по узлам
        for (Object node : processor.getIfNullParamOrIfNotNullParamOrIfParamIs()) {
            setIfNullParams(node, params);
            setIfNotNullParams(node, params);
            setIfParamIs(node, params, processorData);
            setIfParamNotIs(node, params, processorData);
        }
    }

    private void setIfNullParams(Object node, ReportParams params ) {
        // если это "if-null-param"
        if (node instanceof ParamByParamProcessorClass.IfNullParamClass) {
            ParamByParamProcessorClass.IfNullParamClass ifNullParam =
                    (ParamByParamProcessorClass.IfNullParamClass) node;
            // если значение параметра не установлено
            // выставляем значение другому параметру
            if (params.isEmptyParam(ifNullParam.getN())) {
                params.getParam(ifNullParam.getSetParamN()).parse(ifNullParam.getSetParamValue());
            }
        }
    }

    private void setIfNotNullParams(Object node, ReportParams params) {
        // если это "if-not-null-param"
        if (node instanceof ParamByParamProcessorClass.IfNotNullParamClass) {
            ParamByParamProcessorClass.IfNotNullParamClass ifNotNullParam =
                    (ParamByParamProcessorClass.IfNotNullParamClass) node;
            // если значение параметра установлено
            // выставляем значение другому параметру
            if (!params.isEmptyParam(ifNotNullParam.getN())) {
                params.getParam(ifNotNullParam.getSetParamN()).parse(ifNotNullParam.getSetParamValue());
            }
        }
    }

    private void setIfParamIs(Object node, ReportParams params, ReportLayoutData processorData) {
        // если это "if-param-is"
        if (node instanceof ParamByParamProcessorClass.IfParamIsClass) {
            ParamByParamProcessorClass.IfParamIsClass ifParamIs = (ParamByParamProcessorClass.IfParamIsClass) node;

            // если значение параметра установлено и равно некоторому значению
            // выставляем "другое некоторое" значение другому параметру
            if (!params.isEmptyParam(ifParamIs.getN()) && !ifParamIs.isUseSubstrValue()) {
                // параметр
                ReportParams.ReportParam rp = params.getParam(ifParamIs.getN());
                if (Objects.equals(Converter.toObject(rp.value().toString(), rp.getXmlType()),
                        Converter.toObject(ifParamIs.getValue(), rp.getXmlType()))) {
                    params.getParam(ifParamIs.getSetParamN()).parse(ifParamIs.getSetParamValue());
                }
            }
            // тоже самое - срванение подстроки
            // выставляем "другое некоторое" значение другому параметру
            if (!processorData.getParams().isEmptyParam(ifParamIs.getN())
                    && ifParamIs.isUseSubstrValue()
                    && 0 <= params.getParam(ifParamIs.getN()).toString().indexOf(ifParamIs.getValue())) {
                params.getParam(ifParamIs.getSetParamN()).parse(ifParamIs.getSetParamValue());
            }
        }
    }

    private void setIfParamNotIs(Object node, ReportParams params, ReportLayoutData processorData) {
        // если это "if-param-not-is"
        if (node instanceof ParamByParamProcessorClass.IfParamNotIsClass) {
            ParamByParamProcessorClass.IfParamNotIsClass ifParamNotIs =
                    (ParamByParamProcessorClass.IfParamNotIsClass) node;
            // если значение параметра установлено и равно некоторому значению
            // выставляем "другое некоторое" значение другому параметру
            if (!processorData.getParams().isEmptyParam(ifParamNotIs.getN())
                    && !ifParamNotIs.isUseSubstrValue()) {
                ReportParams.ReportParam rp = params.getParam(ifParamNotIs.getN());
                if (!Objects.equals(Converter.toObject(rp.value().toString(), rp.getXmlType()),
                        Converter.toObject(ifParamNotIs.getValue(), rp.getXmlType()))) {
                    params.getParam(ifParamNotIs.getSetParamN()).parse(ifParamNotIs.getSetParamValue());
                }
            }

            // тоже самое - сравнение подстроки
            // выставляем "другое некоторое" значение другому параметру
            if (!params.isEmptyParam(ifParamNotIs.getN())
                    && ifParamNotIs.isUseSubstrValue()
                    && -1 == params.getParam(ifParamNotIs.getN()).toString().indexOf(ifParamNotIs.getValue())) {
                params.getParam(ifParamNotIs.getSetParamN()).parse(ifParamNotIs.getSetParamValue());
            }
        }
    }
}
