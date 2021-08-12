package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.batch.item.ItemProcessor;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;

/**
 * Обработчик импортируемой группы доменных объектов.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportGroupProcessor extends ItemProcessor<ImportGroup, ImportGroup> {

}
