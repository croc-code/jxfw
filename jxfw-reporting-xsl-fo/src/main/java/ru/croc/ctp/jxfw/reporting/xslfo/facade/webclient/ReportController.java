package ru.croc.ctp.jxfw.reporting.xslfo.facade.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;
import ru.croc.ctp.jxfw.reporting.xslfo.IReportService;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.MakeReportParams;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Класс обрабатывающий запросы на получение отчётов.
 * Created by vsavenkov on 31.03.2017.
 */
@RestController
@RequestMapping("**/api/reports")
@ConditionalOnProperty(name = "${ru.croc.ctp.wc.apiVersion}", havingValue = "0", matchIfMissing = true)
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    private IReportService reportService;

    /**
     * Конструктор.
     *
     * @param reportService IReportService
     */
    @Autowired
    public ReportController(IReportService reportService) {
        // текущий экземпляр Report Service
        this.reportService = reportService;
    }

    /**
     * Обработка запроса.
     *
     * @param name     метанаимменование отчёта
     * @param request  запрос
     * @param response отклик
     */

    @RequestMapping(value = "/{name}", method = {RequestMethod.GET, RequestMethod.POST})
    public void post(@PathVariable String name,
                     HttpServletRequest request,
                     HttpServletResponse response) {

        try (OutputStream printWriter = response.getOutputStream()) {
            MakeReportParams makeReportParams = new MakeReportParams(name, response.getCharacterEncoding(), request);
            reportService.makeReport(printWriter, makeReportParams);
            response.setContentType(makeReportParams.getOutputFormat().getMime());
            ControllerBase.addFileDownLoadCookieAndHeader(request, response,
                    makeReportParams.getReportTitle()
                            + makeReportParams.getOutputFormat().getExtension());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
