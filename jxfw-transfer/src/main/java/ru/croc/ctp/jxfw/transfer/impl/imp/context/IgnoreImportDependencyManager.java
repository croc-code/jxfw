package ru.croc.ctp.jxfw.transfer.impl.imp.context;

import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContext;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportDependencyManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;

import java.util.Collections;


/**
 * Игнорирует все конфликты зависимостей при импорте.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("ignoreImportDependencyManager")
public class IgnoreImportDependencyManager implements ImportDependencyManager {
    private ImportDependencyCollisionHandler handler = null;

    /** Игнорирует все конфликты зависимостей при проверке.
     * @param context контекст импорта.
     */
    public void resolve(ImportContext context) {
        //  игнорируем
        if (handler != null) {
            handler.handle(Collections.EMPTY_LIST);
        }
    }

    @Override
    public void setHandler(ImportDependencyCollisionHandler handler) {
        this.handler = handler;
    }
}
