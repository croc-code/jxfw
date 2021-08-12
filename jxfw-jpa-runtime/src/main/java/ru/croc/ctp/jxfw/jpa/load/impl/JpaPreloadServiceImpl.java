package ru.croc.ctp.jxfw.jpa.load.impl;

import java8.lang.Iterables;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil;
import ru.croc.ctp.jxfw.core.load.PreloadService;
import ru.croc.ctp.jxfw.jpa.hibernate.impl.util.ProxyHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация {@link PreloadService} для JPA модуля хранения.
 * 
 * @since 1.3
 * 
 * @author AKogun
 */
@Service
@Transactional(readOnly = true)
public class JpaPreloadServiceImpl implements PreloadService {
    
    @Override
    public List<? extends DomainObject<?>> loadMoreFor(Iterable<? extends DomainObject<?>> objects, 
            List<String> preLoadProps) {

        final List<DomainObject<?>> prepareList = new ArrayList<>();

        Iterables.forEach(objects, o -> preLoadProps.forEach(param -> {
            prepareList.addAll(DomainObjectUtil.loadData(param, o));
        }));

        final List<DomainObject<?>> moreList = new ArrayList<>();
        Iterables.forEach(prepareList, r -> {
            moreList.add(ProxyHelper.initializeAndUnproxy(r));
        });

        return moreList;
    }

}