package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.facade.webclient.ConvertContext;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainDeserializer;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToServicesResolverWebClient;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Сервис, устанавливающий значения полей в доменный объект.
 *
 * @author OKrutova
 * @since 1.6
 */
@Service
public class DomainDeserializerImpl implements DomainDeserializer {

    private final DomainToServicesResolverWebClient resolver;

    /**
     * Конструктор.
     *
     * @param resolver резолвер сервисов
     */
    @Autowired
    public DomainDeserializerImpl(DomainToServicesResolverWebClient resolver) {
        this.resolver = resolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperties(DomainObject<?> domainObject, Map<String, Object> properties) {

        Map<String, Object> notNavigables = newHashMap();
        XfwClass xfwClass = domainObject.getMetadata();

        /*
        установку навигируемых свойств сделаем сами, потому что в то-сервисах есть вероятность,
        что произойдет обращение к бд, а менять код то-сервисов опасно.
         */
        Set<XfwStructuralFeature> navigables = xfwClass.getScalarFieldsOfType(DomainObject.class);
        Set<XfwStructuralFeature> massNavigables = xfwClass.getMassiveFieldsOfType(DomainObject.class);
        properties.keySet().forEach(chainingPropName -> {
            if (!navigables.contains(xfwClass.getChainingEStructuralFeature(chainingPropName))
                    && !massNavigables.contains(xfwClass.getChainingEStructuralFeature(chainingPropName))) {
                notNavigables.put(chainingPropName, properties.get(chainingPropName));
            }
        });

        DomainTo dto = new DomainTo();
        dto.setType(domainObject.getTypeName());
        notNavigables.forEach(dto::addProperty);

        DomainToService toService = resolver.resolveToService(domainObject.getTypeName());
        ConvertContext context = new ConvertContext(newArrayList(), newArrayList());
        toService.fromTo(domainObject, dto, context);

        /*
        теперь установим навигируемые свойства
         */

        for (XfwStructuralFeature feature : navigables) {
            Object value = properties.get(feature.getName());
            if (value instanceof String) {
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(domainObject);
                beanWrapper.setPropertyValue(feature.getName(),
                        navigableInstance((XfwReference) feature, (String) value));
            }
        }

        for (XfwStructuralFeature feature : massNavigables) {
            Object value = properties.get(feature.getName());
            if (value instanceof Iterable) {
                BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(domainObject);
                Collection collection = ((Collection) beanWrapper.getPropertyValue(feature.getName()));
                collection.clear();
                ((Iterable) value).forEach(o -> collection.add(navigableInstance((XfwReference) feature, (String) o)));
            }
        }


    }


    @SuppressWarnings("unchecked")
    private DomainObject navigableInstance(XfwReference feature, String value) {
        // могли бы сделать так: return navService.createNewDomainObject(value);
        // но тогда плохо с абстрактными типами ДО
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(feature.getEReferenceType().getInstanceClass());
        enhancer.setCallback(
                (MethodInterceptor) (obj, method, args, methodProxy) -> methodProxy.invokeSuper(obj, args));
        DomainObject nav = (DomainObject) enhancer.create();
        DomainToService navService = resolver.resolveToService(feature.getEReferenceType().getName());
        nav.setId(navService.parseKey(value));
        return nav;
    }
}
