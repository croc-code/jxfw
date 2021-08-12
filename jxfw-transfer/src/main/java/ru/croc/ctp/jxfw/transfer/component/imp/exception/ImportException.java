package ru.croc.ctp.jxfw.transfer.component.imp.exception;

/**
 * Проблема при импорте файла.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class ImportException extends RuntimeException {

    /** Проблема при импорте файла. */
    public ImportException() {
    }

    /** Проблема при импорте файла.
     * @param message сообщение об ошибке.
     */
    public ImportException(String message) {
        super(message);
    }

    /** Проблема при импорте файла.
     * @param message сообщение об ошибке.
     * @param cause вкладываемое исключение.
     */
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Проблема при импорте файла.
     * @param cause вкладываемое исключение.
     */
    public ImportException(Throwable cause) {
        super(cause);
    }
}
