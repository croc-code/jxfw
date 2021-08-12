package ru.croc.ctp.jxfw.cli;

import org.junit.Test;

import ru.croc.ctp.jxfw.cli.CliApplication;
import ru.croc.ctp.jxfw.cli.CliProperties;

import java.io.File;

import static junit.framework.TestCase.assertTrue;
import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.mockito.Mockito.mock;

public class CliApplicationTest {

    @Test
    public void provideFile() {
        CliApplication cliApplication = new CliApplication();
        cliApplication.setCliProperties(mock(CliProperties.class));
        File testFile = getTestFile("src/test/resources/settings.xml");

        String settings = cliApplication.settings(testFile);
        String pom = cliApplication.pom(testFile);

        //System.out.println("settings = " + settings);

        assertTrue(settings.contains("- найден"));
        assertTrue(pom.contains("- найден"));

        String settingsNotFound = cliApplication.settings(new File("no such File"));
        String pomNotFound = cliApplication.pom(new File("no such File"));

        assertTrue(settingsNotFound.contains("- не найден"));
        assertTrue(pomNotFound.contains("- не найден"));

        //System.out.println("pomNotFound = " + pomNotFound);
    }
}
