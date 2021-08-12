package ru.croc.ctp.jxfw.core.export;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.io.Serializable;


/**
 * Билдер поставщика данных для экспорта.
 * Существует для того, чтобы прикладные проекты могли переопределить
 * класс поставщика.
 *
 * @author OKrutova
 * @since 1.6
 */
public interface ExportDataProviderBuilder {

    /**
     * Построить поставщика данных экcпорта Для доменных объектов.
     *
     * @param queryParams     параметры запроса {@link QueryParams}
     * @param loadContext     контекст конвейера чтения {@link LoadContext}
     * @param exportFormatter exportFormatter {@link ExportFormatter}
     * @param <T>  тип
     * @param <ID> идентификатор.
     * @return поставщик
     */
    <T extends DomainObject<ID>, ID extends Serializable> ExportDataProvider
        build(QueryParams<T, ID> queryParams, LoadContext loadContext,ExportFormatter exportFormatter);


}
