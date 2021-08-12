package ru.croc.ctp.jxfw.transfer.wc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Результат запуска операции.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class TransferOperationStartResult {

    @JsonProperty("opId")
    private String operationId;

    /**
     * Конструктор.
     *
     * @param operationId - идентификатор запущенной операции.
     */
    public TransferOperationStartResult(String operationId) {
        this.operationId = operationId;
    }

    public String getOperationId() {
        return operationId;
    }
}
