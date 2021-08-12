package ru.croc.ctp.jxfw.solr.services;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

/**
 * Интерфейс сервиса для работы с Solr модулем.
 *
 * @author SMufazzalov
 * @since 1.5
 */
public interface DomainSolrService<T extends DomainObject<?>> {

    /**
     * Создать новый объект. Добавлено для полнотекстового поиска. Логика некого адаптера,
     * получая сущность базового хранилища конвертировать в ту которую сможет обработать Solr хранилище.
     * @deprecated subj to change
     * @param domainObject - Доменный объект, копию которого будем делать
     * @return новый объект
     */
    @Deprecated()
    T createNew(DomainObject domainObject);
}
