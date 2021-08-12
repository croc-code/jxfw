package ru.croc.ctp.jxfw.transfer.wc;

import java.time.LocalDateTime;

/**
 * Базовый класс описания состояния операции Transfer Services.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class OperationStateInfo {

    private OperationStatus status;

    private Byte progressPercent;

    private String message;

    private String errorMessage;
    
    private LocalDateTime timeStamp;

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return Прогресс в %.
     */
    public Byte getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(Byte progressPercent) {
        this.progressPercent = progressPercent;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public void setStatus(OperationStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean getIsFinal() {
        return status == OperationStatus.Aborted
                || status == OperationStatus.Failed
                || status == OperationStatus.Completed;
    }
}
