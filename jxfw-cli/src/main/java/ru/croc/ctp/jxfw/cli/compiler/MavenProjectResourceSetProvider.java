package ru.croc.ctp.jxfw.cli.compiler;

import com.google.inject.Provider;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;

/**
 * Провайдер менеджера ресурсов.
 * @author Moritz Eysholdt
 */
public class MavenProjectResourceSetProvider implements Provider<ResourceSet> {

    private MavenProject project;

    /**
     * Конструктор.
     * @param project {@link MavenProject}
     */
    public MavenProjectResourceSetProvider(MavenProject project) {
        super();
        this.project = project;
    }

    /**
     * Получить.
     * @return {@link ResourceSet}
     */
    public ResourceSet get() {
        ResourceSet rs = new XtextResourceSet();
        MavenProjectAdapter.install(rs, project);
        return rs;
    }
}
