package ru.croc.ctp.jxfw.transfer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.TransferService;

/**
 * Подготавливает окружение текущего экземпляра сервиса трансфер.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Component
public class InitializingTransferService {
    private TransferService transferService;

    /**
     * Подготавливает окружение текущего экземпляра сервиса трансфер.
     *
     * @param transferService сервис трансфер.
     */
    @Autowired
    public InitializingTransferService(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Останавливает все старые операции оставшиеся от текущего экземпляра сервиса. Операции могли
     * остаться при завершении работы сервиса с активными операциями. Такие операции
     * перезапустить нельзя.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void stopOldOperationsOfCurrentInstance() {
        transferService.operationsOfCurrentInstance().forEach(jobExecution -> {
            String operationId = jobExecution.getJobParameters().getString(TransferService.OPERATION_ID);
            transferService.abort(operationId);
        });
    }
}
