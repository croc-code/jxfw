package ru.croc.ctp.jxfw.core.export;

import ru.croc.ctp.jxfw.core.export.impl.model.XfwExportConfig;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Сервис для экспорта данных из списков.
 *
 * @author Nosov Alexander
 * @since 1.0
 */
public interface ExportService {
    /**
     * Метод для отправки на клиент готового файла экспорта списка с заданным форматом.
     *
     * @param exportConfig - конфиг для экспорта
     * @param dataProvider      - данные для экспорта
     * @param request      - объект HTTP запроса
     * @param response     - объект HTTP ответа
     */
    void createExport(final XfwExportConfig exportConfig,
                             final ExportDataProvider dataProvider,
                             final HttpServletRequest request,
                             final HttpServletResponse response);


}
