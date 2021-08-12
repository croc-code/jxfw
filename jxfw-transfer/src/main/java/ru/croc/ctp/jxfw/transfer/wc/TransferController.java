package ru.croc.ctp.jxfw.transfer.wc;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static ru.croc.ctp.jxfw.transfer.TransferService.OPERATION_ID;
import static ru.croc.ctp.jxfw.transfer.TransferService.OPERATION_TYPE;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ctp.jxfw.core.facade.webclient.ControllerBase;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.transfer.TransferService;
import ru.croc.ctp.jxfw.transfer.impl.exp.ExecuteExportRequest;
import ru.croc.ctp.jxfw.transfer.impl.imp.ExecuteImportRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер для работы WC и jXFW + модуль transfer.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@RestController
@RequestMapping("api/_transfer")
public class TransferController extends ControllerBase {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    private TransferService transferService;

    private ResourceStore resourceStore;

    @Autowired
    public void setResourceStore(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
    }

    @Autowired
    public void setTransferService(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Запустить задачу Export (job) на выполнение.
     *
     * @param scenario   имя сценария задачи
     * @param allRequestParams все приходящие параметры запроса.
     * @return результат выполнения задачи, объект класса {@link TransferOperationStartResult}.
     */
    @RequestMapping(value = "startexport", method = {POST, GET})
    public ResponseEntity<TransferOperationStartResult> startExport(
            @RequestParam String scenario,
            @RequestParam(required = false) Map<String,String> allRequestParams) {
        allRequestParams.remove("scenario");
        log.debug("start export operation with scenario = {}", scenario);
        final ExecuteExportRequest exportRequest = new ExecuteExportRequest.ExecuteExportRequestBuilder()
                .scenarioName(scenario)
                .allParams(allRequestParams)
                .build();

        final String operationId = transferService.executeExport(exportRequest);
        final TransferOperationStartResult operationStartResult
                = new TransferOperationStartResult(operationId);
        return new ResponseEntity<>(operationStartResult, HttpStatus.CREATED);
    }

    /**
     * Запустить задачу Import на выполнение.
     *
     * @param resourceId идентификатор ресурса,
     *                   загруженного с помощью {@link ru.croc.ctp.jxfw.facade.webclient.file.FileApiController}
     * @param scenario   имя сценария задачи (опционально)
     * @param allRequestParams все приходящие параметры запроса.
     * @return результат выполнения задачи, объект класса {@link TransferOperationStartResult}.
     */
    @RequestMapping(value = "startimport", method = {POST, GET})
    public ResponseEntity<TransferOperationStartResult> startImport(
            @RequestParam String resourceId,
            @RequestParam(required = false) String scenario,
            @RequestParam(required = false) Map<String,String> allRequestParams) {
        allRequestParams.remove("resourceId");
        allRequestParams.remove("scenario");
        log.debug("start import operation with params resourceId = {} and scenario = {}", resourceId, scenario);
        final ExecuteImportRequest importRequest = new ExecuteImportRequest.ExecuteImportRequestBuilder()
                .scenarioName(scenario)
                .uploadedDataId(resourceId)
                .allParams(allRequestParams)
                .build();

        final String operationId = transferService.executeImport(importRequest);
        final TransferOperationStartResult operationStartResult
                = new TransferOperationStartResult(operationId);
        return new ResponseEntity<>(operationStartResult, HttpStatus.CREATED);
    }

    /**
     * Получение статуса (истории состояний) операции.
     *
     * @param opId  идентификатор операции
     * @param since начиная с какого времени получить историю состояний (опционально)
     * @return статус выполняемой задачи
     */
    @RequestMapping(value = "getstatus", method = GET)
    public ResponseEntity<TransferOperationStateHistory> getStatus(
            final String opId, @RequestParam(required = false) LocalDateTime since) {
        final TransferOperationStateHistory transferOperationStateHistory = new TransferOperationStateHistory();
        final OperationStateInfo status = transferService.getStatus(opId);
        transferOperationStateHistory.setLastState(status);
        return new ResponseEntity<>(transferOperationStateHistory, HttpStatus.OK);
    }

    /**
     * Возобновление приостановленной операции.
     *
     * @param opId   идентификатор операции
     * @param action начиная с какого времени получить историю состояний (опционально)
     */
    @RequestMapping(value = "resume", method = {POST, GET})
    public void resume(String opId, int action) {
    }

    /**
     * Прерывание операции.
     *
     * @param opId идентификатор операции
     * @return успешность прерывания операции.
     */
    @RequestMapping(value = "abort", method = {POST, GET})
    public boolean abort(String opId) {
        return transferService.abort(opId);
    }

    /**
     * Скачивание файла с данными экспорта.
     *
     * @param request  объект HTTP запроса
     * @param response объект HTTP ответа
     * @param opId     идентификатор операции
     */
    @RequestMapping(value = "download", method = GET)
    public void download(final HttpServletRequest request,
                         final HttpServletResponse response,
                         @RequestParam String opId) {
        log.debug("Downloading resource " + opId);

        final String resourceId = transferService.downloadResourceId(opId);
        try (InputStream stream = resourceStore.getResourceStream(resourceId)) {
            //ВАЖНО!!! Устанавливать заголовки ответа нужно до вызова response.getOutputStream()
            buildBinPropResponse(request, response, resourceStore.getResourceProperties(resourceId));

            IOUtils.copy(stream, response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Получить список оперций.
     *
     * @param includeCompleted - включая завершенные
     * @return список операций в формате {@link TransferOperationListResult}
     */
    @RequestMapping(value = "list", method = GET)
    public ResponseEntity<TransferOperationListResult> getOperations(
            @RequestParam(required = false, defaultValue = "false") boolean includeCompleted) {
        log.debug("obtain all operations");
        final List<JobExecution> operations = transferService.operations();

        final List<TransferOperationListItem> resultList = new ArrayList<>();
        for (final JobExecution operation : operations) {
            final String operationId = operation.getJobParameters().getString(OPERATION_ID);
            log.debug("process operation with operationId = {}", operationId);

            if (operationId != null) {
                final OperationStatus status = OperationStatus.byBatchStatus(operation.getStatus());
                final String type = operation.getJobParameters().getString(OPERATION_TYPE);
                final TransferOperationListItem item
                        = new TransferOperationListItem(operationId, type, false);

                if (OperationStatus.isFinal(status)) {
                    if (includeCompleted) {
                        item.setCompleted(true);
                        resultList.add(item);
                    }
                } else {
                    resultList.add(item);
                }
            }
        }

        return new ResponseEntity<>(new TransferOperationListResult(resultList), HttpStatus.OK);
    }
}
