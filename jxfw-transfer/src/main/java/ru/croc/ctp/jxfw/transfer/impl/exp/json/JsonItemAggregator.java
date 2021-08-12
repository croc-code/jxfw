package ru.croc.ctp.jxfw.transfer.impl.exp.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

/**
 * Аггрегатор объектов.
 *
 * @author Nosov Alexander
 *
 * @deprecated version 1.5 replaced by component {@link JsonFileItemWriter}.
 */
@Component
@Deprecated
public class JsonItemAggregator implements LineAggregator<DomainTo> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsonItemAggregator.class);

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String aggregate(DomainTo item) {
        String result = null;
        try {
            ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
            result = objectWriter.writeValueAsString(item);
        } catch (JsonProcessingException jpe) {
            logger.error("An error has occured. Error message {} ", jpe.getMessage());
        }
        return result;
    }
}
