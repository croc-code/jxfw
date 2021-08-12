package ru.croc.ctp.jxfw.core.export.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java8.lang.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * сервис загрузки для экспорта, экспериментальный.
 * @deprecated since 1.6.4
 *
 */
@Deprecated
public class ExportLoadService extends LoadServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ExportLoadService.class);

    /**
     * Конструктор.
     *
     * @param domainServicesResolver {@link DomainServicesResolver}
     * @param publisher              {@link ApplicationEventPublisher}
     * @param predicateComposer  имплементация PredicateProvider для JPA
     */
    @Autowired
    public ExportLoadService(DomainServicesResolver domainServicesResolver, ApplicationEventPublisher publisher,
                             PredicateProvider predicateComposer) {
        super(domainServicesResolver, publisher, predicateComposer);
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public <T extends DomainObject<ID>, ID extends Serializable> List<T> load(@Nonnull QueryParams<T, ID> queryParams,
            @Nonnull LoadContext<T> loadContext) {
        return super.load(queryParams, loadContext);
    }

    /**
     * Подгрузка навигируемых свойств внуть объектов uow.
     *
     * @param domainObjects     коллекция доменных объектов
     * @param queryParams список прелоадов
     */
    @Nonnull
    @Override
    protected  <T extends DomainObject<ID>, ID extends Serializable> List<DomainObject<?>> fetchPreloads(
        @Nonnull QueryParams<T, ID> queryParams,
        @Nonnull Iterable<T> domainObjects
    ) {
        checkNotNull(queryParams.getPreloads());
        checkNotNull(domainObjects);
        Iterables.forEach(domainObjects, domainObject -> {
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(domainObject);
            Iterables.forEach(queryParams.getPreloads(), preload -> {
                try {
                    beanWrapper.setPropertyValue(preload, beanWrapper.getPropertyValue(preload));
                } catch (Exception ex) {
                    logger.error("Preloads error {}", preload, ex);
                }

            });
        });

        return new ArrayList<>();
    }
}
