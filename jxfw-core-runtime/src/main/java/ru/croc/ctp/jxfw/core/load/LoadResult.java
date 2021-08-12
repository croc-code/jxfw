package ru.croc.ctp.jxfw.core.load;

import ru.croc.ctp.jxfw.core.domain.DomainObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;


/**
 * Результат загрузки доменных объектов. {@link LoadService#load(QueryParams, LoadContext)} {@link LoadContext#getLoadResult()}.
 *
 * @param <T> тип
 * @author smufazzalov
 * @since jxfw 1.6.0
 */
public class LoadResult<T extends DomainObject<? extends Serializable>> extends GeneralLoadResult<T, DomainObject<? extends Serializable>> {

    /**
     * Конструтор.
     */
    public LoadResult() {
        super();
    }

    /**
     * Конструтор.
     *
     * @param data     список загружаемых доменных объектов.
     * @param moreList список дополнительных доменных объектов .
     * @param hints    хинты, возвращаемые на клиент вместе с результатом.
     */
    public LoadResult(@Nonnull List<T> data, @Nonnull List<DomainObject<? extends Serializable>> moreList, 
            @Nonnull Map<String, Object> hints) {
        super(data, moreList, hints);
    }

    /**
     * Конструтор.
     *
     * @param data список загружаемых доменных объектов.
     */
    public LoadResult(@Nonnull List<T> data) {
        super(data);
    }
}
