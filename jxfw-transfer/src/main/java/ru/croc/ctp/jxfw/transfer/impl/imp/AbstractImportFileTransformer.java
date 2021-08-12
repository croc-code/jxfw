package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportFileTransformer;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportTransformHandler;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService.LocalFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Collections;

/**
 * Преобразует файл перед импортом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public abstract class AbstractImportFileTransformer implements ImportFileTransformer {
    private static final Logger log = LoggerFactory.getLogger(AbstractImportFileTransformer.class);

    /** Фабрика ридеров файла. */
    protected FileDtoReaderFactory readerFactory;
    /** Преобразователь DTO. */
    protected ImportTransformHandler handler;
    /** Вспомогательный сервис работы с контекстом импорта. */
    protected TransferContextService transferContextService;

    /**
     * Конструктор.
     *
     * @param readerFactory фабрика для ридеров определенного типа
     * @param handler обработчик транформации объектов
     * @param transferContextService сервис для работы с контекстом.
     */
    protected AbstractImportFileTransformer(FileDtoReaderFactory readerFactory, ImportTransformHandler handler, TransferContextService transferContextService) {
        this.readerFactory = readerFactory;
        this.handler = handler;
        this.transferContextService = transferContextService;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        final LocalFile localFile = transferContextService.replaceLocalFileIfNewResourceId(chunkContext).get();
        final LocalFile newLocalFile = transferContextService.generateLocalFile("transform.json");
        log.debug("Transforming file: {}", localFile.path);

        try (FileDtoReader reader = readerFactory.createReader(new File(localFile.path));
             BufferedWriter writer = Files.newBufferedWriter(new File(newLocalFile.path).toPath())) {

            writeStartFile(writer);
            transformAndWriteObjects(reader, writer);
            writeEndFile(writer);
        }

        transferContextService.setLocalFiles(chunkContext, Collections.singletonList(newLocalFile));
        log.debug("New file: {} after transforming file.", newLocalFile.path);
        return RepeatStatus.FINISHED;
    }


    /** Записывает начало файла.
     * @param writer объект для обеспечения записи.
     * @throws IOException проблемы при работе с файлами.
     */
    protected abstract void writeStartFile(Writer writer) throws IOException;

    /** Читает последовательно объекты из источника, преобразует и записывает в указаный поток.
     * @param source источник данных.
     * @param target получатель преобразованных данных.
     * @throws IOException проблемы при работе с файлами.
     * @throws ImportParseException проблема с форматом файла.
     */
    protected abstract void transformAndWriteObjects(FileDtoReader source, Writer target)
            throws ImportParseException, IOException ;

    /** Записывает конец файла.
     * @param writer объект для обеспечения записи.
     * @throws IOException проблемы при работе с файлами.
     */
    protected abstract void writeEndFile(Writer writer) throws IOException;
}
