package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportTransformHandler;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.json.JsonDomainToUtils;
import ru.croc.ctp.jxfw.transfer.impl.imp.AbstractImportFileTransformer;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;

/**
 * Преобразует файл перед импортом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public class JsonImportFileTransformer extends AbstractImportFileTransformer {
    private ObjectMapper mapper;

    /** Создат преобразователь файла перед импортом.
     * @param readerFactory фабрика ридера.
     * @param handler преобразователь {@link DomainTo} объектов.
     * @param transferContextService сервис для работы с контекстом
     * @param mapper маппер.
     */
    public JsonImportFileTransformer(
            FileDtoReaderFactory readerFactory,
            ImportTransformHandler handler,
            TransferContextService transferContextService,
            ObjectMapper mapper) {
        super(readerFactory, handler, transferContextService);
        this.mapper = mapper;
    }

    @Override
    protected void writeStartFile(Writer writer) throws IOException {
        writer.write(JsonDomainToUtils.createHeader("transform", LocalDateTime.now()));
    }

    @Override
    protected void transformAndWriteObjects(FileDtoReader source, Writer target)
            throws ImportParseException, IOException {
        boolean isFirst = true;
        ImportDtoInfo dtoInfo;
        while ((dtoInfo = source.next()) != null) {
            final DomainTo newDomainTo = handler.transform(dtoInfo.getDomainTo());
            if (newDomainTo == null) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            } else {
                target.write(JsonDomainToUtils.SEPARATOR);
            }
            target.write(JsonDomainToUtils.transformDomainTo(mapper, newDomainTo));
        }
    }

    @Override
    protected void writeEndFile(Writer writer) throws IOException {
        writer.write(JsonDomainToUtils.TAGS_ARE_ENDED);
        writer.flush();
    }
}
