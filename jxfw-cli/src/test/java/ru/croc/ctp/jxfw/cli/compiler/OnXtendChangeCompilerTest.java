package ru.croc.ctp.jxfw.cli.compiler;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.codehaus.plexus.PlexusTestCase.getTestFile;

import org.apache.maven.project.MavenProject;
import org.junit.Test;

import ru.croc.ctp.jxfw.cli.provider.CompilationTaskProvider;
import ru.croc.ctp.jxfw.cli.provider.MvnProjectProvider;

import java.io.File;

public class OnXtendChangeCompilerTest {

    @Test
    public void outputPath() throws Exception {
        //given:
        MvnProjectProvider provider = new MvnProjectProvider();
        File settings = getTestFile("src/test/resources/settings.xml");
        File pomWithOutputSpecified = getTestFile("src/test/resources/mvn-parent-child/module-xtend-01/pom.xml");
        MavenProject mavenProject = provider.provide(pomWithOutputSpecified, settings);

        //when:
        String outputDirectory = CompilationTaskProvider.resolveOutputDirectory(mavenProject);

        //then:
        assertTrue("Должен быть указана папка в target: " + outputDirectory, outputDirectory.contains("target"));

        //and given:
        File pomWithOUTOutputSpecified = getTestFile("src/test/resources/mvn-parent-child/module-xtend-02/pom.xml");
        mavenProject = provider.provide(pomWithOUTOutputSpecified, settings);

        //and when:
        outputDirectory = CompilationTaskProvider.resolveOutputDirectory(mavenProject);

        //then:
        assertTrue("Должна быть быть в src: " + outputDirectory, outputDirectory.contains("src"));

    }

    @Test
    public void tempDirectory() throws Exception {
        //given:
        MvnProjectProvider provider = new MvnProjectProvider();
        File pomWithOutputSpecified = getTestFile("src/test/resources/mvn-parent-child/module-xtend-01/pom.xml");
        File settings = getTestFile("src/test/resources/settings.xml");
        MavenProject mavenProject = provider.provide(pomWithOutputSpecified, settings);

        //when:
        String tempDirectory = CompilationTaskProvider.resolveTempDirectory(mavenProject);

        //then:
        assertTrue("Должна быть указана папка \"another-temp-directory\": " + tempDirectory, tempDirectory.endsWith("another-temp-directory"));

        //and given:
        File pomWithOUTOutputSpecified = getTestFile("src/test/resources/mvn-parent-child/module-xtend-02/pom.xml");
        mavenProject = provider.provide(pomWithOUTOutputSpecified, settings);

        //and when:
        tempDirectory = CompilationTaskProvider.resolveTempDirectory(mavenProject);

        //then:
        assertTrue("Должна быть указана папка \"xtend\" " + tempDirectory, tempDirectory.endsWith("xtend"));

    }

    @Test
    public void encoding() throws Exception {
        //given:
        MavenProject mavenProject = getMavenProject();

        //when:
        String encoding = CompilationTaskProvider.resolveEncoding(mavenProject);

        //then:
        assertTrue("Должна быть указана \"UTF-TEST\": " + encoding, encoding.equals("UTF-TEST"));

    }

    @Test
    public void javaSourceVersion() throws Exception {
        //given:
        MavenProject mavenProject = getMavenProject();

        //when:
        String javaSourceVersion = CompilationTaskProvider.resolveJavaSourceVersion(mavenProject);

        //then:
        assertTrue("Должна быть указана \"1.8-test\": " + javaSourceVersion, javaSourceVersion.equals("1.8-test"));

    }

    @Test
    public void generateSyntheticSuppressWarnings() throws Exception {
        //given:
        MavenProject mavenProject = getMavenProject();

        //when:
        Boolean generateSyntheticSuppressWarnings = CompilationTaskProvider.resolveGenerateSyntheticSuppressWarnings(mavenProject);

        //then:
        assertFalse(generateSyntheticSuppressWarnings);

    }

    @Test
    public void generateGeneratedAnnotation() throws Exception {
        //given:
        MavenProject mavenProject = getMavenProject();

        //when:
        Boolean generateGeneratedAnnotation = CompilationTaskProvider.resolveGenerateGeneratedAnnotation(mavenProject);

        //then:
        assertTrue(generateGeneratedAnnotation);

    }

    @Test
    public void generateWriteTraceFiles() throws Exception {
        //given:
        MavenProject mavenProject = getMavenProject();

        //when:
        Boolean writeTraceFiles = CompilationTaskProvider.resolveWriteTraceFiles(mavenProject);

        //then:
        assertFalse(writeTraceFiles);

    }

    private MavenProject getMavenProject() throws Exception {
        MvnProjectProvider provider = new MvnProjectProvider();
        File pomWithOutputSpecified = getTestFile("src/test/resources/mvn-parent-child/module-xtend-01/pom.xml");
        File settings = getTestFile("src/test/resources/settings.xml");
        return provider.provide(pomWithOutputSpecified, settings);
    }
}
