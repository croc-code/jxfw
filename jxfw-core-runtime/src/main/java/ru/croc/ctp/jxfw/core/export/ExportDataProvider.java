package ru.croc.ctp.jxfw.core.export;


/**
 * Поставщик данных для экспорта.
 *
 * @author OKrutova
 * @since 1.6
 */
public interface ExportDataProvider {


    /**
     * Отдать следующую пачку данных.
     * Если данные кончились, то пустое множество.
     *
     * @return набор строк экспорта.
     */
    Iterable<ExportRow> getMoreRows();



}