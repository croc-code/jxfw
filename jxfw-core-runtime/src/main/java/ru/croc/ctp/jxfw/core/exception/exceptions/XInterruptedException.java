package ru.croc.ctp.jxfw.core.exception.exceptions;


/**
 * Исключение при прерывании потока с кодом jxfw.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
public class XInterruptedException extends XException {

    /**
     * Исключение при прерывании потока с кодом jxfw.
     *
     * @param message сообщение.
     */
    public XInterruptedException(String message) {
        super(message);
    }
}
