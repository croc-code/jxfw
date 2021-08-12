package ru.croc.ctp.jxfw.core.facade.webclient;

import org.springframework.http.HttpStatus;

/**
 * Тип данных для хранения результата запроса сохранения.
 * 
 * @since 1.4
 */
public interface StoreRequestResult {

    /**
     * @return статус ответа на запрос, {@link HttpStatus}.
     */
    HttpStatus getHttpStatus();

}
