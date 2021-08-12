package ru.croc.ctp.jxfw.solr.impl.services;

import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.stereotype.Service;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkSingleStoreServiceImpl;

import java.util.List;

/**
 * @author SMufazzalov сохранение доменных объектов.
 */
@Service
public class UnitOfWorkSolrStoreServiceImpl extends UnitOfWorkSingleStoreServiceImpl {

    @Override
    public boolean accepted(DomainObject<?> object, List<? extends DomainObject<?>> uow) {
        Class<?> clazz = object.getClass();
        return clazz.isAnnotationPresent(SolrDocument.class);
    }

}
