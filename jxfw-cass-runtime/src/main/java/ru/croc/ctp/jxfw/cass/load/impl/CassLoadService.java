package ru.croc.ctp.jxfw.cass.load.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.cass.load.events.CassBeforeLoadEvent;
import ru.croc.ctp.jxfw.cass.load.events.CassPreCheckSecurityEvent;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.PreCheckSecurityEvent;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * Реализация {@link ru.croc.ctp.jxfw.core.load.LoadService} для CASS модуля хранения.
 *
 * @author OKrutova
 * @since jxfw 1.6.0
 */
@Order(0)
@Service("cassLoadService")
public class CassLoadService extends LoadServiceImpl {

    /**
     * Конструктор.
     *
     * @param domainServicesResolver {@link DomainServicesResolver}
     * @param publisher {@link ApplicationEventPublisher}
     * @param selectComposer  имплементация PredicateProvider для Cass
     */
    public CassLoadService(DomainServicesResolver domainServicesResolver, ApplicationEventPublisher publisher,
                           PredicateProvider selectComposer) {
        super(domainServicesResolver, publisher, selectComposer);
    }


    @Nonnull
    @Override
    public boolean accepted(String typeName) {
        XfwClass xfwClass = XfwModelFactory.getInstance().find(typeName, XfwClass.class);
        return xfwClass != null && xfwClass.getPersistenceModule().contains("CASS");
    }

    @Nonnull
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> PreCheckSecurityEvent<T>
        createPreCheckSecurityEvent(@Nonnull QueryParams<T, ID> queryParams,
                                @Nonnull LoadContext<T> loadContext) {
        return new CassPreCheckSecurityEvent<>(queryParams, loadContext);
    }

    @Nonnull
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> BeforeLoadEvent<T>
        createBeforeLoadEvent(@Nonnull QueryParams<T, ID> queryParams,
                          @Nonnull LoadContext<T> loadContext) {
        return new CassBeforeLoadEvent<>(queryParams, loadContext);
    }

}
