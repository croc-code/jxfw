package ru.croc.ctp.jxfw.jpa.exception;

import org.hibernate.HibernateException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.croc.ctp.jxfw.core.store.StoreContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


/**
 * Обработчик исключений уровня БД, для распределенного менеджера транзакций.
 * т.к. исключения упаковываются в {@link UnexpectedRollbackException}, поэтому
 * переупоковываем и вызываем соответсвующий обработчик.
 *
 * @author Alexander Golovin
 * @since 1.9
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE / 10)
public class UnexpectedRollbackExceptionHandler {
    private final DataIntegrityViolationExceptionHandler dataIntegrityViolationExceptionHandler;
    private final DataAccessExceptionHandler dataAccessExceptionHandler;

    /**
     * Обработчик исключений уровня БД, для распределенного менеджера транзакций.
     *
     * @param dataIntegrityViolationExceptionHandler обработчик для {@link DataIntegrityViolationException}.
     * @param dataAccessExceptionHandler обработчик для {@link DataAccessException}.
     */
    public UnexpectedRollbackExceptionHandler(
            DataIntegrityViolationExceptionHandler dataIntegrityViolationExceptionHandler,
            DataAccessExceptionHandler dataAccessExceptionHandler) {
        this.dataIntegrityViolationExceptionHandler = dataIntegrityViolationExceptionHandler;
        this.dataAccessExceptionHandler = dataAccessExceptionHandler;
    }


    /**
     * Обработчик ошибок {@link UnexpectedRollbackException}.
     *
     * @param ex     исключение
     * @param locale локаль пользователя
     * @param httpRequest запрос.
     */
    @ExceptionHandler(UnexpectedRollbackException.class)
    public ResponseEntity<?> exceptionHandler(final UnexpectedRollbackException ex, Locale locale,
                                              HttpServletRequest httpRequest) {

        final StoreContext storeContext;

        if (ex instanceof UnexpectedRollbackExceptionWithStoreContext) {
            storeContext = ((UnexpectedRollbackExceptionWithStoreContext) ex).getStoreContext();
        } else {
            storeContext = null;
        }

        Throwable exCause = ex.getCause();
        while (exCause != null) {
            if (exCause instanceof HibernateException) {
                // упаковываем исключения Hibernate в Spring
                final DataAccessException dataAccessException = SessionFactoryUtils
                        .convertHibernateAccessException((HibernateException) exCause);
                // обрабатываем исключение соответсвующим обработчиком
                if (dataAccessException instanceof DataIntegrityViolationException) {
                    return dataIntegrityViolationExceptionHandler
                            .exceptionHandler(
                                new DataIntegrityViolationExceptionWithStoreContext(
                                    dataAccessException.getMessage(), dataAccessException.getCause(), storeContext
                                ),
                                locale,
                                httpRequest
                            );
                } else {
                    return dataAccessExceptionHandler
                            .ExceptionToexceptionHandler(dataAccessException, locale, httpRequest);
                }
            }

            exCause = exCause.getCause();
        }
       return null;
    }
}