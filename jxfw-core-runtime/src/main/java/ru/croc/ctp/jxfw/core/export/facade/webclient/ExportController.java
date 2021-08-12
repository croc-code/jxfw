package ru.croc.ctp.jxfw.core.export.facade.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.croc.ctp.jxfw.core.export.ExportService;
import ru.croc.ctp.jxfw.core.export.impl.ClientExportDataProvider;
import ru.croc.ctp.jxfw.core.export.impl.model.XfwExportConfig;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер для экспорта с клиентскими данными.
 *
 * @author Nosov Alexander on 10.09.15.
 */
@RestController
@RequestMapping(value = "**/api/_export")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    /**
     * Сообщение в случае отсутствия сервиса в приложении.
     */
    public static final String NO_EXPORT_ENGINE_MESSAGE = "No export engine available";

    /**
     * Сервис для экспорта данных из списков.
     */
    @Autowired(required = false)
    ExportService exportService;

    /**
     * сценарий экспорта - когда данные передаются с клиента. Если в списке используется классический пейджинг,
     * то экспортироваться будут только данные текущей страницы. Случаи когда требуется этот подход
     * 1) для загрузки данных использовался нестандартный механизм, повторить который при экспорте невозможно
     * 2) используется сложное клиентское форматирование данных, повторить которое на сервере невозможно
     *
     * @param exportConfig данные для выгрузки
     * @param request      запрос
     * @param response     ответ
     */
    @RequestMapping(value = "_plain", method = {RequestMethod.GET, RequestMethod.POST})
    public void plain(@RequestParam(value = "$export", required = true) String exportConfig,
                      final HttpServletRequest request, final HttpServletResponse response) {
        log.debug("Client data for export: {}", exportConfig);
        if (exportService == null) {
            throw new IllegalStateException(NO_EXPORT_ENGINE_MESSAGE);
        }
        try {
            final XfwExportConfig exportConfigObj = new ObjectMapper().readValue(exportConfig, XfwExportConfig.class);
            final List<DomainTo> objects = createListOfDomainTo(exportConfigObj.getRows());
            exportService.createExport(exportConfigObj, new ClientExportDataProvider(objects), request, response);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    /**
     * Создаёт список DTO из списка свойств.
     */
    private static List<DomainTo> createListOfDomainTo(List<Map<String, Object>> rows) {
        final List<DomainTo> dtos = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            dtos.add(createDomainTo(row));
        }
        return dtos;
    }

    /**
     * Создаёт DTO из списка свойста.
     */
    private static DomainTo createDomainTo(Map<String, Object> row) {
        final DomainTo dto = new DomainTo();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            dto.addProperty(entry.getKey(), entry.getValue());
        }
        return dto;
    }





}
