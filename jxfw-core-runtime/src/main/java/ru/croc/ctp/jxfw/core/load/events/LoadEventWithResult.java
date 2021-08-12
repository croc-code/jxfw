package ru.croc.ctp.jxfw.core.load.events;

import java8.util.Optional;
import org.apache.commons.lang3.builder.ToStringBuilder;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.util.List;
import java.util.Map;

/**
 * Базовый класс событий конвейера загрузки, публикуемых после получения результата.
 * @param <T> тип доменного объекта, для которого выполняется запрос на загрузку.
 *
 * @author OKrutova
 * @since 1.6
 */
public abstract class LoadEventWithResult<T extends DomainObject<?>> extends LoadEvent<T> {


    private final LoadResult<T> loadResult;


    /**
     * Конструктор.
     *
     * @param loadResult  результаты загрузки
     * @param queryParams параметры запросов
     * @param loadContext контекст загрузки
     */
    public LoadEventWithResult(
            LoadResult<T> loadResult,
            QueryParams<T, ?> queryParams,
            LoadContext<T> loadContext) {
        super(queryParams, loadContext);
        this.loadResult = loadResult;
    }


    /**
     * Загруженные объекты.
     *
     * @return список загруженных объектов, может быть {@link List}, {@link org.springframework.data.domain.Page}.
     */
    public Iterable<T> getLoadedObjects() {
        return loadResult.getData();
    }

    /**
     * Дополнительно загруженные объекты.
     *
     * @return список дополнительно загруженных объектов.
     */
    public Iterable<DomainObject<?>> getPreloadedObjects() {
        return loadResult.getMoreList();
    }

    /**
     * Хинты.
     *
     * @return хинты
     */
    public Map<String, Object> getHints() {
        return loadResult.getHints();
    }

    /**
     * Возвращает объект, если загрузился ровно один объект.
     *
     * @return доменный объект
     */
    public Optional<T> getSingleLoadedObject() {
        if (loadResult.getData().size() == 1) {
            return Optional.of(loadResult.getData().get(0));
        } else {
            return Optional.ofNullable(null);
        }
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendToString(super.toString())
                .append("loadResult", loadResult)
                .toString();
    }
}