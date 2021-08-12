package ru.croc.ctp.jxfw.cli.command;

import static ru.croc.ctp.jxfw.cli.command.CliConstants.JXFW_CLI_COMMAND_GROUP;

import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.commands.Clear;


/**
 * Реализация стандарной {@link Clear}, сделана для подмены описания.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@ShellComponent
@ShellCommandGroup(JXFW_CLI_COMMAND_GROUP)
public class XfwClear implements Clear.Command {

    @Autowired
    @Lazy
    private Terminal terminal;

    /**
     * Очистка.
     */
    @ShellMethod("Очистить окно терминала.")
    public void clear() {
        terminal.puts(InfoCmp.Capability.clear_screen);
    }

}
