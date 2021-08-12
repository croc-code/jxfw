package ru.croc.ctp.jxfw.transfer.wc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Контейнер для передачи информации об истории состояний операции Transfer Services.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class TransferOperationStateHistory {

    private LocalDateTime collectingDateTime = LocalDateTime.now();

    private OperationStateInfo lastState;

    private List<OperationStateInfo> intermediateStates = new ArrayList<>();

    /**
     * @return Последнее (текущее) состояние операции.
     */
    public OperationStateInfo getLastState() {
        return lastState;
    }

    public void setLastState(OperationStateInfo lastState) {
        this.lastState = lastState;
    }

    /**
     * @return Серверное время формирования истории.
     */
    public LocalDateTime getCollectingDateTime() {
        return collectingDateTime;
    }

    public void setCollectingDateTime(LocalDateTime collectingDateTime) {
        this.collectingDateTime = collectingDateTime;
    }

    public List<OperationStateInfo> getIntermediateStates() {
        return intermediateStates;
    }

    public void setIntermediateStates(List<OperationStateInfo> intermediateStates) {
        this.intermediateStates = intermediateStates;
    }
}
