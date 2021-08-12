package ru.croc.ctp.jxfw.cli.watch;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.croc.ctp.jxfw.cli.CliPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Отслеживает изменения файлов определенного типа в FS.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
public class FileSystemChangesWatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemChangesWatcher.class);

    private Consumer<Collection<File>> onSignal;
    private Path startFolder;
    private Collection<String> fileTypes;
    //регистрация наблюдателя для конкретной директории
    private final Map<WatchKey, Path> keyDirMap = new HashMap<>();
    private CliPrinter cliPrinter;
    private EventsAccumulator accumulator;

    /**
     * Отслеживает изменения в файловой системе.
     *
     * @param consumer    действия по события от файловой системы
     * @param startFolder исходный путь, папка с которой начинать смотреть влаженные
     * @param fileTypes   типы расширений файлов, за которыми {@link FileSystemChangesWatcher} наблюдает
     */
    public FileSystemChangesWatcher(
            Consumer<Collection<File>> consumer,
            Path startFolder,
            Collection<String> fileTypes
    ) {
        this.onSignal = consumer;
        this.startFolder = startFolder;
        this.fileTypes = fileTypes;
        this.accumulator = new EventsAccumulator(onSignal);
    }

    /**
     * Запуск процесса слеженеия за изменениями в файловой системе.
     * @throws IOException exception
     * @throws InterruptedException exception (т.к. сами прерываем CmdDispatcher#stop())
     */
    public void start() throws IOException, InterruptedException {
        WatchService watcher = startFolder.getFileSystem().newWatchService();
        registerWatcherForFileTypes(watcher, startFolder);
        try {
            for (; ; ) {
                //наблюдатель за директорией
                WatchKey watchKey = watcher.take();
                Set<File> files = new HashSet<>();
                List<WatchEvent<?>> events = watchKey.pollEvents();

                //имена файлов (краткие)
                List<String> names = events
                        .stream()
                        .map(
                                watchEvent -> watchEvent.context().toString()
                        )
                        .collect(Collectors.toList());

                //папка в которой произошло событие
                Path eventDir = keyDirMap.get(watchKey);
                if (eventDir == null) {
                    LOGGER.error("WatchKey не определен {}", eventDir);
                    continue;
                }

                //проверяем интересны ли нам события
                names.forEach(s -> {
                    fileTypes.forEach(fileType -> {
                        if (s.endsWith(fileType)) {
                            Path changedFile = eventDir.resolve(s);
                            File changed = changedFile.toFile();
                            files.add(changed);
                            //info т.к. далее сразу сработает компиляция после который будет запрошен infoAndPrompt
                            cliPrinter.info(System.lineSeparator() + changed + " изменен");
                        }
                    });
                });
                watchKey.reset();

                accumulator.add(files);
            }
        } finally {
            watcher.close();
        }
    }

    private WatchService registerWatcherForFileTypes(
            WatchService watcher,
            Path startFolder
    ) throws IOException {
        StringBuilder sb = new StringBuilder();
        Files.walkFileTree(startFolder, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                File folder = new File(dir.toUri());
                if (folder.isDirectory()) {
                    String[] result = folder.list((fileInDir, name) -> {
                        Path resolved = Paths.get(fileInDir.toURI()).resolve(name);
                        File file = resolved.toFile();
                        return file.exists()
                                && file.isFile()
                                && fileTypes.stream()
                                .filter(type -> file.getName().toLowerCase().endsWith(type))
                                .findFirst()
                                .isPresent();

                    });
                    if (result.length > 0) {
                        WatchKey key = dir.register(
                                watcher,
                                StandardWatchEventKinds.ENTRY_MODIFY
                        );
                        keyDirMap.put(key, dir);
                                                
                        sb.append("директория добавлена к отслеживанию изменений xtend " + dir.toFile().getAbsolutePath());
                        sb.append(System.lineSeparator());
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
        if (StringUtils.isNotEmpty(sb.toString())) {
            cliPrinter.infoAndPrompt(System.lineSeparator() + sb);
        }
        return watcher;
    }

    public void setPrinter(CliPrinter cliPrinter) {
        this.cliPrinter = cliPrinter;
    }

    /**
     * Директории в которых отслеживаются изменения.
     * @return ключи (сигналы изменений) / пути
     */
    public Map<WatchKey, Path> getKeyDirMap() {
        return keyDirMap;
    }
}
