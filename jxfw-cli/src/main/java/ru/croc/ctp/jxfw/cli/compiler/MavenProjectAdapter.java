package ru.croc.ctp.jxfw.cli.compiler;

import org.apache.maven.project.MavenProject;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Iterator;

/**
 * Адаптер.
 *
 * @author Moritz Eysholdt
 */
public class MavenProjectAdapter extends AdapterImpl {
    /**
     * Получить.
     *
     * @param rs менеджер ресурсов
     * @return {@link MavenProject}
     */
    public static MavenProject get(ResourceSet rs) {
        for (Adapter a : rs.eAdapters()) {
            if (a instanceof MavenProjectAdapter) {
                return ((MavenProjectAdapter) a).project;
            }
        }

        throw new RuntimeException("The Maven Project is not registered in the ResourceSet");
    }

    /**
     * В адаптеры менеджера ресурсов добавляется {@link MavenProjectAdapter}.
     *
     * @param rs      менеджер ресурсов
     * @param project {@link MavenProject}
     */
    public static void install(ResourceSet rs, MavenProject project) {
        Iterator<Adapter> iterator = rs.eAdapters().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof MavenProjectAdapter) {
                iterator.remove();
            }
        }

        rs.eAdapters().add(new MavenProjectAdapter(project));
    }

    private MavenProject project;

    private MavenProjectAdapter(MavenProject project) {
        super();
        this.project = project;
    }
}
