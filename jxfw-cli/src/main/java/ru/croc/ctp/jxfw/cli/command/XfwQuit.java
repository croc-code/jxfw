package ru.croc.ctp.jxfw.cli.command;

import static ru.croc.ctp.jxfw.cli.command.CliConstants.JXFW_CLI_COMMAND_GROUP;

import org.springframework.shell.ExitRequest;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Quit;

/**
 * Реализация стандарной {@link Quit}, сделана для подмены описания.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@ShellComponent
@ShellCommandGroup(JXFW_CLI_COMMAND_GROUP)
public class XfwQuit implements Quit.Command {

    /**
     * Выход.
     */
    @ShellMethod(value = "Выйти из терминала.", key = {"quit", "exit"})
    public void quit() {
        throw new ExitRequest();
    }

}
