package ru.croc.ctp.jxfw.core.exception.exceptions;


/**
 * Исключение, говорящее о нарушении правил безопасности.
 *
 * @author SMufazzalov от 07.04.2016.
 * @author OKrutova
 * @since jXFW 1.2.0
 */
public class XSecurityException extends XBusinessLogicException {

    /**
     *
     */
    private static final long serialVersionUID = -5516126243329042796L;

    /**
     * Конструктор.
     *
     * @param userName логин пользователя.
     * @param cause    причина
     */
    public XSecurityException(String userName, Throwable cause) {
        super(new Builder<>()
                .addArgument(userName)
                .cause(cause));
    }

    /**
     * Конструктор.
     *
     * @param cause   причина.
     * @param message сообщение
     */
    public XSecurityException(Throwable cause, String message) {
        super(new Builder<>(null, message)
                .cause(cause));
    }

    /**
     * Конструктор.
     *
     * @param userName логин пользователя.
     */
    public XSecurityException(String userName) {
        super(new Builder<>()
                .addArgument(userName));
    }


    /**
     * Конструктор.
     *
     * @param builder - билдер.
     */
    protected XSecurityException(Builder<?> builder) {
        super(builder);
    }


    /**
     * Билдер исключения XSecurityException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XBusinessLogicException.Builder<T> {


        /**
         * Конструктор.
         *
         * @param bundleCode     идентификатор ресурсов с сообщением или шаблоном сообщения об ошибке
         * @param defaultMessage сообщение или шаблон сообщения. Используется, если
         *                       идентификтаор ресурса не задан или ресурс не найден.
         */
        public Builder(String bundleCode, String defaultMessage) {
            super(bundleCode, defaultMessage);
        }

        /**
         * Конструктор.
         */
        public Builder() {
            super("ru.croc.ctp.jxfw.core.exception.exceptions.XSecurityException.message",
                    "Security error for user: {0}.");
        }


        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XSecurityException build() {
            return new XSecurityException(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }


    }
}