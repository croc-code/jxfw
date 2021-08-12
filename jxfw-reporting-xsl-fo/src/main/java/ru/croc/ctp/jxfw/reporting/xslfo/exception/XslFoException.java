package ru.croc.ctp.jxfw.reporting.xslfo.exception;

/**
 * Основной класс исключений.
 * Created by vsavenkov on 24.07.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class XslFoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * TODO: в первоисточнике был доступ к Source. Пока просто сохраняю, если понадобится, то надо как-то его выводить.
     * источник.
     */
    private String source;

    /**
     * Конструктор.
     * @param message        - текст сообщения
     * @param source         - источник
     * @param innerException - вложенное исключение
     */
    public XslFoException(String message, String source, Exception innerException) {
        super(message, innerException);
        this.source = source;
    }

    /**
     * Конструктор.
     * @param message - текст сообщения
     * @param source  - источник
     */
    public XslFoException(String message, String source) {
        super(message);
        this.source = source;
    }
}
