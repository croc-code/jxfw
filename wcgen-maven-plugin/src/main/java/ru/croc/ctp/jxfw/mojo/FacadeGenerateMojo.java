package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Goal плагина для генерации контроллеров по Ecore модели.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
@Mojo(name = "generateFacade", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class FacadeGenerateMojo extends AbstractFacadeGenerateMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project.addCompileSourceRoot(outputDirectory.getPath());
        doGenerate(sourceDirectory, outputDirectory);
    }

}
