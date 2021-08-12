package ru.croc.ctp.jxfw.transfer.component.imp.context;

import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;

/**
 * Разрешает конфликты зависимостей при импорте.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface ImportDependencyManager {
    /** Пытается разрешить неразрешенные зависимости в контексте импорта.
     * @param context контекст импорта.
     */
    void resolve(ImportContext context);

    /** Устанавливает переданый обработчик.
     * @param handler обработчик объетков с неразрешенными зависимостями.
     */
    void setHandler(ImportDependencyCollisionHandler handler);
}
