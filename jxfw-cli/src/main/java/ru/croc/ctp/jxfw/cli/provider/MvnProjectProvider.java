package ru.croc.ctp.jxfw.cli.provider;

import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.model.Profile;
import org.apache.maven.model.building.ModelProcessor;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.SettingsUtils;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Провайдер информации о проекте и модулях используя maven библиотеки.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@Component
public class MvnProjectProvider {

    private PlexusContainer container = new DefaultPlexusContainer(config());

    /**
     * Коструктор.
     *
     * @throws PlexusContainerException ошибка
     */
    public MvnProjectProvider() throws PlexusContainerException {
    }

    /**
     * Получить список pom.xml в которых есть xtend файлы.
     *
     * @param childPom дочерний
     * @param settings настройки
     * @param poms     результат
     * @return множество pom.xml
     * @throws Exception ошибка
     */
    public Set<File> listProjectsAllPomFiles(File childPom, File settings, Set<File> poms) throws Exception {
        poms.add(childPom);

        Set<File> result = new HashSet<>();
        result.add(childPom);

        MavenProject parentProject = provide(childPom, settings);
        File basedir = childPom.getParentFile();
        List<String> modules = parentProject.getModules();
        ModelProcessor modelProcessor = container.lookup(ModelProcessor.class);

        modules.forEach(module -> {
            if (!StringUtils.isEmpty(module)) {
                module = module.replace('\\', File.separatorChar).replace('/', File.separatorChar);

                File moduleFile = new File(basedir, module);

                if (moduleFile.isDirectory()) {
                    try {
                        result.addAll(
                                listProjectsAllPomFiles(modelProcessor.locatePom(moduleFile), settings, poms)
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("bad pom for module - " + moduleFile, e);
                    }
                } else {
                    throw new RuntimeException("bad pom for module - " + moduleFile);
                }
            }
        });

        return result;
    }

    /**
     * Получить {@link MavenProject}.
     *
     * @param pom      родительский
     * @param settings настройки
     * @return {@link MavenProject}
     * @throws Exception ошибка
     */
    public MavenProject provide(File pom, File settings) throws Exception {

        Settings settingsObj = readSettingsFile(settings);
        ProjectBuildingRequest config = new DefaultProjectBuildingRequest();
        config.setProcessPlugins(false);
        config.setResolveDependencies(true);
        for (org.apache.maven.settings.Profile rawProfile : settingsObj.getProfiles()) {
            Profile profile = SettingsUtils.convertFromSettingsProfile(rawProfile);
            config.addProfile(profile);
        }

        String localRepoUrl = "file://" + resolveLocalRepo(settingsObj);
        RepositorySystem repositorySystem = container.lookup(RepositorySystem.class);
        config.setLocalRepository(
                repositorySystem.createArtifactRepository("local", localRepoUrl, new DefaultRepositoryLayout(), null,
                        null));
        config.setActiveProfileIds(settingsObj.getActiveProfiles());

        DefaultRepositorySystemSession repoSession = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(config.getLocalRepository().getBasedir());
        repoSession.setLocalRepositoryManager(
                new SimpleLocalRepositoryManagerFactory().newInstance(repoSession, localRepo));
        config.setRepositorySession(repoSession);

        ProjectBuilder builder = container.lookup(ProjectBuilder.class);
        MavenProject project = builder.build((pom), config).getProject();

        return project;
    }

    private static Settings readSettingsFile(File settingsFile)
            throws IOException, XmlPullParserException {
        Settings settings = null;

        try (Reader reader = ReaderFactory.newXmlReader(settingsFile)) {
            SettingsXpp3Reader modelReader = new SettingsXpp3Reader();

            settings = modelReader.read(reader);
        }
        return settings;
    }

    private final ContainerConfiguration config() {
        ContainerConfiguration config = new DefaultContainerConfiguration();
        String path = "plexus.xml";
        config.setContainerConfiguration(path);
        config.setAutoWiring(true);
        config.setClassPathScanning(PlexusConstants.SCANNING_INDEX);
        return config;
    }

    /**
     * Получить список {@link MavenProject} в которых есть xtend файлы.
     *
     * @param parentPom   родительский
     * @param mvnSettings настройки
     * @return множество {@link MavenProject}
     * @throws Exception ошибка
     */
    public Set<MavenProject> listXtendMvnProjects(File parentPom, File mvnSettings) throws Exception {
        Set<File> allPomFiles = listProjectsAllPomFiles(parentPom, mvnSettings, new HashSet<>());
        return pomsToMvnProjects(allPomFiles, mvnSettings).stream()
                .filter(project -> {
                    return project.getModel().getBuild().getPlugins()
                            .stream()
                            .filter(plugin -> plugin.getArtifactId().contains("xtend-maven-plugin")).findAny()
                            .isPresent();
                })
                .collect(Collectors.toSet());
    }

    public Set<MavenProject> pomsToMvnProjects(Collection<File> poms, File mvnSettings) {
        Set<MavenProject> result = new HashSet<>();
        poms.forEach(file -> {
            try {
                result.add(provide(file, mvnSettings));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    /**
     * Получить путь до локального репозитория.
     *
     * @param settings настройки
     * @return путь
     */
    public String resolveLocalRepo(Settings settings) {
        if (settings != null && StringUtils.isNotEmpty(settings.getLocalRepository())) {
            return settings.getLocalRepository();
        }

        return RepositorySystem.defaultUserLocalRepository.getAbsolutePath();
    }
}
