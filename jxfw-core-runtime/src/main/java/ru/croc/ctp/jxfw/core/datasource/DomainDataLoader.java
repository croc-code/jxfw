package ru.croc.ctp.jxfw.core.datasource;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadResult;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * Загрузчик доменных объектов, использующий LoadService.
 * Применяется для доменых датасорсов.
 *
 * @param <T>  доменный тип
 * @param <ID> идентификатор доменного типа
 *
 * @author OKrutova
 * @since 1.6.0
 */
public interface DomainDataLoader<T extends DomainObject<ID>, ID extends Serializable> {

    /**
     * Загрузить данные.
     * @param loadContext контекст загрузки
     * @return результат загрузки
     */
    @Nonnull
    LoadResult<T> load(@Nonnull LoadContext<T> loadContext);


    /**
     * Загрузить данные с учетом параметров.
     * @param loadContext контекст загрузки
     * @param queryParams параметры загрузки
     * @return результат загрузки
     */
    @Nonnull
    LoadResult<T> load(@Nonnull LoadContext<T> loadContext, @Nonnull QueryParams<T,ID> queryParams);

    /**
     * Признак поддержки постраничного запроса данных.
     * Требуется, чтобы экспорт мог определить можно ли применять к этому источнику
     * механизм экспорта чанками.
     * @return да\нет
     */
    boolean supportsPaging();
    
}
