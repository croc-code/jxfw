package ru.croc.ctp.jxfw.core.datasource;

import ru.croc.ctp.jxfw.core.load.GeneralLoadContext;
import ru.croc.ctp.jxfw.core.load.GeneralLoadResult;

import javax.annotation.Nonnull;

/**
 * Общий интерфейс загрузчика данных.
 * Используется в датасорсах.
 *
 * @param <T> доменный тип
 * @param <U> тип дополнительных объектов
 *
 * @author OKrutova
 * @since 1.6.0
 */
public interface DataLoader<T,U>  {


    /**
     * Запросить данные.
     * @param loadContext контекст загрузки
     * @return результат загрузки
     */
    @Nonnull
    GeneralLoadResult<T,U> load(@Nonnull GeneralLoadContext<T, U> loadContext);
    
}
