package ru.croc.ctp.jxfw.cli.command;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static ru.croc.ctp.jxfw.cli.command.CliConstants.JXFW_CLI_COMMAND_GROUP;

import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.CommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.ParameterDescription;
import org.springframework.shell.ParameterResolver;
import org.springframework.shell.Utils;
import org.springframework.shell.standard.CommandValueProvider;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.Help;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;



/**
 * Реализация стандарной {@link Help}, сделана для подмены описания.
 *
 * @author SMufazzalov
 * @since 1.8.x
 */
@ShellComponent
@ShellCommandGroup(JXFW_CLI_COMMAND_GROUP)
public class XfwHelp implements Help.Command {

    private final List<ParameterResolver> parameterResolvers;

    private CommandRegistry commandRegistry;

    private MessageInterpolator messageInterpolator = Validation.buildDefaultValidatorFactory()
            .getMessageInterpolator();

    /**
     * Конструктор.
     *
     * @param parameterResolvers список реализаций, которыя исходя из текстового input могут конвертировать его
     *                           в объект.
     */
    @Autowired
    public XfwHelp(List<ParameterResolver> parameterResolvers) {
        this.parameterResolvers = parameterResolvers;
    }

    @Autowired // ctor injection impossible b/c of circular dependency
    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }


    @Autowired(required = false)
    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.messageInterpolator = validatorFactory.getMessageInterpolator();
    }


    /**
     * Помощь.
     *
     * @param command комманда
     * @return описание
     * @throws IOException exception
     */
    @ShellMethod(value = "Помощь по доступным коммандам.", prefix = "-")
    public CharSequence help(
            @ShellOption(defaultValue = ShellOption.NULL, valueProvider = CommandValueProvider.class, value = {"-C",
                    "--command"}, help = "The command to obtain help for.") String command)
            throws IOException {
        if (command == null) {
            return listCommands();
        } else {
            return documentCommand(command);
        }

    }

    /**
     * Return a description of a specific command. Uses a layout inspired by *nix man pages.
     */
    private CharSequence documentCommand(String command) {
        MethodTarget methodTarget = commandRegistry.listCommands().get(command);
        if (methodTarget == null) {
            throw new IllegalArgumentException("Unknown command '" + command + "'");
        }

        AttributedStringBuilder result = new AttributedStringBuilder().append("\n\n");
        List<ParameterDescription> parameterDescriptions = getParameterDescriptions(methodTarget);

        // NAME
        documentCommandName(result, command, methodTarget.getHelp());

        // SYNOPSYS
        documentSynopsys(result, command, parameterDescriptions);

        // OPTIONS
        documentOptions(result, parameterDescriptions);

        // ALSO KNOWN AS
        documentAliases(result, command, methodTarget);

        // AVAILABILITY
        documentAvailability(result, methodTarget);

        result.append("\n");
        return result;
    }

    private void documentCommandName(AttributedStringBuilder result, String command, String help) {
        result.append("NAME", AttributedStyle.BOLD).append("\n\t");
        result.append(command).append(" - ").append(help).append("\n\n");
    }

    private void documentSynopsys(AttributedStringBuilder result, String command,
                                  List<ParameterDescription> parameterDescriptions) {
        result.append("SYNOPSYS", AttributedStyle.BOLD).append("\n\t");
        result.append(command, AttributedStyle.BOLD);
        result.append(" ");

        for (ParameterDescription description : parameterDescriptions) {

            if (description.defaultValue().isPresent() && description.formal().length() > 0) {
                result.append("["); // Whole parameter is optional, as there is a default value (1)
            }
            List<String> keys = description.keys();
            if (!keys.isEmpty()) {
                if (!description.mandatoryKey()) {
                    result.append("["); // Specifying a key is optional (ie positional params). (2)
                }
                result.append(first(keys), AttributedStyle.BOLD);
                if (!description.mandatoryKey()) {
                    result.append("]"); // (close 2)
                }
                if (!description.formal().isEmpty()) {
                    result.append(" ");
                }
            }
            if (description.defaultValueWhenFlag().isPresent()) {
                result.append("["); // Parameter can be used as a toggle flag (3)
            }
            appendUnderlinedFormal(result, description);
            if (description.defaultValueWhenFlag().isPresent()) {
                result.append("]"); // (close 3)
            }
            if (description.defaultValue().isPresent() && description.formal().length() > 0) {
                result.append("]"); // (close 1)
            }
            result.append("  "); // two spaces between each param for better legibility
        }
        result.append("\n\n");
    }

    private void documentOptions(AttributedStringBuilder result, List<ParameterDescription> parameterDescriptions) {
        if (!parameterDescriptions.isEmpty()) {
            result.append("OPTIONS", AttributedStyle.BOLD).append("\n");
        }
        for (ParameterDescription description : parameterDescriptions) {
            result.append("\t").append(description.keys().stream().collect(Collectors.joining(" or ")),
                    AttributedStyle.BOLD);
            if (description.formal().length() > 0) {
                if (!description.keys().isEmpty()) {
                    result.append("  ");
                }
                description.defaultValueWhenFlag().ifPresent(f -> result.append('['));
                appendUnderlinedFormal(result, description);
                description.defaultValueWhenFlag().ifPresent(f -> result.append(']'));
                result.append("\n\t");
            } else if (description.keys().size() > 1) {
                result.append("\n\t");
            }
            result.append("\t");
            result.append(description.help()).append('\n');
            // Optional parameter
            if (description.defaultValue().isPresent()) {
                result
                        .append("\t\t[Optional, default = ", AttributedStyle.BOLD)
                        .append(description.defaultValue().get(), AttributedStyle.BOLD.italic());
                description.defaultValueWhenFlag().ifPresent(s -> result.append(", or ", AttributedStyle.BOLD)
                        .append(s, AttributedStyle.BOLD.italic())
                        .append(" if used as a flag", AttributedStyle.BOLD));

                result.append("]", AttributedStyle.BOLD);
            } else if (description.defaultValueWhenFlag().isPresent()) {
                // Mandatory parameter, but with a default when used as a flag
                result
                        .append("\t\t[Mandatory, default = ", AttributedStyle.BOLD)
                        .append(description.defaultValueWhenFlag().get(), AttributedStyle.BOLD.italic())
                        .append(" when used as a flag]", AttributedStyle.BOLD);
            } else {
                // true mandatory parameter
                result.append("\t\t[Mandatory]", AttributedStyle.BOLD);
            }
            result.append('\n');
            if (description.elementDescriptor() != null) {
                for (ConstraintDescriptor<?> constraintDescriptor : description.elementDescriptor()
                        .getConstraintDescriptors()) {
                    String friendlyConstraint = messageInterpolator.interpolate(
                            constraintDescriptor.getMessageTemplate(), new XfwHelp.DummyContext(constraintDescriptor));
                    result.append("\t\t[" + friendlyConstraint + "]\n", AttributedStyle.BOLD);
                }
            }
            result.append('\n');
        }
    }

    private void documentAliases(AttributedStringBuilder result, String command, MethodTarget methodTarget) {
        Set<String> aliases = commandRegistry.listCommands().entrySet().stream()
                .filter(e -> e.getValue().equals(methodTarget))
                .map(Map.Entry::getKey)
                .filter(c -> !command.equals(c))
                .collect(toCollection(TreeSet::new));

        if (!aliases.isEmpty()) {
            result.append("ALSO KNOWN AS", AttributedStyle.BOLD).append("\n");
            for (String alias : aliases) {
                result.append('\t').append(alias).append('\n');
            }
        }
    }

    private void documentAvailability(AttributedStringBuilder result, MethodTarget methodTarget) {
        Availability availability = methodTarget.getAvailability();
        if (!availability.isAvailable()) {
            result.append("CURRENTLY UNAVAILABLE", AttributedStyle.BOLD).append("\n");
            result.append('\t').append("This command is currently not available because ")
                    .append(availability.getReason())
                    .append(".\n");
        }
    }

    private String first(List<String> keys) {
        return keys.iterator().next();
    }

    private CharSequence listCommands() {
        Map<String, MethodTarget> commandsByName = commandRegistry.listCommands();

        SortedMap<String, Map<String, MethodTarget>> commandsByGroupAndName = commandsByName.entrySet().stream()
                .collect(groupingBy(e -> e.getValue().getGroup(), TreeMap::new, // group by and sort by command group
                        toMap(Map.Entry::getKey, Map.Entry::getValue)));

        AttributedStringBuilder result = new AttributedStringBuilder();
        result.append("ДОСТУПНЫЕ КОМАНДЫ\n\n", AttributedStyle.BOLD);

        // display groups, sorted alphabetically, "Default" first
        commandsByGroupAndName.forEach((group, commandsInGroup) -> {
            result.append("".equals(group) ? "Default" : group, AttributedStyle.BOLD).append('\n');

            Map<MethodTarget, SortedSet<String>> commandNamesByMethod = commandsInGroup.entrySet().stream()
                    .collect(groupingBy(Map.Entry::getValue, // group by command method
                            mapping(Map.Entry::getKey, toCollection(TreeSet::new)))); // sort command names

            // display commands, sorted alphabetically by their first alias
            commandNamesByMethod.entrySet().stream().sorted(sortByFirstCommandName()).forEach(e -> {
                result
                        .append(isAvailable(e.getKey()) ? "        " : "      * ")
                        .append(String.join(", ", e.getValue()), AttributedStyle.BOLD)
                        .append(": ")
                        .append(e.getKey().getHelp())
                        .append('\n');
            });

            result.append('\n');
        });

        if (commandsByName.values().stream().distinct().anyMatch(m -> !isAvailable(m))) {
            result.append(
                    "Commands marked with (*) are currently unavailable.\nType `help <command>` to learn more.\n\n"
            );
        }

        return result;
    }

    private Comparator<Map.Entry<MethodTarget, SortedSet<String>>> sortByFirstCommandName() {
        return Comparator.comparing(e -> e.getValue().first());
    }

    private boolean isAvailable(MethodTarget methodTarget) {
        return methodTarget.getAvailability().isAvailable();
    }

    private void appendUnderlinedFormal(AttributedStringBuilder result, ParameterDescription description) {
        for (char c : description.formal().toCharArray()) {
            if (c != ' ') {
                result.append("" + c, AttributedStyle.DEFAULT.underline());
            } else {
                result.append(c);
            }
        }
    }

    private List<ParameterDescription> getParameterDescriptions(MethodTarget methodTarget) {
        return Utils.createMethodParameters(methodTarget.getMethod())
                .flatMap(mp -> parameterResolvers.stream().filter(pr -> pr.supports(mp)).limit(1L)
                        .flatMap(pr -> pr.describe(mp)))
                .collect(Collectors.toList());

    }

    private static class DummyContext implements MessageInterpolator.Context {

        private final ConstraintDescriptor<?> descriptor;

        private DummyContext(ConstraintDescriptor<?> descriptor) {
            this.descriptor = descriptor;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return descriptor;
        }

        @Override
        public Object getValidatedValue() {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    }

}
