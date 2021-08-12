package ru.croc.ctp.jxfw.transfer.component.exp.exception;

/**
 * Ошибка экспорта бинарного свойства.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class ExportBinaryPropertyException extends RuntimeException {
    /**
     *  Ошибка экспорта бинарного свойства.
     *
     * @param message сообщние
     * @param cause исключение.
     */
    public ExportBinaryPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *  Ошибка экспорта бинарного свойства.
     *
     * @param cause исключение.
     */
    public ExportBinaryPropertyException(Throwable cause) {
        super(cause);
    }
}
