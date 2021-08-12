package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoPropertiesLoader;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;

import java.io.File;
import java.io.IOException;

/**
 * Фабрика парсера для чтение доменных объектов формата json.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component
@Profile("importJson")
public class JsonFileDtoReaderFactory implements FileDtoReaderFactory {
    private ObjectMapper objectMapper;

    /**
     * Создаёт фабрику для создания парсера файлоа содержащий доменные объекты в определенном формате json.
     * @param serializingObjectMapper json mapper.
     */
    @Autowired
    public JsonFileDtoReaderFactory(ObjectMapper serializingObjectMapper) {
        this.objectMapper = serializingObjectMapper;
    }

    @Override
    public JsonFileDtoReader createReader(File file) throws IOException {
        return new JsonFileDtoReader(file, objectMapper);
    }

    @Override
    public FileDtoPropertiesLoader createLoader(File file, String encoding) throws ImportParseException {
        return new JsonFileDtoPropertiesLoader(file, objectMapper, encoding);
    }
}
