package ru.croc.ctp.jxfw.mojo;

import static ru.croc.ctp.jxfw.generator.FacadeGenerator.EXCLUDE_FILTERS;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.GENERATE_EXPORT_OPTION;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.INCLUDE_FILTERS;
import static ru.croc.ctp.jxfw.generator.FacadeGenerator.REPORTING_ENGINE_OPTION;

import com.google.common.collect.Maps;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import ru.croc.ctp.jxfw.core.reporting.ReportingEngine;
import ru.croc.ctp.jxfw.generator.FacadeGenerator;
import ru.croc.ctp.jxfw.metamodel.filter.impl.AllClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.impl.ClassifierFilterFactoryImpl;
import ru.croc.ctp.jxfw.metamodel.filter.impl.NoneClassifierFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Абстрактный класс для реализации разых режимов выполнения генератора фасада
 * WebClient.
 * <p/>
 * Created by SPlaunov on 29.07.2016.
 */
abstract class AbstractFacadeGenerateMojo extends AbstractWcgenMojo {
    /**
     * Каталог в который будет генерирроваться код.
     */
    @Parameter(readonly = true, required = true)
    protected File outputDirectory;

    /**
     * Каталог, в который будет генерироваться код по тестовой модели.
     */
    @Parameter(readonly = true, required = true)
    protected File testOutputDirectory;

    /**
     * Перечисление сущностей, для которых НЕ будут сгенерированы контроллеры и
     * сервисы ТО. Для остальных сущностей генерация произойдет.
     */
    @Parameter
    protected String[] excludes = new String[]{};

    /**
     * Перечисление сущностей, для которых будут сгенерированы контроллеры и
     * сервисы ТО. Для остальных сущностей генерация не произойдет.
     */
    @Parameter
    protected String[] includes = new String[]{};


    /**
     * Перечисление файлов ecore, из которых НЕ будут сгенерированы контроллеры и
     * сервисы ТО. Для остальных сущностей генерация произойдет.
     */
    @Parameter
    protected String[] excludeFiles = new String[]{};

    /**
     * Перечисление файлов ecore, из которых будут сгенерированы контроллеры и
     * сервисы ТО. Для остальных сущностей генерация не произойдет.
     */
    @Parameter
    protected String[] includeFiles = new String[]{};

    /**
     * Перечисление регулярных выражений, из которых НЕ будут сгенерированы контроллеры и
     * сервисы ТО. Для остальных сущностей генерация произойдет.
     */
    @Parameter
    protected String[] excludeRegexps = new String[]{};

    /**
     * Перечисление регулярных выражений, из которых будут сгенерированы контроллеры и
     * сервисы ТО. Для остальных сущностей генерация не произойдет.
     */
    @Parameter
    protected String[] includeRegexps = new String[]{};


    /**
     * The project itself. This parameter is set by maven.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;


    /**
     * Запуск процесса генерации ресурсов.
     *
     * @param sourceDir исходная папка.
     * @param outputDir выходная папка.
     */
    void doGenerate(File sourceDir, File outputDir) {

        final Map<String, Object> options = Maps.newHashMap();
        options.put(GENERATE_EXPORT_OPTION, true);


        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters(includes, includeFiles, includeRegexps,
                        new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters(excludes, excludeFiles, excludeRegexps,
                        new NoneClassifierFilter()));


        List<ReportingEngine> reportingEngineList = Arrays.asList(ReportingEngine.values());
        for (Artifact artifact : project.getDependencyArtifacts()) {
            if (artifact.getType().toLowerCase().contains("jar")) {
                for (ReportingEngine reportingEngine : reportingEngineList) {
                    if (artifact.getArtifactId().startsWith(reportingEngine.getModuleName())) {
                        if (options.containsKey(REPORTING_ENGINE_OPTION)) {
                            throw new IllegalStateException("More than one reporting dependency in classpath: "
                                    + reportingEngine.getModuleName() + " "
                                    + ((ReportingEngine) options.get(REPORTING_ENGINE_OPTION)).getModuleName());
                        }
                        options.put(REPORTING_ENGINE_OPTION, reportingEngine);
                    }
                }
            }
        }

        final FacadeGenerator facadeGenerator = new FacadeGenerator(sourceDir.toPath(), outputDir.toPath(), options,
                getLog());
        facadeGenerator.generate();
    }


}
