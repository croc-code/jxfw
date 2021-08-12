package ru.croc.ctp.jxfw.core.store;

/**
 * Сервис сохранения UoW в хранилищах разных типов. Полный набор объектов UoW
 * передается для сохранения сервису каждого хранилища. Сервис хранилища сам
 * реашает, какие из объектов набора ему сохранять, а какие игнорировать.
 */
public interface UnitOfWorkMultiStoreService extends UnitOfWorkService {

}
