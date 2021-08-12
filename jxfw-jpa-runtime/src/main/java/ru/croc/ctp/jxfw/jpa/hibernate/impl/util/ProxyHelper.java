package ru.croc.ctp.jxfw.jpa.hibernate.impl.util;

import static org.hibernate.proxy.HibernateProxyHelper.getClassWithoutInitializingProxy;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилитный класс для работы с proxy объектами Hibernate.
 *
 * @author Nosov Alexander
 * @since 1.2
 */
public final class ProxyHelper {
    
    private static final Logger log = LoggerFactory.getLogger(ProxyHelper.class);

    /**
     * @param entity - сущность
     * @return возвращает реальный класс который скрывает proxy.
     */
    public static Class<?> getRealClass(Object entity) {
        if (entity == null) {
            throw new IllegalStateException("entity can't be null");
        }
        if (HibernateProxy.class.isAssignableFrom(entity.getClass())) {
            final Class<?> realClass = getClassWithoutInitializingProxy(entity);
            log.trace("Unproxy instance {} to real class {}", entity.getClass(), realClass);
            return realClass;
        }
        return entity.getClass();
    }

    /**
     * Избавления от прокси, возвращает реализацию объекта.
     *
     * @param entity объект
     * @param <T>    класс
     * @return реализация.
     * @since jXFW 1.4.0
     */
    @SuppressWarnings("unchecked")
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new IllegalStateException("entity can't be null");
        }
        if (entity instanceof HibernateProxy) {
            Hibernate.initialize(entity);
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }
}
