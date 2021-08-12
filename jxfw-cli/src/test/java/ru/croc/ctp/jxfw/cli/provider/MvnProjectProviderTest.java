package ru.croc.ctp.jxfw.cli.provider;

import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.PlexusContainerException;
import org.junit.Test;

import ru.croc.ctp.jxfw.cli.provider.MvnProjectProvider;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.codehaus.plexus.PlexusTestCase.getTestFile;

public class MvnProjectProviderTest {

    @Test
    public void getAllPoms() throws Exception {
        //given:
        MvnProjectProvider provider = new MvnProjectProvider();
        String parentPomPath = "src/test/resources/mvn-parent-child/pom.xml";
        File parentPom = getTestFile(parentPomPath);
        File settings = getTestFile("src/test/resources/settings.xml");

        assertTrue("Не найден родительский pom.xml - " + parentPomPath, parentPom.exists());

        //when:
        Set<File> poms = provider.listProjectsAllPomFiles(parentPom, settings, new HashSet<>());

        //then:
        int expected = 4;
        assertEquals("Всего д.б. " + expected + " pom файла", expected, poms.size());
        poms.forEach(file -> assertTrue(file.exists()));

    }

    @Test
    public void getAllPomsRecursively() throws Exception {
        //given:
        MvnProjectProvider provider = new MvnProjectProvider();
        String parentPomPath = "src/test/resources/mvn-recursive/pom.xml";
        File parentPom = getTestFile(parentPomPath);
        File settings = getTestFile("src/test/resources/settings.xml");

        assertTrue("Не найден родительский pom.xml - " + parentPomPath, parentPom.exists());

        //when:
        Set<File> poms = provider.listProjectsAllPomFiles(parentPom, settings, new HashSet<>());

        //then:
        int expected = 5;
        assertEquals("Всего д.б. " + expected + " pom файла", expected, poms.size());
        poms.forEach(file -> assertTrue(file.exists()));

    }

    @Test
    public void getAllXtendProjects() throws Exception {
        //given:
        MvnProjectProvider provider = new MvnProjectProvider();
        String parentPomPath = "src/test/resources/mvn-parent-child/pom.xml";
        File parentPom = getTestFile(parentPomPath);
        File settings = getTestFile("src/test/resources/settings.xml");

        assertTrue("Не найден родительский pom.xml - " + parentPomPath, parentPom.exists());

        //when:
        Set<MavenProject> xtendMvnProjects = provider.listXtendMvnProjects(parentPom, settings);

        //then:
        int expected = 2;
        assertEquals("Всего д.б. " + expected + " pom файла", expected, xtendMvnProjects.size());
    }

    @Test
    public void getAllXtendProjectsRecursively() throws Exception {
        //given:
        MvnProjectProvider provider = new MvnProjectProvider();
        String parentPomPath = "src/test/resources/mvn-recursive/pom.xml";
        File parentPom = getTestFile(parentPomPath);
        File settings = getTestFile("src/test/resources/settings.xml");

        assertTrue("Не найден родительский pom.xml - " + parentPomPath, parentPom.exists());

        //when:
        Set<MavenProject> xtendMvnProjects = provider.listXtendMvnProjects(parentPom, settings);

        //then:
        int expected = 3;
        assertEquals("Всего д.б. " + expected + " pom файла", expected, xtendMvnProjects.size());
    }

    @Test
    public void defaultRepo() throws PlexusContainerException {
        MvnProjectProvider provider = new MvnProjectProvider();
        Settings settings = new Settings();
        String repoPath = provider.resolveLocalRepo(settings);

        String absolutePath = RepositorySystem.defaultUserLocalRepository.getAbsolutePath();
        //System.out.println("absolutePath = " + absolutePath);
        assertEquals(absolutePath, repoPath);

    }
}
