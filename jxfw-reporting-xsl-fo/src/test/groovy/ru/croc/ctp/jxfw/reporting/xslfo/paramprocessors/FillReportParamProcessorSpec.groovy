package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors

import ru.croc.ctp.jxfw.reporting.xslfo.data.IReportDataProvider
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData
import ru.croc.ctp.jxfw.reporting.xslfo.types.FillParamsProcessorClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass
import ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors.FillReportParamProcessor
import spock.lang.Specification


/**
 * Тестовая спецификация для обработчика параметров.
 *
 * @author PaNovikov
 * @since 27.04.2017
 */
class FillReportParamProcessorSpec extends Specification{

    private FillParamsProcessorClass processorProfile
    private ReportLayoutData processorData
    private FillReportParamProcessor fillReportParamProcessor
    private ReportParams params
    private final oneObject = new Object()
    private final twoObject = new Object()
    public final String PARAM_ONE_NAME = "oneParam"
    public final String PARAM_TWO_NAME = "twoParam"


    def setup() {
        processorProfile = new FillParamsProcessorClass()
        fillReportParamProcessor = new FillReportParamProcessor()
        def listFillParam = getListParam()
        processorProfile.setFillParam(listFillParam)
        params = getReportParams()
        def dataProvider = getDataProvider()
        processorData = new ReportLayoutData(null, params, dataProvider, null, null)
    }

    def "fill parameter single value"() {
        given: "single param"
            processorProfile.setSingleParam(true);
        when: "Run FilleportParamProcessor.process"
            fillReportParamProcessor.doProcess(processorProfile, processorData)
        then: "Check that parameter is filling"
            params.getParam(PARAM_ONE_NAME).value() == oneObject
    }

    def "fill many parameters" (){
        given: "many param"
            processorProfile.setSingleParam(false);
        when: "Run FilleportParamProcessor.process"
            fillReportParamProcessor.doProcess(processorProfile, processorData)
        then: "Check that parameter is filling"
            params.getParam(PARAM_ONE_NAME).value() == oneObject
            params.getParam(PARAM_TWO_NAME).value() == twoObject
    }

    private def getListParam(){
        def listFillParam = new ArrayList<FillParamsProcessorClass.FillParamClass>()
        def paramOne = new FillParamsProcessorClass.FillParamClass()
        paramOne.setN(PARAM_ONE_NAME)
        paramOne.setDsField(PARAM_ONE_NAME)
        def paramTwo = new FillParamsProcessorClass.FillParamClass()
        paramTwo.setN(PARAM_TWO_NAME)
        paramTwo.setDsField(PARAM_TWO_NAME)
        listFillParam.add(paramOne)
        listFillParam.add(paramTwo)
        return listFillParam
    }
    private def getDataProvider(){
        def dataProvider = Mock(IReportDataProvider)
        dataProvider.getValue(_, _) >> oneObject
        def Map<String, Object> map = new HashMap<>()
        map.put(PARAM_ONE_NAME, oneObject);
        map.put(PARAM_TWO_NAME, twoObject);
        dataProvider.getDataAsMap(_,_) >> map
        return dataProvider
    }

    def getReportParams(){
        def listReportParams = new ArrayList<ReportClass.ParamsClass.ParamClass>()
        def paramOne = new ReportClass.ParamsClass.ParamClass()
        paramOne.setVt(VarTypesClass.STRING)
        paramOne.setN(PARAM_ONE_NAME)
        def paramTwo = new ReportClass.ParamsClass.ParamClass()
        paramTwo.setVt(VarTypesClass.STRING)
        paramTwo.setN(PARAM_TWO_NAME)
        listReportParams.add(paramOne)
        listReportParams.add(paramTwo)
        return new ReportParams(listReportParams , null)
    }
}