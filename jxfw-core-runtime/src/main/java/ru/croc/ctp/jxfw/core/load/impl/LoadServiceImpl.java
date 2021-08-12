package ru.croc.ctp.jxfw.core.load.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.querydsl.core.types.Predicate;
import java8.lang.Iterables;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.datasource.DomainDataLoader;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.LoadServiceSupport;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.events.AfterLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.CheckSecurityEvent;
import ru.croc.ctp.jxfw.core.load.events.PreCheckSecurityEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Базовая реализация {@link ru.croc.ctp.jxfw.core.load.LoadService}.
 *
 * @author smufazzalov
 * @since jxfw 1.6.0
 */
@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class LoadServiceImpl extends LoadServiceSupport {

    private static final Logger logger = LoggerFactory.getLogger(LoadServiceImpl.class);

    /**
     * Резолвер доменных сервисов.
     */
    protected final DomainServicesResolver domainServicesResolver;
    private final ApplicationEventPublisher publisher;
    private final PredicateProvider predicateProvider;

    /**
     * Конструктор.
     *
     * @param domainServicesResolver {@link DomainServicesResolver}
     * @param publisher              {@link ApplicationEventPublisher}
     * @param predicateProviderImpl  дефолтная имплементация PredicateProvider
     */
    public LoadServiceImpl(DomainServicesResolver domainServicesResolver, ApplicationEventPublisher publisher,
                           PredicateProvider predicateProviderImpl) {
        this.domainServicesResolver = domainServicesResolver;
        this.publisher = publisher;
        this.predicateProvider = predicateProviderImpl;
    }

    protected PredicateProvider getPredicateProvider() {
        return predicateProvider;
    }

    /**
     * Создание события PreCheckSecurityEvent.
     *
     * @param queryParams {@link QueryParams}
     * @param loadContext Контекст read конвейера
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * @return экземпляр PreCheckSecurityEvent
     */
    @Nonnull
    protected <T extends DomainObject<ID>, ID extends Serializable> PreCheckSecurityEvent<T>
        createPreCheckSecurityEvent(@Nonnull QueryParams<T, ID> queryParams,
                                @Nonnull LoadContext<T> loadContext) {
        return new PreCheckSecurityEvent<>(queryParams, loadContext);
    }

    /**
     * Создание события BeforeLoadEvent.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param loadContext Контекст read конвейера
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * @return экземпляр BeforeLoadEvent
     */
    @Nonnull
    protected <T extends DomainObject<ID>, ID extends Serializable> BeforeLoadEvent<T>
        createBeforeLoadEvent(@Nonnull QueryParams<T, ID> queryParams,
                          @Nonnull LoadContext<T> loadContext) {
        return new BeforeLoadEvent<>(queryParams, loadContext);
    }


    @Nonnull
    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> List<T> load(
            @Nonnull QueryParams<T, ID> queryParams,
            @Nonnull LoadContext<T> loadContext) {

        publisher.publishEvent(createPreCheckSecurityEvent(queryParams, loadContext));
        publisher.publishEvent(createBeforeLoadEvent(queryParams, loadContext));

        LoadResult<T> loadResult = loadData(queryParams, loadContext);

        List<DomainObject<?>> preloads = fetchPreloads(queryParams, loadResult.getData(),
                loadContext.isForExport());

        loadResult.getMoreList().addAll(preloads);

        loadContext.setLoadResult(loadResult);

        publisher.publishEvent(new CheckSecurityEvent<>(loadResult, queryParams, loadContext));
        publisher.publishEvent(new AfterLoadEvent<>(loadResult, queryParams, loadContext));

        return loadResult.getData();
    }

    /**
     * Загрузка навигируемых св-в для списка доменных объектов.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param domainObjects список доменных объектов
     * @param <T>           доменный тип
     * @param <ID>           тип ключа доменного типа
     * @return список доменных объектов (из загруженных навигируемых св-в)
     */
    @Nonnull
    protected  <T extends DomainObject<ID>, ID extends Serializable> List<DomainObject<?>> fetchPreloads(
            @Nonnull QueryParams<T, ID> queryParams,
            @Nonnull Iterable<T> domainObjects) {

        List<DomainObject<?>> fetched = newArrayList();

        domainObjects.forEach(domainObject ->
            queryParams.getPreloads().forEach(preload -> fetched.addAll(
                DomainObjectUtil.loadData(preload, domainObject))
            )
        );

        return DomainObjectUtil.distinct(fetched);
    }

    /**
     * Загрузка навигируемых св-в для списка доменных объектов.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param domainObjects список доменных объектов
     * @param isForExport  признак того, что загрузка данных для экспорта
     * @param <T>           доменный тип
     * @return список доменных объектов (из загруженных навигируемых св-в)
     */
    @Nonnull
    private <T extends DomainObject<ID>, ID extends Serializable> List<DomainObject<?>> fetchPreloads(
            @Nonnull QueryParams<T, ID> queryParams,
            @Nonnull Iterable<T> domainObjects,
            boolean isForExport) {


        /*
         * Если данные загружаются для последующего экспорта в файл, то прелоады
         * требуется прогрузить внутрь объектов, к которым эти прелоады относятся.
         */
        if (isForExport) {
            checkNotNull(queryParams.getPreloads());
            checkNotNull(domainObjects);
            Iterables.forEach(domainObjects, domainObject -> {
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(domainObject);
                Iterables.forEach(queryParams.getPreloads(), preload -> {
                    try {
                        if (!StringUtils.isBlank(preload)) {
                            beanWrapper.setPropertyValue(preload, beanWrapper.getPropertyValue(preload));
                        }
                    } catch (Exception ex) {
                        logger.debug("Preloads error {}", preload, ex);
                    }

                });
            });

            return new ArrayList<>();
        } else {
            return fetchPreloads(queryParams, domainObjects);
        }

    }

    /**
     * Реализация загрузки данных.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * @param loadContext контекст загрузки
     * @return список загруженных объектов
     */
    @Nonnull
    protected <T extends DomainObject<ID>, ID extends Serializable> LoadResult<T> loadData(
            @Nonnull QueryParams<T, ID> queryParams,
            @Nonnull LoadContext<T> loadContext) {

        if (queryParams instanceof DomainDataLoader) {
            return ((DomainDataLoader<T, ID>) queryParams).load(loadContext, queryParams);
        } else {
            DomainService<T, ID, Predicate> service
                    = resolveDomainService(queryParams.getDomainObjectTypeName());
            LoadResult<T> result = new LoadResult<>();
            if (queryParams.getId() != null) {
                result.getData().add(loadObjectById(loadContext, queryParams));
                return result;
            } else {
                Iterable<T> domainObjects;                    
                if (queryParams.getPageable() != null) {
                    domainObjects = service.getObjects(queryParams.getPredicate(), queryParams.getPageable());
                } else if (queryParams.getSort() != null && queryParams.getSort().isSorted()) {
                    domainObjects = service.getObjects(queryParams.getPredicate(), queryParams.getSort());
                } else {
                    domainObjects = service.getObjects(queryParams.getPredicate());
                }

                if (domainObjects instanceof Page) {
                    populateHints((Page) domainObjects, result.getHints());
                }
                result.getData().addAll(newArrayList(domainObjects));
                return result;
            }
        }
    }

    /**
     * Метод реализует загрузку объекта по ид. В базовой реализации
     * не учитывается предикат из queryParams, т.к. построение предиката по ид
     * реализовано только для JPA.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param loadContext Контекст read конвейера
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * @return единственный загруженный объект
     */
    protected <T extends DomainObject<ID>, ID extends Serializable> T loadObjectById(
            @Nonnull LoadContext<T> loadContext,
            @Nonnull QueryParams<T, ID> queryParams) {

        DomainService<T, ID, Predicate> service
                = resolveDomainService(queryParams.getDomainObjectTypeName());
        return service.getObjectById(queryParams.getId());

    }

    @Override
    public boolean accepted(String typeName) {
        return true;
    }

    /**
     * Хинты.
     *
     * @param page  доменные объекты
     * @param hints хинты
     */
    private void populateHints(Page page, Map<String, Object> hints) {
        hints.put("hasNext", page.hasNext());
        hints.put("paging", true);
        hints.put("total", page.getTotalElements());
    }

    /**
     * Найти доменный сервис.
     * @param <T> тип доменного объекта
     * @param <ID> идентификатор доменного объекта
     * @param typeName имя доменного типа
     * @return экземпляр сервиса
     */
    //TODO: надо разобраться с доменными сервисами и сервисами полнотекстового поиска, убрать эту спецфику в
    @Nonnull
    protected <T extends DomainObject<ID>, ID extends Serializable> DomainService<T, ID, Predicate>
        resolveDomainService(String typeName) {
        return domainServicesResolver.resolveDomainService(typeName, "Service");
    }
}