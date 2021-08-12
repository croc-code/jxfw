package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import ru.croc.ctp.jxfw.generator.JsModelGenerator;

import java.io.File;

/**
 * Задача генерации JS файлов на основе данных из ecore модели.
 */
@Mojo(name = "jsModel", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class GenerateJsModelMojo extends AbstractWcgenMojo {

    @Parameter(readonly = true, required = true)
    private File outputDirectory;
    
    @Parameter(readonly = true, required = true)
    private String jsModelMetaFileName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(
            "Source dir: " + sourceDirectory.getAbsolutePath());
        getLog().info(
            "Output dir: " + outputDirectory.getAbsolutePath());

        JsModelGenerator jsModelGenerator = new JsModelGenerator(sourceDirectory, outputDirectory, jsModelMetaFileName);
        jsModelGenerator.process();
    }

}
