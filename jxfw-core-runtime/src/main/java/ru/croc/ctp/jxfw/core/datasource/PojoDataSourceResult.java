package ru.croc.ctp.jxfw.core.datasource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Контейнер для результата выполнения запроса из dataSource, который содержит в себе
 * объекты формата POJO.
 *
 * @param <T> - тип данных, который отдает DS.
 * @author Nosov Alexander
 * @since 1.0
 * @deprecated 1.5
 */
@Deprecated
public final class PojoDataSourceResult<T> implements DataSourceResult<T> {

    private List<T> data;

    private List<T> more;

    private Map<String, Object> hints;

    /**
     * @param data  - данные, которые передаются из DS.
     * @param more  - дополнительные данные.
     * @param hints - хинты для правильного отображение на WC
     */
    public PojoDataSourceResult(final Iterable<T> data, final Iterable<T> more, Map<String, Object> hints) {
        this.data = Lists.newArrayList(data);
        this.more = Lists.newArrayList(more);
        this.hints = Maps.newHashMap(hints);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public List<T> getMore() {
        return more;
    }

    @Override
    public Map<String, Object> getHints() {
        return hints;
    }
}

