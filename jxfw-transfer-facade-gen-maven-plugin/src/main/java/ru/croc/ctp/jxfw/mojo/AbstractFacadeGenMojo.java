package ru.croc.ctp.jxfw.mojo;

import static ru.croc.ctp.jxfw.generator.AbstractFacadeGenerator.EXCLUDE_FILTERS;
import static ru.croc.ctp.jxfw.generator.AbstractFacadeGenerator.INCLUDE_FILTERS;

import com.google.common.collect.Maps;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import ru.croc.ctp.jxfw.metamodel.filter.impl.AllClassifierFilter;
import ru.croc.ctp.jxfw.metamodel.filter.impl.ClassifierFilterFactoryImpl;
import ru.croc.ctp.jxfw.metamodel.filter.impl.NoneClassifierFilter;

import java.io.File;
import java.util.Map;

/**
 * Абстрактный класс для реализации команд генерации фасада мавен-плагином.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
abstract class AbstractFacadeGenMojo extends AbstractMojo {

    /**
     * Каталог с исходными текстами, в котором находится ecore-модель.
     */
    @Parameter(readonly = true, required = true)
    protected File sourceDirectory;

    /**
     * Каталог с исходными текстами тестов, в котором находится ecore-модель.
     */
    @Parameter(readonly = true, required = true)
    protected File testSourceDirectory;

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
     * Возвращает {@link Map} опций.
     *
     * @return опции.
     */
    protected Map<String, Object> getOptions() {
        final Map<String, Object> options = Maps.newHashMap();


        options.put(INCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters(includes, includeFiles, includeRegexps,
                        new AllClassifierFilter()));
        options.put(EXCLUDE_FILTERS,
                ClassifierFilterFactoryImpl.INSTANCE.createFilters(excludes, excludeFiles, excludeRegexps,
                        new NoneClassifierFilter()));
        return options;
    }

}
