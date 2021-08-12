package ru.croc.ctp.jxfw.solr.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.core.query.Query;

import java.io.Serializable;

/**
 * Интерфейс является точкой раширения, для случаев,
 * когда недостачно стандартной функциональности тех CRUD операций,
 * которые предоставляет интерфейс SolrCrudRepository.
 *
 * @param <T>  Класс доменной модели
 * @param <ID> Класс идентификатора
 */
@NoRepositoryBean
public interface SolrQueryExecutorRepository<T, ID extends Serializable> {


    /**
     * Возвращают набор сущностей, которые удовлетворяют запросу, плюс некоторую дополнительную информацию,
     * такую как общее количество найденных элементов, количество страниц и т.д.
     *
     * @param query SolrDataQuery запрос
     * @return Page
     */
    Page<T> findAll(Query query);


    /**
     * Возвращают набор сущностей, которые удовлетворяют запросу, плюс некоторую дополнительную информацию,
     * такую как общее количество найденных элементов, количество страниц и т.д.
     *
     * @param query    SolrDataQuery запрос
     * @param pageable C поддержкой пагинациеи
     * @return Page
     */
    Page<T> findAll(Query query, Pageable pageable);

    /**
     * уничтожает данные согласно запросу.
     *
     * @param query SolrDataQuery запрос
     */
    void delete(Query query);

    /**
     * Возвращает общее количество элементов, которые удовлетворяют запросу, без "пересылки",
     * внутри http запроса самих элементов.
     *
     * @param query SolrDataQuery запрос
     * @return количество элементов
     */
    long count(Query query);
}
