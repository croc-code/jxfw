package ru.croc.ctp.jxfw.export.poi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import ru.croc.ctp.jxfw.core.export.ExportService;
import ru.croc.ctp.jxfw.export.poi.PoiExportService;
import ru.croc.ctp.jxfw.export.poi.impl.views.ExcelView;
import ru.croc.ctp.jxfw.export.poi.impl.views.PdfView;

/**
 * Конфигурация экспорта на POI.
 */
@Configuration
public class XfwPoiExportConfig {



    /**
     * Рендерер в Excel.
     *
     * @param resourceLoader resourceLoader
     * @return бин
     */
    @Bean
    ExcelView excelView(ResourceLoader resourceLoader) {
        return new ExcelView(resourceLoader);
    }


    /**
     * Рендерер в pdf.
     *
     * @return бин
     */
    @Bean
    PdfView pdfView() {
        return new PdfView();
    }


    /**
     * Имплементация сервиса экспорта на основе POI.
     *
     * @param resourceLoader resourceLoader
     * @param parallelCount  Ограничение на количество одновременно выполняемых экспортов
     * @return бин
     */
    @Bean
    ExportService poiExportService(ResourceLoader resourceLoader,
                                   @Value("${jxfw.export.parallelCount:0}") int parallelCount) {

        return new PoiExportService(excelView(resourceLoader), pdfView(), parallelCount);

    }
}
