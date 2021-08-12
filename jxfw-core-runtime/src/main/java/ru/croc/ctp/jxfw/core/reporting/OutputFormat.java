package ru.croc.ctp.jxfw.core.reporting;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.croc.ctp.jxfw.core.reporting.facade.webclient.ItemWithWcSpecificSerialization;

import java.util.HashMap;
import java.util.Map;

/**
 * Описание форматов для отчетов.
 *
 * @since 1.3
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OutputFormat implements ItemWithWcSpecificSerialization {


    /*
    { "html": { "name": "HTML", "format": "HTML", "mime": "text/html" },
    "pdf": { "name": "PDF", "format": "PDF", "mime": "application/pdf" },
    "word": { "name": "WORD", "format": "WORD", "mime": "application/msword" },
    "xps": { "name": "XPS", "format": "XPS", "mime": "application/vnd.ms-xpsdocument" },
    "excel": { "name": "EXCEL", "format": "EXCEL", "mime": "application/vnd.ms-excel" },
     "xsl-fo": { "name": "XSL-FO", "format": "XSL-FO", "mime": "text/xml" } }
     */

    /**
     * Формат HTML.
     */
    HTML5("html", "text/html", ".html"), //
    /**
     * Формат PDF.
     */
    PDF("pdf", "application/pdf", ".pdf"), //

    /**
     * Формат Word.
     */
    WORD("word", "application/msword", ".doc"),

    /**
     * Формат XPS.
     */
    XPS("xps", "application/vnd.ms-xpsdocument", ".xps"),
    /**
     * Формат EXCEL.
     */
    EXCEL("excel", "application/vnd.ms-excel", ".xls"), //
    /**
     * Формат EXCEL (xlsx).
     */
    EXCEL2010("xlsx", "application/vnd.ms-excel", ".xlsx"),

    /**
     * Формат XSL-FO.
     */
    XSL_FO("xsl-fo", "text/xml", ".xml");


    private final String formatName;

    private final String mime;

    private final String extension;

    /**
     * Конструктор.
     *
     * @param formatName - имя формата.
     * @param mime       - описание типа контента.
     * @param extension расширение формируемого файла
     */
    OutputFormat(String formatName, String mime, String extension) {
        this.formatName = formatName;
        this.mime = mime;
        this.extension = extension;
    }

    /**
     * Получение выходного формата отчета по его имени.
     *
     * @param formatName - имя формата.
     * @return описание формата.
     */
    public static OutputFormat from(String formatName) {
        for (OutputFormat format : OutputFormat.values()) {
            if (format.formatName.equalsIgnoreCase(formatName)) {
                return format;
            }
        }
        return HTML5; // default;
    }

    @JsonIgnore
    public String getFormatName() {
        return formatName;
    }

    public String getMime() {
        return mime;
    }

    public String getName() {
        return formatName.toUpperCase();
    }

    public String getFormat() {
        return formatName.toUpperCase();
    }

    @JsonIgnore
    public String getExtension() {
        return extension;
    }


    @JsonIgnore
    /**
     * Свойства для конфигурации веб-клиента.
     */
    public Map<String, String> getConfigProperties() {
        Map<String, String> config = new HashMap();
        config.put("format", getFormat());
        config.put("mime", getMime());
        config.put("name", getName());
        return config;
    }


}
