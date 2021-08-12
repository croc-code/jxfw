package ru.croc.ctp.jxfw.transfer.impl.imp.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoPropertiesLoader;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import javax.validation.constraints.NotNull;




/**
 * Обеспичивает загрузку всех свойств доменного объекта по иформации о его рассположении в json файле.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class JsonFileDtoPropertiesLoader implements FileDtoPropertiesLoader {
    /** Файл из которого происходит загрузка. */
    private final File file;
    /** Доступ к фалу на чтение. */
    private RandomAccessFile randomAccessFile;
    /** Открыт ли файл. */
    private boolean open = false;
    private final JsonFactory jsonFactory;
    private final String encoding;

    /** Создаёт загрузчик свойств доменных объектов из файла.
     * @param file файл из которого загружаются данные.
     * @param objectMapper маппер для json объектов.
     * @param encoding кодировка.
     * */
    public JsonFileDtoPropertiesLoader(@NotNull File file, @NotNull ObjectMapper objectMapper,
                                       @NotNull String encoding) {
        this.file = file;
        jsonFactory = new JsonFactory();
        jsonFactory.setCodec(objectMapper != null ? objectMapper : new ObjectMapper());
        this.encoding = encoding;
    }

    @Override
    public void open() throws IOException {
        randomAccessFile = new RandomAccessFile(file, "r");
        open = true;
    }

    @Override
    public void close() {
        open = false;
        try {
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public boolean loadProperties(@NotNull ImportDtoInfo dtoInfo) throws IOException {
        final int length = (int) (dtoInfo.getOffsetLastByteInFile() - dtoInfo.getOffsetFirstByteInFile());
        final byte[] buffer = new byte[length];

        randomAccessFile.seek(dtoInfo.getOffsetFirstByteInFile());
        randomAccessFile.readFully(buffer);
        String strJson = new String(buffer, encoding);
        strJson = (strJson.charAt(0) == ',') ? strJson.substring(1) : strJson;
        DomainTo dto = null;
        try {
            dto = jsonFactory.createParser(strJson).readValueAs(DomainTo.class);
        } catch (Exception e) {
            System.out.println(new String(buffer));
        }
        dtoInfo.setDomainTo(dto);
        return true;
    }

    @Override
    public boolean loadPropertiesForGroup(Collection<ImportDtoInfo> group) throws IOException, ImportParseException {
        for (ImportDtoInfo dtoInfo : group) {
            boolean isSuccess = loadProperties(dtoInfo);
            if (!isSuccess) {
                return false;
            }
        }
        return true;
    }
}
