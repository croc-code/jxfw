package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;


/**
 * Компонент для чтения групп объектов из файла импорта.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportGroupReader extends ItemReader<ImportGroup>, ItemStream, StepExecutionListener {
}
