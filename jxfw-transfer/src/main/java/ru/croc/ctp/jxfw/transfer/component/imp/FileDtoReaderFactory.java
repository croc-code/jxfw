package ru.croc.ctp.jxfw.transfer.component.imp;

import org.springframework.core.io.Resource;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;

import java.io.File;
import java.io.IOException;

/**
 * Фабрика парсеров для чтение доменных объектов определенного формата.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface FileDtoReaderFactory {
    /** Создаёт парсер на ресурс и открывает его.
     * @param file файл содержащий доменные объекты в определенном формате.
     * @return Открытый парсер прочитавший шапку файла и готовый к чтению доменных объектов.
     * @throws IOException проблемы при чтении файла.
     * @throws ImportParseException проблема с форматом документа.
     */
    FileDtoReader createReader(File file) throws ImportParseException, IOException;

    /** Создаёт загрузчик на ресурс.
     * @param file файл содержащий доменные объекты в определенном формате.
     * @param encoding кодировка документа.
     * @return Загрузчик данных для объектов прочитанных парсером.
     * @throws ImportParseException проблема с форматом документа.
     */
    FileDtoPropertiesLoader createLoader(File file, String encoding) throws ImportParseException;
}
