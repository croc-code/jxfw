package ru.croc.ctp.jxfw.reporting.xslfo.impl;

import static ru.croc.ctp.jxfw.reporting.xslfo.impl.XfwXslfoReport.PARAM_OUTPUT_FORMAT;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;

import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * Класс входных и выходных параметров для формирования отчета.
 * Created by vsavenkov on 14.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class MakeReportParams {


    //region Входные параметры

    /**
     * Имя профиля отчёта.
     */
    private final String reportName;

    /**
     * Выходной формат формируемого отчета.
     */
    private final OutputFormat outputFormat;

    /**
     * Коллекция параметров отчета.
     */
    private final Map<String, String> reportParamsCollection;

    /**
     * Кодировка отчета.
     */
    private final Charset encoding;

    /**
     * Пользовательские данные.
     */
    private final Object customData;

    //endregion

    //region Выходные параметры


    /**
     * Заголовок/наименование файла отчета.
     */
    private String reportTitle;

    //endregion

    /**
     * Конструктор.
     *
     * @param request http запрос.
     */
    public MakeReportParams(String reportName, String encoding, HttpServletRequest request) {
        this(reportName, encoding, getQueryParams(request));
    }

    public MakeReportParams(String reportName, String encoding, Map<String, String> params) {

        if (StringUtils.isBlank(reportName)) {
            throw new IllegalArgumentException("Не задано имя профиля отчета");
        }
        this.reportName = reportName;
        this.reportParamsCollection = params == null ? new HashMap<>() : params;
        this.encoding = Charset.forName(encoding);
        this.customData = null;
        this.outputFormat = OutputFormat.from(reportParamsCollection.get(PARAM_OUTPUT_FORMAT));

    }

    public String getReportName() {
        return reportName;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public Map<String, String> getReportParamsCollection() {
        return reportParamsCollection;
    }

    public Charset getEncoding() {
        return encoding;
    }

    public Object getCustomData() {
        return customData;
    }


    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }


    private static Map<String, String> getQueryParams(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, String> map = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            for (String paramValue : paramValues) {
                String value = map.get(paramName);
                if (value != null) {
                    value += "&" + paramValue;
                } else {
                    value = paramValue;
                }
                map.put(paramName, value);
            }
        }
        return map;
    }

}
