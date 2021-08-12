package ru.croc.ctp.jxfw.transfer.component.imp.context.data;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.List;

/**
 * Группа импортируемых доменных объектов.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportGroup {
    /**
     * @return Идентификатор группы в контексте импорта.
     */
    Long getId();

    /**
     * @return Список импортируемых объектов.
     */
    List<DomainTo> getObjects();
}
