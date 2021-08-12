package ru.croc.ctp.jxfw.jpa.exception;

import org.springframework.transaction.UnexpectedRollbackException;
import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Расширение класса {@link UnexpectedRollbackException},
 * с добавлением объекта контекста сохранения {@link StoreContext}.
 *
 * @author SPyatykh
 * @since 1.9.0
 */

public class UnexpectedRollbackExceptionWithStoreContext extends UnexpectedRollbackException {

    private final StoreContext storeContext;

    /**
     * Констукор исключения.
     *
     * @param msg          сообщение
     * @param cause        причина исключения
     * @param storeContext контекст сохранения
     */
    public UnexpectedRollbackExceptionWithStoreContext(String msg, Throwable cause, StoreContext storeContext) {
        super(msg, cause);
        this.storeContext = storeContext;
    }


    /**
     * Получить контекст сохранения.
     *
     * @return контекст сохранения
     */
    public StoreContext getStoreContext() {
        return storeContext;
    }
}
