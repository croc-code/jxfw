package ru.croc.ctp.jxfw.reporting.xslfo;

import ru.croc.ctp.jxfw.core.reporting.XfwReportService;
import ru.croc.ctp.jxfw.reporting.xslfo.impl.MakeReportParams;
import ru.croc.ctp.jxfw.reporting.xslfo.types.ReportClass;

import java.io.OutputStream;
import java.util.List;

/**
 * Предоставить доступ к отчетам.
 */
public interface IReportService extends XfwReportService {
    /**
     * Генерирует отчет.
     *
     * @param outputStream Поток для формирования отчета
     * @param reportParams Входные и выходные параметры формируемого отчета
     */
    void makeReport(OutputStream outputStream, MakeReportParams reportParams);

    /**
     * Параметры отчёта.
     *
     * @param reportName имя отчёта
     * @return параметры отчёта
     */
    List<ReportClass.ParamsClass.ParamClass> getReportParams(String reportName);

}
