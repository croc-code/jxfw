package ru.croc.ctp.jxfw.cli;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.ResultHandler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Настройки работы пользователем cli утилиты.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@Component
public class CliProperties extends Properties {

    /**
     * 
     */
    private static final long serialVersionUID = -1565194708991355436L;
    
    /**
     * Имя файла.
     */
    public static final String PROP_FILENAME = "jxfw-cli.properties";
    /**
     * Проперти для последнего pom.xml.
     */
    public static final String POM_FILE = "pom.file";
    /**
     * Проперти для последнего settings.xml.
     */
    public static final String MVN_SETTINGS_FILE = "mvn.settings.file";
    /**
     * Проперти, список pom.xml c xtend-maven-plugin через ";".
     */
    public static final String TRACKED_POMS = "tracked.poms";

    private String pom;
    private String settings;
    private Set<File> trackedPoms = new HashSet<>();
    private File location;

    private ResultHandler resultHandler;

    public CliProperties(@Qualifier("main") ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    /**
     * Здесь расположены настройки утилиты.
     *
     * @return файл настроек утилиты
     */
    public static File getCliPropertiesFileLocation() {
        File userDirectory = new File(System.getProperty("user.dir"));
        return userDirectory.toPath().resolve(PROP_FILENAME).toFile();
    }


    private void flushChanges() {
        try {
            store(new FileWriter(getLocation()), null);
        } catch (IOException e) {
            resultHandler.handleResult(e);
        }
    }

    public String getPom() {
        return pom;
    }

    /**
     * Указать последний pom.xml.
     * @param pom файл
     */
    public void setPom(String pom) {
        this.pom = pom;
        put(POM_FILE, pom);
        {
            //замена корневого pom должен сбросить информацию об отслеживаемых pom (c xtend плагином)
            remove(TRACKED_POMS);
            trackedPoms.clear();
        }
        flushChanges();
    }

    public String getSettings() {
        return settings;
    }

    /**
     * Указать последний settings.xml.
     * @param settings файл
     */
    public void setSettings(String settings) {
        this.settings = settings;
        put(MVN_SETTINGS_FILE, settings);
        flushChanges();
    }

    public Set<File> getTrackedPoms() {
        return trackedPoms;
    }

    /**
     * Указать коллекцию pom.xml для которых производится отслеживание xtend на изменения.
     * @param trackedPoms множество pom.xml
     */
    public void setTrackedPoms(Set<File> trackedPoms) {
        if (trackedPoms == null || trackedPoms.isEmpty()) {
            this.trackedPoms = new HashSet<>();
            remove(TRACKED_POMS);
        } else {
            this.trackedPoms = trackedPoms;
            String poms = trackedPoms.stream().map(File::getAbsolutePath).sorted().collect(Collectors.joining(";"));
            put(TRACKED_POMS, poms);
        }

        flushChanges();
    }

    public File getLocation() {
        return location;
    }

    public void setLocation(File location) {
        this.location = location;
        try (Reader r = new InputStreamReader(new FileInputStream(location))) {
            load(r);
            //Сеттеры не используем!!!!!
            pom = getOrDefault(POM_FILE, "").toString();
            settings = getOrDefault(MVN_SETTINGS_FILE, "").toString();
            //парсинг строки с pom.xml' ями
            {
                String paths = getOrDefault(TRACKED_POMS, "").toString();
                if (StringUtils.isNotEmpty(paths)) {
                    trackedPoms = Arrays
                            .stream(paths.split(";")).map(path -> new File(path)).collect(Collectors.toSet());
                }
            }
        } catch (IOException e) {
            //намернное давим ошибку, чтобы сообщение не сбивало с толку
        }
    }

}
