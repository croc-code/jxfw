package ru.croc.ctp.jxfw.core.reporting;

import org.springframework.core.io.Resource;

import java.io.File;

/**
 * Фабрика шаблонов отчетов. Знает, какое расширение имеют отчеты в данном движке.
 *
 * @author OKrutova
 * @since 1.6
 */
public interface XfwReportProfileFactory {


    /**
     * Шаблон имени отчета с учетом расширения.
     *
     * @param reportName имя отчета. если не задано, то формируется шаблон для поиска.
     * @return шаблон
     */
    String getProfileTemplate(String reportName);

    /**
     * Регулярное выражение имени отчета с учетом расширения.
     *
     * @param reportName имя отчета. если не задано, то формируется шаблон для поиска.
     * @return шаблон
     */
    String getProfileTemplateRegex(String reportName);


    /**
     * Создает описание отчета по ресурсу.
     * @param resource ресурс
     * @return описание отчета
     */
    XfwReportProfile getInstance(Resource resource);

    /**
     * Расширение шаблона отчета.
     * @return  Расширение шаблона отчета.
     */
    String getReportProfileExtension();

}
