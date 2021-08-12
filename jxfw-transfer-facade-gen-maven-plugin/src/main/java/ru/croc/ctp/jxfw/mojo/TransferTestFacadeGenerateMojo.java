package ru.croc.ctp.jxfw.mojo;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import ru.croc.ctp.jxfw.generator.TransferFacadeGenerator;


/**
 * Плагин для генерации фасада модуля Transfer.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Mojo(name = "testGenerateFacade", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST)
public class TransferTestFacadeGenerateMojo extends AbstractFacadeGenMojo {
    @Override
    public void execute() {
        project.addTestCompileSourceRoot(testOutputDirectory.getPath());
        new TransferFacadeGenerator(testSourceDirectory.toPath(), testOutputDirectory.toPath(),
                getOptions(), getLog()).generate();
    }
}
