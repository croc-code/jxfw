package ru.croc.ctp.jxfw.transfer.component.imp;

import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportFileDtoHead;

import java.io.IOException;

/**
 * Парсер обеспечивающий возможность чтение траснопортных доменных объектов
 * с последовательным доступом.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface FileDtoReader extends AutoCloseable {
    /** Возвращает заголовк из начала файла.
     * @return метаинформация из шапки импортируемого файла. */
    ImportFileDtoHead getHeader();

    /** Читает следующий доменный объект, его свойства и инф. об рассположение объекта в файле.
     * @return доманный объект и информация об его рассположении в файле. null если объектов больше нет.
     * @throws IOException при возникновеннии проблем при чтении файла.
     * @throws ImportParseException при не совпадающем формате домента.
     */
    ImportDtoInfo next() throws ImportParseException, IOException;

    /** Закрывает файл. */
    void close();

    /** Возращает название кодировки документа.
     * @return название кодировки документа, если отсутсвует то UTF-8.
     */
    String getEncoding();
}
