package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportFileDtoHead;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Парсер для файлов импорта JXFW формата json.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class JsonFileDtoReader implements FileDtoReader {
    /** Json парсер. */
    private final JsonParser parser;
    /** Заголовок файла. */
    private final ImportFileDtoHead header;
    private final String encoding;


    /** Создаёт парсер на ресурс и открывает его.
     * @param file файл содержащий доменные объекты в определенном формате.
     * @param objectMapper маппер.
     * @throws IOException Если проблемы вводом/выводом к ресурсу или форматом файла.
     */
    protected JsonFileDtoReader(File file, ObjectMapper objectMapper) throws IOException {
        parser = new JsonFactory().createParser(file);
        parser.setCodec(objectMapper);
        header = readHeader();
        encoding = StandardCharsets.UTF_8.displayName();
    }

    /** Читает заголовок с метаданными и переносит курсор к началу чтения доменных объектов.
     *  @return заголовочные метаданные.
     */
    private ImportFileDtoHead readHeader() throws IOException {
        final ImportFileDtoHead head = new ImportFileDtoHead();
        //TODO читаем шапку, а не просто пролистываем
        while ((parser.nextToken()) != JsonToken.START_ARRAY) {}
        return head;
    }

    @Override
    public ImportFileDtoHead getHeader() {
        return header;
    }

    @Override
    public ImportDtoInfo next() throws IOException {
        final long offsetFirstByte = parser.getCurrentLocation().getByteOffset();
        parser.nextToken();
        final DomainTo dto = parser.readValueAs(DomainTo.class);
        final long offsetLastByte = parser.getCurrentLocation().getByteOffset();
        if (dto == null) {
            return null;
        }

        return new ImportDtoInfo.Builder(dto.getType(), dto.getId())
                .domainTo(dto)
                .offsetFirstByteInFile(offsetFirstByte)
                .offsetLastByteInFile(offsetLastByte)
                .build();
    }

    @Override
    public void close() {
        if (parser == null) {
            return;
        }

        try {
            parser.close();
        } catch (IOException e) {
            //ignore
        }
    }

    @Override
    public String getEncoding() {
        return encoding;
    }
}
