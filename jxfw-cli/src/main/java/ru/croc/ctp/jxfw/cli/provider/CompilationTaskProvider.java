package ru.croc.ctp.jxfw.cli.provider;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.xtext.util.Strings.concat;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.emf.common.util.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.ResultHandler;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.cli.CliPrinter;
import ru.croc.ctp.jxfw.cli.compiler.MavenProjectResourceSetProvider;
import ru.croc.ctp.jxfw.cli.compiler.SelectedFilesCompiler;
import ru.croc.ctp.jxfw.cli.watch.FileSystemChangesWatcher;

import java.io.File;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Класс регистрирует стратегию для компиляции изменных xtend файлов.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@Component
public class CompilationTaskProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompilationTaskProvider.class);

    private CliPrinter cliPrinter;

    @Autowired
    @Qualifier("main")
    private ResultHandler resultHandler;
    
    private XtendCompilerProvider compilerProvider = new XtendCompilerProvider();
    private static final Predicate<String> FILE_EXISTS = filePath -> new File(filePath).exists();

    /**
     * Получить задачу {@link Runnable} по компиляции измененных xtend.
     *
     * @param mavenProject {@link MavenProject} модуля
     * @return задача компиляции
     */
    public CompilationTask getTask(MavenProject mavenProject) {
        SelectedFilesCompiler compiler = prepareCompiler(mavenProject);
        //описание того что требуется сделать с изменившимися xtend файлами
        Consumer<Collection<File>> onSignal = changedFiles -> {
            try {
                //говорим компилятору какие xtend нужно перегенерить
                compiler.setChangedFiles(changedFiles);
                LocalDateTime start = LocalDateTime.now();
                if (compiler.compile()) {
                    cliPrinter.infoAndPrompt(
                            MessageFormat.format("Компиляция прошла успешно {0}, общая продолжительность {1} сек.",
                                    getConcatFiles(changedFiles),
                                    Duration.between(start, LocalDateTime.now()).getSeconds()
                            )
                    );
                } else {
                    cliPrinter.infoAndPrompt(
                            MessageFormat.format("Ошибка компиляции {0}, общая продолжительность {1} сек.",
                                    getConcatFiles(changedFiles),
                                    Duration.between(start, LocalDateTime.now()).getSeconds()
                            )
                    );
                }
            } catch (Exception e) {
                resultHandler.handleResult(e);
            }
        };
        FileSystemChangesWatcher watcher = new FileSystemChangesWatcher(
                onSignal,
                Paths.get(mavenProject.getBasedir().toURI()),
                Arrays.asList(".xtend")
        );

        watcher.setPrinter(cliPrinter);

        return new CompilationTask(mavenProject, watcher, resultHandler);
    }

    private String getConcatFiles(Collection<File> changedFiles) {
        return concat(
                File.pathSeparator,
                changedFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList())
        );
    }

    private SelectedFilesCompiler prepareCompiler(MavenProject mavenProject) {
        SelectedFilesCompiler compiler = compilerProvider.provide();
        String outputDirectory = resolveOutputDirectory(mavenProject);
        List<String> compileSourceRoots = Lists.newArrayList(mavenProject.getCompileSourceRoots());
        compileSourceRoots.remove(outputDirectory);
        String classPath = concat(File.pathSeparator, getClassPath(mavenProject, Arrays.asList(outputDirectory)));
        compiler.setClassPath(classPath);
        compiler.setResourceSetProvider(new MavenProjectResourceSetProvider(mavenProject));
        String baseDir = mavenProject.getBasedir().getAbsolutePath();
        LOGGER.debug("Set baseDir: " + baseDir);
        compiler.setBasePath(baseDir);
        LOGGER.debug("Set Java Compliance Level: " + resolveJavaSourceVersion(mavenProject));
        compiler.setJavaSourceVersion(resolveJavaSourceVersion(mavenProject));
        LOGGER.debug("Set temp directory: " + resolveTempDirectory(mavenProject));
        compiler.setTempDirectory(resolveTempDirectory(mavenProject));
        LOGGER.debug("Set generateSyntheticSuppressWarnings: "
                + resolveGenerateSyntheticSuppressWarnings(mavenProject));
        compiler.setGenerateSyntheticSuppressWarnings(resolveGenerateSyntheticSuppressWarnings(mavenProject));
        LOGGER.debug("Set generateGeneratedAnnotation: " + resolveGenerateGeneratedAnnotation(mavenProject));
        compiler.setGenerateGeneratedAnnotation(resolveGenerateGeneratedAnnotation(mavenProject));
        LOGGER.debug("Set DeleteTempDirectory: " + false);
        compiler.setDeleteTempDirectory(false);
        String encoding = resolveEncoding(mavenProject);
        //выставляю если только явно задан, для варианта по умолчанию как указано в xtend
        //${project.build.sourceEncoding} по какой то причине нет необходимого api
        if (StringUtils.isNoneEmpty(encoding)) {
            LOGGER.debug("Set encoding: " + encoding);
            compiler.setFileEncoding(encoding);
        }
        compiler.setSourcePath(concat(File.pathSeparator, newArrayList(compileSourceRoots)));
        LOGGER.debug("Set outputDirectory: " + outputDirectory);
        compiler.setOutputPath(outputDirectory);
        LOGGER.debug("Set writeTraceFiles: " + resolveWriteTraceFiles(mavenProject));
        compiler.setWriteTraceFiles(resolveWriteTraceFiles(mavenProject));

        return compiler;
    }

    /**
     * Получить папку, куда будут сложены java исходники сгенерированные по xtend.
     *
     * @param project {@link MavenProject} модуля
     * @return путь outputDirectory
     */
    public static String resolveOutputDirectory(MavenProject project) {
        Optional<String> outputDirectory = lookForConfigValue(project, "outputDirectory");
        // дефолт "${basedir}/src/main/generated-sources/xtend"
        return outputDirectory.orElse(project.getBasedir() + "/src/main/generated-sources/xtend");
    }

    /**
     * Папка для сгенерированных компилятором stub файлов будущих исходников.
     *
     * @param project {@link MavenProject} модуля
     * @return путь tempDirectory
     */
    public static String resolveTempDirectory(MavenProject project) {
        Optional<String> tempDirectory = lookForConfigValue(project, "tempDirectory");
        //дефолт ${project.build.directory}/xtend
        return tempDirectory.orElse(project.getBuild().getDirectory() + "/xtend");
    }

    /**
     * Кодировка будущих исходников.
     *
     * @param project {@link MavenProject} модуля
     * @return кодировка
     */
    public static String resolveEncoding(MavenProject project) {
        Optional<String> encoding = lookForConfigValue(project, "encoding");

        //дефолт в xtend указан такой ${project.build.sourceEncoding} (не хватает api)
        return encoding.orElse(null);
    }

    /**
     * Создание исходников совместимых с версией Java.
     *
     * @param project {@link MavenProject} модуля
     * @return версия Java
     */
    public static String resolveJavaSourceVersion(MavenProject project) {
        Optional<String> javaSourceVersion = lookForConfigValue(project, "javaSourceVersion");

        //дефолт ${maven.compiler.source} 1.6
        return javaSourceVersion.orElse("1.6");
    }

    /**
     * Генерация аннотации {@link SuppressWarnings}.
     *
     * @param project {@link MavenProject} модуля
     * @return да/нет
     */
    public static Boolean resolveGenerateSyntheticSuppressWarnings(MavenProject project) {
        Optional<String> generateSyntheticSuppressWarnings = lookForConfigValue(
                project, "generateSyntheticSuppressWarnings"
        );

        if (generateSyntheticSuppressWarnings.isPresent()) {
            return Boolean.valueOf(generateSyntheticSuppressWarnings.get());
        }

        return true;
    }

    /**
     * Создание *._trace файлов.
     *
     * @param project {@link MavenProject} модуля
     * @return да/нет
     */
    public static Boolean resolveWriteTraceFiles(MavenProject project) {
        Optional<String> writeTraceFiles = lookForConfigValue(
                project, "writeTraceFiles"
        );

        if (writeTraceFiles.isPresent()) {
            return Boolean.valueOf(writeTraceFiles.get());
        }

        return true;
    }

    /**
     * Генерация <code>@Generated</code> для не вложенных (not nested) типов.
     *
     * @param project {@link MavenProject} модуля
     * @return да/нет
     */
    public static Boolean resolveGenerateGeneratedAnnotation(MavenProject project) {
        Optional<String> generateGeneratedAnnotation = lookForConfigValue(
                project, "generateGeneratedAnnotation"
        );

        if (generateGeneratedAnnotation.isPresent()) {
            return Boolean.valueOf(generateGeneratedAnnotation.get());
        }

        return false;
    }

    private static Optional<String> lookForConfigValue(MavenProject project, String paramName) {
        Optional<Plugin> any = project
                .getBuild()
                .getPlugins().stream()
                .filter(plugin -> plugin.getArtifactId().equals("xtend-maven-plugin")).findAny();
        if (any.isPresent()) {
            Plugin pluginXtend = any.get();
            Optional<PluginExecution> pluginExecutionAny = pluginXtend.getExecutions()
                    .stream()
                    .filter(pluginExecution -> pluginExecution.getGoals().contains("compile"))
                    .findAny();
            if (pluginExecutionAny.isPresent()) {
                PluginExecution pluginExecution = pluginExecutionAny.get();
                //та что переопрделена (более конкретная) для goal плагина
                Xpp3Dom configuration = (Xpp3Dom) pluginExecution.getConfiguration();
                if (configuration != null && configuration.getChild(paramName) != null) {
                    return Optional.of(configuration.getChild(paramName).getValue());
                } else {
                    //общая для всего плагина
                    configuration = (Xpp3Dom) pluginXtend.getConfiguration();
                    if (configuration != null && configuration.getChild(paramName) != null) {
                        return Optional.of(configuration.getChild(paramName).getValue());
                    }
                }
            }

        }
        return Optional.empty();
    }

    private static List<String> getClassPath(MavenProject mavenProject, Collection<String> exclude) {
        Set<String> classPath = Sets.newLinkedHashSet();
        classPath.add(mavenProject.getBuild().getSourceDirectory());
        try {
            classPath.addAll(mavenProject.getCompileClasspathElements());
        } catch (DependencyResolutionRequiredException e) {
            throw new WrappedException(e);
        }
        addDependencies(classPath, mavenProject.getCompileArtifacts());
        classPath.removeAll(exclude);
        return newArrayList(filter(classPath, FILE_EXISTS));
    }

    private static void addDependencies(Set<String> classPath, List<Artifact> dependencies) {
        for (Artifact artifact : dependencies) {
            classPath.add(artifact.getFile().getAbsolutePath());
        }
    }

    @Autowired
    public void setCliPrinter(CliPrinter cliPrinter) {
        this.cliPrinter = cliPrinter;
    }
}
