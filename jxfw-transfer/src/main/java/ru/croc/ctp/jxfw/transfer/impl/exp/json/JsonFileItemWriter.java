package ru.croc.ctp.jxfw.transfer.impl.exp.json;

import static ru.croc.ctp.jxfw.transfer.impl.TransferContextService.DEFAULT_FILE_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.AbstractFileItemWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Записывает данные со всех шагов в json один файл.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component
@Profile("exportJson")
@JobScope
public class JsonFileItemWriter extends AbstractFileItemWriter {
    /** Json маппер. */
    private ObjectMapper mapper;
    /** Счётчик количества записанных объектов. */
    private long count = 0;

    /**
     * Записывает данные со всех шагов в json один файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param scenarioName имя задачи
     * @param serializingObjectMapper mapper.
     * @throws IOException ошибка при работе с файловой системой.
     */
    @Autowired
    public JsonFileItemWriter(TransferContextService transferContextService,
                              @Value("#{jobParameters[scenarioName]}") String scenarioName,
                              ObjectMapper serializingObjectMapper) throws IOException {
        this(transferContextService, scenarioName, serializingObjectMapper, DEFAULT_FILE_NAME);
    }

    /**
     * Записывает данные со всех шагов в json один файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param scenarioName имя задачи
     * @param serializingObjectMapper mapper
     * @param fileName имя файла.
     * @throws IOException ошибка при работе с файловой системой.
     */
    public JsonFileItemWriter(TransferContextService transferContextService,
                              String scenarioName,
                              ObjectMapper serializingObjectMapper, String fileName) throws IOException {
        super(transferContextService, scenarioName, fileName);
        this.mapper = serializingObjectMapper;
    }

    @Override
    protected String formatOfFile() {
        return "json";
    }

    @Override
    protected String generateHeader() {
        return JsonDomainToUtils.createHeader(getScenarioName(), LocalDateTime.now());
    }

    @Override
    protected String getEndOfFile() {
        return JsonDomainToUtils.TAGS_ARE_ENDED;
    }

    @Override
    public void write(List<? extends DomainTo> items) throws Exception {
        final StringBuilder builder = new StringBuilder();

        for (DomainTo domainTo : items) {
            if (count++ > 0) {
                builder.append(JsonDomainToUtils.SEPARATOR);
            }
            builder.append(JsonDomainToUtils.transformDomainTo(mapper, domainTo));
        }

        writeData(builder.toString());
    }
}
