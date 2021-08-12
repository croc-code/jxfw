package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors

import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData
import ru.croc.ctp.jxfw.reporting.xslfo.types.ParamByParamProcessorClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass
import ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors.ParamByParamProcessor
import spock.lang.Specification

class ParamByParamProcessorSpec extends Specification {
    private ParamByParamProcessorClass processorProfile
    private ReportLayoutData processorData
    private ParamByParamProcessor processor
    private ReportParams params
    private final String PARAM_ONE_NAME = "oneParam"
    private final String PARAM_TWO_NAME = "twoParam"
    private final String ifNullValue = "ifNullValue"
    private final String ifNotNullValue = "ifNotNullValue"
    private final String ifIsParamValue = "ifIsParamValue"
    private final String ifNotIsParamValue = "ifNotIsParamValue"

    def setup(){
        processorProfile  = new ParamByParamProcessorClass()
        processor = new ParamByParamProcessor()
        prepareNodes()

    }

    def "process if null"() {
        given: "list nodes"
            params = getReportParams()
            processorData = new ReportLayoutData(null, params, null, null, null)
        when: "Run ParamByParamProcessor.process"
            processor.doProcess(processorProfile, processorData)
        then: "params fill"
            params.getParam(PARAM_TWO_NAME).value() == ifNullValue

    }

    def "process if not null"() {
        given: "list nodes"
            params = getReportParams()
            params.setParamValue(PARAM_ONE_NAME, "some value");
            processorData = new ReportLayoutData(null, params, null, null, null)
        when: "Run ParamByParamProcessor.process"
            processor.doProcess(processorProfile, processorData)
        then: "params fill"
            params.getParam(PARAM_TWO_NAME).value() == ifNotNullValue

    }

    def "process if is"() {
        given: "list nodes"
            params = getReportParams()
            params.setParamValue(PARAM_ONE_NAME, "some value");
            processorData = new ReportLayoutData(null, params, null, null, null)
        when: "Run ParamByParamProcessor.process"
            processor.doProcess(processorProfile, processorData)
        then: "params fill"
            params.getParam(PARAM_TWO_NAME).value() == ifNotNullValue

    }

    private def getReportParams(){
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

    private def prepareNodes(){
        List<Object> nodes = new ArrayList<>()
        nodes.add(getIfNullNode())
        nodes.add(getIfNotNullNode())
        processorProfile.ifNullParamOrIfNotNullParamOrIfParamIs.addAll(nodes)
    }
    private def getIfNullNode(){
        def ifNull = new ParamByParamProcessorClass.IfNullParamClass()
        ifNull.setN(PARAM_ONE_NAME)
        ifNull.setSetParamN(PARAM_TWO_NAME)
        ifNull.setSetParamValue(ifNullValue)
        return ifNull
    }

    private def getIfNotNullNode(){
        def ifNotNull = new ParamByParamProcessorClass.IfNotNullParamClass()
        ifNotNull.setN(PARAM_ONE_NAME)
        ifNotNull.setSetParamN(PARAM_TWO_NAME)
        ifNotNull.setSetParamValue(ifNotNullValue)
        return ifNotNull
    }
}
