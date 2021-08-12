package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReader;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportDependencyManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;

/**
 * Создаёт {@link ImportGroupReader}.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("importGroupReaderFactory")
public class DefaultImportGroupReaderFactory implements ImportGroupReaderFactory {
    private DomainFacadeIgnoreService domainFacadeIgnoreService;

    /**
     * Фабрика {@link ImportGroupReader}.
     *
     * @param domainFacadeIgnoreService сервис определяющий игнорируемые фасадом поля.
     */
    @Autowired
    public DefaultImportGroupReaderFactory(DomainFacadeIgnoreService domainFacadeIgnoreService) {
        this.domainFacadeIgnoreService = domainFacadeIgnoreService;
    }

    @Override
    public ImportGroupReader create(ImportContextManager importContextManager,
                                    FileDtoReaderFactory loaderFactory,
                                    TransferContextService transferContextService,
                                    ImportDependencyManager dependencyManager,
                                    ImportContextSplitterAndAggregator splitter,
                                    ImportDependencyCollisionHandler handler,
                                    boolean isIgnoreObjectsOfUnknownType) throws IllegalArgumentException {
        return create(
                importContextManager,
                loaderFactory,
                transferContextService,
                dependencyManager,
                splitter,
                handler,
                isIgnoreObjectsOfUnknownType,
                false
        );
    }

    @Override
    public ImportGroupReader create(ImportContextManager importContextManager,
                                    FileDtoReaderFactory loaderFactory,
                                    TransferContextService transferContextService,
                                    ImportDependencyManager dependencyManager,
                                    ImportContextSplitterAndAggregator splitter,
                                    ImportDependencyCollisionHandler handler,
                                    boolean isIgnoreObjectsOfUnknownType,
                                    boolean ignoreBidirectional) throws IllegalArgumentException {
        if (importContextManager == null || loaderFactory == null || transferContextService == null
                || dependencyManager == null || splitter == null || handler == null) {
            throw new IllegalArgumentException("Метод не принимает null параметры!");
        }
        dependencyManager.setHandler(handler);
        return new DefaultImportGroupReader(
                importContextManager,
                loaderFactory,
                dependencyManager,
                splitter,
                transferContextService,
                isIgnoreObjectsOfUnknownType,
                ignoreBidirectional,
                domainFacadeIgnoreService

        );
    }
}
