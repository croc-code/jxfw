package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportTransformHandler;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.xml.XmlDomainToUtils;
import ru.croc.ctp.jxfw.transfer.impl.imp.AbstractImportFileTransformer;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformer;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;

/**
 * Преобразует файл перед импортом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
//TODO не используется, трансформируем только в json
public class XmlImportFileTransformer extends AbstractImportFileTransformer {
    private TransferToTransformer transferToTransformer;

    /** Создат преобразователь файла перед импортом.
     * @param readerFactory фабрика ридера.
     * @param handler преобразователь {@link DomainTo} объектов.
     * @param transferContextService сервис для работы с контекстом
     * @param transferToTransformer xml трансформер.
     */
    public XmlImportFileTransformer(
            FileDtoReaderFactory readerFactory,
            ImportTransformHandler handler,
            TransferContextService transferContextService,
            TransferToTransformer transferToTransformer) {
        super(readerFactory, handler, transferContextService);
        this.transferToTransformer = transferToTransformer;
    }



    @Override
    protected void writeStartFile(Writer writer) throws IOException {
        writer.write(XmlDomainToUtils.createHeader(LocalDateTime.now()));
    }

    @Override
    protected void transformAndWriteObjects(FileDtoReader source, Writer target)
            throws ImportParseException, IOException {
        boolean isFirst = true;
        ImportDtoInfo dtoInfo;
        while ((dtoInfo = source.next()) != null) {
            DomainTo newDomainTo = handler.transform(dtoInfo.getDomainTo());
            if (newDomainTo == null) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
            } else {
                target.write(XmlDomainToUtils.SEPARATOR);
            }
            // newDomainTo = resolver.resolveToService(newDomainTo.getType()).fromTo(newDomainTo, )
            target.write(XmlDomainToUtils.transformDomainTo(transferToTransformer, newDomainTo));
        }
    }

    @Override
    protected void writeEndFile(Writer writer) throws IOException {
        writer.write(XmlDomainToUtils.TAGS_ARE_ENDED);
        writer.flush();
    }
}
