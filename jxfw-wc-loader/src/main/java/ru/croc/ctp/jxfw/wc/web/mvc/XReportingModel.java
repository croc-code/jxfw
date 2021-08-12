package ru.croc.ctp.jxfw.wc.web.mvc;

import ru.croc.ctp.jxfw.core.reporting.XfwReportProfile;
import ru.croc.ctp.jxfw.core.reporting.facade.webclient.CollectionWithWcSpecificSerialization;


/**
 * Обертка для списка описании отчетов,
 * обеспечивает сериализацию в Json, которую ожидает WC.
 * @author OKrutova
 * @since 1.6
 */
public class XReportingModel {


    private final String reportName;

    private final CollectionWithWcSpecificSerialization reports;


    public XReportingModel(String reportName, Iterable<XfwReportProfile> reports) {
        this.reportName = reportName;
        this.reports = new CollectionWithWcSpecificSerialization(reports);
    }

    public String getReportName() {
        return reportName;
    }

    public CollectionWithWcSpecificSerialization getReports() {
        return reports;
    }
}
