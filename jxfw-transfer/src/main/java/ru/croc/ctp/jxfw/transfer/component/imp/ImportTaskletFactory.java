package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.batch.core.step.tasklet.Tasklet;

/**
 * Фабрика для создания {@link Tasklet}, который реализует read => processor => writer операции импорта в рамках
 * одной транзакции.
 *
 * @since 1.6
 * @author Alexander Golovin
 */
public interface ImportTaskletFactory {
    /**
     * Создаёт {@link Tasklet}, который реализует read => processor => writer операции импорта в рамках
     * одной транзакции.
     *
     * @param reader reader
     * @param processor processor
     * @param writer writer
     * @return {@link Tasklet} реализующий read => processor => writer операции импорта в рамках одной транзакции.
     */
    Tasklet create(ImportGroupReader reader, ImportGroupProcessor processor, ImportGroupWriter writer);
}
