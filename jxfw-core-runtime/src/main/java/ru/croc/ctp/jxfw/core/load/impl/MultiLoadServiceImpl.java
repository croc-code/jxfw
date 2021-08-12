package ru.croc.ctp.jxfw.core.load.impl;

import java8.util.stream.StreamSupport;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.LoadServiceSupport;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;


/**
 * Имплементация {@link LoadService} которая по заданному {@link QueryParams} определит нужный {@link LoadService}
 * которому можно делегировать вызов.
 */
@Service
@Primary
public class MultiLoadServiceImpl extends LoadServiceSupport {

    private final List<LoadService> loadServiceList;

    /**
     * Конструктор.
     * @param loadServiceList все сервисы загрузки
     */
    public MultiLoadServiceImpl(List<LoadService> loadServiceList) {
        this.loadServiceList = loadServiceList;
    }

    @Nonnull
    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> List<T> load(
            @Nonnull QueryParams<T, ID> queryParams, @Nonnull LoadContext<T> loadContext) {
       return StreamSupport.stream(loadServiceList)
               .filter( loadService -> loadService.accepted(queryParams.getDomainObjectTypeName()))
               .map( loadService -> loadService.load(queryParams, loadContext))
               .findFirst().orElseGet(Collections::emptyList);
    }

    @Override
    public boolean accepted(String typeName) {
        //т.к. данный сервис лишь делегирует подходящему сервису
        return false;
    }

}
