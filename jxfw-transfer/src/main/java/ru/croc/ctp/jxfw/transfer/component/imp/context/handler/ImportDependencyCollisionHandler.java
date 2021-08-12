package ru.croc.ctp.jxfw.transfer.component.imp.context.handler;

import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.util.List;
import java.util.Set;

/**
 * Обработчик для разрешения конфликта зависимсстей между объектами при импорте.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportDependencyCollisionHandler {
    /** Получает список групп объектов, которые загруженны не будут. Если пользователя
     * устраивают такие условия импорта следует вернуть true, если нет, то false и
     * операция импорта прервется.
     * @param groupsOfProblem список групп объектов, которые не могут быть загруженны.
     * @return Если импорт одлжен продолжаться true, иначе false и импорт завершается с ошибкой.
     */
    boolean handle(List<Set<ImportDtoInfo>> groupsOfProblem);
}
