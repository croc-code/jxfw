package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportDependencyManager;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotNull;

/**
 * Управляет жизненым циклом контекстов импорта. Обеспечивает использования одного и того же контекста
 * для различных операций с одним ресурсом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public class DefaultImportContextManager implements ImportContextManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultImportContextManager.class);
    private FileDtoReaderFactory readerFactory;
    private DomainServicesResolver resolver;


    /** Директория в которой будут сохранятся файлы с контекстами. */
    private String contextStoreDirectory;
    private String suffixIndexFileName = "-context.json";
    /** Задача очистки старых файлов контекста в фоне. */
    private Timer cleanerTask;
    private int periodOfMinutes = 60;

    private LoadingCache<ContextKey, DefaultImportContext> contexts = CacheBuilder.newBuilder()
            .expireAfterAccess(20, TimeUnit.MINUTES)
            .build(new CacheLoader<ContextKey, DefaultImportContext>() {
                @Override
                public DefaultImportContext load(ContextKey contextKey) throws Exception {
                    if (Files.exists(getPathOfContextFile(contextKey.pathOfImportFile, suffixIndexFileName))) {
                        return loadImportContext(contextKey.pathOfImportFile);
                    } else {
                        return createImportContext(contextKey);
                    }
                }
            });


    /** Новый контейнер контекстов импорта. */
    private DefaultImportContextManager() {
    }

    /**
     * Возращает контекст для текущего ресурса.
     *
     * @param pathOfImportFile путь к файлу импорта.
     * @param dependencyManager компонент проверяющий корректность загружаемых объектов.
     * @param splitter компонент для разбиения объектов по группам.
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа, иначе падаем.
     * @param ignoreBidirectional игнорировать ли обратные ссылки.
     * @param domainFacadeIgnoreService сервис определяющий игнорируемые фасадом поля.
     * @return формированный контекст импорта.
     * @throws ExecutionException если в процессе загрузки контекста произошла ошибка.
     */
    @Override
    public DefaultImportContext getImportContext(
            String pathOfImportFile,
            ImportDependencyManager dependencyManager,
            ImportContextSplitterAndAggregator splitter,
            boolean isIgnoreObjectsOfUnknownType,
            boolean ignoreBidirectional,
            DomainFacadeIgnoreService domainFacadeIgnoreService) throws ExecutionException {
        return contexts.get(new ContextKey(
                pathOfImportFile,
                dependencyManager,
                splitter,
                isIgnoreObjectsOfUnknownType,
                ignoreBidirectional,
                domainFacadeIgnoreService
        ));
    }

    /** Класс контейнер для передачи обработчиков в контекст. */
    private static class ContextKey {
        String pathOfImportFile;
        ImportDependencyManager dependencyManager;
        ImportContextSplitterAndAggregator splitter;
        boolean isIgnoreObjectsOfUnknownType;
        boolean ignoreBidirectional;
        DomainFacadeIgnoreService domainFacadeIgnoreService;

        /** Создаёт класс контейнер для передачи обработчиков в контекст.
         * @param pathOfImportFile ид ресурса.
         * @param dependencyManager компонент проверяющий корректность загружаемых объектов.
         * @param splitter компонент для разбиения объектов по группам.
         * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа, иначе падаем.
         * @param domainFacadeIgnoreService сервис определяющий игнорируемые фасадом поля.
         */
        public ContextKey(String pathOfImportFile, ImportDependencyManager dependencyManager,
                          ImportContextSplitterAndAggregator splitter, boolean isIgnoreObjectsOfUnknownType,
                          boolean ignoreBidirectional, DomainFacadeIgnoreService domainFacadeIgnoreService) {
            this.pathOfImportFile = pathOfImportFile;
            this.dependencyManager = dependencyManager;
            this.splitter = splitter;
            this.isIgnoreObjectsOfUnknownType = isIgnoreObjectsOfUnknownType;
            this.ignoreBidirectional = ignoreBidirectional;
            this.domainFacadeIgnoreService = domainFacadeIgnoreService;
        }

        /** Создаёт класс с значениями по умолчанию.
         * Note: только для поиска в кэше.
         * @param pathOfImportFile ид ресурса.
         */
        public ContextKey(String pathOfImportFile) {
            this(pathOfImportFile, null, null, false, false, null);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            ContextKey that = (ContextKey) obj;

            return pathOfImportFile.equals(that.pathOfImportFile);
        }

        @Override
        public int hashCode() {
            return pathOfImportFile.hashCode();
        }
    }

    /** Загружает сохраненный контекст из файла.
     * @param pathOfImportFile ид ресурса.
     * @return загруженный контекст.
     */
    private DefaultImportContext loadImportContext(String pathOfImportFile) throws IOException {
        log.debug("Starting to load import context from file. Key: {}", pathOfImportFile);
        final Path pathContext = getPathOfContextFile(pathOfImportFile, suffixIndexFileName);

        try (JsonParser parser = new JsonFactory().createParser(pathContext.toFile())) {
            parser.setCodec(new ObjectMapper());
            DefaultImportContext context = parser.readValueAs(DefaultImportContext.class);

            log.debug("Import context is loaded from file. Key: {}", pathOfImportFile);
            return context;
        }
    }

    /** Возращает путь к файлу для сохранения контекста.
     * @param pathOfImportFile путь к файлу импорта.
     * @param suffix суффикс файла.
     * @return путь к файлу.
     */
    private Path getPathOfContextFile(String pathOfImportFile, String suffix) {
        return Paths.get(
                contextStoreDirectory,
                pathOfImportFile.substring(pathOfImportFile.lastIndexOf("/") + 1) + suffix
        );
    }

    /** Создаёт контекст на основе файла ресурса.
     * @param contextKey ключ контекста.
     * @return созданный и сохраненый контекст.
     */
    private DefaultImportContext createImportContext(ContextKey contextKey)
            throws IOException, ImportParseException {
        log.debug("Starting to create import context. Key: {}", contextKey.pathOfImportFile);
        final DefaultImportContext context = new DefaultImportContext(contextKey.pathOfImportFile);
        final DefaultFileScanner scanner = new DefaultFileScanner(
                resolver,
                readerFactory,
                context,
                new File(contextKey.pathOfImportFile),
                contextKey.isIgnoreObjectsOfUnknownType,
                contextKey.ignoreBidirectional,
                contextKey.domainFacadeIgnoreService
        );

        log.debug("Starting to scan import file. Key: {}", contextKey.pathOfImportFile);
        scanner.scan();
        log.debug("Starting to split import objects. Key: {}", contextKey.pathOfImportFile);
        contextKey.splitter.split(context);
        log.debug("Starting to resolve objects dependencies. Key: {}", contextKey.pathOfImportFile);
        contextKey.dependencyManager.resolve(context);
        log.debug("Starting to aggregate objects. Key: {}", contextKey.pathOfImportFile);
        contextKey.splitter.aggregate(context);
        log.debug("Starting to save import context. Key: {}", contextKey.pathOfImportFile);
        saveContext(context, contextKey.pathOfImportFile);
        log.debug("Import context is created. Key: {}", contextKey.pathOfImportFile);
        return context;
    }

    /** Сохраняет контекст в файл.
     * @param context контекст импорта, который будет сохранен.
     * @param pathOfImportFile ид ресурса.
     */
    private void saveContext(DefaultImportContext context, String pathOfImportFile) throws IOException {
        synchronized (context) {
            checkContextStoreDirectory();
            final Path pathContext = getPathOfContextFile(pathOfImportFile, suffixIndexFileName);

            try (JsonGenerator contextWriter = new JsonFactory().createGenerator(Files.newOutputStream(pathContext))) {
                contextWriter.setCodec(new ObjectMapper());
                contextWriter.writeObject(context);
            }
        }
    }

    /** Создаёт директорию для сохранения контекста, если она не существует. */
    private void checkContextStoreDirectory() throws IOException {
        final Path path = Paths.get(contextStoreDirectory);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }

    /** Проверяет используется ли контекст.
     * @param fileName имя файла без пути
     * @return true если контекст используется, иначе false.
     */
    @Override
    public boolean isUsingContext(String fileName) {
        return contexts.asMap().keySet().stream()
                .anyMatch(key -> key.pathOfImportFile.endsWith(fileName));
    }


    /** Удаляет не используемые контексты из файловой системы. Нужно запускать переодически. */
    public synchronized void cleanStoreContext() {
        try {
            File folder = new File(contextStoreDirectory);
            if (folder == null) {
                log.debug(String.format("При очистке не обнаружена директория с контекстами: %s.",
                        contextStoreDirectory));
                return;
            }
            final File[] files = folder.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.canWrite() && file.getName().endsWith(suffixIndexFileName)
                        && !isUsingContext(file.getName().replace(suffixIndexFileName, ""))) {
                    log.debug("Starting to delete file: {}", file.getName());
                    file.delete();
                    log.debug("File {} is deleted.", file.getName());
                }
            }
        } catch (Exception e) {
            log.debug("Ошибка при очистке устаревших файлов.", e);
        }
    }

    public String getContextStoreDirectory() {
        return contextStoreDirectory;
    }

    public int getPeriodOfMinutes() {
        return periodOfMinutes;
    }

    /** Запускает в фоне очистку не используемых файлов. */
    private void startCleanTask() {
        cleanerTask = new Timer();
        cleanerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                log.debug("Start clean store context.");
                cleanStoreContext();
            }
        }, 1000 * 60 * periodOfMinutes, 1000 * 60 * periodOfMinutes);
    }

    /**
     * Билдер для {@link DefaultImportContextManager}.
     */
    public static class Builder {
        private DefaultImportContextManager importContextManager = new DefaultImportContextManager();

        /** Устанавливает хранилище ресурсов.
         * @param readerFactory хранилище ресурсов.
         * @return билдер.
         */
        public Builder readerFactory(@NotNull FileDtoReaderFactory readerFactory) {
            importContextManager.readerFactory = readerFactory;
            return this;
        }

        /** Устанавливает сопоставитель сервисов доменных объектов.
         * @param resolver сопоставитель сервисов доменных объектов.
         * @return билдер.
         */
        public Builder resolver(@NotNull DomainServicesResolver resolver) {
            importContextManager.resolver = resolver;
            return this;
        }

        /** Устанавливает директорию для сохранения файлов.
         * @param contextStoreDirectory директория для сохранения файлов.
         * @return билдер.
         */
        public Builder contextStoreDirectory(@NotNull String contextStoreDirectory) {
            importContextManager.contextStoreDirectory = contextStoreDirectory;
            return this;
        }

        /** Устанавливает период очистки не используемых файлов.
         * @param period период в минутах.
         * @return билдер.
         */
        public Builder cleanPeriod(int period) {
            importContextManager.periodOfMinutes = period;
            return this;
        }

        /**
         * Возращает собранный менеджер контеста импорта. Для сборки должны быть установлены все поля.
         * @return менеджер контекстов импорта.
         */
        public DefaultImportContextManager build() {
            if (importContextManager.readerFactory != null
                    && importContextManager.resolver != null && importContextManager.contextStoreDirectory != null) {
                if (importContextManager.periodOfMinutes > 0) {
                    importContextManager.startCleanTask();
                }
                return importContextManager;
            }
            throw new IllegalArgumentException("Установлены не все параметры!");
        }
    }
}
