package ru.croc.ctp.jxfw.cli.provider;

import org.apache.maven.project.MavenProject;
import org.springframework.shell.ResultHandler;

import ru.croc.ctp.jxfw.cli.watch.FileSystemChangesWatcher;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link Runnable} отслеживания + компиляции изменных xtend файлов.
 */
public class CompilationTask implements Runnable {

    private final MavenProject mavenProject;
    private final FileSystemChangesWatcher watcher;
    private final ResultHandler resultHandler;

    /**
     * Конструктор.
     * @param mavenProject {@link MavenProject}
     * @param watcher {@link FileSystemChangesWatcher}
     * @param resultHandler {@link ResultHandler}
     */
    public CompilationTask(
            MavenProject mavenProject,
            FileSystemChangesWatcher watcher,
            ResultHandler resultHandler
    ) {
        this.mavenProject = mavenProject;
        this.watcher = watcher;
        this.resultHandler = resultHandler;
    }

    private List<String> prepareListOfXtendFiles() {
        final List<String> trackedFiles = new ArrayList<>();

        watcher.getKeyDirMap()
                .values()
                .forEach(dir -> {
                    File[] xtendFiles = dir.toFile().listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(".xtend");
                        }
                    });
                    Arrays.stream(xtendFiles).forEach(xtend -> trackedFiles.add(xtend.getAbsolutePath()));
                });
        return trackedFiles;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("xtend-compiler-for-" + mavenProject.getArtifact());

        try {
            watcher.start();
        } catch (IOException e) {
            resultHandler.handleResult(e);
        } catch (InterruptedException e) {
            //давим, ошибка ожидаема т.к. вызываем CmdDispatcher#stop
        }
    }

    public List<String> getListOfXtendFiles() {
        return prepareListOfXtendFiles();
    }
}
