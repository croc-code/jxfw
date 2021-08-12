package ru.croc.ctp.jxfw.core.reporting;

/**
 * Сервис для генерации отчетов в различных форматах.
 *
 * @author Nosov Alexander
 * @since 1.3
 */
public interface XfwReportService {

    /**
     * Сообщение об отсутствии в прилодении модуля отчетов.
     */
    String NO_REPORTING_ENGINE = "No reporting engine available";


    /**
     * Получить доступные форматы отчетов.
     *
     * @return список доступных форматов отчетов
     */
    Iterable<OutputFormat> getFormats();



}
