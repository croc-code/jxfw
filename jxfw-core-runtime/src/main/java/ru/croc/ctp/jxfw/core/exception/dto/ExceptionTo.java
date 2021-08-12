package ru.croc.ctp.jxfw.core.exception.dto;


import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

/**
 * ТО для объекта-исключения содержащее подробную информацию.
 * Передается в WebClient.
 *
 * @author Nosov Alexander
 * @see ExceptionToSerializer
 * @see ExceptionToBuilder - билдер для DSL
 * @since 1.1
 */
public class ExceptionTo {

    /**
     * сообщение об ошибке.
     */
    protected final String message;

    /**
     * имя класса ошибки.
     */
    protected final String className;

    /**
     * стектрейс ошибки.
     */
    protected final String stackTrace;


    private final List<String> parentClasses;

    /**
     * Конструктор.
     *
     * @param message    - сообщение об ошибке
     * @param className  - имя класса ошибки
     * @param stackTrace - стектрейс ошибки
     */
    public ExceptionTo(String message, String className, String stackTrace) {
        this.message = message;
        this.className = className;
        this.stackTrace = stackTrace;
        this.parentClasses = newArrayList();
    }
    /**
     * Конструктор.
     *
     * @param message    - сообщение об ошибке
     * @param className  - имя класса ошибки
     * @param stackTrace - стектрейс ошибки
     * @param parentClasses список имен родительских классов данного исключения
     */
    public ExceptionTo(String message, String className, String stackTrace, List<String> parentClasses) {
        this.message = message;
        this.className = className;
        this.stackTrace = stackTrace;
        this.parentClasses = parentClasses;
    }

    public String getMessage() {
        return message;
    }

    public String getClassName() {
        return className;
    }


    public List<String> getParentClasses() {
        return parentClasses;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    @Override
    public String toString() {
        return "ExceptionTo [message=" + message + ", className=" + className + ", stackTrace=" + stackTrace + "]";
    }

    /**
     * Билдер для констурирвоания объекта {@link ExceptionTo}.
     */
    public static class ExceptionToBuilder {

        private String message;

        private String className;

        private String stackTrace;

        /**
         * Билдер.
         * @return создаем новый билдр {@link ExceptionToBuilder}.
         */
        public static ExceptionToBuilder create() {
            return new ExceptionToBuilder();
        }

        /**
         * Добавить текст сообщения в объект {@link ExceptionTo}.
         *
         * @param message - текст сообщения в объект
         * @return билдр {@link ExceptionToBuilder}
         */
        public ExceptionToBuilder message(String message) {
            this.message = message == null ? "" : message;
            return this;
        }

        /**
         * Добавить имя класса ошибки в объект {@link ExceptionTo}.
         *
         * @param className - имя класса
         * @return билдр {@link ExceptionToBuilder}
         */
        public ExceptionToBuilder className(String className) {
            this.className = className;
            return this;
        }

        /**
         * Добавить стектрейс в объект {@link ExceptionTo}.
         *
         * @param stackTrace - стектрейс фрейма
         * @return билдр {@link ExceptionToBuilder}
         */
        public ExceptionToBuilder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        /**
         * Создать новый объект на основе данных внесенных в билдер.
         *
         * @return новый объект {@link ExceptionTo}.
         */
        public ExceptionTo build() {
            return new ExceptionTo(message, className, stackTrace);
        }
    }
}
