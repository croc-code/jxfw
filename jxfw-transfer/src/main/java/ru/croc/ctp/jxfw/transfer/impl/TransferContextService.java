package ru.croc.ctp.jxfw.transfer.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceProperties;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.transfer.TransferService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * Вспомогательный сервис для работы с контекстом и его параметрами.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Service
public class TransferContextService {
    private static final Logger log = LoggerFactory.getLogger(TransferContextService.class);
    /**
     * Ключ в контексте к списку идентификаторов результирующих ресурсов в хранилище.
     * Внимание: при завершении Job'а, будет использоваться только первый элемент сприска(не забываем агрегировать).
     */
    public static final String RESULT_RESOURCES_IDS_KEY_NAME = "resId";
    /** Имя результирующего файла по умолчанию(без расширения). */
    public static final String DEFAULT_FILE_NAME = "output";
    /** Ключ в контексте к идентификатору ресурса, который импортируется. */
    public static final String IMPORT_RESOURCE_ID = TransferService.RESOURCE_ID;
    /** Ключ в контексте к списку локальных файлов. */
    public static final String LOCAL_FILES = "localFiles";

    /** Хранилище ресурсов. */
    private ResourceStore resourceStore;
    private ObjectMapper objectMapper;
    /** Директория для хранения локальных файлов. */
    private String directory;
    private Timer cleaner;

    /**
     * Конструктор.
     *
     * @param resourceStore хранилище ресурсов
     * @param periodOfCleaning период между очистками директории с файлами(секунды)
     * @param storeTime время хранения файлов в минутах
     */
    @Autowired
    public TransferContextService(ResourceStore resourceStore,
                                  ObjectMapper jacksonObjectMapper,
                                  @Value("${transfer.store.directory:transfer}") String directory,
                                  @Value("${transfer.store.periodOfCleaning:600}") Long periodOfCleaning,
                                  @Value("${transfer.store.storeTime:300}") Long storeTime) throws IOException {
        this.resourceStore = resourceStore;
        this.objectMapper = jacksonObjectMapper;
        this.directory = directory;
        final File folder = new File(directory);
        if (!folder.exists()) {
            Files.createDirectories(folder.toPath());
        }
        cleaner = new Timer();
        cleaner.schedule(new TimerTask() {
            @Override
            public void run() {
                cleanDirectory(storeTime*60*1000);
            }
        }, 1000 * periodOfCleaning, 1000 * periodOfCleaning);
    }

    /**
     * Удаляет файлы в {@code directory}, если последние изменения в них старше заданного периода.
     *
     * @param storeTime количество милисекунд после которого файл считается устаревшим.
     */
    public void cleanDirectory(Long storeTime) {
        try {
            final File[] files = new File(directory).listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.canWrite() && (new Date().getTime() - file.lastModified() >= storeTime)) {
                    log.debug("Starting to delete file: {}", file.getName());
                    file.delete();
                    log.debug("File {} is deleted.", file.getName());
                }
            }
        } catch (Exception e) {
            log.debug("Cann't clean directory.", e);
        }
    }

    /**
     * Информация о локальном файле.
     */
    public static class LocalFile {
        /** Имя файла с расширением. */
        public final String fileName;
        /** Полный путь к файлу с сложным служебным именем. т.к. имена могут пересекаться. */
        public final String path;

        /**
         * Информация о локальном файле.
         *
         * @param fileName имя файла с расширением
         * @param path Полный путь к файлу.
         */
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public LocalFile(@JsonProperty("fileName") String fileName, @JsonProperty("path") String path) {
            this.fileName = fileName;
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LocalFile localFile = (LocalFile) o;
            return fileName.equals(localFile.fileName) &&
                    path.equals(localFile.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fileName, path);
        }

        @Override
        public String toString() {
            return "LocalFile{" +
                    "fileName='" + fileName + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }
    }

    /**
     * Возвращает список локальных файлов.
     *
     * @param chunkContext контекст
     * @return список локальных файлов.
     */
    public List<LocalFile> getLocalFiles(ChunkContext chunkContext) throws IOException {
        final ExecutionContext executionContext = chunkContext
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();
        return getLocalFiles(executionContext);
    }

    /**
     * Возвращает список локальных файлов.
     *
     * @param executionContext контекст
     * @return список локальных файлов.
     */
    public List<LocalFile> getLocalFiles(ExecutionContext executionContext) throws IOException {
        if (executionContext.containsKey(LOCAL_FILES)) {
            final String jsonLocalFiles = executionContext.getString(LOCAL_FILES);
            if (jsonLocalFiles == null) {
                return Collections.emptyList();
            }
            return objectMapper.readValue(jsonLocalFiles, new TypeReference<List<LocalFile>>() {});
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Добавляет новые уникальные идентификаторы ресурсов в список.
     *
     * @param stepExecution контекст
     * @param newLocalFiles список добавляемых файлов.
     */
    public void addLocalFiles(StepExecution stepExecution, List<LocalFile> newLocalFiles) throws IOException {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        addLocalFiles(executionContext, newLocalFiles);
    }

    /**
     * Добавляет новые уникальные идентификаторы ресурсов в список.
     *
     * @param executionContext контекст
     * @param newLocalFiles список добавляемых файлов.
     */
    public void addLocalFiles(ExecutionContext executionContext, List<LocalFile> newLocalFiles) throws IOException {
        List<LocalFile> result = new ArrayList<>();
        result.addAll(getLocalFiles(executionContext));
        result.addAll(newLocalFiles);
        result = new ArrayList<>(new LinkedHashSet<>(result));// упорядоченные и уникальные
        executionContext.put(LOCAL_FILES, objectMapper.writeValueAsString(result));
    }

    /**
     * Устанавливает новые список уникальных локальных файлов.
     *
     * @param chunkContext контекст
     * @param localFiles список локальных файлов.
     */
    public void setLocalFiles(ChunkContext chunkContext, List<LocalFile> localFiles) throws IOException {
        final StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        setLocalFiles(stepExecution, localFiles);
    }

    /**
     * Устанавливает новые список уникальных локальных файлов.
     *
     * @param stepExecution контекст
     * @param localFiles список локальных файлов.
     */
    public void setLocalFiles(StepExecution stepExecution, List<LocalFile> localFiles) throws IOException {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();
        setLocalFiles(executionContext, localFiles);
    }

    /**
     * Устанавливает новые список уникальных локальных файлов.
     *
     * @param executionContext контекст
     * @param localFiles список локальных файлов.
     */
    public void setLocalFiles(ExecutionContext executionContext, List<LocalFile> localFiles) throws IOException {
        final List<LocalFile> result = new ArrayList<>(new LinkedHashSet<>(localFiles));
        executionContext.put(LOCAL_FILES, objectMapper.writeValueAsString(result));
    }

    /**
     * Создаёт копию ресурса в файловой системе вместо resourceId, если такое поле существует.
     * Внимание: ресурс удаляется из хранилища.
     *
     * @return значение свойства с ключем {@code LOCAL_FILES}.
     */
    public Optional<LocalFile> replaceLocalFileIfNewResourceId(ChunkContext chunkContext) throws IOException {
        return replaceLocalFileIfNewResourceId(chunkContext.getStepContext().getStepExecution());
    }

    /**
     * Создаёт копию ресурса в файловой системе вместо resourceId, если такое поле существует.
     * Внимание: ресурс удаляется из хранилища.
     *
     * @return значение свойства с ключем {@code LOCAL_FILES}.
     */
    public Optional<LocalFile> replaceLocalFileIfNewResourceId(StepExecution stepExecution) throws IOException {
        final ExecutionContext executionContext = stepExecution.getJobExecution().getExecutionContext();

        final String resourceId;
        if (executionContext.containsKey(IMPORT_RESOURCE_ID)) { // подменили ресурс на новый
            resourceId = executionContext.getString(IMPORT_RESOURCE_ID);
            executionContext.remove(IMPORT_RESOURCE_ID);
        } else if (!executionContext.containsKey(LOCAL_FILES)) { // не создавалась локальная копия
            resourceId = stepExecution.getJobParameters().getString(IMPORT_RESOURCE_ID);
        } else { // локальная копия остаётся без изменений
            return getFirstLocalFile(executionContext);
        }

        final LocalFile localFile = generateLocalFile(resourceStore.getResourceProperties(resourceId));
        final File file = new File(localFile.path);
        if (!file.getParentFile().exists()) {
            Files.createDirectories(file.getParentFile().toPath());
        }

        try (InputStream source = resourceStore.getResourceStream(resourceId);
             OutputStream target = new FileOutputStream(file)) {
            StreamUtils.copy(source, target);
        }

        setLocalFiles(stepExecution, Arrays.asList(localFile));
        resourceStore.deleteResource(resourceId);
        return getFirstLocalFile(executionContext);
    }

    /**
     * Формирует {@link LocalFile}.
     *
     * @param properties свойства ресурса в хранилище
     * @return информация о локальном файле.
     */
    protected LocalFile generateLocalFile(ResourceProperties properties) {
        if (properties != null) {
            return generateLocalFile(properties.getFileName());
        } else {
            throw new IllegalArgumentException("resourceProperties is null!");
        }
    }

    /**
     * Формирует {@link LocalFile}.
     *
     * @param fileName имя файла с расширением.
     * @return информация о локальном файле.
     */
    public LocalFile generateLocalFile(String fileName) {
        return new LocalFile(fileName, String.format("%s/%s-%s", directory, UUID.randomUUID(), fileName));
    }

    /**
     * Путь к первому локальному файлу.
     *
     * @return информация о первом локальном файле.
     */
    public Optional<LocalFile> getFirstLocalFile(ChunkContext chunkContext) throws IOException {
        final ExecutionContext executionContext = chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext();
        return getFirstLocalFile(executionContext);
    }

    /**
     * Путь к первому локальному файлу.
     *
     * @return информация о первом локальном файле.
     */
    public Optional<LocalFile> getFirstLocalFile(ExecutionContext executionContext) throws IOException {
        return getLocalFiles(executionContext).stream().findFirst();
    }


    /**
     * Загружает локальный файл в хранилище ресурсов.
     *
     * @param executionContext контекст
     * @return ид ресурса
     * @throws IOException ошибка работы с файловой системой или БД контекста
     */
    public String loadLocalFileToResourceStore(ExecutionContext executionContext) throws IOException {
        final Optional<LocalFile> localFile = getFirstLocalFile(executionContext);
        if (!localFile.isPresent()) {
            return null;
        }

        final String fileName = localFile.get().fileName;
        final int pointIndex = fileName.lastIndexOf(".");
        final ResourceProperties resourceProperties = new ResourceProperties(
                pointIndex >= 0 ? fileName.substring(fileName.lastIndexOf(".") + 1) : null,
                fileName,
                new File(localFile.get().path).length()
        );
        try (InputStream data = new FileInputStream(localFile.get().path)) {
            return resourceStore.addResource(resourceProperties, data);
        }
    }
}
