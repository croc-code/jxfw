package ru.croc.ctp.jxfw.transfer.wc;

/**
 * Контейнер для передачи на WC информации об операции.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class TransferOperationListItem {
    private String opId;

    private String type;

    private boolean isCompleted;

    /**
     * Конструктор.
     *
     * @param opId        ИД операции
     * @param type        тип операции
     * @param isCompleted статус опреции (завершена или не завершена)
     */
    public TransferOperationListItem(String opId, String type, boolean isCompleted) {
        this.opId = opId;
        this.type = type;
        this.isCompleted = isCompleted;
    }

    /**
     * Конструктор.
     */
    private TransferOperationListItem() {
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
