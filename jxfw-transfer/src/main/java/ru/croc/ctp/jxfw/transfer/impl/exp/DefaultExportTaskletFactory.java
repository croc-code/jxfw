package ru.croc.ctp.jxfw.transfer.impl.exp;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportTaskletFactory;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.tasklet.ExportAggregateFilesToZipTasklet;

/**
 * Фабрика {@link Tasklet} компонентов используемых при экспорте.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Component("exportTaskletFactory")
public class DefaultExportTaskletFactory implements ExportTaskletFactory {
    private TransferContextService transferContextService;

    /**
     * Фабрика для {@link Tasklet}, которые задают параметры контекста экспорта для дальнейших шагов.
     *
     * @param transferContextService сервис для работы с контекстом.
     */
    @Autowired
    public DefaultExportTaskletFactory(TransferContextService transferContextService) {
        this.transferContextService = transferContextService;
    }

    @Override
    public Tasklet createAggregateFilesToZipTasklet(String archiveFileName) {
        return new ExportAggregateFilesToZipTasklet(transferContextService, archiveFileName);
    }
}
