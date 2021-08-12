package ru.croc.ctp.jxfw.transfer.component;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

/**
 * Компонент временного хранения объектов для агрегации результата выполнения
 * нескольких шагов задачи.
 * 
 * @author Alexander Golovin
 * @since 1.5
 */
public interface DataHolder extends ItemWriter<DomainTo>, ItemReader<DomainTo> {
}
