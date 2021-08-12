package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.batch.item.ItemWriter;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.List;

/**
 * Процессор для импортирования доменных объектов в БД.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public interface ImportDomainToWriter extends ItemWriter<List<? extends DomainTo>> {
}
