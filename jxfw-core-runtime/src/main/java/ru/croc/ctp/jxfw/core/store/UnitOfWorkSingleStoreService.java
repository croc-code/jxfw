package ru.croc.ctp.jxfw.core.store;

import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkSingleStoreServiceImpl;

/**
 * Сервис сохранения UoW в хранилищах одного типа или в единственном хранилище.
 * UoW может содержать объекты, которые не совместимы с данным типом хранилища.
 * В таком случае сервис должен игнорировать несовместимые с хранилищем
 * объекты. XA-транзакция не создается.
 * 
 * @since 1.1
 * @see UnitOfWorkSingleStoreServiceImpl
 */
public interface UnitOfWorkSingleStoreService extends UnitOfWorkService {

}
