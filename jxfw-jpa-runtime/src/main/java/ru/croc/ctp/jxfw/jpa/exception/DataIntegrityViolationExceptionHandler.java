package ru.croc.ctp.jxfw.jpa.exception;

import static com.google.common.collect.Lists.newArrayList;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.croc.ctp.jxfw.core.exception.XfwExceptionHandler;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.store.StoreContext;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

/**
 * Обработчик исключений уровня БД.
 *
 * @author OKrutova
 * @since 1.6
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE / 2)
public class DataIntegrityViolationExceptionHandler {


    private final ExceptionParser exceptionParser;
    private final XfwExceptionHandler xfwExceptionHandler;

    public DataIntegrityViolationExceptionHandler(ExceptionParser exceptionParser, XfwExceptionHandler
            xfwExceptionHandler) {
        this.exceptionParser = exceptionParser;
        this.xfwExceptionHandler = xfwExceptionHandler;
    }


    /**
     * Обработчик ошибок  DataIntegrityViolationException.
     *
     * @param ex     исключение
     * @param locale локаль пользователя
     * @return Exception ТО для объекта-исключения
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> exceptionHandler(final DataIntegrityViolationException ex, Locale locale,
                                              HttpServletRequest httpRequest) {


        StoreContext storeContext = new StoreContext(newArrayList());
        storeContext.setLocale(locale);

        if(ex instanceof DataIntegrityViolationExceptionWithStoreContext){
            storeContext = ((DataIntegrityViolationExceptionWithStoreContext)ex).getStoreContext();
        }


        XException integrityViolationException = exceptionParser.parseException(ex, storeContext);


        if (integrityViolationException != null) {
            return xfwExceptionHandler.exceptionHandler(integrityViolationException, locale, httpRequest);
        } else {
            return xfwExceptionHandler.exceptionHandler(new XException.Builder(
                    "ru.croc.ctp.jxfw.core.exception.exceptions.DataAccessException.message",
                    "Storage access error. Contact your system administrator.")
                    .cause(ex)
                    .build(), locale, httpRequest);

        }

    }


}