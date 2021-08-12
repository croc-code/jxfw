package ru.croc.ctp.jxfw.core.load.impl;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilder;
import ru.croc.ctp.jxfw.core.load.QueryParamsBuilderFactory;
//import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
//import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
//import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Имплементация фабрики {@link QueryParamsBuilderFactory} для постройки {@link QueryParamsBuilder}.
 *
 * @author smufazzalov
 * @since jxfw 1.6.0
 */
@Service
public class QueryParamsBuilderFactoryImpl implements QueryParamsBuilderFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(Class<T> type) {
        return newBuilder(DomainObjectUtil.getDomainObjectTypeName(type));
    }

    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(Class<T> type,
            Predicate predicate) {
        return newBuilder(DomainObjectUtil.getDomainObjectTypeName(type), predicate);
    }

    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(Class<T> type,
            ID id) {
        return newBuilder(DomainObjectUtil.getDomainObjectTypeName(type), id);
    }

    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(String typeName) {
        return new QueryParamsBuilder<>(typeName, getPredicateProvider(typeName));
    }

    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(
            String typeName,
            Predicate predicate
    ) {
        return new QueryParamsBuilder<>(typeName, getPredicateProvider(typeName), predicate);
    }

    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> QueryParamsBuilder<T, ID> newBuilder(
            String typeName,
            ID id
    ) {
        return new QueryParamsBuilder<>(typeName, getPredicateProvider(typeName), id);
    }

    private PredicateProvider getPredicateProvider(String typeName) {
        List<PredicateProvider> predicateProviders = new ArrayList<>(
                applicationContext.getBeansOfType(PredicateProvider.class).values()
        );
        predicateProviders.sort(AnnotationAwareOrderComparator.INSTANCE);

/*        XfwModel xfwModel = XfwModelFactory.getInstance();
        Class<? extends DomainObject<?>> domainType = xfwModel.findThrowing(typeName, XfwClass.class).getInstanceClass();
*/
        for (PredicateProvider predicateProvider : predicateProviders) {
            if (predicateProvider.accepts(typeName)) {
                return predicateProvider;
            }
        }

        throw new RuntimeException("Не найдены подходящие " + PredicateProvider.class.getSimpleName()
                + " чтобы построить " + QueryParamsBuilder.class.getSimpleName());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
}
