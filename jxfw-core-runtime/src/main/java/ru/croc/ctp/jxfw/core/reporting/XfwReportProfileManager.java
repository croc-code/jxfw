package ru.croc.ctp.jxfw.core.reporting;


import org.springframework.core.io.Resource;

/**
 * Сервис управления поиском шаблонов отчетов.
 * Должен находить шаблоны отчетов по имени.
 *
 * @since 1.6
 * @author OKrutova
 *
 */
public interface XfwReportProfileManager {

    /**
     * Получить описание отчетов.
     *
     * @return список описаний отчетов
     */
    Iterable<XfwReportProfile> getReports();

    /**
     * Получить данный отчет по имени.
     * @param reportName имя
     * @return описание отчета
     */
    XfwReportProfile getReport(String reportName);


    /**
     * Ищет ресурс в папке отчетов во внешней директории или в класспасе.
     * @param fileName имя ресурса относительно папки отчетов
     * @return
     */
    Resource getResource(String fileName);

}
