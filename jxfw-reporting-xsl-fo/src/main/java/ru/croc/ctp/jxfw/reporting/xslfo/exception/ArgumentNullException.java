package ru.croc.ctp.jxfw.reporting.xslfo.exception;

/**
 * Класс, реализующий форматирование сообщения о незаданном значении параметра.
 * Created by vsavenkov on 14.02.2017.
 *
 * @since jXFW 1.6.0
 */
public class ArgumentNullException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;
    /**
     * Конструктор.
     * @param paramName - Имя параметра, вызвавшего текущее исключение
     */
    public ArgumentNullException(String paramName) {
        super(String.format("Parameter %1$s is null!", paramName));
    }


}
