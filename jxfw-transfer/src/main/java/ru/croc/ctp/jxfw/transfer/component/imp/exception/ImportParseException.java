package ru.croc.ctp.jxfw.transfer.component.imp.exception;

/**
 * Проблема при парсинге файла импорта.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class ImportParseException extends Exception {

    /** Проблема при парсинге файла импорта. */
    public ImportParseException() {
    }

    /** Проблема при парсинге файла импорта.
     * @param message сообщение об ошибке.
     */
    public ImportParseException(String message) {
        super(message);
    }

    /** Проблема при парсинге файла импорта.
     * @param message сообщение об ошибке.
     * @param cause вкладываемое исключение.
     */
    public ImportParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Проблема при парсинге файла импорта.
     * @param cause вкладываемое исключение.
     */
    public ImportParseException(Throwable cause) {
        super(cause);
    }
}
