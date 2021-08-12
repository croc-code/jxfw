package ru.croc.ctp.jxfw.core.export.impl;

import ru.croc.ctp.jxfw.core.datasource.DomainDataLoader;
import ru.croc.ctp.jxfw.core.datasource.impl.LoadingQueryParams;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.export.ExportDataProvider;
import ru.croc.ctp.jxfw.core.export.ExportFormatter;
import ru.croc.ctp.jxfw.core.export.ExportRow;
import ru.croc.ctp.jxfw.core.load.LoadContext;
import ru.croc.ctp.jxfw.core.load.LoadService;
import ru.croc.ctp.jxfw.core.load.QueryParams;

import java.io.Serializable;
import java.util.List;


/**
 * Реализация поставщика данных для экспорта  с пагинацией.
 *
 * @param <T>  тип
 * @param <ID> идентификатор.
 * @author OKrutova
 * @since 1.6
 */
public class PagingExportDataProvider<T extends DomainObject<ID>, ID extends Serializable>
        implements ExportDataProvider {


    private final QueryParams<T, ID> queryParams;
    private final int chunkSize;
    private final LoadService loadService;
    private final LoadContext loadContext;
    private final ExportFormatter exportFormatter;

    private int skip = 0;


    /**
     * Конструктор.
     *
     * @param loadService     loadService
     * @param queryParams     queryParams
     * @param loadContext     loadContext
     * @param chunkSize       chunkSize
     * @param exportFormatter exportFormatter
     */
    public PagingExportDataProvider(LoadService loadService,
                                    QueryParams<T, ID> queryParams,
                                    LoadContext loadContext,
                                    int chunkSize,
                                    ExportFormatter exportFormatter) {
        this.queryParams = queryParams;
        this.chunkSize = chunkSize;
        this.loadContext = loadContext;
        this.loadService = loadService;
        this.exportFormatter = exportFormatter;
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("ChunkSize must be positive. Actual value = " + chunkSize);
        }
    }

    @Override
    public Iterable<ExportRow> getMoreRows() {

        QueryParams<T, ID> chunkingQueryParams = new ExportChunkQueryParams<>(queryParams, chunkSize, skip);
        List<?> loadResult;
        if (queryParams instanceof DomainDataLoader) {

            /*
            Надо совместить в одном loadingQueryParams модифицированные параметры chunkingQueryParams
            и метод загрузки из основного queryParams
             */
            QueryParams<T, ID> loadingQueryParams = new LoadingQueryParams<T, ID>(chunkingQueryParams,
                    (DomainDataLoader<T, ID>) queryParams);
            loadResult = loadService.load(loadingQueryParams, loadContext);
        } else {

            loadResult = loadService.load(chunkingQueryParams, loadContext);
        }

        skip += chunkSize;

        ExportDataProvider exportDataProvider = new BaseExportDataProvider(loadResult, exportFormatter, loadContext);

        return exportDataProvider.getMoreRows();
    }

}
