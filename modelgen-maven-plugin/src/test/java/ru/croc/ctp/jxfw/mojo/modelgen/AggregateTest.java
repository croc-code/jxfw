package ru.croc.ctp.jxfw.mojo.modelgen;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.ecore.ENamedElement;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.zeroturnaround.zip.commons.FileUtils;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWModel;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;


public class AggregateTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testModelCopy() throws IOException {
        AggregateModelsMojo mojo = new AggregateModelsMojo();
        FileUtils.deleteDirectory(new File("src/test/resources/modelsDest"));
        mojo.copyModels(new File("src/test/resources/modelsSource"),
                "test", new File("src/test/resources/modelsDest"));
        Assert.assertTrue(new File("src/test/resources/modelsDest/XFWModel_test.ecore").exists());
        Assert.assertTrue(new File("src/test/resources/modelsDest/XFWModel_ctp-integration.ecore").exists());
    }

    /**
     * JXFW-1696. Проверяем, что при подключении модуля основное ecore-описание определённое в нём,
     * имело приоритет над всеми ecore-описаниями модели модуля с этим же наименованием, полученными из других модулей транзитивно.
     * <p/>
     * В модуле module2-1.0-SNAPSHOT.jar содержажся основная ecore-модель и полученная из module1 транзитивно.<br>
     * В модуле module1-1.0-SNAPSHOT.jar содержится ecore-модель, которая отличаетя от хранимой в module2
     */
    @Test
    public void checkEcorePriority() throws MojoFailureException, MojoExecutionException, IOException {
        FileUtils.deleteDirectory(new File("target/generated-test-sources/xtend"));
        AggregateModelsMojo mojo = new AggregateModelsMojo();
        mojo.project = new MavenProject();
        ReflectionTestUtils.setField(mojo, "sourceDirectory", new File("target/generated-test-sources/xtend"));

        Set<Artifact> artifacts = new HashSet<>();
        mojo.project.setDependencyArtifacts(artifacts);

        Artifact artifactProject2 = new DefaultArtifact(
                "testGroupId",
                "module2",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier2",
                null);

        artifactProject2.setFile(new File("src/test/resources/testModulesJars/module2-1.0-SNAPSHOT.jar"));
        artifacts.add(artifactProject2);
        mojo.execute();

        //Из ecore моделей получим названия классов без module1
        XFWModel xfwModel = new XFWModelImpl(Paths.get("target/generated-test-sources/xtend"));
        Set<String> classesNamesFromModule2 = xfwModel.getAll(XFWClass.class).stream().map(ENamedElement::getName).collect(Collectors.toSet());

        Assert.assertEquals(2, classesNamesFromModule2.size());
        Assert.assertTrue(classesNamesFromModule2.contains("User2") && classesNamesFromModule2.contains("User1"));
        //Добавим модуль module1
        mojo.nonTransitiveEcoreModels.clear();
        FileUtils.deleteDirectory(new File("target/generated-test-sources/xtend"));
        Artifact artifactProject1 = new DefaultArtifact(
                "testGroupId",
                "module1",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier1",
                null);
        artifactProject1.setFile(new File("src/test/resources/testModulesJars/module1-1.0-SNAPSHOT.jar"));
        artifacts.add(artifactProject1);
        mojo.execute();

        XFWModel xfwModelWithModule1 = new XFWModelImpl(Paths.get("target/generated-test-sources/xtend"));
        Set<String> classesNamesFromModule2AndModule1 = xfwModelWithModule1.getAll(XFWClass.class).stream().map(ENamedElement::getName).collect(Collectors.toSet());

        Assert.assertEquals(2, classesNamesFromModule2AndModule1.size());
        Assert.assertTrue(classesNamesFromModule2AndModule1.contains("User2")
                && classesNamesFromModule2AndModule1.contains("UserUpdated")
                && !classesNamesFromModule2AndModule1.contains("User1"));

    }


    /**
     * JXFW-1696. Проверяем, что при подключении модуля основное ecore-описание определённое в нём,
     * имело приоритет над всеми ecore-описаниями модели модуля с этим же наименованием, полученными из других модулей транзитивно.
     * <p/>
     * Рассматривается случай Eclipse когда зависимость в проект добавляется, как ссылка на папку target/classes другого проекта
     * jar указывает на target/classes папки-зависимости
     */
    @Test
    public void checkEcorePriorityEclipseCase() throws MojoFailureException, MojoExecutionException, IOException {
        FileUtils.deleteDirectory(new File("target/generated-test-sources/xtend"));
        AggregateModelsMojo mojo = new AggregateModelsMojo();
        mojo.project = new MavenProject();
        ReflectionTestUtils.setField(mojo, "sourceDirectory", new File("target/generated-test-sources/xtend"));

        Set<Artifact> artifacts = new HashSet<>();
        mojo.project.setDependencyArtifacts(artifacts);

        Artifact artifactProject2 = new DefaultArtifact(
                "testGroupId",
                "module2",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier2",
                null);

        artifactProject2.setFile(new File("src/test/resources/testModulesJars/module2-1.0-SNAPSHOT.jar"));
        artifacts.add(artifactProject2);
        mojo.execute();

        //Из ecore моделей получим названия классов без module1
        XFWModel xfwModel = new XFWModelImpl(Paths.get("target/generated-test-sources/xtend"));
        Set<String> classesNamesFromModule2 = xfwModel.getAll(XFWClass.class).stream().map(ENamedElement::getName).collect(Collectors.toSet());

        Assert.assertEquals(2, classesNamesFromModule2.size());
        Assert.assertTrue(classesNamesFromModule2.contains("User2") && classesNamesFromModule2.contains("User1"));

        //Добавим модуль module1
        mojo.nonTransitiveEcoreModels.clear();
        FileUtils.deleteDirectory(new File("target/generated-test-sources/xtend"));
        Artifact artifactProject1 = new DefaultArtifact(
                "testGroupId",
                "module1",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier1",
                null);
        artifactProject1.setFile(new File("src/test/resources/testModulesJars/"));
        artifacts.add(artifactProject1);
        mojo.execute();

        XFWModel xfwModelWithModule1 = new XFWModelImpl(Paths.get("target/generated-test-sources/xtend"));
        Set<String> classesNamesFromModule2AndModule1 = xfwModelWithModule1.getAll(XFWClass.class).stream().map(ENamedElement::getName).collect(Collectors.toSet());

        Assert.assertEquals(2, classesNamesFromModule2AndModule1.size());
        Assert.assertTrue(classesNamesFromModule2AndModule1.contains("User2")
                && classesNamesFromModule2AndModule1.contains("UserUpdated")
                && !classesNamesFromModule2AndModule1.contains("User1"));

    }

    /**
     * JXFW-1696.
     * <br/>При подключении модулей с одинаковым artifactId падаем с ошибкой, так как это случай двусмысленности: непонятно какую ecore модель из модулей выбирать
     * <br/> Подключаем в проект два jar модуля с одинаковым artifactId
     */
    @Test
    public void checkEcorePriorityExpectException() throws MojoFailureException, MojoExecutionException, IOException{
        FileUtils.deleteDirectory(new File("target/generated-test-sources/xtend"));
        AggregateModelsMojo mojo = new AggregateModelsMojo();
        mojo.project = new MavenProject();
        ReflectionTestUtils.setField(mojo, "sourceDirectory", new File("target/generated-test-sources/xtend"));

        Set<Artifact> artifacts = new HashSet<>();
        mojo.project.setDependencyArtifacts(artifacts);
        //jar зависимость
        Artifact artifactProject1 = new DefaultArtifact(
                "testGroupId1",
                "module1",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier1",
                null);

        artifactProject1.setFile(new File("src/test/resources/testModulesJars/module1-1.0-SNAPSHOT.jar"));
        artifacts.add(artifactProject1);

        //еще одна jar зависимость с тем же artifactId
        Artifact artifactProject2 = new DefaultArtifact(
                "testGroupId2",
                "module1",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier1",
                null);

        artifactProject2.setFile(new File("src/test/resources/testModulesJars/module1-1.0-SNAPSHOT-Error.jar"));
        artifacts.add(artifactProject2);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(containsString("Found more than one ecore model with artifactId: 'module1'"));
        mojo.execute();
    }

    /**
     * JXFW-1696.
     * <br/>При подключении модулей с одинаковым artifactId падаем с ошибкой, так как это случай двусмысленности: непонятно какую ecore модель из модулей выбирать
     * <br/>Случай Eclipse. Один модуль из jar, другой как путь к папке
     */
    @Test
    public void checkEcorePriorityEclipseCaseExpectException() throws MojoFailureException, MojoExecutionException, IOException{
        FileUtils.deleteDirectory(new File("target/generated-test-sources/xtend"));
        AggregateModelsMojo mojo = new AggregateModelsMojo();
        mojo.project = new MavenProject();
        ReflectionTestUtils.setField(mojo, "sourceDirectory", new File("target/generated-test-sources/xtend"));

        Set<Artifact> artifacts = new HashSet<>();
        mojo.project.setDependencyArtifacts(artifacts);
        //jar зависимость
        Artifact artifactProject1 = new DefaultArtifact(
                "testGroupId1",
                "module1",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier1",
                null);

        artifactProject1.setFile(new File("src/test/resources/testModulesJars/module1-1.0-SNAPSHOT.jar"));
        artifacts.add(artifactProject1);

        //eclipe зависимость
        Artifact artifactProject2 = new DefaultArtifact(
                "testGroupId2",
                "module1",
                "1.0-SNAPSHOT",
                "test",
                "jar",
                "testClassifier1",
                null);

        artifactProject2.setFile(new File("src/test/resources/testModulesJars/"));
        artifacts.add(artifactProject2);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(containsString("Found more than one ecore model with artifactId: 'module1'"));
        mojo.execute();
    }


}
