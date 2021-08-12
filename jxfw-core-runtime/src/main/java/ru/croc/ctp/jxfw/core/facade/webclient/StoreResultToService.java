package ru.croc.ctp.jxfw.core.facade.webclient;

import ru.croc.ctp.jxfw.core.store.AggregateStoreResult;
import ru.croc.ctp.jxfw.core.store.StoreResult;

import java.util.Locale;

/**
 * Сервис трансформации StoreResult -> StoreResultDTO.
 */
public interface StoreResultToService {
    
    /**
     * Трансформирует {@link StoreResult} в DTO удобный для дальнейшей трансформации в JSON.
     * @param storeResult Входной объект для трансформации в DTO
     * @return Результат трансформации
     */
    StoreResultDto toTo(StoreResult storeResult, Locale locale);

    /**
     * Трансформирует {@link AggregateStoreResult} в DTO удобный для дальнейшей трансформации в JSON.
     * @param storeResult Входной объект для трансформации в DTO
     * @return Результат трансформации
     */
    StoreResultDto toTo(AggregateStoreResult storeResult, Locale locale);

}
