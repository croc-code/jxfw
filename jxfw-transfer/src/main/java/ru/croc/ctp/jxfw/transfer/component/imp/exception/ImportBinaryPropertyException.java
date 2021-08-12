package ru.croc.ctp.jxfw.transfer.component.imp.exception;

/**
 * Ошибка импорта бинарного свойства.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class ImportBinaryPropertyException extends RuntimeException {
    /**
     *  Ошибка импорта бинарного свойства.
     *
     * @param message сообщние
     * @param cause исключение.
     */
    public ImportBinaryPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *  Ошибка импорта бинарного свойства.
     *
     * @param cause исключение.
     */
    public ImportBinaryPropertyException(Throwable cause) {
        super(cause);
    }
}
