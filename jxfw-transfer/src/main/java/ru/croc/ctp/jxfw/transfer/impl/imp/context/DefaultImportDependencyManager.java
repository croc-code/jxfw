package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContext;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportDependencyManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Разрешает конфликты зависимостей при импорте.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("importDependencyManager")
public class DefaultImportDependencyManager implements ImportDependencyManager {
    private ImportDependencyCollisionHandler handler = null;

    /** Пытается разрешить неразрешенные зависимости в контексте импорта.
     * @param context контекст импорта.
     */
    public void resolve(ImportContext context) {
        final List<Set<ImportDtoInfo>> issues = findAllDtoInfoWithNotResolvedDependencies(context);
        if (handler == null || !handler.handle(issues)) {
            throw new RuntimeException("Имопорт прерван, так как существуют группы объектов "
                    + "с неразрешенными зависимостями.");
        }
    }

    /** Находит все доменные объекты с неразрешенными зависимостями и удаляет их из контекста импорта.
     * @return список групп доменных объектов с неразрешенными зависимостями.
     */
    private static List<Set<ImportDtoInfo>> findAllDtoInfoWithNotResolvedDependencies(ImportContext context) {
        final List<Set<ImportDtoInfo>> result = new ArrayList<>();

        for (Iterator<Map.Entry<Long, Set<ImportDtoInfo>>> iterator =
                context.getGroupsOfLoading().entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Long, Set<ImportDtoInfo>> group = iterator.next();

            for (ImportDtoInfo dtoInfo : group.getValue()) {
                if (dtoInfo.isLoadFromFile() && !dtoInfo.getDependenciesOfNotResolved().isEmpty()) {
                    result.add(group.getValue());
                    // Note: удаляем проблемную группу
                    iterator.remove();
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public void setHandler(ImportDependencyCollisionHandler handler) {
        this.handler = handler;
    }
}
