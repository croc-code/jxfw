package ru.croc.ctp.jxfw.cli.dispatcher;

import org.apache.maven.project.MavenProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.cli.CliProperties;
import ru.croc.ctp.jxfw.cli.provider.CompilationTask;
import ru.croc.ctp.jxfw.cli.provider.CompilationTaskProvider;
import ru.croc.ctp.jxfw.cli.provider.MvnProjectProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Диспатчер команд от интерфейса {@link ru.croc.ctp.jxfw.cli.CliApplication}
 * к ответственным за логику сервисам.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@Component
public class CmdDispatcher {

    private Set<MavenProject> xtendProjects = Collections.emptySet();
    private MvnProjectProvider mvnProjectProvider;
    private ExecutorService executorService;
    private List<CompilationTask> tasks = new ArrayList<>();

    @Autowired
    private CompilationTaskProvider taskProvider;

    /**
     * Запуск.
     *
     * @param pom         родительский pom.xml проекта
     * @param mvnSettings настройки maven
     * @param cliProperties все настройки
     * @return ""
     * @throws Exception ошибка
     */
    public String start(
            File pom,
            File mvnSettings,
            CliProperties cliProperties
    ) throws Exception {

        Set<File> trackedPoms = cliProperties.getTrackedPoms();

        //не можем быстро стартовать использую предыдущие знания
        if (trackedPoms.isEmpty()) {
            // получить mvnProject для всех pom где есть xtend плагин
            xtendProjects = mvnProjectProvider.listXtendMvnProjects(pom, mvnSettings);

            //запищем в настройки
            cliProperties.setTrackedPoms(
                    xtendProjects.stream().map(project -> project.getFile()).collect(Collectors.toSet())
            );
        } else {
            //т.к. trackedPoms те что содержат xtend плагин
            xtendProjects = mvnProjectProvider.pomsToMvnProjects(trackedPoms, mvnSettings);
        }

        // для каждого модуля поиск всех входящих в него xtend файлов и отложенная компиляция по событию
        if (!xtendProjects.isEmpty()) {
            executorService = getExecutorService();

            xtendProjects
                    .stream()
                    .map(taskProvider::getTask)
                    .peek(tasks::add)
                    .forEach(compilationTask -> {
                        executorService.submit(compilationTask);
                    });
        }

        return "";
    }

    private ExecutorService getExecutorService() {
        return Executors.newFixedThreadPool(xtendProjects.size(), r -> {
            Thread newThread = Executors.defaultThreadFactory().newThread(r);
            newThread.setDaemon(true);
            return newThread;
        });
    }

    /**
     * Прервать процесс наблюдения за xtend и компиляцию.
     *
     * @return ""
     */
    public String stop() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        tasks.clear();
        return "";
    }

    @Autowired
    public void setMvnProjectProvider(MvnProjectProvider mvnProjectProvider) {
        this.mvnProjectProvider = mvnProjectProvider;
    }

    /**
     * Список текущих (отслеживаемых) xtend файлов.
     */
    public String listSourcesInTracking() {
        StringBuilder stringBuilder = new StringBuilder();
        tasks.forEach(task -> {
            task.getListOfXtendFiles().forEach(xtendFile -> {
                stringBuilder.append(xtendFile);
                stringBuilder.append(System.lineSeparator());
            });
        });
        return stringBuilder.toString();
    }
}
