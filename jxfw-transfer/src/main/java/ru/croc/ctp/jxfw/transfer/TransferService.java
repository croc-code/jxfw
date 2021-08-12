package ru.croc.ctp.jxfw.transfer;

import org.springframework.batch.core.JobExecution;
import ru.croc.ctp.jxfw.transfer.impl.exp.ExecuteExportRequest;
import ru.croc.ctp.jxfw.transfer.impl.imp.ExecuteImportRequest;
import ru.croc.ctp.jxfw.transfer.wc.OperationStateInfo;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Nosov Alexander
 * @since 1.4
 */
public interface TransferService {

    /**
     * ключ для параметра - ИД операции.
     */
    String OPERATION_ID = "operationId";

    /**
     * ключ для параметра - ИД экземпляра приложения.
     */
    String INSTANCE_ID = "instanceId";

    /**
     * ключ для параметра - имя операции.
     */
    String SCENARIO_NAME = "scenarioName";

    /**
     * ключ для параметра - ИД ресурса.
     */
    String RESOURCE_ID = "resourceId";

    /**
     * ключ ждя параметра - сообщение об ошибке.
     */
    String FAILED_MESSAGE = "failedMessage";

    /**
     * ключ ждя параметра - тип операции.
     */
    String OPERATION_TYPE = "operationType";


    /**
     * ключ ждя параметра - дата создания операции.
     */
    String CREATION_DATE = "creationDate";

    /**
     * Запустить задачу импорта, на основе данных переданных в объекте {@link ExecuteImportRequest}.
     *
     * @param request данные для операции импорта (объект {@link ExecuteImportRequest}).
     * @return идентификатор запущенной операции импорта.
     */
    @Nonnull
    String executeImport(@Nonnull ExecuteImportRequest request);

    /**
     * Запускает операцию экспорта, на основе данных переданных в объекте {@link ExecuteImportRequest}.
     *
     * @param request запрос с параметрами для запуска операции экспорта (объект {@link ExecuteImportRequest}).
     * @return идентификатор запущенной операции.
     */
    @Nonnull
    String executeExport(@Nonnull ExecuteExportRequest request);

    /**
     * Получить список операций доступных в системе.
     *
     * @return список оперций.
     */
    @Nonnull
    List<JobExecution> operations();


    /**
     * Получить статус по операции.
     *
     * @param opId идентификатор операции
     * @return последняя операция
     */
    @Nullable
    OperationStateInfo getStatus(@Nonnull String opId);

    /**
     * Скачать файл с результатом выполнения операции.
     *
     * @param operationId ИД операции
     * @return ИД файла в хранилище, может быть {@code null}.
     */
    @Nullable
    String downloadResourceId(@Nonnull String operationId);

    /**
     * Прервать операцию.
     * 
     * @param operationId ИД операции
     * @return успех операции
     */
    boolean abort(@Nonnull String operationId);

    /**
     * Возвращает идентификатор экземпляра приложения установленный для сервиса трансфер.
     *
     * @return идентификатор.
     */
    String getInstanceId();

    /**
     * Получить список операций выполняющихся на текущем экземпляре сервиса.
     *
     * @return список оперций.
     */
    @Nonnull
    List<JobExecution> operationsOfCurrentInstance();
}
