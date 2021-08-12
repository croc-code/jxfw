package ru.croc.ctp.jxfw.core.export.impl.model;

import static ru.croc.ctp.jxfw.core.export.impl.BaseExportDataProvider.getPropertyPlaceholders;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import ru.croc.ctp.jxfw.core.reporting.OutputFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Объект для хранения конфигов экспорта списка.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
@JsonSerialize
@JsonDeserialize
public class XfwExportConfig {
    /**
     * Наименование формата выходного файла.
     */
    private String format;
    /**
     * Наименование выходного файла без расширения.
     */
    private String fileName;
    /**
     * Префикс для формирования файла, если не задано поле fileName.
     * Имя выходного файла формируется из префикса, текущего времени
     * и расширения как {fileNamePrefix}-{yyyyMMdd-HHmmss}.{ext}
     */
    private String fileNamePrefix = "Export";
    /**
     * Параметры выводы страницы.
     */
    private Layout layout = new Layout();


    private boolean template;
    /**
     * Описание колонок.
     */
    private List<Column> columns= new ArrayList<>();
    /**
     * Данные для экспорта.
     */
    private List<Map<String, Object>> rows;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    /**
     * Формирует имя файла из настроек экспорта.
     *
     * @param fileExt рассширения файла
     * @return имя файла с рассширением
     */
    public String calculateFileNameFromConfig(String fileExt) {
        if (getFileName() != null && !getFileName().isEmpty()) {
            return getFileName() + fileExt;
        } else {
            final String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            return getFileNamePrefix() + "-" + date + fileExt;
        }
    }

    /**
     * Формирует имя шалона из настроек экспорта.
     *
     * @param fileExt рассширения файла
     * @return имя файла с рассширением
     */
    public String getTemplateName(String fileExt) {
        return getFileNamePrefix() + fileExt;
    }

    /**
     * Есть шаблон для файла экспорта.
     *
     * @return да\нет
     */

    public boolean hasTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }


    /**
     * Удалить скрытые столбцы.
     */
    public void deleteHiddenColumns() {
        for (int i = 0; i < getColumns().size(); i++) {
            final Column column = getColumns().get(i);
            if (column.isHidden()) {
                getColumns().set(i, null);
            }
        }
        getColumns().removeAll(Collections.singleton(null));
    }

    public OutputFormat getOutputFormat(){
        return OutputFormat.from(format);
    }

    /**
     * Формирует параметр expand, он же preloads, на основе цепочек свойств, заданных в формате колонок.
     * @return
     */
    public String getExpand(){
        return  StreamSupport.stream(getPreloads())
                .collect(Collectors.joining(","));
    }
    /**
     * Формирует параметр expand, он же preloads, на основе цепочек свойств, заданных в формате колонок.
     * @return
     */
    public Set<String> getPreloads(){
        return  StreamSupport.stream(getColumns())
                .flatMap(column -> StreamSupport.stream(getPropertyPlaceholders(column.getFormat())))
                .distinct().collect(Collectors.toSet());
    }

}
