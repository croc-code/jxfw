package ru.croc.ctp.jxfw.transfer.component.exp;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;

import java.io.IOException;
import java.util.function.Function;

/**
 * Фабрика writer'ов для экспорта {@link DomainTo} в файл.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public interface ExportItemFileWriterFactory {

    /**
     * Создаёт writer с именем файла по умолчанию.
     *
     * @return writer.
     * @throws IOException проблемы при работе с файловой системой.
     */
    ExportDomainToWriter create() throws IOException;


    /**
     * Создаёт writer с заданым именем файла.
     *
     * @param fileName имя файла.
     * @return writer.
     * @throws IOException проблемы при работе с файловой системой.
     */
    ExportDomainToWriter create(String fileName) throws IOException;

    /**
     * Создаёт writer, который пишет в диномически формируемое
     * множество файлов. Имя файла в который попадет каждый из объектов задаётся переданной
     * функцией.
     * Внимание: Для получения результирующих файлов необходимо вконце использоватб шаг агрегации.
     *
     * @param function функция вычисляющая имя файла для {@link DomainTo}
     * @return writer.
     */
    ExportDomainToWriter create(Function<DomainTo, String> function);
}
