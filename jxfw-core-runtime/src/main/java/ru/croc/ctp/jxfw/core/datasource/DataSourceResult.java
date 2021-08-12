package ru.croc.ctp.jxfw.core.datasource;

import java.util.List;
import java.util.Map;

/**
 * Контейнер для возвращаемых данных из DataSource
 *
 * @author Nosov Alexander
 * @since 1.1
 * @param <T> тип возвращаемых данных
 * @deprecated since 1.6
 */
@Deprecated
public interface DataSourceResult<T> {

    /**
     * Получить данные.
     *
     * @return данные хранимые в контейнере
     */
    List<T> getData();

    /**
     * Получить extend данные.
     *
     * @return extend данные хранимые в контейнере
     */
    List<?> getMore();

    /**
     * Получить хинты для отображения.
     *
     * @return хинты для отображения в типе {@code Map} 
     */
    Map<String, Object> getHints();
}
