package ru.croc.ctp.jxfw.core.reporting;

public enum ReportingEngine {
    NO_REPORT("jxfw-no-reporting"),
    BIRT("jxfw-reporting-birt"),
    XSLFO("jxfw-reporting-xsl-fo"),
    JASPER("jxfw-reporting-jasper");

    private final String moduleName;

    ReportingEngine(String moduleName){
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }
}
