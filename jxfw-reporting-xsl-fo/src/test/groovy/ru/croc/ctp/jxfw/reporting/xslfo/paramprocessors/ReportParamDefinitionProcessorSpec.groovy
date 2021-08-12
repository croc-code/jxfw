package ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentException
import ru.croc.ctp.jxfw.reporting.xslfo.impl.ReportParams
import ru.croc.ctp.jxfw.reporting.xslfo.layouts.ReportLayoutData
import ru.croc.ctp.jxfw.reporting.xslfo.types.AndClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.AndNotClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.CheckParamClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.OrClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.ParamDefinitionProcessorClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass
import ru.croc.ctp.jxfw.reporting.xslfo.paramprocessors.ReportParamDefintionProcessor
import spock.lang.Specification

class ReportParamDefinitionProcessorSpec extends Specification {
    private ParamDefinitionProcessorClass processorProfile
    private ReportLayoutData processorData
    private ReportParams params
    private ReportParamDefintionProcessor processor
    public final String PARAM_ONE_NAME = "oneParam"
    public final String PARAM_TWO_NAME = "twoParam"

    def setup(){
        params = getReportParams()
        processorData = new ReportLayoutData(null, params, null, null, null)
        processor = new ReportParamDefintionProcessor()

    }

    def "check params is null"() {
        given: "check params is null"
            processorProfile = new ParamDefinitionProcessorClass()
        when: "Run ReportParamDefintionProcessor.process"
            processor.doProcess(processorProfile, processorData)
        then: "throw ArgumentException"
            ArgumentException ex = thrown()

    }

    def "check params And "() {
        given: "check params has one And fail"
            processorProfile = getProcessorProfileWithCheckOneAndParams(true)
        when: "Run ReportParamDefintionProcessor.process"
            processor.doProcess(processorProfile, processorData)
        then: "throw ArgumentException"
        ArgumentException ex = thrown()

    }

    def "check params AndNot "() {
        given: "check params has one And fail"
            processorProfile = getProcessorProfileWithCheckOneAndParams(false)
        when: "Run ReportParamDefintionProcessor.process"
            processor.doProcess(processorProfile, processorData)
        then: "dont throw ArgumentException"
            notThrown(ArgumentException)

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

    private def getProcessorProfileWithCheckOneAndParams(boolean andCondition){
        def pp = new ParamDefinitionProcessorClass()
        ParamDefinitionProcessorClass.CheckParamsClass checkParams = new ParamDefinitionProcessorClass.CheckParamsClass()
        CheckParamClass check = new CheckParamClass()
        check.setN(PARAM_ONE_NAME)
        def and
        if (andCondition){
            and = new AndClass()
        } else {
            and = new AndNotClass()
        }
        List<CheckParamClass> listCheck = new ArrayList<>()
        listCheck.add(check)
        List listAnd = new ArrayList<>()
        and.setCheckParam(listCheck)
        listAnd.add(and)
        List<OrClass> listOr = new ArrayList<>()
        OrClass or1 = new OrClass()
        if (andCondition){
            or1.setAnd(listAnd)
        } else {
            or1.setAndNot(listAnd)
        }

        listOr.add(or1)
        checkParams.setOr(listOr)
        pp.setCheckParams(checkParams)
        return pp
    }

}
