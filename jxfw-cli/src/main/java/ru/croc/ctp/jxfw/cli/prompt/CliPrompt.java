package ru.croc.ctp.jxfw.cli.prompt;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.cli.CliApplication;

import java.text.MessageFormat;

/**
 * Кастомизация состояния строки запроса.
 * jXFW (готова к началу работы):> ...
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@Component
public class CliPrompt implements PromptProvider {

    private CliApplication cliApplication;

    /**
     * Конструктор.
     * @param cliApplication интерфейс
     */
    public CliPrompt(CliApplication cliApplication) {
        this.cliApplication = cliApplication;
    }

    @Override
    public AttributedString getPrompt() {
        String template = "jXFW ({0}):>";
        String prompt = "";
        if (cliApplication.isRunning()) {
            prompt = MessageFormat.format(template, "отслеживает изменения xtend");
        } else if (cliApplication.startAvailability().isAvailable()) {
            prompt = MessageFormat.format(template, "не отслеживает изменения xtend");
        } else {
            prompt = MessageFormat.format(template, "не задана конфигурация, \"show-current-settings\", \"scs\"");
        }

        return new AttributedString(prompt, AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
    }
}
