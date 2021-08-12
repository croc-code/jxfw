package ru.croc.ctp.jxfw.generator;


import com.squareup.javapoet.JavaFile;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.emf.ecore.EClassifier;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.ClassifierFiltrator;
import ru.croc.ctp.jxfw.metamodel.filter.impl.ClassifierFiltratorImpl;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Абстрактный генератор фасада по Ecore модели.
 * Класс расчитывает найти Ecore модели по пути target/classes/model.
 *
 * @author Alexander Golovin
 * @since  1.6
 */
public abstract class AbstractFacadeGenerator {

    /**
     * Опция для исключения классов по фильтрам.
     */
    public static final String EXCLUDE_FILTERS = "excludeFilters";

    /**
     * Опция для включения классов по фильтрам.
     */
    public static final String INCLUDE_FILTERS = "includeFilters";

    /**
     * Папка, в которой будут найдены файлы моделей.
     */
    protected final Path sourceFolder;

    /**
     * Папка, в которую будет сгенерирован код.
     */
    protected final Path outputFolder;

    /**
     * Набор опций для задачи.
     */
    protected final Map<String, Object> options;

    /**
     * Логгер maven.
     */
    protected final Log log;


    private ClassifierFiltrator classifierFiltrator = new ClassifierFiltratorImpl();


    /**
     * Абстрактный генератор фасада по Ecore модели.
     *
     * @param sourceFolder Папка, в которой будут найдены файлы моделей.
     * @param outputFolder Папка, в которую будет сгенерирован код.
     * @param options      Набор опций для задачи.
     * @param log          Логгер maven.
     */
    public AbstractFacadeGenerator(Path sourceFolder, Path outputFolder, Map<String, Object> options, Log log) {
        this.sourceFolder = sourceFolder;
        this.outputFolder = outputFolder;
        this.options = options;
        this.log = log;
    }

    /**
     * Генерация фасада для сущностей из Ecore моделей.
     */
    public final void generate() {
        final Set<XFWClass> classes = new XFWModelImpl(sourceFolder).getAll(XFWClass.class).stream()
                .filter(createFiltersPredicate())
                .collect(Collectors.toSet());
        generate(classes);
    }

    /**
     * Генерирует фасад для сущностей переданных в параметре.
     *
     * @param classes сущностей для которых необходимо сгенерировать фасад.
     */
    protected abstract void generate(Set<XFWClass> classes);

    private Predicate<? super EClassifier> createFiltersPredicate() {
        return classifier -> (classifierFiltrator.anyMatch(
                (Set<ClassifierFilter>) options.get(INCLUDE_FILTERS), classifier)
                &&
                !classifierFiltrator.anyMatch(
                        (Set<ClassifierFilter>) options.get(EXCLUDE_FILTERS), classifier)
        );
    }

    /**
     * Сохраняет java-файл в output директорию.
     *
     * @param javaFile java-файл.
     */
    protected void save(JavaFile javaFile) {
        try {
            log.debug("Save Java File: " + javaFile.packageName + "." + javaFile.typeSpec.name);
            javaFile.writeTo(outputFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
