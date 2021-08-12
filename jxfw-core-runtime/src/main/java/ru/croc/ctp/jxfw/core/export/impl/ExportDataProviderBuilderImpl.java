package ru.croc.ctp.jxfw.core.export.impl;

import ru.croc.ctp.jxfw.core.datasource.DomainDataLoader;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportDataProviderBuilder;
import ru.croc.ctp.jxfw.core.export.ExportFormatter;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParams;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.io.Serializable;


/**
 * Для контроллеров доменны объектов сторит провайдера, который умеет загружать данные чанками,
 * в остальных случаях строит BaseExportDataProvider.
 */

public class ExportDataProviderBuilderImpl implements ExportDataProviderBuilder {

    private final int chunkSize;
    private final LoadService loadService;

    /**
     * Констурктор.
     *
     * @param loadService loadService
     * @param chunkSize   chunkSize
     */
    public ExportDataProviderBuilderImpl(LoadService loadService, int chunkSize) {
        this.chunkSize = chunkSize;
        this.loadService = loadService;
    }

    @Override
    public <T extends DomainObject<ID>, ID extends Serializable> ExportDataProvider
        build(QueryParams<T, ID> queryParams, LoadContext loadContext, ExportFormatter exportFormatter) {


        boolean supportPaging = false;

        if (queryParams instanceof DomainDataLoader) {
            /*
            Сюда попадаем для контроллеров датасорсов
            */

            supportPaging = ((DomainDataLoader) queryParams).supportsPaging() && chunkSize > 0;
        } else {
            /*
            Сюда попадаем из контроллеров доменных объектов. Не из контроллеров датасорсов!
            ДЛя не JPA-хранилищ отключаем пагинацию при экспорте. Т.к. нет времени изучать,
            для каких хранилищ она работает, а для каких нет.
            */
            XfwClass xfwClass = XfwModelFactory.getInstance().find(
                    queryParams.getDomainObjectTypeName(),
                    XfwClass.class);
            supportPaging =
                    ((chunkSize > 0) && xfwClass != null)
                            && xfwClass.getPersistenceModule().contains("JPA");
        }

        if (supportPaging) {
            return new PagingExportDataProvider<>(loadService, queryParams,
                    loadContext, chunkSize, exportFormatter);
        } else {
            return new BaseExportDataProvider(loadService.load(queryParams, loadContext),
                    exportFormatter, loadContext);
        }
    }


}
