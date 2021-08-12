package ru.croc.ctp.jxfw.mojo.modelgen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;

/**
 * Goal плагина для генерации xtend файла модели по xfwmm.
 * @author AKogun
 */
@Mojo(name = "generateModel", defaultPhase = LifecyclePhase.INITIALIZE)
public class GenerateModelMojo extends AbstractMojo {

    /**
     * The project itself. This parameter is set by maven.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(readonly = true, required = true)
    private File sourceModel;

    @Parameter(readonly = true, required = true)
    private File outputDirectory;

    @Parameter(readonly = true, required = true)
    private String basePackage;

    /**
     * {@inheritDoc}.
     */
    public void execute() throws MojoExecutionException {
        getLog().info("Source model: " + sourceModel.getAbsolutePath().toString());
        getLog().info("Output dir: " + outputDirectory.getAbsolutePath().toString());
        getLog().info("Base package: " + basePackage);

        ModelGenerator mg = new ModelGenerator(sourceModel, outputDirectory,
                basePackage, getLog());
        
        project.addCompileSourceRoot(outputDirectory.getPath());
        project.addCompileSourceRoot(sourceModel.getParentFile().getPath());
        
        mg.process();
        
        EnumGenerator eg = new EnumGenerator(sourceModel, outputDirectory.toPath(), 
                basePackage, new HashMap<String, Object>());
        eg.generate();
    }        
}
