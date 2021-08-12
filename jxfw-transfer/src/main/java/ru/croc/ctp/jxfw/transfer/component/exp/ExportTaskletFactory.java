package ru.croc.ctp.jxfw.transfer.component.exp;

import org.springframework.batch.core.step.tasklet.Tasklet;

/**
 * Фабрика {@link Tasklet} компонентов используемых при экспорте.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public interface ExportTaskletFactory {
    /**
     * Создаёт {@link Tasklet} заменяющий результирующий список идентификаторов на
     * один идентификтор, представляющий zip-архив агрегирующий файлы из списка.
     *
     * @param archiveFileName имя результирующего файла архива(без расширения)
     * @return {@link Tasklet}.
     */
    Tasklet createAggregateFilesToZipTasklet(String archiveFileName);

}
