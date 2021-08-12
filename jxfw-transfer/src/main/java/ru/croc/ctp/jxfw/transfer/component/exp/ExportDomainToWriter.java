package ru.croc.ctp.jxfw.transfer.component.exp;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemWriter;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

/**
 * Writer для {@link DomainTo} при экспорте данных через трансфер JXFW.
 * Использует {@link StepExecutionListener} для работы с контекстом экспорта.
 *
 * @author Alexander Golovin
 * @since 1.9
 */
public interface ExportDomainToWriter extends ItemWriter<DomainTo>, StepExecutionListener {
}
