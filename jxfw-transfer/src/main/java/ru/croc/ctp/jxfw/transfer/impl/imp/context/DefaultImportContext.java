package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContext;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
public class DefaultImportContext implements ImportContext {
    /** Исочник импорта. */
    private String pathOfImportFile;
    /** Кодировка файла. */
    private String encoding = StandardCharsets.UTF_8.displayName();
    /** Списки и структура доменных объектов участвующих в импорте.
     * тип -> ид -> {@link ImportDtoInfo}. */
    @JsonIgnore
    private Map<String, Map<String, ImportDtoInfo>> objects = new HashMap<>();
    /** Группы доменных объектов, которые можно загружать по отдельности. */
    private Map<Long, Set<ImportDtoInfo>> groupsOfLoading = new HashMap<>();

    /** Новый контекст содержащий информацию о зависимостях загружаемых доменных объектов
     * и об их рассположение в источнике данных. И сопутсвующие объекты участвующие в импорте.
     */
    public DefaultImportContext() {
    }

    /** Новый контекст содержащий информацию о зависимостях загружаемых доменных объектов
     * и об их рассположение в источнике данных. И сопутсвующие объекты участвующие в импорте.
     * @param pathOfImportFile путь к файлу импорта.
     */
    protected DefaultImportContext(String pathOfImportFile) {
        this.pathOfImportFile = pathOfImportFile;
    }

    public String getPathOfImportFile() {
        return pathOfImportFile;
    }

    protected Map<String, Map<String, ImportDtoInfo>> getObjects() {
        return objects;
    }

    public Map<Long, Set<ImportDtoInfo>> getGroupsOfLoading() {
        return groupsOfLoading;
    }

    protected void setPathOfImportFile(String pathOfImportFile) {
        this.pathOfImportFile = pathOfImportFile;
    }

    protected void setObjects(Map<String, Map<String, ImportDtoInfo>> objects) {
        this.objects = objects;
    }

    protected void setGroupsOfLoading(Map<Long, Set<ImportDtoInfo>> groupsOfLoading) {
        this.groupsOfLoading = groupsOfLoading;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
