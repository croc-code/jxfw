package ru.croc.ctp.jxfw.core.store;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.http.HttpStatus;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainToServicesResolver;
import ru.croc.ctp.jxfw.core.domain.IdentityMapping;
import ru.croc.ctp.jxfw.core.exception.exceptions.XException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XOptimisticConcurrencyException;
import ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.StoreRequestResult;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilder;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilderFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Тип данных для хранения результата сохранения UoW.
 *
 * @since 1.1
 */
public class StoreResult implements StoreRequestResult {

    private final List<IdentityMapping> idMapping = new ArrayList<>();
    private final List<DomainTo> updatedObjects = new ArrayList<>();
    private final List<DomainTo> originalObjects = new ArrayList<>();
    private final List<DomainTo> errorObjects = new ArrayList<>();

    @JsonIgnore
    private final List<DomainObject<?>> originalDomainObjects = new ArrayList<>();

    @JsonIgnore
    private final List<DomainObject<?>> updatedDomainObjects = new ArrayList<>();

    private final XException error;
    private final HttpStatus httpStatus;

    /**
     * Создать результат без ошибок.
     */
    public StoreResult() {
        this((XException) null, null, null, null);
    }

    /**
     * Создать результат с ошибкой.
     *
     * @param error - объект исключения.
     * @param domainToSrvResolver {@link DomainToServicesResolver}
     * @param paramsBuilderFactory {@link QueryParamsBuilderFactory}
     * @param loadService {@link LoadService}
     */
    public StoreResult(XException error, DomainToServicesResolver domainToSrvResolver,
                       QueryParamsBuilderFactory paramsBuilderFactory, LoadService loadService) {
        this.error = error;
        this.httpStatus = calcHttpStatus(error);
        if (error instanceof XOptimisticConcurrencyException) {
            XOptimisticConcurrencyException ex = (XOptimisticConcurrencyException)error;
            for (DomainTo obsoleteObject : ex.getObsoleteObjects()) {
                DomainTo originalDomainTo = getOriginalDomainToObject(obsoleteObject, domainToSrvResolver,
                        paramsBuilderFactory, loadService);
                originalObjects.add(originalDomainTo);
            }
        }

    }

    private DomainTo getOriginalDomainToObject(@Nonnull DomainTo errorToObject,
                                               @Nonnull DomainToServicesResolver domainToServicesResolver,
                                               @Nonnull QueryParamsBuilderFactory paramsBuilderFactory,
                                               @Nonnull LoadService loadService) {
        DomainToService<DomainObject<?>, ?> domainToService = domainToServicesResolver
                .resolveToService(errorToObject.getType());

        QueryParamsBuilder<DomainObject<Serializable>, Serializable> paramsBuilder = paramsBuilderFactory.newBuilder(errorToObject.getType(),
                domainToService.parseKey(errorToObject.getId()));

        DomainObject<Serializable> domainObject = loadService.loadOne(paramsBuilder.build(),
                // FIXME JXFW-1223 сюда должен пойти контекст сохранения => контексты должны иметь общую иерархию
                new LoadContext<>());

        return domainToService.toTo(domainObject);
    }


    /**
     * Объединение данных двух и более StoreResult.
     *
     * @param existResult - текущий результат
     * @param storeResults - объекты для объединения
     */
    public StoreResult(final StoreResult existResult, final StoreResult... storeResults) {

        mergeProperties(existResult);

        XException error = existResult.error;
        HttpStatus status = existResult.httpStatus;

        for (StoreResult sr: storeResults) {
            mergeProperties(sr);
            if (sr.getError() != null) {
                error = sr.getError();
                status = sr.getHttpStatus();
            }
        }

        this.error = error;
        this.httpStatus = status;
    }

    private void mergeProperties(final StoreResult sr) {

        this.getOriginalObjects().addAll(sr.getOriginalObjects());
        this.getOriginalDomainObjects().addAll(sr.getOriginalDomainObjects());

        this.getUpdatedObjects().addAll(sr.getUpdatedObjects());
        this.getUpdatedDomainObjects().addAll(sr.getUpdatedDomainObjects());

        this.getErrorObjects().addAll(sr.getErrorObjects());

        this.getIdMapping().addAll(sr.getIdMapping());
    }

    /**
     * {@link HttpStatus}.
     *
     * @param error error
     * @return {@link HttpStatus}
     */
    public static HttpStatus calcHttpStatus(Exception error) {
        HttpStatus result = HttpStatus.CREATED;
        if (error instanceof XOptimisticConcurrencyException) {
            result = HttpStatus.CONFLICT;
        } else if (error instanceof XSecurityException) {
            result = HttpStatus.FORBIDDEN;
        } else if (error instanceof XException) {
            result = HttpStatus.BAD_REQUEST;
            //  если исключение- только XException, то это обертка надо исключением ORM,
            // не является клиентской ошибкой.
            if (error.getClass().isAssignableFrom(XException.class)) {
                result = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        } else if (error instanceof Exception) {
            result = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * @return Ошибка при сохранении группы объектов.
     */
    public XException getError() {
        return error;
    }

    public List<DomainTo> getOriginalObjects() {
        return originalObjects;
    }

    /**
     * @return Список объектов, измененных при сохранении UoW на сервере.
     */
    public List<DomainTo> getUpdatedObjects() {
        return updatedObjects;
    }

    /**
     * @return Список измененных идентификаторов сохраненных новых объектов.
     */
    public List<IdentityMapping> getIdMapping() {
        return idMapping;
    }

    /**
     * @return Список доменных объектов в оригинальном варианте в результате
     *         сохранения которых произошла ошибка кофликта версий объекта.
     */
    public List<DomainObject<?>> getOriginalDomainObjects() {
        return originalDomainObjects;
    }

    /**
     * @return Список объектов, связанных с ошибкой сохранения, в ответе
     *         возвращается только тип и идентификатор.
     */
    public List<DomainTo> getErrorObjects() {
        return errorObjects;
    }

    public List<DomainObject<?>> getUpdatedDomainObjects() {
        return updatedDomainObjects;
    }
}
