package ru.croc.ctp.jxfw.core.exception;

import static com.google.common.base.Throwables.getStackTraceAsString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ru.croc.ctp.jxfw.core.exception.dto.ExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.ExceptionTo.ExceptionToBuilder;

/**
 * Глобальный перехватчик исключений для оборачивания в ExceptionTO.
 *
 * @author Nosov Alexander
 *         on 08.06.15.
 * @see ExceptionTo
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    /**
     * Логгер.
     */
    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Обработчик ошибок "общий" для случаев когда не описан более узкий обработчик.
     *
     * @param ex Exception ТО для объекта-исключения
     * @return ExceptionTO
     * @see ExceptionTo
     * @see ExceptionToBuilder
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionTo exceptionHandler(final Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ExceptionToBuilder.create()
                .message(ex.getMessage())
                .className(ex.getClass().getSimpleName())
                .stackTrace(getStackTraceAsString(ex))
                .build();
    }

}