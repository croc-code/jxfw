package ru.croc.ctp.jxfw.transfer.impl.exp.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.time.LocalDateTime;

/**
 * Преобразует части DTO в json строки.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public class JsonDomainToUtils {
    /** Разделитель междуу объектами. */
    public static final String SEPARATOR = ",\n";
    /** Закрывающие теги файла импорта в json строке. */
    public static final String TAGS_ARE_ENDED = "\n]\n}";

    /** Создаёт шапку файла импорта в json формате.
     * @param scenarioName имя сценария.
     * @param dateTime дата и время создания.
     * @return шапка документа в json.
     */
    public static String createHeader(String scenarioName, LocalDateTime dateTime) {
        return String.format(
                "{"
                        + "\"scenarioName\":\"%s\","
                        + "\"exportTime\":\"%s\","
                        + "\"objects\":[\n",
                scenarioName,
                dateTime == null ? LocalDateTime.now().toString() : dateTime.toString()
        );
    }

    /** Преобразует {@link ru.croc.ctp.jxfw.core.facade.webclient.DomainTo} в строку json.
     *  @param mapper маппер json.
     *  @param domainTo трансформируемый доменный объект.
     *  @return доменный объект в строке json.
     *  @throws JsonProcessingException проблема с преобразованием.
     */
    public static String transformDomainTo(ObjectMapper mapper, DomainTo domainTo) throws JsonProcessingException {
        return mapper.writeValueAsString(domainTo);
    }
}
