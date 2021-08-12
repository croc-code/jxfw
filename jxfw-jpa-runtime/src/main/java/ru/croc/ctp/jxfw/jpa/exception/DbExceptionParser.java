package ru.croc.ctp.jxfw.jpa.exception;

/**
 * Сервис разбора исключения, специфичного для конкретной СУБД типа.
 */
public interface DbExceptionParser {

    /**
     * Разобрать исключение.
     *
     * @param throwable исключение
     * @return описание исключения, сформированное в результате обработки, либо null, если его обработать не удалось
     */
    DbExceptionDescriptor parse(Throwable throwable);

}
