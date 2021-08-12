package ru.croc.ctp.jxfw.reporting.xslfo.renderer.excel;

/**
 * Класс, переопределящий аргументы события.
 * Created by vsavenkov on 10.08.2017. Import from $/Крок.R-and-D.ReportRenderers/Sources/Repo-1_1.
 */
public class XslFoEventArgs {

    /**
     * Сообщение.
     */
    private String message;

    /**
     * Конструктор.
     * @param message - сообщение
     */
    public XslFoEventArgs(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
