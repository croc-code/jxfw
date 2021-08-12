package ru.croc.ctp.jxfw.transfer.config;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReader;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextManager;
import ru.croc.ctp.jxfw.transfer.component.imp.context.ImportContextSplitterAndAggregator;
import ru.croc.ctp.jxfw.transfer.component.imp.context.handler.ImportDependencyCollisionHandler;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.DefaultImportContextManager;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.DefaultImportDependencyManager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/**
 * Spring конфиг для модуля xml компонентов Transfer.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
@Configuration
public class XmlXfwTransferConfig {

    /**
     * @param xmlFileDtoReaderFactory фабрика xml ридеров.
     * @param transferContextService сервис для работы с контекстом.
     * @param xmlImportContextManager менеджер контекстов импорта xml файлов.
     * @param importGroupReaderFactory фабрика для создания {@link ImportGroupReader}.
     * @param importDependencyManager компонент проверяющий корректность загружаемых объектов.
     * @param importContextSplitterAndAggregator компонент для разбиения объектов по группам.
     * @param importDependencyCollisionHandler обработчик объетков с неразрешенными зависимостями.
     * @param isIgnoreObjectsOfUnknownType указывает игнорировать ли объекты неизвестного типа.
     * @return reader больших xml файлов.
     * @throws IOException ошибка чтения файла.
     * @throws ExecutionException проблема с загрузкой контекста.
     * @throws ImportParseException проблема с форматом документа.
     */
    @Bean
    @Profile("importXml")
    @JobScope
    @Autowired
    public ImportGroupReader xmlImportGroupItemReader(
            FileDtoReaderFactory xmlFileDtoReaderFactory,
            TransferContextService transferContextService,
            ImportContextManager xmlImportContextManager,
            ImportGroupReaderFactory importGroupReaderFactory,
            DefaultImportDependencyManager importDependencyManager,
            ImportContextSplitterAndAggregator importContextSplitterAndAggregator,
            ImportDependencyCollisionHandler importDependencyCollisionHandler,
            @Value("${transfer.import.xml.isIgnoreObjectsOfUnknownType:false}") boolean isIgnoreObjectsOfUnknownType
    ) throws IOException, ExecutionException, ImportParseException {
        return importGroupReaderFactory.create(
                xmlImportContextManager,
                xmlFileDtoReaderFactory,
                transferContextService,
                importDependencyManager,
                importContextSplitterAndAggregator,
                importDependencyCollisionHandler,
                isIgnoreObjectsOfUnknownType
        );
    }

    /**
     * @param xmlFileDtoReaderFactory фабрика xml ридеров.
     * @param resolver сопоставитель типов данных.
     * @param contextStoreDirectory директория для хранения файлов контекста импорта.
     * @return менеджер контекстов импорта для xml файлов.
     */
    @Bean
    @Profile("importXml")
    @Autowired
    public ImportContextManager xmlImportContextManager(
            FileDtoReaderFactory xmlFileDtoReaderFactory,
            DomainServicesResolver resolver,
            @Value("${transfer.import.store.xml:./import-context-store/xml}") String contextStoreDirectory) {
        return new DefaultImportContextManager.Builder()
                .readerFactory(xmlFileDtoReaderFactory)
                .resolver(resolver)
                .contextStoreDirectory(contextStoreDirectory)
                .cleanPeriod(3 * 60)
                .build();
    }
}
