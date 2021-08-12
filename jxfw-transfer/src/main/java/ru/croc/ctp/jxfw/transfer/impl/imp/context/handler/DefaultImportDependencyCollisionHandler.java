package ru.croc.ctp.jxfw.transfer.impl.imp.context.handler;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.List;
import java.util.Set;

/**
 * Обработчик для разрешения конфликта зависимостей.
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("importDependencyCollisionHandler")
public class DefaultImportDependencyCollisionHandler implements ImportDependencyCollisionHandler {
    @Override
    public boolean handle(List<Set<ImportDtoInfo>> groupsOfProblem) {
        return groupsOfProblem.isEmpty();
    }
}
