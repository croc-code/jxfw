package ru.croc.ctp.jxfw.jpa.store.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.croc.ctp.jxfw.core.store.StoreContext;
import ru.croc.ctp.jxfw.core.store.UnitOfWorkMultiStoreService;
import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkMultiStoreServiceImpl;

/**
 * Реализация сервиса сохранения  {@link UnitOfWorkMultiStoreService} при наличии в рантайм JPA модуля сохранения.
 * @author AKogun
 * @since 1.5
 */
@Service
@Primary
public class UnitOfWorkJpaMultiStoreServiceImpl extends UnitOfWorkMultiStoreServiceImpl {
    
    @Override
    @Transactional
    public void store(StoreContext storeContext) {
        super.store(storeContext);
    }

}
