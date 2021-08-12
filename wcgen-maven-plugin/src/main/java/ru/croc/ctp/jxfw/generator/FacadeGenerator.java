package ru.croc.ctp.jxfw.generator;

import com.squareup.javapoet.JavaFile;
import org.apache.commons.lang3.ClassUtils;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.core.reporting.ReportingEngine;
import ru.croc.ctp.jxfw.facade.ControllerCreator;
import ru.croc.ctp.jxfw.facade.DataSourceCreator;
import ru.croc.ctp.jxfw.facade.ToServiceCreator;
import ru.croc.ctp.jxfw.facade.WebClientControllerCreator;
import ru.croc.ctp.jxfw.facade.WebClientDataSourceCreator;
import ru.croc.ctp.jxfw.facade.WebClientToServiceCreator;
import ru.croc.ctp.jxfw.facade.report.BirtDataSetCreator;
import ru.croc.ctp.jxfw.facade.report.WebClientBirtReportControllerCreator;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWModel;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFiltrator;
import ru.croc.ctp.jxfw.metamodel.filter.impl.ClassifierFiltratorImpl;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Генератор HTTP Spring-контроллеров по Ecore модели(-ям)
 * Класс расчитывает найти Ecore модели по пути target/classes/model.
 *
 * @author Nosov Alexander
 * @since 09.11.15.
 */
public class FacadeGenerator {


    /**
     * Опция для исключения классов по фильтрам.
     */
    public static final String EXCLUDE_FILTERS = "excludeFilters";

    /**
     * Опция для включения классов по фильтрам.
     */
    public static final String INCLUDE_FILTERS = "includeFilters";


    /**
     * Опция указывает на необходимость генерации путей для экспорта в контроллерах.
     */
    public static final String GENERATE_EXPORT_OPTION = "generateExport";
    /**
     * Опция указывает на необходимость генерации путей для экспорта в контроллерах.
     */
    public static final String REPORTING_ENGINE_OPTION = "reportingEngine";

    private static final String FILE_MODEL_EXTENSION = XFWMMPackage.eINSTANCE.getModelFileExtension();

    private final Path sourceFolder;

    private final Path outputFolder;

    private final Map<String, Object> options;

    private final Log log;

    private ClassifierFiltrator classifierFiltrator = new ClassifierFiltratorImpl();

    /**
     * Конструктор.
     *
     * @param sourceFolder - Папка, в которой будут найдены файлы моделей
     * @param outputFolder - папка, в которую будет сгенерирован код
     * @param options      - набор опций для задачи
     * @param log          - логгер maven
     */
    public FacadeGenerator(Path sourceFolder, Path outputFolder, Map<String, Object> options, Log log) {
        this.sourceFolder = sourceFolder;
        this.outputFolder = outputFolder;
        this.options = options;
        this.log = log;
    }

    /**
     * Генерация Java-файлов - контроллеров для сущностей из Ecore моделей.
     */
    public void generate() {

        log.info("WebClient facade generation - start");

        XFWModel xfwModel = new XFWModelImpl(sourceFolder);


        final Set<XFWClass> classes = xfwModel.getAll(XFWClass.class);

        final ControllerCreator controllerCreator = new WebClientControllerCreator();
        final ToServiceCreator serviceToCreator = new WebClientToServiceCreator();
        final Set<XFWClass> readonlyClasses = Collections.unmodifiableSet(classes);

        classes.stream()
                .filter(createFiltersPredicate())
                .filter(cls -> cls.getEAnnotation(XFWConstants.SERVER_ONLY_ANNOTATION.getUri()) == null)
                .forEach((xfwClass) -> {
                    //Для временных типов контроллеры не создаем
                    String controllerOrToServiceClassName = xfwClass.getInstanceClassName();
                    if (xfwClass.isPersistentType()) {
                        log.debug("start  creating Controller for class " + controllerOrToServiceClassName);
                        controllerCreator.create(xfwClass, options).forEach(this::save);
                        log.debug("finish creating Controller for class " + controllerOrToServiceClassName);
                    }
                    if (xfwClass.isTransientType() || xfwClass.isPersistentType()) {
                        log.debug("start  creating TO service for class " + controllerOrToServiceClassName);
                        serviceToCreator.create(xfwClass, readonlyClasses, options).forEach(this::save);
                        log.debug("finish creating TO service for class " + controllerOrToServiceClassName);
                    }
                });

        final DataSourceCreator dataSourceCreator = new WebClientDataSourceCreator();
        final DataSourceCreator reportCreator = new WebClientBirtReportControllerCreator();
        ReportingEngine reportingEngine
                = (ReportingEngine) options.getOrDefault(REPORTING_ENGINE_OPTION, ReportingEngine.NO_REPORT);

        final Map<String, List<XFWDataSource>> dataSources = xfwModel.getAll(XFWDataSource.class).stream()
                .filter(createFiltersPredicate())
                .filter(xfwDataSource -> xfwDataSource.getOperationCount() > 0)
                .collect(Collectors.groupingBy(XFWDataSource::getInstanceClassName));

        BirtDataSetCreator birtDataSetCreator = new BirtDataSetCreator();

        dataSources.forEach((controllerName, xfwDataSources) -> {
            log.debug("start  creating DataSource " + controllerName);
            dataSourceCreator.create(controllerName, xfwDataSources, options, false).forEach(this::save);
            if (reportingEngine == ReportingEngine.BIRT) {
                reportCreator.create(controllerName, xfwDataSources, options, false).forEach(this::save);
                birtDataSetCreator.create(controllerName, xfwDataSources).forEach(this::save);
            }
            log.debug("finish creating DataSource " + controllerName);
        });


        final Map<String, List<XFWDataSource>> dataSourcesClasses = xfwModel.getAll(XFWDataSource.class).stream()
                .filter(createFiltersPredicate())
                .filter(xfwDataSource -> xfwDataSource.getOperationCount() == 0)
                .collect(Collectors.groupingBy(ds -> ClassUtils.getPackageName(ds.getInstanceClassName())));
        dataSourcesClasses.forEach((controllerName, xfwDataSources) -> {
            log.debug("start  creating DataSource " + controllerName);
            dataSourceCreator.create(
                    controllerName + ".DataSourceAutoGenerated", xfwDataSources, options, false)
                    .forEach(this::save);
            if (reportingEngine == ReportingEngine.BIRT) {
                //  reportCreator.create(controllerName + ".DataSourceAutoGenerated", xfwDataSources, options)
                // .forEach(this::save);
                dataSourceCreator.create(
                        controllerName + ".DataSourceAutoGeneratedReports", xfwDataSources, options, true)
                        .forEach(this::save);
                birtDataSetCreator.create(controllerName + ".DataSourceAutoGenerated", xfwDataSources)
                        .forEach(this::save);
            }
            log.debug("finish creating DataSource " + controllerName);
        });


        log.info("WebClient facade generation - finish");
    }


    private void save(JavaFile javaFile) {
        try {
            log.debug("Save Java File: " + javaFile.packageName + "." + javaFile.typeSpec.name);
            javaFile.writeTo(outputFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Predicate<? super EClassifier> createFiltersPredicate() {
        return classifier -> (classifierFiltrator.anyMatch(
                (Set<ClassifierFilter>) options.get(INCLUDE_FILTERS), classifier)
                &&
                !classifierFiltrator.anyMatch(
                        (Set<ClassifierFilter>) options.get(EXCLUDE_FILTERS), classifier)
        );
    }


}
