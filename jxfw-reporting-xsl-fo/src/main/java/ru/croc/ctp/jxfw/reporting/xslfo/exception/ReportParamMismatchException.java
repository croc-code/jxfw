package ru.croc.ctp.jxfw.reporting.xslfo.exception;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ArgumentException;

/**
 * Исключение о неверном типе параметра отчета.
 * Created by vsavenkov on 02.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class ReportParamMismatchException extends ArgumentException {
    private static final long serialVersionUID = 1L;
    /**
     * Значение параметра.
     */
    protected String paramValue;

    /**
     * Конструктор исключения.
     * @param message сообщение исключения
     * @param paramName наименование параметра, приведшего к исключению<
     * @param paramValue значение параметра
     * @param inner вложенное исключение
     */
    public ReportParamMismatchException(String message, String paramName, String paramValue, Exception inner) {
        super(message, paramName, inner);
        this.paramValue = paramValue;
    }

    /**
     * Конструктор исключения.
     * @param paramName     - Наименование параметра, приведшего к исключению
     * @param paramValue    - Значение параметра
     * @param inner         - Вложенное исключение
     */
    public ReportParamMismatchException(String paramName, String paramValue, Exception inner) {
        this("ReprortParamMismatchException", paramName, paramValue, inner);
    }

    /**
     * Конструктор исключения.
     * @param paramName     - Наименование параметра, приведшего к исключению
     * @param paramValue    - Значение параметра
     */
    public ReportParamMismatchException(String paramName, String paramValue) {
        this(paramName, paramValue, null);
    }

    /**
     * Конструктор исключения.
     * @param paramName - Наименование параметра, приведшего к исключению
     */
    public ReportParamMismatchException(String paramName) {
        this(paramName, null, null);
    }

    /**
     * Отображение в виде строки.
     * @return String - Строка описания ошибки
     */
    @Override
    public String toString() {
        return "paramName=" + paramName + "\nparamValue=" + paramValue + "\n\n" + super.toString();
    }
}
