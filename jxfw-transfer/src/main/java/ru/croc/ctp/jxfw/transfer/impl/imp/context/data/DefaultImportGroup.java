package ru.croc.ctp.jxfw.transfer.impl.imp.context.data;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Группа импортируемых доменных объектов.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class DefaultImportGroup implements ImportGroup {
    /** Идентификатор группы в контексте импорта. */
    private final Long id;
    /** Список импортируемых объектов. */
    private final List<DomainTo> objects;

    /** Создаёт объект содержащий группу импортируемых доменных объектов.
     * @param id идентификатор группы.
     * @param objects список объектов группы.
     */
    public DefaultImportGroup(Long id, List<DomainTo> objects) {
        this.id = id;
        this.objects = objects;
    }

    /** Создаёт объект содержащий пустую группу импортируемых доменных объектов.
     * @param id идентификатор группы.
     */
    public DefaultImportGroup(Long id) {
        this(id, new ArrayList<>());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<DomainTo> getObjects() {
        return objects;
    }
}
