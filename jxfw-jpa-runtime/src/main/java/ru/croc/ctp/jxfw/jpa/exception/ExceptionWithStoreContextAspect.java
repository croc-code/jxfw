package ru.croc.ctp.jxfw.jpa.exception;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import ru.croc.ctp.jxfw.core.store.StoreContext;

/**
 * Аспект ловит исключения, возникшие в процессе сохранения,
 * и добавляет в исключение информацию о контексте сохранения {@link StoreContext}.
 *
 * @author OKrutova
 * @since 1.7.0
 */
@Aspect
@Component
public class ExceptionWithStoreContextAspect implements Ordered {

    /**
     * Явное указание проядка необходимо, чтобы аспект вызывался снаружи {@link TransactionInterceptor}.
     * По умолчанию {@link TransactionInterceptor} имеет низший порядок, поэтому сначала будет вызываться этот аспект,
     * а потом {@link TransactionInterceptor}. Благодяря этому данный аспект поймает исключение, которое произойдет
     * при завершении транзакции в {@link TransactionInterceptor}.
     *
     * @return порядок
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * Расширение исключения {@link DataIntegrityViolationException}.
     *
     * @param storeContext контекст сохранения
     * @param ex           исключение
     */
    @AfterThrowing(
            pointcut = "execution(* ru.croc.ctp.jxfw.jpa.store.impl.UnitOfWorkJpaMultiStoreServiceImpl.store(..))"
                    + " && args(storeContext)", throwing = "ex")
    public void wrapDataIntegrityViolationException(StoreContext storeContext, DataIntegrityViolationException ex) {

        throw new DataIntegrityViolationExceptionWithStoreContext(ex.getMessage(),
                ex.getCause(), storeContext);

    }

    @AfterThrowing(
        pointcut = "execution(* ru.croc.ctp.jxfw.jpa.store.impl.UnitOfWorkJpaMultiStoreServiceImpl.store(..))"
            + " && args(storeContext)", throwing = "ex")
    public void wrapDataIntegrityViolationException(StoreContext storeContext, UnexpectedRollbackException ex) {

        throw new UnexpectedRollbackExceptionWithStoreContext(ex.getMessage(),
            ex.getCause(), storeContext);

    }

}
