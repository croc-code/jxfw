package ru.croc.ctp.jxfw.core.datasource;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;

/**
 * Фибрика для создания различных типов контейнеров,
 * содержащих в различном представлении результаты выполнения запроса в DataSource
 *
 * @author Nosov Alexander
 * @since 1.0
 * @deprecated since 1.6
 */
@Deprecated
public final class DataSourceFactory {

    private DataSourceFactory() {
    }

    /**
     * Фабричный метод для создания кокретного типа контейнера.
     *
     * @param inputData - входные данные
     * @param type      - тип в котором храняться данные в контейнере
     * @return экземпляр контейнера
     */
    @SuppressWarnings({"unchecked", "raw"})
    public static DataSourceResult create(final List inputData, final Type type) {
        return create(inputData, newArrayList(), type);
    }

    /**
     * Фабричный метод для создания кокретного типа контейнера.
     *
     * @param inputData - входные данные
     * @param moreData  - дополнительные входные данные
     * @param type      - тип в котором храняться данные в контейнере
     * @return экземпляр контейнера
     */
    @SuppressWarnings({"unchecked", "raw"})
    public static DataSourceResult create(final List inputData, final List moreData, final Type type) {
        return create(inputData, moreData, newHashMap(), type);
    }

    /**
     * Фабричный метод для создания кокретного типа контейнера.
     *
     * @param inputData - входные данные
     * @param moreData  - дополнительные входные данные
     * @param hints     - дополнительные hint'ы для отображения в WC
     * @param type      - тип в котором храняться данные в контейнере
     * @return экземпляр контейнера
     */
    @SuppressWarnings({"unchecked", "raw"})
    public static DataSourceResult create(final List inputData, final List moreData,
                                          Map<String, Object> hints, final Type type) {
        switch (type) {
            case TUPLE:
                assert (inputData.size() > 0);
                return new QTupleDataSourceResult(inputData, moreData, hints);
            case DOMAIN_OBJECT:
                return new DomainObjectDataSourceResult(inputData, moreData, hints);
            case GENERAL:
                return new GeneralDataSourceResult(inputData, moreData, hints);
            case POJO_OBJECT:
                return new PojoDataSourceResult<>(inputData, moreData, hints);
            default:
                throw new RuntimeException("Unknown type of data source result.");
        }
    }

    /**
     * Тип в котором будут возвращены данные.
     */
    public enum Type {
        /**
         * Наборы нетипизованных полей-значений.
         */
        TUPLE,
        /**
         * Доменные объекты.
         */
        DOMAIN_OBJECT,
        /**
         * Коллекция простых типов.
         */
        GENERAL,
        /**
         * Простые POJO объекты. Следует использовать DOMAIN_OBJECT.
         * @deprecated 1.5.
         */
        @Deprecated
        POJO_OBJECT
    }
}
