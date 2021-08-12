package ru.croc.ctp.jxfw.jpa.load.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.load.context.LoadContextWithStoreContext;
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent;
import ru.croc.ctp.jxfw.core.load.events.PreCheckSecurityEvent;
import ru.croc.ctp.jxfw.core.load.impl.LoadServiceImpl;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.jpa.load.events.JpaBeforeLoadEvent;
import ru.croc.ctp.jxfw.jpa.load.events.JpaPreCheckSecurityEvent;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

/**
 * Реализация {@link ru.croc.ctp.jxfw.core.load.LoadService} для JPA модуля хранения.
 *
 * @author smufazzalov
 * @since jxfw 1.6
 */
@Order(0)
@Service("jpaLoadService")
public class JpaLoadServiceImpl extends LoadServiceImpl {

    private JpaContext jpaContext;
    
    /**
     * Конструктор.
     *
     * @param domainServicesResolver {@link DomainServicesResolver}
     * @param publisher              {@link ApplicationEventPublisher}
     * @param predicateComposer      имплементация PredicateProvider для JPA
     * @param jpaContext             текущий контекст jpa
     */
    @Autowired
    public JpaLoadServiceImpl(DomainServicesResolver domainServicesResolver, ApplicationEventPublisher publisher,
                              PredicateProvider predicateComposer, JpaContext jpaContext) {
        super(domainServicesResolver, publisher, predicateComposer);
        this.jpaContext = jpaContext;
    }

    @Nonnull
    @Override
    @Transactional(readOnly = true)
    public <T extends DomainObject<ID>, ID extends Serializable> List<T> load(
        @Nonnull QueryParams<T, ID> queryParams,
        @Nonnull LoadContext<T> loadContext) {
        return super.load(queryParams, loadContext);
    }

    @Value("${ru.croc.ctp.jpa.querypreloads:false}")
    private boolean queryPreloads;

    @Nonnull
    @Override
    protected  <T extends DomainObject<ID>, ID extends Serializable> List<DomainObject<?>> fetchPreloads(
        @Nonnull QueryParams<T, ID> queryParams,
        @Nonnull Iterable<T> domainObjects) {

        if (!queryPreloads) {
            return super.fetchPreloads(queryParams, domainObjects);
        }

        List<List<String>> preloads = new ArrayList<>();
        queryParams.getPreloads().forEach(s ->
            preloads.add(new ArrayList<>(Arrays.asList(s.split("\\."))))
        );

        DomainService service = domainServicesResolver.resolveService(queryParams.getDomainObjectTypeName());

        if (!preloads.isEmpty() && service != null) {
            return service.getPreloads(domainObjects, preloads);
        }
        return new ArrayList<>();
    }


    @Override
    public boolean accepted(String typeName) {
        try {
            XfwClass xfwClass = XfwModelFactory.getInstance()
                    .findBySimpleNameThrowing(typeName, XfwClass.class);
            if (xfwClass.getPersistenceModule().contains("JPA")) {
                return true;
            }
        } catch (Exception e) {
            //давим
        }
        return false;
    }

    @Nonnull
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> PreCheckSecurityEvent<T>
        createPreCheckSecurityEvent(@Nonnull QueryParams<T, ID> queryParams,
                                @Nonnull LoadContext<T> loadContext) {
        return new JpaPreCheckSecurityEvent<>(queryParams, loadContext);
    }

    @Nonnull
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> BeforeLoadEvent<T>
        createBeforeLoadEvent(@Nonnull QueryParams<T, ID> queryParams,
                          @Nonnull LoadContext<T> loadContext) {
        return new JpaBeforeLoadEvent<>(queryParams, loadContext);
    }


    /**
     * Метод реализует загрузку объекта по идс учетом предиката из queryParams.
     *
     * @param queryParams параметры запроса {@link QueryParams}
     * @param <T>         доменный тип
     * @param <ID>        тип ключа доменного типа
     * @return единственный загруженный объект
     */
    @Override
    protected <T extends DomainObject<ID>, ID extends Serializable> T loadObjectById(
            @Nonnull LoadContext<T> loadContext,
            @Nonnull QueryParams<T, ID> queryParams) {
        
        String domainObjectTypeName = queryParams.getDomainObjectTypeName();
        DomainService<T, ID, Predicate> service = resolveDomainService(domainObjectTypeName);
        
        // для случая, когда загрузка происходит из конвейера сохранения, см JXFW-
        if (LoadContextWithStoreContext.from(loadContext).exists()) {
            EntityManager em = jpaContext.getEntityManagerByManagedType(
                DomainObjectUtil.getDomainObjectType(domainObjectTypeName)
            );
            if (!FlushModeType.COMMIT.equals(em.getFlushMode())) {
                em.setFlushMode(FlushModeType.COMMIT);
            }
        }
                
        BooleanBuilder byIdPredicate
                = (BooleanBuilder) getPredicateProvider().buildPredicateById(domainObjectTypeName,
                queryParams.getId());
        return service.getObjectById(byIdPredicate.and(queryParams.getPredicate()), queryParams.getId());
    }
}
