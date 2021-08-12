package ru.croc.ctp.jxfw.jpa.exception;

import org.springframework.dao.DataIntegrityViolationException;
import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Расширение класса {@link DataIntegrityViolationException},
 * с добавлением объекта контекста сохранения {@link StoreContext}.
 *
 * @author OKrutova
 * @since 1.7.0
 */
public class DataIntegrityViolationExceptionWithStoreContext extends DataIntegrityViolationException {

    private final StoreContext storeContext;

    /**
     * Констукор исключения.
     *
     * @param msg          сообщение
     * @param cause        причина исключения
     * @param storeContext контекст сохранения
     */
    public DataIntegrityViolationExceptionWithStoreContext(String msg, Throwable cause, StoreContext storeContext) {
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
