package ru.croc.ctp.jxfw.core.reporting.facade.webclient;

import static ru.croc.ctp.jxfw.core.reporting.XfwReportService.NO_REPORTING_ENGINE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;
import ru.croc.ctp.jxfw.core.reporting.XfwReportService;


/**
 * Контроллер реализует общие для разных движков отчетов endpoint-ы.
 * @author OKrutova
 * @since 1.6
 */
@RestController
@RequestMapping(value = "**/api/reports")
@ConditionalOnProperty(name = "${ru.croc.ctp.wc.apiVersion}", havingValue = "0", matchIfMissing = true)
public class ReportingController {

    @Autowired(required = false)
    private XfwReportService reportService;

    @Autowired
    private XfwReportProfileManager reportProfileManager;

    /**
     * Список всех отчетов.
     * @return все отчеты.
     */
    @RequestMapping(value = {"_list"} , produces = MediaType.APPLICATION_JSON_VALUE)
    public CollectionWithWcSpecificSerialization reportsList() {
        if (reportService == null) {
            throw new IllegalStateException(NO_REPORTING_ENGINE);
        }
        return new CollectionWithWcSpecificSerialization(reportProfileManager.getReports());
    }

    /**
     * Список форматов отчетов.
     * @return все форматы.
     */
    @RequestMapping(value = {"_formats"}, produces = MediaType.APPLICATION_JSON_VALUE )
    public CollectionWithWcSpecificSerialization reportsFormat() {
        if (reportService == null) {
            throw new IllegalStateException(NO_REPORTING_ENGINE);
        }
        return new CollectionWithWcSpecificSerialization(reportService.getFormats());
    }

}
