package ru.croc.ctp.jxfw.transfer.component.imp.context;

import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.Map;
import java.util.Set;

/**
 * Контекст содержащий информацию о зависимостях загружаемых доменных объектов
 * и об их рассположение в источнике данных. И сопутсвующие объекты участвующие
 * в импорте.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportContext {
    /**
     * Возвращает идентификатор ресурса.
     *
     * @return идентификатор ресурса.
     */
    String getPathOfImportFile();

    /**
     * Возвращает название кодировки ресурса.
     *
     * @return кодировка ресурса.
     */
    String getEncoding();

    /**
     *
     *
     * @return множество групп загружаемых объектов, пронумерованых от 1..n.
     */
    Map<Long, Set<ImportDtoInfo>> getGroupsOfLoading();
}
