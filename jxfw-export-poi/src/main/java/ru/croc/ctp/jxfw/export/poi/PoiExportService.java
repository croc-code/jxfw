package ru.croc.ctp.jxfw.export.poi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportService;
import ru.croc.ctp.jxfw.core.export.impl.model.XfwExportConfig;
import ru.croc.ctp.jxfw.export.poi.impl.views.ExcelView;
import ru.croc.ctp.jxfw.export.poi.impl.views.PdfView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервис для экспорта данных из списков.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
public class PoiExportService implements ExportService {
    private static final Logger log = LoggerFactory.getLogger(PoiExportService.class);

    private final ExcelView excelView;

    private final PdfView pdfView;

    private Semaphore semaphore;

    private int parallelCount;

    /**
     * Конструктор.
     * <p/>
     *
     * @param excelView генератор Excel-представления
     * @param pdfView   генератор PDF-предстваления
     * @param parallelCount Ограничение на количество одновременно выполняемых экспортов
     */
    public PoiExportService(ExcelView excelView, PdfView pdfView,
                            int parallelCount) {
        this.excelView = excelView;
        this.pdfView = pdfView;

        //ручной барьер - сколько процесить параллельно выгрузок
        this.parallelCount = parallelCount;
        semaphore = new Semaphore(parallelCount);

    }

    @Override
    public void createExport(final XfwExportConfig exportConfig,
                             final ExportDataProvider dataProvider,
                             final HttpServletRequest request,
                             final HttpServletResponse response) {


        LocalDateTime start = LocalDateTime.now();

        final Map<String, Object> map = new HashMap();

        exportConfig.deleteHiddenColumns();

        map.put("config", exportConfig);
        map.put("data", dataProvider);

        try {
            logInfo();

            if (parallelCount > 0) {
                semaphore.acquire();
            }

            if ("pdf".equalsIgnoreCase(exportConfig.getFormat())) {
                pdfView.render(map, request, response);
            } else if ("excel".equalsIgnoreCase(exportConfig.getFormat())) {
                excelView.render(map, request, response);
            }
            log.info(
                    "Preparing EXCEL document ({}), elapsed time {} second(s)", exportConfig.getFileNamePrefix(),
                    Duration.between(start, LocalDateTime.now()).getSeconds());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (parallelCount > 0) {
                semaphore.release();
            }
        }


    }


    private void logInfo() {
        if (parallelCount <= 0) {
            log.info("Очередь выгрузок не используется");
        } else {
            if (semaphore.availablePermits() < 1) {
                log.info("Выгрузка поставлена в очередь, т.к. превышено число одновременных выгрузок - {}",
                        parallelCount);
                log.info("В работе {} выгрузок(ки), ожидают очереди приблизительно - {} выгрузок(ки)",
                        parallelCount, semaphore.getQueueLength() + 1);
            } else {
                log.info("Очередь свободна, выгрузка начинается немедленно");
            }
        }
    }

}
