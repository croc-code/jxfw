package ru.croc.ctp.jxfw.wc.web.controllers;

import static ru.croc.ctp.jxfw.core.reporting.XfwReportService.NO_REPORTING_ENGINE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.croc.ctp.jxfw.core.reporting.XfwReportProfileManager;
import ru.croc.ctp.jxfw.core.reporting.XfwReportService;
import ru.croc.ctp.jxfw.wc.web.mvc.MainPageModel;
import ru.croc.ctp.jxfw.wc.web.mvc.XReportingModel;
import ru.croc.ctp.jxfw.wc.web.mvc.controllers.PageControllerBase;

import javax.servlet.http.HttpServletRequest;

/**
 * Контроллер поддерживает SPA приложение репортинга в веб-клиенте.
 *
 * @author OKrutova
 * @since 1.6
 */
@Controller
public class XReportController extends PageControllerBase {

    @Autowired(required = false)
    private XfwReportService reportService;

    @Autowired
    private XfwReportProfileManager reportProfileManager;

    @Autowired
    private ObjectMapper objectMapper;

    protected String getMainScriptName() {
        return "report-main.js";
    }

    /**
     * Хендлер для корневого URL.
     *
     * @param request - объект запроса.
     * @param model   - модель
     * @param name имя отчета
     * @return HTML страница
     */
    @RequestMapping(value = {"/display/report/{name}"}, produces = "text/html;charset=UTF8")
    public String index(HttpServletRequest request, Model model, @PathVariable(required = false) String name) {

        if (reportService == null) {
            throw new IllegalStateException(NO_REPORTING_ENGINE);
        }

        final MainPageModel pageModel = new MainPageModel();
        initializePageModel(pageModel, request);

        setupModel(model, pageModel);
        XReportingModel reportingModel = new XReportingModel(name, reportProfileManager.getReports());
        try {
            model.addAttribute("clientConfig",
                    "var xconfig = " + pageModel.getClientConfigJsonString() + ";"
                            + "var xmodel = " + objectMapper.writeValueAsString(reportingModel) + ";");
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

        return "index";
    }


}


