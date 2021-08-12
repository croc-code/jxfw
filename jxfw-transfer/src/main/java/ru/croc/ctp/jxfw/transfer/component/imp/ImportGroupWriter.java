package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.batch.item.ItemWriter;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;

/**
 * Компонент записи группы импорта доменных объектов в репозитории.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportGroupWriter extends ItemWriter<ImportGroup> {
}
