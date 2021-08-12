package ru.croc.ctp.jxfw.reporting.xslfo.exception;

/**
 * Исключение, используемое репорт сервисом для генерации сообщений об ошибках.
 * Created by vsavenkov on 21.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ReportException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    /**
     * Конструктор.
     * @param message сообщение исключения
     */
    public ReportException(String message) {
        super(message);
    }

    /**
     * Конструктор.
     * @param message сообщение исключения
     * @param cause причина
     */
    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}