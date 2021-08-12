package ru.croc.ctp.jxfw.cass.impl.services;

import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.stereotype.Service;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkSingleStoreServiceImpl;

import java.util.List;

/**
 * Cохранение доменных объектов.
 * @author SMufazzalov
 * @since 1.4
 */
@Service
public class UnitOfWorkCassStoreServiceImpl extends UnitOfWorkSingleStoreServiceImpl {

    @Override
    public boolean accepted(DomainObject<?> object, List<? extends DomainObject<?>> uow) {
        Class<?> clazz = object.getClass();
        return clazz.isAnnotationPresent(Table.class);
    }

}
