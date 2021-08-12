package ru.croc.ctp.jxfw.core.facade.webclient.file;

import java8.lang.Iterables;
import java8.util.Maps;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис временного хранилища. Хранит файлы в папке файловой системы.
 */
public class LocalResourceStore implements ResourceStore {

    private static final Logger logger = LoggerFactory.getLogger(LocalResourceStore.class);

    /**
     * Наименование общей папки. Для хранения контента, загруженного неаутентифицированными пользователями.
     */
    private static final String ALL_USERS_FOLDER_NAME = "all_users";

    /**
     * Ия папки для хранения контента. Создается сервисом в папке заданной в параметре конструктора.
     */
    private static final String INBOUND_DIR_NAME = "lrs_inbound";

    /**
     * Хранит созданные в рамках потока (thread) экземпляры {@link FileInputStream}
     * для того, чтобы можно было их закрыть в методе {@code endReading}.
     */
    private static final ThreadLocal<Map<String, List<InputStream>>> INPUT_STREAMS_LIST =
        new ThreadLocal<Map<String, List<InputStream>>>() {
            @Override
            protected Map<String, List<InputStream>> initialValue() {
                return new HashMap<>();
            }
        };

    /**
     * Доступное одному пользователю место для хранения загруженных файлов.
     */
    private final Long quotaPerUser;


    /**
     * Перечень хранимых ресурсов.
     */
    private final Map<String, ResourceMetaContainer> resIdMetaMap = new ConcurrentHashMap<>();

    private File inboundDir;

    /**
     * Конструктор.
     *
     * @param rootDirAbsolutePath Абсолютный путь файловой папки, где будет храниться контент
     * @param quotaPerUser        Объем хранилища в байтах, доступный одному пользователю
     */
    public LocalResourceStore(String rootDirAbsolutePath, Long quotaPerUser) {
        Assert.hasLength(rootDirAbsolutePath,
                "Constructor parameter rootDirAbsolutePath should not be null or empty string");
        Assert.notNull(quotaPerUser, "Constructor parameter quotaPerUser should not be null");
        this.quotaPerUser = quotaPerUser;

        inboundDir = Paths.get(rootDirAbsolutePath).resolve(INBOUND_DIR_NAME).toFile();
        //noinspection ResultOfMethodCallIgnored
        inboundDir.mkdir();

    }

    @Override
    public String addResource(ResourceProperties resourceProperties, InputStream contentStream) throws IOException {
        //проверки для конкретного пользователя может выбрасывать runtime exception !!!
        runUserChecksBeforeUpload(resourceProperties);

        File dir = getUserInboundDir();
        File file = dir.toPath().resolve(Thread.currentThread().getName() + "_" + System.currentTimeMillis() + "_"
                + resourceProperties.getFileName()).toFile();
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(contentStream, out);
        }

        String resourceId = createResourceId();
        resIdMetaMap.put(resourceId, new ResourceMetaContainer(resourceProperties, file));

        logger.debug("Resource added: " + resourceId);

        return resourceId;
    }

    @Override
    public void deleteResource(String resourceId) {
        Map<String, List<InputStream>> openedStreams = INPUT_STREAMS_LIST.get();
        List<InputStream> resourceOpenedStreams = openedStreams.get(resourceId);
        if (resourceOpenedStreams != null) {
            Iterables.forEach(resourceOpenedStreams, inputStream -> {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            openedStreams.remove(resourceId);
        }
        doDeleteResource(resourceId);
    }

    private void doDeleteResource(String resourceId) {
        ResourceMetaContainer resourceMetaContainer = resIdMetaMap.get(resourceId);
        if (resourceMetaContainer != null) {
            File fileToRemove = resourceMetaContainer.file;
            if (fileToRemove != null) {
                if (fileToRemove.delete()) {
                    resIdMetaMap.remove(resourceId);
                }
            }
        }
    }

    @Override
    public void startReading() {
        INPUT_STREAMS_LIST.get().clear();
    }

    @Override
    public void endReading() {
        Map<String, List<InputStream>> openedStreams = INPUT_STREAMS_LIST.get();

        Maps.forEach(openedStreams, (resourceId, streams) -> {
            Iterables.forEach(streams, inputStream -> {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            doDeleteResource(resourceId);
        });

        openedStreams.clear();
        INPUT_STREAMS_LIST.remove();
    }

    @Override
    public InputStream getResourceStream(String resourceId) {
        File file = getFile(resourceId);
        InputStream inputStream;
        try {
            inputStream = new ResettableFileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
        Map<String, List<InputStream>> openedStreams = INPUT_STREAMS_LIST.get();
        List<InputStream> resourceOpenedStreams = openedStreams.get(resourceId);
        if (resourceOpenedStreams == null) {
            resourceOpenedStreams = new ArrayList<>();
            openedStreams.put(resourceId, resourceOpenedStreams);
        }
        resourceOpenedStreams.add(inputStream);
        return inputStream;
    }

    /**
     * Возвращает файл, в который был сохранен ресурс с заданным ID.
     *
     * @param resourceId Идентификатор ресруса
     * @return Файл, в котором сохранен ресурс
     */
    public File getFile(String resourceId) {
        return Paths.get(getResourceMetaContainer(resourceId).getFile().getPath()).toFile();
    }

    private ResourceMetaContainer getResourceMetaContainer(String resourceId) {
        ResourceMetaContainer resourceMetaContainer = resIdMetaMap.get(resourceId);
        if (resourceMetaContainer == null) {
            throw new IllegalStateException(
                    "Resource not found in the store: " + resourceId);
        }
        return resourceMetaContainer;
    }

    @Override
    public ResourceProperties getResourceProperties(String resourceId) {
        return getResourceMetaContainer(resourceId).getResourceProperties();
    }

    /**
     * Выполнить проверки перед сохарнением.
     *
     * @param resourceProperties настройки ресурса.
     */
    protected void runUserChecksBeforeUpload(ResourceProperties resourceProperties) {
    }

    /**
     * @return получить пользовательскую директорию для сохранения файлов.
     */
    public File getUserInboundDir() {
        File dir = inboundDir.toPath().resolve(getStoreFolderName()).toFile();
        //noinspection ResultOfMethodCallIgnored
        dir.mkdir();
        return dir;
    }

    private String createResourceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return возвращается имя папки дял хранения файлов.
     */
    protected String getStoreFolderName() {
        return ALL_USERS_FOLDER_NAME; //возвращаем имя общей папки.
    }

    public Long getQuotaPerUser() {
        return quotaPerUser;
    }

    /**
     * Контейнер информации о хранимом ресурсе.
     */
    private static class ResourceMetaContainer {
        private ResourceProperties resourceProperties;

        private File file;

        ResourceMetaContainer(ResourceProperties resourceProperties, File file) {
            this.resourceProperties = resourceProperties;
            this.file = file;
        }

        File getFile() {
            return file;
        }

        ResourceProperties getResourceProperties() {
            return resourceProperties;
        }

    }
}