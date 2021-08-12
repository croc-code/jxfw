package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter;
import ru.croc.ctp.jxfw.core.facade.webclient.PredicateProvider;

import java.io.Serializable;
import javax.annotation.Nonnull;


/**
 * Поставщик предикатов дефолтный.
 */
@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class PredicateProviderImpl implements PredicateProvider {
    @Override
    public Predicate buildPredicate(
            @Nonnull String domainTypeName,
            ObjectFilter objectFilter,
            Sort sort,
            Pageable pageable
    ) {
        return new BooleanBuilder();
    }

    @Override
    public Predicate buildPredicateById(@Nonnull String domainTypeName, @Nonnull Serializable id) {
        return new BooleanBuilder();
    }

    @Override
    public boolean accepts(String domainTypeName) {
        return true;
    }
}
