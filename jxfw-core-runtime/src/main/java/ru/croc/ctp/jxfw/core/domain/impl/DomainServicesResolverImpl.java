package ru.croc.ctp.jxfw.core.domain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainService;
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToService;

import java.beans.Introspector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Реализация интерфейса {@link DomainServicesResolver} для определения сервисов доменных объектов.
 * 
 * @since 1.1
 */
@Service
public class DomainServicesResolverImpl extends ApplicationObjectSupport
        implements DomainServicesResolver {

    private static final Logger log = LoggerFactory.getLogger(DomainServicesResolverImpl.class);

    @SuppressWarnings({"rawtypes"})
    @Override
    @Nonnull
    public DomainService resolveService(DomainObject<?> entity) {
        final String typeName = entity.getTypeName();
        DomainService entityService = resolveDomainService(typeName, "Service");
        if (entityService == null) {
            throw new RuntimeException("Not found domain service for " + typeName);
        }
        return entityService;
    }

    @SuppressWarnings({"rawtypes"})
    @Override
    public DomainService resolveService(String typeName) {
        DomainService result = null;
        try {
            result = resolveDomainService(typeName, "Service");
        } catch (NoSuchBeanDefinitionException e) {
            // Если бин не найден, просто возвращаем null
            log.debug(e.getMessage());
        }
        return result;
    }

    @SuppressWarnings({"rawtypes"})
    @Nonnull
    @Override
    public DomainToService resolveToService(String typeName) {
        DomainToService domainToService = resolveDomainService(typeName, "ToService");
        if (domainToService == null) {
            throw new RuntimeException("Not found TO service for " + typeName);
        }
        return domainToService;
    }

    @Override
    public <T> T resolveDomainService(DomainObject<?> domainObject, String... suffixes) {
        assert suffixes.length > 0;
        assert domainObject != null;
        return resolveDomainService(domainObject.getTypeName(), suffixes);
    }

    @SuppressWarnings({"rawtypes"})
    @Nonnull
    @Override
    public DomainToService resolveFulltextToService(String typeName) {
        DomainToService domainToService = resolveDomainService(
                "solr" + typeName.substring(0,1).toUpperCase() + typeName.substring(1),
                "ToService");
        if (domainToService == null) {
            throw new RuntimeException("Not found TO service for " + typeName);
        }
        return domainToService;
    }

    @SuppressWarnings({"rawtypes"})
    @Nullable
    @Override
    public DomainService resolveFulltextService(String typeName) {
        //для кейса, когда имя типа получали типизировано SOME_FULLTEXT_OBJECT#TYPE_NAME
        String prefix = "solr.";
        if (typeName.startsWith(prefix)) {
            typeName = typeName.replace(prefix, "");
        }
        DomainService result = null;
        try {
            result = resolveDomainService(
                    "solr" + typeName.substring(0,1).toUpperCase() + typeName.substring(1),
                    "Service");
        } catch (NoSuchBeanDefinitionException e) {
            // Если бин не найден, просто возвращаем null
            log.debug(e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolveDomainService(String typeName, String... suffixes) {
        assert typeName != null; 
        T result = (T) getApplicationContext().getBean(createBeanName(typeName, suffixes));
        Class<?> clazz = null;
        if (result instanceof TargetClassAware) {
            //в противном случае получим прокси для Transactional сервисов
            clazz = ((TargetClassAware) result).getTargetClass();
        } else {
            clazz = result.getClass();
        }
        //для того, чтобы получить бины сервисов c признаком primary = true
        result = (T) getApplicationContext().getBean(clazz);
        return result;
    }

    /**
     * Формирует ися бина по типу и суффиксам.
     *
     * @param typeName имя типа
     * @param suffixes суффиксы
     * @return имя бина
     */
    protected String createBeanName(String typeName, String... suffixes) {
        String shortClassName = ClassUtils.getShortName(typeName);
        if (suffixes != null) {
            for (String suffix : suffixes) {
                shortClassName = shortClassName.concat(suffix);
            }
        }
        return Introspector.decapitalize(shortClassName);
    }

}
