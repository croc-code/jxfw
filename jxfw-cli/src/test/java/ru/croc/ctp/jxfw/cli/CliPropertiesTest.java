package ru.croc.ctp.jxfw.cli;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.shell.ResultHandler;

import ru.croc.ctp.jxfw.cli.CliApplication;
import ru.croc.ctp.jxfw.cli.CliProperties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CliPropertiesTest {

    public static final String TEST_STR = "jXFW-test";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void read() {
        File propFile = getTestFile("src/test/resources/jxfw-cli.properties");
        @SuppressWarnings("unchecked")
        CliProperties cliProperties = new CliProperties(mock(ResultHandler.class));
        cliProperties.setLocation(propFile);

        String pom = cliProperties.getPom();
        String settings = cliProperties.getSettings();

        assertEquals("mvn-parent-child/pom.xml", pom);
        assertEquals("settings.xml", settings);
    }

    @Test
    public void flushChanges() throws IOException {
        File props = File.createTempFile("test-jxfw-cli", "properties");

        @SuppressWarnings("unchecked")
        CliProperties cliProperties = new CliProperties(mock(ResultHandler.class));
        cliProperties.setLocation(props);

        String locationOfPom = UUID.randomUUID().toString();
        //сохраним новое значение
        cliProperties.setPom(locationOfPom);

        //перезагрузим c диска
        cliProperties.setLocation(props);

        assertEquals(locationOfPom, cliProperties.getPom());

        String locationOfSettings = UUID.randomUUID().toString();
        //сохраним новое значение
        cliProperties.setSettings(locationOfSettings);

        //перезагрузим
        cliProperties.setLocation(props);
        assertEquals(locationOfSettings, cliProperties.getSettings());
        assertEquals(locationOfPom, cliProperties.getPom());
    }

    @Test
    public void cliPropertiesLocation() {
        File props = CliProperties.getCliPropertiesFileLocation();
        assertTrue(props.getAbsolutePath().contains(new File(System.getProperty("user.dir")).getAbsolutePath()));
        assertTrue(props.getAbsolutePath().endsWith(CliProperties.PROP_FILENAME));
    }

    @Test
    public void pomIsSearchedOnStart() throws IOException {
        //создаем темповый pom.xml
        File testPom = createTestPomFile();

        //меняем рабочую директорию приложения
        System.setProperty("user.dir", testPom.getParent());

        //запускаем процесс анализа настроек для старта приложения
        CliApplication app = new CliApplication();
        @SuppressWarnings("unchecked")
        CliProperties cliProperties = new CliProperties(mock(ResultHandler.class));
        app.setCliProperties(cliProperties);
        app.onApplicationEvent(null);

        //проверяем что pom находится
        String lastPom = app.getCliProperties().getPom();
        String content = FileUtils.fileRead(lastPom);
        assertEquals(TEST_STR, content);

    }

    @Test
    public void onPomChangedTrackedPomsInfoRemoved() throws IOException {
        File props = File.createTempFile("test-jxfw-cli", "properties");

        @SuppressWarnings("unchecked")
        CliProperties properties = new CliProperties(mock(ResultHandler.class));
        properties.setLocation(props);
        HashSet<File> trackedPoms = new HashSet<>();
        trackedPoms.add(mock(File.class));
        properties.setTrackedPoms(trackedPoms);

        //when
        properties.setPom("123");

        //then
        assertTrue(properties.getTrackedPoms().isEmpty());
    }

    private File createTestPomFile() throws IOException {
        File testPom = folder.newFile("pom.xml");
        FileWriter fileWriter = new FileWriter(testPom);
        fileWriter.write(TEST_STR);
        fileWriter.close();

        return testPom;
    }
}
