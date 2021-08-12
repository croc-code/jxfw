package ru.croc.ctp.jxfw.transfer.impl.imp.context.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Структура данных для удобства хранения информации о доменном объекта и его связях в файле.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class ImportDtoInfo {
    /** Идетификатор объекта. */
    private String id;
    /** Тип объекта. */
    private String type;

    /** Полный список зависимотсей. */
    @JsonIgnore
    private final Set<ImportDtoInfo> dependencies;
    /** Список неразрешенных зависимостей из источника. */
    @JsonIgnore
    private final Set<ImportDtoInfo> dependenciesOfNotResolvedInSource;
    /** Список неразрешенных зависимостей из источника и репозитория. */
    @JsonIgnore
    private final Set<ImportDtoInfo> dependenciesOfNotResolved;

    /** DTO, если не загружен, то null. */
    @JsonIgnore
    private DomainTo domainTo = null;
    /** Смещение первого байта объекта от начада файла. Если меньше 0, то из файла загрузки не было. */
    private long offsetFirstByteInFile = -1;
    /** Смещение последнего байта объекта от начада файла. Если меньше 0, то из файла загрузки не было. */
    private long offsetLastByteInFile = -1;
    /** Значение позиции в файле, если файл не загружали. */
    public static final long NOT_LOADED = -1;

    /** Создаёт объект с незаполненым описанием связей и рассположения доменного объекта в файле.
     * @param type тип доменного объекта.
     * @param id идентификатор объекта.
     */
    public ImportDtoInfo(String type, String id) {
        this();
        this.type = type;
        this.id = id;
    }

    /** Создаёт объект с незаполненым описанием связей и рассположения доменного объекта в файле.*/
    public ImportDtoInfo() {
        dependencies = new HashSet<>();
        dependenciesOfNotResolvedInSource = new HashSet<>();
        dependenciesOfNotResolved = new HashSet<>();
    }

    /** Проверяет был ли dto загружен из файла.
     * @return true если объект загружался из файла, иначе false.
     */
    @JsonIgnore
    public boolean isLoadFromFile() {
        return offsetFirstByteInFile >= 0 && offsetLastByteInFile >= 0;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    /**
     * @return Полный список зависимотсей.
     */
    public Set<ImportDtoInfo> getDependencies() {
        return dependencies;
    }

    /**
     * @return Список не разрешенных зависимостей из источника.
     */
    public Set<ImportDtoInfo> getDependenciesOfNotResolvedInSource() {
        return dependenciesOfNotResolvedInSource;
    }

    /**
     * @return Список не разрешенных зависимостей из источника и репозитория.
     */
    public Set<ImportDtoInfo> getDependenciesOfNotResolved() {
        return dependenciesOfNotResolved;
    }

    /**
     * @return DTO, если не загружен, то null.
     */
    public DomainTo getDomainTo() {
        return domainTo;
    }

    public void setDomainTo(DomainTo domainTo) {
        this.domainTo = domainTo;
    }

    /**
     * @return Смещение первого байта объекта от начада файла. Если меньше 0, то из файла загрузки не было.
     */
    public long getOffsetFirstByteInFile() {
        return offsetFirstByteInFile;
    }

    public void setOffsetFirstByteInFile(long offsetFirstByteInFile) {
        this.offsetFirstByteInFile = offsetFirstByteInFile;
    }

    /**
     * @return Смещение последнего байта объекта от начада файла. Если меньше 0, то из файла загрузки не было.
     */
    public long getOffsetLastByteInFile() {
        return offsetLastByteInFile;
    }

    public void setOffsetLastByteInFile(long offsetLastByteInFile) {
        this.offsetLastByteInFile = offsetLastByteInFile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    /**
     * Билдер для {@link ImportDtoInfo}.
     *
     * @author Golovin Alexander
     * @since 1.5
     */
    public static class Builder {
        private final ImportDtoInfo dtoInfo;

        /** Новый билдер для {@link ImportDtoInfo}.
         * @param type тип доменного объекта.
         * @param id идентификатор объекта.
         */
        public Builder(String type, String id) {
            this.dtoInfo = new ImportDtoInfo(type, id);
        }

        /** Устанавливает доменный объект.
         * @param domainTo доменный объект.
         * @return текущий билдер.
         */
        public Builder domainTo(DomainTo domainTo) {
            dtoInfo.setDomainTo(domainTo);
            return this;
        }

        /** Устанавливает смещение первого байта объекта от начала файла.
         * @param value значение смещения первого байта объекта от начала файла.
         * @return текущий билдер.
         */
        public Builder offsetFirstByteInFile(long value) {
            dtoInfo.setOffsetFirstByteInFile(value);
            return this;
        }

        /** Устанавливает смещение последнего байта объекта от начала файла.
         * @param value значение смещения последнего байта объекта от начала файла.
         * @return текущий билдер.
         */
        public Builder offsetLastByteInFile(long value) {
            dtoInfo.setOffsetLastByteInFile(value);
            return this;
        }

        /** Возращает собранный {@link ImportDtoInfo}. Возращается всегда один объект, а не копия.
         * @return собранный {@link ImportDtoInfo}.
         */
        public ImportDtoInfo build() {
            return dtoInfo;
        }
    }
}
