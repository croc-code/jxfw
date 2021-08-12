package ru.croc.ctp.jxfw.jpa.exception;


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.croc.ctp.jxfw.core.exception.XfwExceptionHandler;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;


/**
 * Обработчик исключений слоя хранения.
 *
 * @author OKrutova
 * @since 1.6
 */
@ControllerAdvice
@Order((Ordered.LOWEST_PRECEDENCE / 4) * 3)
public class DataAccessExceptionHandler {

    private final XfwExceptionHandler xfwExceptionHandler;

    public DataAccessExceptionHandler(XfwExceptionHandler
                                              xfwExceptionHandler) {
        this.xfwExceptionHandler = xfwExceptionHandler;
    }


    /**
     * Обработчик ошибок ORM. Отдает клиенту общую обертку, скрывает детали
     * сообщения об ошибке.
     *
     * @param ex     исключение
     * @param locale локаль пользователя
     * @return Exception ТО для объекта-исключения
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> ExceptionToexceptionHandler(final DataAccessException ex, Locale locale,
                                                         HttpServletRequest httpRequest) {

        return xfwExceptionHandler.exceptionHandler(new XException.Builder(
                "ru.croc.ctp.jxfw.core.exception.exceptions.DataAccessException.message",
                "Storage access error. Contact your system administrator.")
                .cause(ex)
                .build(), locale, httpRequest);


    }

}
