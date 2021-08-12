package ru.croc.ctp.jxfw.reporting.birt;

import ru.croc.ctp.jxfw.core.reporting.OutputFormat;
import ru.croc.ctp.jxfw.core.reporting.XfwReportService;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Сервис для генерации отчетов в различных форматах.
 *
 * @author Nosov Alexander
 * @since 1.3
 */
public interface BirtReportService extends XfwReportService {

    /**
     * Отрисовать отчет в формате HTML.
     *
     * @param request        - html запрос
     * @param response       - html ответ
     * @param format         - описание формата, экземпляр класса {@link OutputFormat}
     * @param params         - параметры для отчета
     * @param reportFileName - имя файла отчета
     */
    void renderHtmlReport(HttpServletRequest request,
                          HttpServletResponse response,
                          OutputFormat format,
                          Map<String, Object> params,
                          String reportFileName);

    /**
     * Отрисовать отчет в формате PDF.
     *
     * @param request        - html запрос
     * @param response       - html ответ
     * @param format         - описание формата, экземпляр класса {@link OutputFormat}
     * @param params         - параметры для отчета
     * @param reportFileName - имя файла отчета
     */
    void renderPdfReport(HttpServletRequest request,
                         HttpServletResponse response,
                         OutputFormat format,
                         Map<String, Object> params,
                         String reportFileName);

    /**
     * Отрисовать отчет в формате EXCEL (xls).
     *
     * @param request        - html запрос
     * @param response       - html ответ
     * @param format         - описание формата, экземпляр класса {@link OutputFormat}
     * @param params         - параметры для отчета
     * @param reportFileName - имя файла отчета
     */
    void renderExcelReport(HttpServletRequest request,
                           HttpServletResponse response,
                           OutputFormat format,
                           Map<String, Object> params,
                           String reportFileName);


    /**
    * Отрисовать отчет.
    *
    * @param request        - html запрос
    * @param response       - html ответ
    * @param outputFormat         - описание формата, экземпляр класса {@link OutputFormat}
    * @param params         - параметры для отчета
    * @param reportFileName - имя файла отчета
    */
    void renderReport(HttpServletRequest request,
                       HttpServletResponse response,
                       OutputFormat outputFormat,
                       Map<String, Object> params,
                       String reportFileName);



}
