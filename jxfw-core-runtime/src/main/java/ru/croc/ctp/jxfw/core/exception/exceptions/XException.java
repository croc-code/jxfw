package ru.croc.ctp.jxfw.core.exception.exceptions;

import static com.google.common.collect.Lists.newArrayList;

import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Базовый класс всех исключений jXFW.
 *
 * @author SMufazzalov от 07.04.2016.
 * @since jXFW 1.2.0
 */
public class XException extends RuntimeException {
    /**
     * ключ "имя компьютера" из переменных окружения.
     */
    public static final String COMPUTERNAME = "COMPUTERNAME";
    /**
     * ключ "имя хоста" из переменных окружения.
     */
    public static final String HOSTNAME = "HOSTNAME";


    private static final long serialVersionUID = -8853350213105618867L;
    private final XfwMessageTemplate messageTemplate;
    private String sourceMachineName = getComputerName();
    private Map<String, String> data = new HashMap<>();
    private String helpLink;
    /**
     * Признак того, что исключение содержит "культурный" текст, который
     * можно показать пользователю, что не случилось ничего страшного,
     * и показывать пользователю отладочное сообщение не нужно.
     */
    private boolean containsUserDescription;
    /**
     * Уникальный идентификатор записи в логе машины, на которой возникло исключение. Передается клиенту.
     */
    private String sourceLogEntryUniqueId;

    /**
     * Конструктор.
     *
     * @param message сообщение об ошибке.
     */
    public XException(String message) {
        this(new Builder<>(null, message));

    }

    /**
     * Конструктор.
     *
     * @param message сообщение об ошибке.
     * @param cause   причина которая сохраняется для последуещего извлечения через {@link #getCause()}
     */
    public XException(String message, Throwable cause) {
        this(new Builder<>(null, message)
                .cause(cause));
    }

    /**
     * Конструктор.
     *
     * @param message                 сообщение об ошибке.
     * @param cause                   причина которая сохраняется для последуещего извлечения через {@link #getCause()}
     * @param containsUserDescription Признак того, что исключение содержит "культурный" текст
     * @param sourceLogEntryUniqueId  Уникальный идентификатор записи в логе машины
     * @param helpLink                ссылка
     */
    public XException(String message, Throwable cause, boolean containsUserDescription, String sourceLogEntryUniqueId,
                      String helpLink) {
        this(new Builder<>(null, message)
                .cause(cause)
                .containsUserDescription(containsUserDescription)
                .sourceLogEntryUniqueId(sourceLogEntryUniqueId)
                .helpLink(helpLink));
    }

    /**
     * Конструктор.
     *
     * @param builder - билдер.
     */
    protected XException(Builder<?> builder) {
        super(MessageFormat.format(
                builder.defaultMessage != null ? builder.defaultMessage : "",
                builder.arguments.toArray(new Object[]{})),
                builder.cause);
        this.containsUserDescription = builder.containsUserDescription;
        this.sourceLogEntryUniqueId = builder.sourceLogEntryUniqueId;
        this.helpLink = builder.helpLink;
        this.messageTemplate
                = new XfwMessageTemplate(builder.bundleCode,
                builder.defaultMessage,
                builder.arguments.toArray(new String[0]));
    }

    public XfwMessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    /**
     * Физическое имя машины, на которой возникло исключение. Передается клиенту.
     *
     * @return String имя компьютера на котором произошла ошибка, либо "Unknown Computer" если информация не доступна
     */
    public String getSourceMachineName() {
        return sourceMachineName;
    }

    /**
     * Дополнительная информация об источнике.
     *
     * @return String "Croc.JXFW.Server"
     */
    public String getSource() {
        return "Croc.JXFW.Server";
    }

    /**
     * Контейнер информации.
     *
     * @return Map
     */
    public Map<String, String> getData() {
        return data;
    }

    public String getHelpLink() {
        return helpLink;
    }

    public boolean isContainsUserDescription() {
        return containsUserDescription;
    }

    public String getSourceLogEntryUniqueId() {
        return sourceLogEntryUniqueId;
    }

    private String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey(COMPUTERNAME)) {
            return env.get(COMPUTERNAME);
        } else if (env.containsKey(HOSTNAME)) {
            return env.get(HOSTNAME);
        } else {
            return "Unknown Computer";
        }
    }

    /**
     * Список классов исключений, родительских для данного исключения.
     * @return список родительских классов
     */
    public List<String> getParentClasses() {
        List<String> result = newArrayList();
        Class<?> clazz = this.getClass().getSuperclass();
        while (XException.class.isAssignableFrom(clazz)) {
            result.add(clazz.getSimpleName());
            clazz = clazz.getSuperclass();
        }

        return result;
    }

    /**
     * Сформировать транспортный объект.
     *
     * @param resolver резолвер шаблона
     * @param locale   требуемая локаль
     * @return DTO
     */
    public XExceptionTo toTo(XfwMessageTemplateResolver resolver, Locale locale) {
        return new XExceptionTo(this, resolver, locale);
    }

    /**
     * Билдер исключений.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> {
        // Required parameters
        private final String defaultMessage;
        private final String bundleCode;
        // Optional parameters - initialized to default values
        private Throwable cause;
        private String helpLink;
        private boolean containsUserDescription;
        private String sourceLogEntryUniqueId;
        private LinkedList<String> arguments = new LinkedList<>();


        /**
         * Конструктор.
         *
         * @param bundleCode     идентификатор ресурсов с сообщением или шаблоном сообщения об ошибке
         * @param defaultMessage сообщение или шаблон сообщения. Используется, если
         *                       идентификтаор ресурса не задан или ресурс не найден.
         */
        public Builder(String bundleCode, String defaultMessage) {
            this.defaultMessage = defaultMessage;
            this.bundleCode = bundleCode;
        }


        /**
         * Установить причину исключения.
         *
         * @param cause причина
         * @return билдер
         */
        public T cause(Throwable cause) {
            this.cause = cause;
            return getThis();
        }


        /**
         * Установить Признак того, что исключение содержит "культурный" текст, который
         * можно показать пользователю, что не случилось ничего страшного,
         * и показывать пользователю отладочное сообщение не нужно..
         *
         * @param containsUserDescription Признак
         * @return билдер
         */
        public T containsUserDescription(boolean containsUserDescription) {
            this.containsUserDescription = containsUserDescription;
            return getThis();
        }


        /**
         * Установить ссылку.
         *
         * @param helpLink ссылка
         * @return билдер
         */
        public T helpLink(String helpLink) {
            this.helpLink = helpLink;
            return getThis();
        }

        /**
         * Установить Уникальный идентификатор записи в логе машины,
         * на которой возникло исключение. Передается клиенту..
         *
         * @param sourceLogEntryUniqueId Уникальный идентификатор записи в логе машины, на которой возникло исключение.
         * @return билдер
         */
        public T sourceLogEntryUniqueId(String sourceLogEntryUniqueId) {
            this.sourceLogEntryUniqueId = sourceLogEntryUniqueId;
            return getThis();
        }

        /**
         * Добавить аргумент для подстановки в шаблон.
         *
         * @param argument аргумент
         * @return билдер
         */
        public T addArgument(Object argument) {
            this.arguments.add(String.valueOf(argument));
            return getThis();
        }

        /**
         * Добавить аргумент в начало списка аргументов для подстановки в шаблон.
         *
         * @param argument аргумент
         * @return билдер
         */
        protected T pushArgument(Object argument) {
            this.arguments.push(String.valueOf(argument));
            return getThis();
        }

        /**
         * Удалить первый аргумент из списка аргументов.
         *
         * @return билдер
         */
        protected T popArgument() {
            this.arguments.pop();
            return getThis();
        }

        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }

        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XException build() {
            return new XException(this);
        }
    }

}
