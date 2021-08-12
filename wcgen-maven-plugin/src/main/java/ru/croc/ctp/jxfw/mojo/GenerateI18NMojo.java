package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import ru.croc.ctp.jxfw.generator.I18NGenerator;

import java.io.File;

/**
 * Задача для генерации i18n файлов на основе данных из ecore модели.
 */
@Mojo(name = "i18n", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class GenerateI18NMojo extends AbstractWcgenMojo {

    @Parameter(readonly = true, required = true)
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info(
                "Source dir: " + sourceDirectory.getAbsolutePath());
        getLog().info(
                "Output dir: " + outputDirectory.getAbsolutePath());
        
        I18NGenerator i18NGenerator = new I18NGenerator(sourceDirectory, outputDirectory);
        i18NGenerator.process();
    }
}
