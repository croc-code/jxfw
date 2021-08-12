package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;

import java.io.File;

/**
 * Абстрактный класс для реализации команд мавен-плагина wcgen.
 * <p/>
 * Created by SPlaunov on 29.07.2016.
 */
abstract class AbstractWcgenMojo extends AbstractMojo {

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
     * The project itself. This parameter is set by maven.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

}
