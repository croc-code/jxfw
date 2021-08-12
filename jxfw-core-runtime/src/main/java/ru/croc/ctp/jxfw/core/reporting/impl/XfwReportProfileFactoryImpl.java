package ru.croc.ctp.jxfw.core.reporting.impl;

import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileFactory;

public abstract class XfwReportProfileFactoryImpl implements XfwReportProfileFactory {

    @Override
    public String getProfileTemplate(String reportName) {
        if (reportName == null || reportName.isEmpty()) {
            return "*" + getReportProfileExtension();
        } else {
            return reportName + getReportProfileExtension();
        }
    }

    @Override
    public String getProfileTemplateRegex(String reportName) {
        if (reportName == null || reportName.isEmpty()) {
            return ".*\\" + getReportProfileExtension();
        } else {
            return reportName + getReportProfileExtension();
        }
    }


}
