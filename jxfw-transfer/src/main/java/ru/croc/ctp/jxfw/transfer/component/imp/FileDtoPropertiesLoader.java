package ru.croc.ctp.jxfw.transfer.component.imp;

import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;

import java.io.IOException;
import java.util.Collection;

/**
 * Обеспичивает загрузку всех свойств доменного объекта по иформации о его рассположении в файле.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public interface FileDtoPropertiesLoader extends AutoCloseable {
    /** Открывает файл для прямого доступа.
     * @throws IOException если проблемы при чтении файла.
     */
    void open() throws IOException;

    /** Закрывает файл. */
    void close();

    /** Проверяет открыт ли файл.
     * @return true если свойства были считаны, иначе false.
     */
    boolean isOpen();

    /** Догружает прямым достпуом свойства объека из файла, по инф. об рассположении объекта в файле.
     * @param dtoInfo - метаданные доменного объекта: смещение первого байта и последнего от начала файла.
     * @return выполнилась ли загрузка свойств успешно.
     * @throws IOException проблемы при чтении файла.
     * @throws ImportParseException проблема с форматом документа.
     */
    boolean loadProperties(ImportDtoInfo dtoInfo) throws IOException, ImportParseException;

    /** Догружает прямым достпуом свойства группы объеков из файла, по инф. об рассположении объекта в файле.
     * @param group - метаданные группы доменных объектов: смещение первого байта и последнего от начала файла.
     * @return выполнилась ли загрузка свойств успешно.
     * @throws IOException проблемы при чтении файла.
     * @throws ImportParseException проблема с форматом документа.
     */
    boolean loadPropertiesForGroup(Collection<ImportDtoInfo> group) throws IOException, ImportParseException;
}
