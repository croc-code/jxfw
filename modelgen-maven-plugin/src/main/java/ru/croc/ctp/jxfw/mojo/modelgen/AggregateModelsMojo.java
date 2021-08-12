package ru.croc.ctp.jxfw.mojo.modelgen;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.zeroturnaround.zip.commons.FileUtils;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.zeroturnaround.zip.ZipUtil.unpack;
import static ru.croc.ctp.jxfw.metamodel.XFWMMPackage.eINSTANCE;

/**
 * Goal плагина для сбора всех Ecore моделей из зависимостей.
 */
@Mojo(name = "aggregate", defaultPhase = LifecyclePhase.INITIALIZE)
public class AggregateModelsMojo extends AbstractMojo {

    /**
     * Расширение файла модели.
     *
     * @see XFWMMPackage#getModelFileExtension()
     */
    static final String MODEL_FILE_EXTENSION = XFWMMPackage.eINSTANCE.getModelFileExtension();

    /**
     * Расширение файла модели с точкой в начале.
     */
    static final String MODEL_FILE_EXTENSION_WITH_DOT = "." + MODEL_FILE_EXTENSION;

    /**
     * Путь к папке модели.
     *
     * @see XFWMMPackage#getModelFolderPath()
     */
    static final String MODEL_FILE_FOLDER = XFWMMPackage.eINSTANCE.getModelFolderPath().toString();

    @Parameter(readonly = true, required = true)
    private File sourceDirectory;

    @Parameter(readonly = true, required = false)
    private File testSourceDirectory;

    /**
     * The project itself. This parameter is set by maven.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    /**
     * Спиок ecore моделей из зависимостей, которые подключаются как модули
     */
    protected final Set<String> nonTransitiveEcoreModels = new HashSet<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("aggregate");
        try {
            Path sourceModelPath = sourceDirectory.toPath().resolve(eINSTANCE.getModelFolderPath());
            Files.createDirectories(sourceModelPath);
            unpackAllModelFiles(sourceDirectory);
            if (testSourceDirectory != null) {
                Path testSourceModelPath = testSourceDirectory.toPath().resolve(eINSTANCE.getModelFolderPath());
                Files.createDirectories(testSourceModelPath);
                FileUtils.copyDirectory(sourceModelPath.toFile(), testSourceModelPath.toFile());
            }
        } catch (IOException ex) {
            getLog().error(ex);
            throw new RuntimeException(ex);
        }
    }


    private void unpackAllModelFiles(File sourceDirectory) {
        final Set<Artifact> dependencyArtifacts = project.getDependencyArtifacts();
        getLog().debug(
                "unpackAllModelFiles() - Search model with file path: " + eINSTANCE.getModelFilePath());

        dependencyArtifacts.stream().filter(artifact -> artifact.getType().toLowerCase().contains("jar"))
                .forEach(artifact -> {
                    final String artifactId = artifact.getArtifactId();
                    final File jar = artifact.getFile();
                    if (jar != null && jar.isFile()) {
                        getLog().debug("Dependency '" + jar.getName() + "' recognize as jar file");
                        if (sourceDirectory != null) {
                            unpack(jar, sourceDirectory, name -> {
                                String modelName = Paths.get(MODEL_FILE_FOLDER, eINSTANCE.getModelFileName())
                                        .toString().replaceAll("[/\\\\]+", "/");
                                if (name.startsWith(modelName)) {
                                    getLog().info("Unpack model with name  '" + name + "'");
                                    // Если модель в jar-нике поименована XFWModel.ecore, то переименовывае ее
                                    // а если нет, то эта модель уже была переименована
                                    if (name.equals(modelName + MODEL_FILE_EXTENSION_WITH_DOT)) {
                                        getLog().info("Rename and copy  '" + name + "'");
                                        name = name.replace(MODEL_FILE_EXTENSION_WITH_DOT,
                                                "_" + artifactId + MODEL_FILE_EXTENSION_WITH_DOT);
                                        //Если переименовываем, тогда модуль является подключаемым внешним модулем
                                        //и имеет приоритет над транзитивными ecore моделями
                                        if(nonTransitiveEcoreModels.contains(name)){
                                            throw new IllegalStateException(String.format("Found more than one ecore model with artifactId: '%s'",artifactId));
                                        }
                                        nonTransitiveEcoreModels.add(name);
                                        return name;
                                    } else if (!nonTransitiveEcoreModels.contains(name)) {
                                        getLog().info("Copy  '" + name + "'");
                                        return name;
                                    } else {
                                        return null;

                                    }
                                } else {
                                    // returning null from the map method will
                                    // disregard the entry
                                    return null;
                                }
                            });
                        }
                    } else if (jar != null && jar.isDirectory()) {
                        getLog().debug("Dependency '" + jar.getName() + "' recognize as directory");
                        // Случай Eclipse когда зависимость в проекте
                        // добавляется,
                        // как ссылка на папку target/classes другого проекта
                        // jar указывает на target/classes папки-зависимости
                        copyModels(new File(jar, MODEL_FILE_FOLDER), artifactId, sourceDirectory.toPath()
                                .resolve(MODEL_FILE_FOLDER).toFile());
                    }
                });
    }


    /**
     * Копирует и переименовывает модели если надо.
     *
     * @param modelDir   - директория откуда.
     * @param artifactId - имя Jar-ника
     * @param targetDir  - директория куда.
     */
    protected void copyModels(File modelDir, String artifactId, File targetDir) {
        if (modelDir.exists()) {
            try (final DirectoryStream<Path> stream
                         = Files.newDirectoryStream(modelDir.toPath(), "*.{"
                    + XFWMMPackage.eINSTANCE.getModelFileExtension() + "}")) {
                stream.forEach(p -> {
                    File modelFile = p.toFile();
                    if (modelFile.exists()) {
                        getLog().debug("Found model '" + modelFile.getName() + "'");
                        // копируем в папку текущего проета с измененным
                        // именем
                        String newFileName = modelFile.getName();
                        if (newFileName.equals(eINSTANCE.getModelFileName()
                                + MODEL_FILE_EXTENSION_WITH_DOT)) {
                            newFileName = newFileName.replace(MODEL_FILE_EXTENSION_WITH_DOT,
                                    "_" + artifactId + MODEL_FILE_EXTENSION_WITH_DOT);
                            if(nonTransitiveEcoreModels.contains("model/" + newFileName)){
                                throw new IllegalStateException(String.format("Found more than one ecore model with artifactId: '%s'",artifactId));
                            }
                            nonTransitiveEcoreModels.add("model/" + newFileName);
                        } else if (nonTransitiveEcoreModels.contains(newFileName)) {
                            newFileName = null;
                        }

                        if (newFileName != null) {
                            final File targetFile = new File(
                                    targetDir, newFileName);
                            try {
                                getLog().debug("Copy model to '" + targetFile + "'");
                                FileUtils.copyFile(modelFile, targetFile);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                });
            } catch (IOException ex) {
                getLog().error(ex);
                throw new RuntimeException(ex);
            }
        }
    }

}
