package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors;

import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractParamProcessorClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.FillParamsProcessorClass;

import java.util.Map;

/**
 * Import from Croc.XmlFramework.ReportService .Net.2.0
 * Обработчик параметров, заполняющий приведенные в профиле параметры отчета
 * результатами, возвращаемыми источником данных, указанном в атрибуте профиля
 * data-source. Заполняемые параметры указаны во вложенных элементах профиля
 * fill-param, причем наименования параметров отчета приводятся в атрибуте n,
 * а соответствующие им наименования параметров, возвращаемых источником данных
 *(например названия столбцов рекордсета) в атрибуте ds-field.
 */
public class FillReportParamProcessor extends ReportAbstractParamProcessor {
    /**
     * Обработка парметров отчёта.
     * @param processorProfile профиль процессора
     * @param processorData данные
     */
    @Override
    protected void doProcess(AbstractParamProcessorClass processorProfile, ReportLayoutData processorData) {
        /*
        различаются 2 случая: возвращается скалярное значение или
        запись в виде DataReader
        */
        FillParamsProcessorClass processor = (FillParamsProcessorClass) processorProfile;
        String dataSourceName = MacroProcessor.process(processor.getDataSource(), processorData);
        ReportParams params = processorData.getParams();
        IReportDataProvider dataProvider = processorData.getDataProvider();
        Object customData = processorData.getCustomData();
        //если возвращается скалярное значение
        if (processor.isSingleParam()) {
            // значение параметра из источника данных
            Object val = dataProvider.getValue(dataSourceName, customData);
            // получаем параметр, которому надо задать значение
            // он должен быть один
            String paramName = (processor.getFillParam().get(0).getN());
            // присваиваем значение параметру
            params.setParamValue(paramName, val);
            // если возвращается карта
        } else {
            Map<String, Object> mapValues = dataProvider.getDataAsMap(dataSourceName, customData);
            for (FillParamsProcessorClass.FillParamClass node : processor.getFillParam()) {
                //имя параметра
                String paramName = node.getN();
                // имя значения параметра в запросе
                String key = node.getDsField();
                Object val = mapValues.get(key);
                // вставляем в коллекцию параметров
                params.setParamValue(paramName, val);
            }
        }
    }
}
