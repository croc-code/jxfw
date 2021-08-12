package ru.croc.ctp.jxfw.transfer.component.exp;

import org.springframework.batch.item.ItemProcessor;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

/**
 * Компонент для преобразования доменных объектов в соответствующие TO объекты.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ExportDomainToProcessor extends ItemProcessor<DomainObject<?>, DomainTo> {
}
