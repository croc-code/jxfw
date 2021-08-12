package ru.croc.ctp.jxfw.cass.repo;

import com.datastax.driver.core.querybuilder.Select;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Раширение возможностей репозитория Cass.
 * @param <T> тип доменного объекта
 * @param <ID> ключ
 * @author SMufazzalov
 * @since 1.4
 */
@NoRepositoryBean
public interface CassandraQueryRepository<T, ID extends Serializable> {
    /**
     * Получить все доменные объекты по запросу.
     *
     * @param query statement select
     * @return список
     */
    List<T> findAll(Select query);
}
