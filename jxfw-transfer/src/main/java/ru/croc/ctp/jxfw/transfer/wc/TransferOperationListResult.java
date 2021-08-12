package ru.croc.ctp.jxfw.transfer.wc;

import java.util.List;

/**
 * Контейнер для хранения и передачи на WC информации о доступных операциях.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class TransferOperationListResult {
    private List<TransferOperationListItem> operations;

    /**
     * Список операций.
     *
     * @param operations список операций
     */
    public TransferOperationListResult(List<TransferOperationListItem> operations) {
        this.operations = operations;
    }

    /**
     * Конструктор.
     */
    private TransferOperationListResult() {
    }

    public List<TransferOperationListItem> getOperations() {
        return operations;
    }

    public void setOperations(List<TransferOperationListItem> operations) {
        this.operations = operations;
    }
}
