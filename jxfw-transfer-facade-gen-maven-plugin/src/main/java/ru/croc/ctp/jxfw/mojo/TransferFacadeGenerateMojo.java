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
@Mojo(name = "generateFacade", defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class TransferFacadeGenerateMojo extends AbstractFacadeGenMojo {
    @Override
    public void execute() {
        project.addCompileSourceRoot(outputDirectory.getPath());
        new TransferFacadeGenerator(sourceDirectory.toPath(), outputDirectory.toPath(),
                getOptions(), getLog()).generate();
    }
}
