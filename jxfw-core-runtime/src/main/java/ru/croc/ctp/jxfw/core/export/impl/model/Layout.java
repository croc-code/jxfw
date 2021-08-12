package ru.croc.ctp.jxfw.core.export.impl.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * POJO класс для хранения информации об разметке для экспорта cgbcrjd.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
@JsonSerialize
@JsonDeserialize
public class Layout {

    private String pageFormat = "A4"; //Возможные значения: A2, A3, A4, A5, Letter

    private String pageOrientation = "portrait";

    private String pageMargin = "20mm";


    /**
     * Количество полей в документе(право\лево верх\низ header\footer).
     */
    public static final short MARGINS_COUNT = 6;

    /**
     * настройка печати из шаблона.
     */
    private Object templatePrintSetup;

    private boolean templateFitToPage;

    private double[] templateMargins = new double[]{0, 0, 0, 0, 0, 0};


    public String getPageFormat() {
        return pageFormat;
    }

    public void setPageFormat(String pageFormat) {
        this.pageFormat = pageFormat;
    }

    public String getPageOrientation() {
        return pageOrientation;
    }

    public void setPageOrientation(String pageOrientation) {
        this.pageOrientation = pageOrientation;
    }

    public String getPageMargin() {
        return pageMargin;
    }

    public void setPageMargin(String pageMargin) {
        this.pageMargin = pageMargin;
    }

    public Object getTemplatePrintSetup() {
        return templatePrintSetup;
    }

    public void setTemplatePrintSetup(Object templatePrintSetup) {
        this.templatePrintSetup = templatePrintSetup;
    }

    public boolean isTemplateFitToPage() {
        return templateFitToPage;
    }

    public void setTemplateFitToPage(boolean templateFitToPage) {
        this.templateFitToPage = templateFitToPage;
    }

    public double[] getTemplateMargins() {
        return templateMargins;
    }

    public void setTemplateMargins(double[] templateMargins) {
        this.templateMargins = templateMargins;
    }
}
