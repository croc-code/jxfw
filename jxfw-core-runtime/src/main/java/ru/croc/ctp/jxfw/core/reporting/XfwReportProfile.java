package ru.croc.ctp.jxfw.core.reporting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.croc.ctp.jxfw.core.reporting.facade.webclient.ItemWithWcSpecificSerialization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Описание отчета.
 * @since 1.6
 * @author OKrutova
 */
public interface XfwReportProfile extends ItemWithWcSpecificSerialization {

    /**
     * Идентификатор отчета.
     * @return идентификатор отчета
     */
    String getName();

    /**
     * Наименование (описание) отчета.
     * @return наименование отчёта
     */
    String getTitle();


    /**
     * Открывает поток для чтения шаблона отчета.
     * @return
     */
    @JsonIgnore
    InputStream getStream() throws IOException;





}
