package ru.croc.ctp.jxfw.jpa.store.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Component;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

/**
 * Актуализация версии для сущности
 *
 * @author Nosov Alexander
 *         on 12.10.15.
 */
@Component
public class VersionActuator {

    @Autowired
    private JpaContext jpaContext;

    /**
     * Актуализировать версию доменного объекта в БД.
     * 
     * @param domain - доменный объект.
     */
    public void actuate(DomainObject<?> domain) {
        if (!(domain instanceof Editable)) {
            return;
        }
        Editable entity = (Editable) domain;
        if (domain.isNew()) {
            entity.setVersion(1L);
        } else if (entity.getVersion() == -1) {            
            
            final EntityManager entityManager = jpaContext.getEntityManagerByManagedType(entity.getClass());
            
            //TODO: не будет работать для сущностей с кастомным маппингом на таблицу
            final String classSimpleName = entity.getClass().getSimpleName();
            
            final Query query = entityManager.createQuery(
                    String.format("SELECT %s FROM %s o WHERE o.id = '%s'",
                            entity.getNameOfVersionField(),
                            classSimpleName,
                            domain.getId())).setFlushMode(FlushModeType.COMMIT);
            
            if (query.getResultList().isEmpty()) {
                return;
            }
            
            final Long latestVersion = (Long) query.getResultList().get(0);
            entity.setVersion(latestVersion);
        }
    }
}