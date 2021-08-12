package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportFileTransformer;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportFileTransformerFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportTransformHandler;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;

/**
 * Фабрика преобразователей json файлов пред импортом.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
@Component("jsonImportFileTransformerFactory")
@Profile("transformJson")
public class JsonImportFileTransformerFactory implements ImportFileTransformerFactory {
    private final TransferContextService transferContextService;
    private final ObjectMapper mapper;


    /**
     * Фабрика преобразователей json файлов пред импортом.
     *
     * @param transferContextService сервис для работы с контекстом
     * @param serializingObjectMapper json mapper.
     */
    @Autowired
    public JsonImportFileTransformerFactory(TransferContextService transferContextService,
                                            ObjectMapper serializingObjectMapper) {
        this.transferContextService = transferContextService;
        this.mapper = serializingObjectMapper;
    }

    @Override
    public ImportFileTransformer create(ImportTransformHandler handler) {
        return new JsonImportFileTransformer(new JsonFileDtoReaderFactory(mapper), handler,
                transferContextService, mapper);
    }
}
