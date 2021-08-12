package ru.croc.ctp.jxfw.core.datasource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Контейнер для результата выполнения запроса в dataSource, который содержит в себе
 * объекты формата DomainObject
 *
 * @author Nosov Alexander
 *         on 27.04.15.
 * @see DomainObject
 * @deprecated since 1.6
 */
@Deprecated
public final class DomainObjectDataSourceResult implements DataSourceResult<DomainObject<? extends Serializable>> {

    private List<DomainObject<? extends Serializable>> data;

    private List<DomainObject<? extends Serializable>> more;

    private Map<String, Object> hints;

    /**
     * @param data - данные хранящиеся в контейнере.
     */
    public DomainObjectDataSourceResult(final Iterable<DomainObject<? extends Serializable>> data) {
        this.data = Lists.newArrayList(data);
    }

    /**
     * @param data  - данные хранящиеся в контейнере.
     * @param more  - доп. данные хранящиеся в контейнере.
     * @param hints - хинты для отображения
     */
    public DomainObjectDataSourceResult(final Iterable<DomainObject<? extends Serializable>> data,
                                        final Iterable<DomainObject<? extends Serializable>> more,
                                        Map<String, Object> hints) {
        this.data = Lists.newArrayList(data);
        this.more = Lists.newArrayList(more);
        this.hints = Maps.newHashMap(hints);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<DomainObject<? extends Serializable>> getData() {
        return data;
    }

    @Override
    public List<DomainObject<? extends Serializable>> getMore() {
        return more;
    }

    @Override
    public Map<String, Object> getHints() {
        return hints;
    }
}
