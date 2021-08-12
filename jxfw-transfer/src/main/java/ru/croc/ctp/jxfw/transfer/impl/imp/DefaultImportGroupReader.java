package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoPropertiesLoader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReader;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContext;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportDependencyManager;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService.LocalFile;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.ImportContextUtils;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.DefaultImportGroup;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Читает по очереди группы доменных объектов указынных в контексте импорта.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class DefaultImportGroupReader implements ImportGroupReader {
    private static final Logger log = LoggerFactory.getLogger(DefaultImportGroupReader.class);
    private ImportContext context;
    private LocalFile localFile;
    private FileDtoPropertiesLoader loader;

    private static String CURRENT_INDEX = "current.index";
    private Long currentIndex = -1L;

    private final ImportContextManager importContextManager;
    private final FileDtoReaderFactory loaderFactory;
    private final ImportDependencyManager dependencyManager;
    private final ImportContextSplitterAndAggregator splitter;
    private final TransferContextService transferContextService;
    private final DomainFacadeIgnoreService domainFacadeIgnoreService;
    /** Указывает игнорировать ли объекты неизвестного типа. */
    private final boolean isIgnoreObjectsOfUnknownType;
    /** Игнорировать ли обратные ссылки. */
    private final boolean ignoreBidirectional;
    /** Метамодель. */
    private final XfwModel xfwModel;



    /** Ридер для чтения групп объектов из файла.
     * @param importContextManager контейнер контекстов имопрта.
     * @param loaderFactory фабрика загрузчиков данных из файла определенного формата.
     * @param dependencyManager компонент проверяющий корректность загружаемых объектов.
     * @param splitter компонент для разбиения объектов по группам.
     * @param transferContextService сервис для работы с контекстом
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа.
     * @param ignoreBidirectional игнорировать ли обратные ссылки.
     * @param domainFacadeIgnoreService сервис определяющий игнорируемые фасадом поля.
     */
    public DefaultImportGroupReader(ImportContextManager importContextManager,
                                    FileDtoReaderFactory loaderFactory,
                                    ImportDependencyManager dependencyManager,
                                    ImportContextSplitterAndAggregator splitter,
                                    TransferContextService transferContextService,
                                    boolean isIgnoreObjectsOfUnknownType,
                                    boolean ignoreBidirectional,
                                    DomainFacadeIgnoreService domainFacadeIgnoreService) {
        this.importContextManager = importContextManager;
        this.loaderFactory = loaderFactory;
        this.dependencyManager = dependencyManager;
        this.splitter = splitter;
        this.transferContextService = transferContextService;
        this.isIgnoreObjectsOfUnknownType = isIgnoreObjectsOfUnknownType;
        this.ignoreBidirectional = ignoreBidirectional;
        this.xfwModel = XfwModelFactory.getInstance();
        this.domainFacadeIgnoreService = domainFacadeIgnoreService;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        if (executionContext.containsKey(CURRENT_INDEX)) {
            currentIndex = executionContext.getLong(CURRENT_INDEX);
        } else {
            currentIndex = -1L;
        }
        log.debug("Open stream with current index: {}", currentIndex);
    }

    /** Загружает контекст импорта.
     * @throws ItemStreamException если проблема с импортируемым файлом.
     */
    private void loadImportContext() throws ItemStreamException {
        try {
            // анализируем файл и сохраняем план импорта в контексте
            this.context = importContextManager.getImportContext(
                    localFile.path,
                    dependencyManager,
                    splitter,
                    isIgnoreObjectsOfUnknownType,
                    ignoreBidirectional,
                    domainFacadeIgnoreService
            );
            // инициализируем загрузчик
            this.loader = loaderFactory.createLoader(new File(localFile.path), context.getEncoding());
            loader.open();
        } catch (Exception e) {
            throw new ItemStreamException("Проблема с файлом импорта.", e);
        }
    }

    @Override
    public DefaultImportGroup read() throws Exception {
        if (context == null) {
            log.debug("Not found import context. Loading import context is starting.");
            loadImportContext();
        }
        currentIndex++;
        if (context.getGroupsOfLoading().containsKey(currentIndex)) {
            log.debug("Loading group with index: {}", currentIndex);
            return loadGroup(currentIndex.intValue());
        }

        if (loader != null) {
            loader.close();
        }
        log.debug("Reading is finished.");
        return null;
    }

    /** Формирует группу импорта для следующей группы и подгружает недостающие данные.
     * @param indexGroup индекс группы.
     * @return группа импорта.
     */
    private DefaultImportGroup loadGroup(int indexGroup) throws ImportParseException {
        final DefaultImportGroup importGroup = new DefaultImportGroup((long) indexGroup);
        // фильтруем объекты не загруженные из файла
        final List<ImportDtoInfo> objectsOfLoaded = new ArrayList<>();
        for (ImportDtoInfo dtoInfo : context.getGroupsOfLoading().get(importGroup.getId())) {
            if (dtoInfo.isLoadFromFile()) {
                objectsOfLoaded.add(dtoInfo);
            }
        }

        // загружает DTO
        try {
            loader.loadPropertiesForGroup(objectsOfLoaded);
        } catch (IOException e) {
            throw new ImportParseException();
        }

        // формируем группу DTO, и затираем данные в контексте импорта
        for (ImportDtoInfo dtoInfo : context.getGroupsOfLoading().get(importGroup.getId())) {
            if (ignoreBidirectional) {
                final XfwClass xfwClass = xfwModel.findBySimpleName(dtoInfo.getType(), XfwClass.class);
                dtoInfo = ImportContextUtils.createDtoInfoWithoutBidirectionalLinks(dtoInfo, xfwClass);
            }
            importGroup.getObjects().add(dtoInfo.getDomainTo());
            dtoInfo.setDomainTo(null);
        }

        log.debug("Loaded import group. index: {} size: {}", indexGroup, importGroup.getObjects().size());
        return importGroup;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putLong(CURRENT_INDEX, currentIndex);
        log.debug("Update stream with current index: {}", currentIndex);
    }

    @Override
    public void close() throws ItemStreamException {
        if (loader != null) {
            loader.close();
        }
        log.debug("Close stream with current index: {}", currentIndex);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        try {
            localFile = transferContextService.replaceLocalFileIfNewResourceId(stepExecution).get();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        log.debug("Transfer uses file: {}", localFile.path);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}
