package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Goal плагина для генерации контроллеров по Ecore модели тестов.
 * <p/>
 * Created by SPlaunov on 29.07.2016.
 */
@Mojo(name = "testGenerateFacade", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
    requiresDependencyResolution = ResolutionScope.TEST)
public class FacadeTestGenerateMojo extends AbstractFacadeGenerateMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        project.addTestCompileSourceRoot(testOutputDirectory.getPath());
        doGenerate(testSourceDirectory, testOutputDirectory);
    }
}
