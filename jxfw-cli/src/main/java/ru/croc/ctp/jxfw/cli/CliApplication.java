package ru.croc.ctp.jxfw.cli;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import ru.croc.ctp.jxfw.cli.dispatcher.CmdDispatcher;

import static ru.croc.ctp.jxfw.cli.command.CliConstants.JXFW_CLI_COMMAND_GROUP;

import java.io.File;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Точка входа, интерфейс взаимодействия с пользователем cli утилиты.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@SpringBootApplication
@ShellComponent
@ShellCommandGroup(JXFW_CLI_COMMAND_GROUP)
public class CliApplication implements ApplicationListener<ContextRefreshedEvent> {

    private CliProperties cliProperties;
    private CmdDispatcher dispatcher;
    private CliPrinter cliPrinter;
    private File pom;
    private File mvnSettings;

    private boolean running = false;

    /**
     * Main.
     *
     * @param args аргументы
     */
    public static void main(String[] args) {
        //отключаем комманды по умолчанию (в рамках чтобы можно было задать свои наименования)
        String[] disabledCommands = {"--spring.shell.command.script.enabled=false"};
        String[] fullArgs = org.springframework.util.StringUtils.concatenateStringArrays(args, disabledCommands);
        SpringApplication.run(CliApplication.class, fullArgs);
    }

    /**
     * Указать настройки maven settings.xml.
     *
     * @param settings настройки settings.xml
     * @return true если подошел
     */
    @ShellMethod(
            value = "Указать файл настроек maven (прим. settings <папка пользователя>\\\\.m2\\\\settings.xml).",
            key = {"set-maven-settings", "sms"}
    )
    @ShellMethodAvailability("settingsAvailability")
    public String settings(File settings) {
        boolean exists = settings.exists();
        if (exists) {
            this.mvnSettings = settings;
            cliProperties.setSettings(mvnSettings.getAbsolutePath());
        }
        return fileFoundStatus(settings);
    }

    /**
     * Указать родительский pom.xml.
     *
     * @param pom родительский pom.xml
     * @return true если подошел
     */
    @ShellMethod(
            value = "Указать родительский pom.xml проекта (прим. pom <корень проекта>\\\\pom.xml).",
            key = {"set-project-pom", "spp"}
    )
    @ShellMethodAvailability("pomAvailability")
    public String pom(File pom) {
        boolean exists = pom.exists();
        if (exists) {
            this.pom = pom;
            cliProperties.setPom(pom.getAbsolutePath());
        }
        return fileFoundStatus(pom);
    }

    private String fileFoundStatus(File file) {
        if (file.exists()) {
            return MessageFormat.format("{0} - найден", file.getAbsolutePath());
        }
        return MessageFormat.format("{0} - не найден, укажите верное расположение файла", file.getAbsolutePath());
    }

    /**
     * Запуск слежения/компиляции xtend.
     *
     * @throws Exception ошибка
     */
    @ShellMethod(
            value = "Начать отслеживать изменения xtend файлов.",
            key = {"watch-start", "wst"}
    )
    @ShellMethodAvailability("startAvailability")
    public void start(boolean reset) throws Exception {
        if (reset) {
            //это заставит расчитатать по новой структуру приложения, тех pom.xml которые содержат xtend плагин
            cliProperties.remove(CliProperties.TRACKED_POMS);
        }
        
        dispatcher.start(pom, mvnSettings, cliProperties);
        running = true;
    }
    
    public void start() throws Exception {
    	start(false);
    }

    /**
     * Останов слежения/компиляции xtend.
     */
    @ShellMethod(
            value = "Прекратить отслеживать изменения xtend файлов.",
            key = {"watch-stop", "wsp"}
    )
    @ShellMethodAvailability("onlyStarted")
    public void stop() {
        dispatcher.stop();
        running = false;
    }

    /**
     * Отобразить текущие настройки.
     */
    @ShellMethod(
            value = "Отобразить текущие настройки.",
            key = {"show-current-settings", "scs"}
    )
    public void showCurrentSettings() {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("настройки утилиты: {0}", cliProperties.getLocation()));
        sb.append(System.lineSeparator());
        if (pom != null) {
            sb.append(MessageFormat.format("pom.xml, используется: {0}, найден: {1}", pom, pom.exists()));
        } else {
            sb.append("pom.xml не указан");
        }
        sb.append(System.lineSeparator());
        if (mvnSettings != null) {
            sb.append(
                    MessageFormat.format("settings.xml, используется: {0}, найден: {1}",
                            mvnSettings,
                            mvnSettings.exists())
            );
        } else {
            sb.append("settings.xml не указан");
        }

        sb.append(System.lineSeparator());
        sb.append("Информация об отслеживаемых pom.xml, которые содержат в себе xtend плагин");

        if (cliProperties.getTrackedPoms().isEmpty()) {
            sb.append(", появится после первого запуска \"watch-start\", \"wst\"");
        } else {
            String structure = cliProperties.getTrackedPoms()
                    .stream().map(File::getAbsolutePath).collect(Collectors.joining(", "));
            sb.append(": " + structure);
        }
        sb.append(System.lineSeparator());

        cliPrinter.info(sb.toString());
    }

    /**
     * Отобразить текущие настройки.
     */
    @ShellMethod(
            value = "Отобразить список отслеживаемых xtend файлов.",
            key = {"sxs", "show-xtend-sources"}
    )
    @ShellMethodAvailability("onlyStarted")
    public void showXtendSources() {
        cliPrinter.info(dispatcher.listSourcesInTracking());
    }

    /**
     * Доступность команды ru.croc.ctp.jxfw.cli.CliApplication#pom.
     *
     * @return достпуность
     */
    public Availability pomAvailability() {
        return !running ? Availability.available() :
                Availability.unavailable(
                        "Для использования команды (Указать родительский pom.xml) "
                                + "отслеживание должно быть остановлено: \"watch-stop\", \"wsp\""
                );
    }

    /**
     * Доступность команды ru.croc.ctp.jxfw.cli.CliApplication#settings.
     *
     * @return достпуность
     */
    public Availability settingsAvailability() {
        return !running ? Availability.available() :
                Availability.unavailable(
                        "Для использования команды (Указать настройки maven settings.xml) "
                                + "отслеживание должно быть остановлено: \"watch-stop\", \"wsp\""
                );
    }

    /**
     * Доступность команды.
     *
     * @return достпуность
     */
    public Availability onlyStarted() {
        return running ? Availability.available() :
                Availability.unavailable("Для использования команды отслеживание должно быть запущено:"
                        + " \"watch-start\", \"wst\"");
    }

    /**
     * Доступность команды.
     *
     * @return достпуность
     */
    public Availability startAvailability() {
        if (!running) {
            boolean pomPresent = pom != null && pom.exists();
            boolean settingsPresent = mvnSettings != null && mvnSettings.exists();
            StringBuilder sb = new StringBuilder();
            if (pom == null) {
                sb.append("pom.xml - не указан.");
            } else if (!pom.exists()) {
                sb.append("pom.xml - не найден.");
            }
            if (mvnSettings == null) {
                sb.append("settings.xml - не указан.");
            } else if (!mvnSettings.exists()) {
                sb.append("settings.xml - не найден.");
            }
            return pomPresent && settingsPresent ? Availability.available() : Availability.unavailable(sb.toString());
        }

        return Availability.unavailable(
                "Для использования команды отслеживание изменений xtend должно быть остановлено:"
                        + " \"watch-stop\", \"wsp\""
        );
    }

    @Autowired
    public void setCmdDispatcher(CmdDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Autowired
    public void setCliProperties(CliProperties cliProperties) {
        this.cliProperties = cliProperties;
    }

    public CliProperties getCliProperties() {
        return cliProperties;
    }

    @Autowired
    public void setCliPrinter(CliPrinter cliPrinter) {
        this.cliPrinter = cliPrinter;
    }

    public boolean isRunning() {
        return running;
    }

    private static Optional<File> getFile(String pathname) {
        if (StringUtils.isNotEmpty(pathname)) {
            return Optional.of(new File(pathname));
        }
        return Optional.empty();
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        //InitializingBean тут  не подходит т.к. нету еще созданной виртуальной консоли для вывода в случае ошибки
        cliProperties.setLocation(CliProperties.getCliPropertiesFileLocation());
        //определяем pom.xml
        identifyPomLocation();
        //определяем settings.xml для maven
        identifyMvnSettingsLocation();
    }

    private void identifyPomLocation() {
        Optional<File> pomOptional = getFile(cliProperties.getPom());
        if (pomOptional.isPresent()) {
            this.pom = pomOptional.get();
        } else {
            //пробуем найти корневой pom.xml
            File defaultPomSearchLocation = new File(System.getProperty("user.dir"), "pom.xml");
            if (defaultPomSearchLocation.exists()) {
                pom(defaultPomSearchLocation);
            }
        }
    }

    private void identifyMvnSettingsLocation() {
        Optional<File> settingsOptional = getFile(cliProperties.getSettings());
        if (settingsOptional.isPresent()) {
            this.mvnSettings = settingsOptional.get();
        } else {
            //По умолчанию задать путь до %USERPROFILE%/.m2/settings.xml
            File dir = new File(System.getProperty("user.home"), ".m2");
            settings(new File(dir, "settings.xml"));
        }
    }
}

