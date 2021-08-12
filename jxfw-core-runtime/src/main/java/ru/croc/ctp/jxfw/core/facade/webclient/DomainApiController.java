package ru.croc.ctp.jxfw.core.facade.webclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.impl.WebclientQueryParamsBuilder;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.store.AggregateStoreResult;
import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.StoreResult;
import ru.croc.ctp.jxfw.core.store.StoreService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java8.lang.Iterables.forEach;

/**
 * Основной контроллер для работы с UoW приходящей из WebClient.
 *
 * @since 1.0
 */
@RestController
@RequestMapping("**/api/")
public class DomainApiController {
    private static final Logger logger = LoggerFactory.getLogger(DomainApiController.class);

    private final ResourceStore resourceStore;

    private StoreResultToService storeResultToService;

    private StoreService storeService;

    private DomainFacadeIgnoreService domainFacadeIgnoreService;

    private LoadService loadService;

    private WebclientQueryParamsBuilder<? extends DomainObject, String> webclientQueryParamsBuilder;

    private DomainToServicesResolverWebClient domainToServicesResolver;


    /**
     * Конструктор.
     *
     * @param resourceStore Сервис временного хранения загруженных с клиента файлов
     * @param loadService Сервис загрузки доменных объектов
     * @param webclientQueryParamsBuilder билдер параметров запросов с wc
     * @param domainToServicesResolver Сервис для поиска сервисов трансформации доменных объектов в DomainTO
     */
    @Autowired
    public DomainApiController(ResourceStore resourceStore,
                               LoadService loadService,
                               WebclientQueryParamsBuilder<? extends DomainObject, String> webclientQueryParamsBuilder,
                               DomainToServicesResolverWebClient domainToServicesResolver
                               ) {
        Assert.notNull(resourceStore, "Constructor parameter resourceStore should not be null");
        this.resourceStore = resourceStore;
        this.loadService = loadService;
        this.domainToServicesResolver = domainToServicesResolver;
        this.webclientQueryParamsBuilder = webclientQueryParamsBuilder;
    }

    /**
     * Сохранить UoW приходящего из WebClient.
     *
     * @param uow       - список объектов на сохранение.
     * @param sync      - признак синхронизации, 1 - производится синхронизация.
     * @param hintsStr  -  хинты, переданные с клиента строкой. будут переданы в StoreContext.
     * @param hintsArr  -  хинты, переданные с клиента массивом. будут переданы в StoreContext.
     * @param txId      - txId
     * @param locale    - локаль
     * @param timeZone  - таймзона
     * @param principal - принципал
     * @return ответ для WebClient в формате {@link StoreResultDto}
     */
    @RequestMapping(value = "_store", method = RequestMethod.POST)
    public ResponseEntity<StoreResultDto> store(
            @RequestBody List<DomainTo> uow,
            @RequestParam(required = false, name = "$sync", defaultValue = "0") int sync,
            @RequestParam(required = false, name = "$hints", defaultValue = "") List<String> hintsStr,
            @RequestParam(required = false, name = "$hints[]", defaultValue = "") List<String> hintsArr,
            @RequestParam(required = false, name = "$tx", defaultValue = "") String txId,
            Locale locale,
            TimeZone timeZone,
            Principal principal
    ) {

        logger.info("Store UoW by request './api/_store' uow: \n{} \n $sync: {}", uow, sync);

        try {
            resourceStore.startReading();

            final List<String> hints = Stream.concat(hintsStr.stream(), hintsArr.stream()).collect(Collectors.toList());

            final List<String> hintsList = hints.stream()
                    .map(hint -> {
                        try {
                            return URLDecoder.decode(hint, "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            return hint;
                        }
                    })
                    .collect(Collectors.toList());


            List<StoreResult> storeResults = new ArrayList<>();
            if (sync == 1) {
                final Map<String, List<DomainTo>> transactionUowMap = splitByTransactions(uow);
                forEach(transactionUowMap.entrySet(), (entry) -> {
                    storeResults.add(store(entry.getValue(), hintsList, locale, entry.getKey(), timeZone, principal));
                });
            } else {
                storeResults.add(store(uow, hintsList, locale, txId, timeZone, principal));
            }

            StoreRequestResult storeRequestResult =
                    storeResults.size() == 1 ? storeResults.get(0) : new AggregateStoreResult(storeResults);
            
            if (!storeRequestResult.getHttpStatus().isError()) {
            	deleteResources(uow, resourceStore);
            }
            
            StoreResultDto resultDto;
            if (storeRequestResult instanceof StoreResult) {
                resultDto = storeResultToService.toTo((StoreResult) storeRequestResult, locale);
            } else {
                resultDto = storeResultToService.toTo((AggregateStoreResult) storeRequestResult, locale);
            }           
            
            HttpStatus httpCode = storeRequestResult.getHttpStatus();

            logger.info("Result of store UoW by request './api/_store' httpCode = {}, result = {} ", httpCode,
                    resultDto);
            return new ResponseEntity<>(resultDto, httpCode);

        } catch (RuntimeException exception) {
            String msg = MessageFormat
                    .format("Exception on store UoW by request './api/_store' uow: \n{0} \n $sync: {1}", uow, sync);
            logger.error(msg, exception);

            throw exception;
        } finally {
            resourceStore.endReading();
	    }
    }

    /**
     * Загрузка доменных объектов по типу объекта и ид.
     * @param ids список ид с типами, которые необходимо загрузить
     * @param expand Массивные ссылочные свойства, которые необходимо добавить
     * @param hintsArr  -  хинты, переданные с клиента массивом. будут переданы в LoadContext.
     * @param locale    - локаль
     * @param timeZone  - таймзона
     * @param principal - принципал
     * @return список DomainTO объектов
     */
    @RequestMapping(value = "_load", method = RequestMethod.GET)
    @ConditionalOnProperty(name = "${ru.croc.ctp.wc.apiVersion}", havingValue = "2")
    public ResponseEntity<DomainResult> load(
        @RequestParam("ids") List<DomainObjectIdentity<String>> ids,
        @RequestParam(value = "$expand", required = false) String expand,
        @RequestParam(value = "$hints[]", required = false, defaultValue = "") List<String> hintsArr,
        Locale locale,
        TimeZone timeZone,
        Principal principal
    ) {
        LoadContext loadContext = new LoadContext.Builder<>()
            .withLocale(locale)
            .withPrincipal(principal)
            .withTimeZone(timeZone)
            .withHints(hintsArr).build();

        Map<String, Set<String>> filterById = new HashMap<>();
        //группируем по типу, чтобы одним запросом получить список объектов
        ids.forEach(stringDomainObjectIdentity -> {
            if (!filterById.containsKey(stringDomainObjectIdentity.getTypeName())) {
                Set<String> idsType = new HashSet<>();
                filterById.put(stringDomainObjectIdentity.getTypeName(), idsType);
            }

            filterById.get(stringDomainObjectIdentity.getTypeName()).add(stringDomainObjectIdentity.getId());

        });

        List<DomainObject<String>> resultList = new ArrayList<>();

        filterById.forEach((type, typeIds) -> {
            //добавляем фильтр для каждого отдельного типа и выполняем запрос
            ObjectFilter filter = new ObjectFilter();
            filter.addSimple("id", Filter.IN, typeIds);

            webclientQueryParamsBuilder.withDomainType(type)
                .withExpand(expand)
                .withFilter(filter);
            loadService.load(webclientQueryParamsBuilder.build(), loadContext);

            resultList.addAll(loadContext.getLoadResult().getData());
        });

        LoadResult<? extends DomainObject> loadResult = loadContext.getLoadResult();

        final DomainResult result = new DomainResult.Builder()
            .result(toDomainToList(resultList))
            .build();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    private StoreResult store(
            List<DomainTo> uow,
            List<String> hints,
            Locale locale,
            String txId,
            TimeZone timeZone,
            Principal principal
    ) {
        final List<DomainTo> filteredUow = new ArrayList<>();
        for (DomainTo dto : uow) {
            if (!domainFacadeIgnoreService.isIgnore(dto.getType(), "webclient")) {
                filteredUow.add(dto);
            }
        }
        StoreContext storeContext = new StoreContext.StoreContextBuilder()
                .withUow(filteredUow)
                .withHints(hints)
                .withLocale(locale)
                .withTxId(txId)
                .withTimeZone(timeZone)
                .withPrincipal(principal)
                .build();
        StoreResult result = storeService.store(storeContext);
        return result;
    }

    private Map<String, List<DomainTo>> splitByTransactions(List<DomainTo> uow) {
        Map<String, List<DomainTo>> result = new HashMap<>();
        for (DomainTo domainTo : uow) {
            Object transactionIdProperty = domainTo.getProperty("__tx");
            String transactionId = transactionIdProperty instanceof String ? (String) transactionIdProperty : "unknown";
            if (result.get(transactionId) == null) {
                result.put(transactionId, new ArrayList<>());
            }
            result.get(transactionId).add(domainTo);
        }
        return result;
    }

    @Autowired
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * Инъекция сервиса трансформации для возвращаемого объекта.
     *
     * @param toService - сервис трансформации.
     */
    @Autowired
    public void setStoreResultToService(StoreResultToService toService) {
        this.storeResultToService = toService;
    }

    /**
     * Сервис определения можно ли использовать доменный объекта в данном фасаде.
     *
     * @param domainFacadeIgnoreService сервис определения игнорирования доменного объекта фасадом
     */
    @Autowired
    public void setDomainFacadeIgnoreService(DomainFacadeIgnoreService domainFacadeIgnoreService) {
        this.domainFacadeIgnoreService = domainFacadeIgnoreService;
    }
    
    /**
     * Пометить все ресурсы из uow в resourceStore, как более не нужные и удалить, после сохранения в постоянное хранилище.
     * @param uow - TO объекты, которые уже сохранены
     * @param resourceStore - временное хранилище ресурсов
     */
    @SuppressWarnings("unchecked")
	protected void deleteResources(List<DomainTo> uow, ResourceStore resourceStore) {
        uow.forEach(dto -> {
            dto.forEachLobProperty((name, value) -> {
        	    String resourceId = (String)((Map<String, Object>) value).get("resourceId");
        	    resourceStore.deleteResource(resourceId);
            });
        }); 
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<DomainTo> toDomainToList(List<? extends DomainObject<?>> data, String... expand) {
        List<DomainTo> result = new ArrayList<>();
        data.forEach(domainObject -> {
            DomainToService domainToService = domainToServicesResolver.resolveToService(domainObject.getTypeName());
            DomainTo domainTo = domainToService.toToPolymorphic(domainObject, expand);
            result.add(domainTo);
        });
        return result;
    }

}
