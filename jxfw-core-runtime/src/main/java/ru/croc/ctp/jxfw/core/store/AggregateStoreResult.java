package ru.croc.ctp.jxfw.core.store;

import org.springframework.http.HttpStatus;

import ru.croc.ctp.jxfw.core.facade.webclient.StoreRequestResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Тип данных для хранения результата синхронизации UoW.
 * 
 * @since 1.4
 */
public class AggregateStoreResult implements StoreRequestResult {
    
    private final HttpStatus aggregateStatus;

    private final List<StoreResult> results = new ArrayList<>();
    
    public AggregateStoreResult(List<StoreResult> results) {
        HttpStatus currentStatus = null;
        for (StoreResult sr: results) {
            currentStatus = add(sr, currentStatus);
        }
        aggregateStatus = currentStatus.is4xxClientError() ? HttpStatus.BAD_REQUEST : currentStatus;
    }

    /**
     * Добавить еще один StoreResult.
     *
     * @param storeResult - объект для добавления.
     */
    private HttpStatus add(final StoreResult storeResult, HttpStatus currentStatus) {
        this.results.add(storeResult);
        if (currentStatus == null) {
            return storeResult.getHttpStatus();
        } else {
            return getMaxStatus(aggregateStatus, storeResult.getHttpStatus());
        }
    }
    
    private static HttpStatus getMaxStatus(HttpStatus... httpStatuses) {
        Arrays.sort(httpStatuses);
        return httpStatuses[httpStatuses.length - 1];
    }

    /**
     * @return Список объектов, результатов синхронизации.
     */
    public List<StoreResult> getStoreResults() {
        return results;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return aggregateStatus;
    }
}
