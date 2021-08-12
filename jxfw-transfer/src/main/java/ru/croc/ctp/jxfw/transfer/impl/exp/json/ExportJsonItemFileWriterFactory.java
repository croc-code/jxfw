package ru.croc.ctp.jxfw.transfer.impl.exp.json;

import static ru.croc.ctp.jxfw.transfer.impl.TransferContextService.DEFAULT_FILE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportItemFileWriterFactory;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.ExportDynamicFileItemWriter;

import java.io.IOException;
import java.util.function.Function;


/**
 * Фабрика writer'ов для экспорта {@link DomainTo} в json файл.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Component
@Profile("exportJson")
@JobScope
public class ExportJsonItemFileWriterFactory implements ExportItemFileWriterFactory {
    private final TransferContextService transferContextService;
    private final ObjectMapper serializingObjectMapper;
    private final String scenarioName;
    /** Один экземпляр уже объявлен. */
    private int count = 1;

    /**
     * Фабрика writer'ов для экспорта {@link DomainTo} в json файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param serializingObjectMapper mapper
     * @param scenarioName имя задачи.
     */
    public ExportJsonItemFileWriterFactory(TransferContextService transferContextService,
                                           ObjectMapper serializingObjectMapper,
                                           @Value("#{jobParameters[scenarioName]}") String scenarioName) {
        this.transferContextService = transferContextService;
        this.serializingObjectMapper = serializingObjectMapper;
        this.scenarioName = scenarioName;
    }


    @Override
    public JsonFileItemWriter create() throws IOException {
        return create(DEFAULT_FILE_NAME + "-" + count++);
    }

    @Override
    public JsonFileItemWriter create(String fileName) throws IOException {
        return new JsonFileItemWriter(transferContextService, scenarioName, serializingObjectMapper, fileName);
    }

    @Override
    public ExportDynamicFileItemWriter create(Function<DomainTo, String> function) {
        return new ExportDynamicFileItemWriter(function, this, transferContextService);
    }
}
