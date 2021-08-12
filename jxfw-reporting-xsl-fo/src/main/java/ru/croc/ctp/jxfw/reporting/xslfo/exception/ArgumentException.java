package ru.croc.ctp.jxfw.reporting.xslfo.exception;

/**
 * Класс, реализующий форматирование сообщения о некорректном значении параметра.
 * Created by vsavenkov on 14.02.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ArgumentException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    /**
     * Имя параметра.
     */
    protected String paramName;

    /**
     * Конструктор.
     */
    public ArgumentException() {
    }

    /**
     * Конструктор.
     * @param message   - Сообщение об ошибке с объяснением причины исключения
     */
    public ArgumentException(String message) {
        new ArgumentException(paramName, null);
    }

    /**
     * Конструктор.
     * @param message   - Сообщение об ошибке с объяснением причины исключения
     * @param paramName - Имя параметра, вызвавшего текущее исключение
     */
    public ArgumentException(String message, String paramName) {
        new ArgumentException(paramName, message, null);
    }

    /**
     * Конструктор.
     * @param message   - Сообщение об ошибке с объяснением причины исключения
     * @param paramName - Имя параметра, вызвавшего текущее исключение
     * @param cause     - Вложенное исключение
     */
    public ArgumentException(String message, String paramName, Throwable cause) {
        super(String.format("Parameter %1$s is invalid! %2$s.", paramName, message), cause);
        this.paramName = paramName;
    }
}
