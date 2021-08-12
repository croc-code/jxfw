package ru.croc.ctp.jxfw.transfer.wc;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.batch.core.BatchStatus;

/**
 * Статус асинхронной операции Transfer Services.
 *
 * @author Nosov Alexander
 */
public enum OperationStatus {
    /**
     * Статус не задан/не известен.
     */
    None(0),
    /**
     * Еще не запущена.
     */
    StartingUp(1),
    /**
     * Выполняется.
     */
    Running(2),
    /**
     * Запрошена отмена.
     */
    Aborting(3),
    /**
     * Отменена. Финальное состояние.
     */
    Aborted(4),
    /**
     * Завершена из-за ошибки. Финальное состояние.
     */
    Failed(5),
    /**
     * Приостановлена.
     */
    Suspended(6),
    /**
     * Выполнена. Финальное состояние.
     */
    Completed(7),
    /**
     * Происходит передача данных экспорта/импорта.
     */
    StreamingData(8);


    private final int code;

    /**
     * @param code код операции.
     */
    OperationStatus(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }


    /**
     * Получить статус операции для WC по {@link BatchStatus}.
     *
     * @param batchStatus статус операции в Spring Batch
     * @return статус операции для WC
     */
    public static OperationStatus byBatchStatus(BatchStatus batchStatus) {
        switch (batchStatus) {
            case COMPLETED:
                return Completed;
            case FAILED:
                return Failed;
            case STARTED:
                return Running;
            case UNKNOWN:
                return None;
            case STARTING:
                return StartingUp;
            case STOPPING:
                return Aborting;
            case STOPPED:
            case ABANDONED:
                return Aborted;
            default:
                return None;
        }
    }

    /**
     * Проверяет заверешенна ли операция.
     *
     * @param status статус операции.
     * @return true если операция завершена.
     */
    public static boolean isFinal(OperationStatus status) {
        switch (status) {
            case Completed:
            case Failed:
            case Suspended:
            case Aborted:
                return true;
            default:
                return false;
        }
    }
}
