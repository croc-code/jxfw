package ru.croc.ctp.jxfw.core.localization;

import java.text.MessageFormat;
import java.util.Arrays;


/**
 * Класс обеспечивает формирование сообщений по шаблону
 * и их локализацию.
 *
 * @author OKrutova
 * @since 1.6
 */
public class XfwMessageTemplate {

    private final String defaultMessage;
    private final String bundleCode;
    private final String[] arguments;

    /**
     * Конструктор.
     *
     * @param bundleCode     идентификатор ресурсов с сообщением или шаблоном сообщения об ошибке
     * @param defaultMessage сообщение или шаблон сообщения. Используется, если
     *                       идентификтаор ресурса не задан или ресурс не найден.
     * @param arguments      аргументы для подстановки в шаблон.
     */
    public XfwMessageTemplate(String bundleCode, String defaultMessage, String... arguments) {
        this.defaultMessage = defaultMessage;
        this.bundleCode = bundleCode;
        this.arguments = arguments;
    }


    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getBundleCode() {
        return bundleCode;
    }

    public String[] getArguments() {
        return arguments;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        XfwMessageTemplate that = (XfwMessageTemplate) obj;

        if (defaultMessage != null ? !defaultMessage.equals(that.defaultMessage) : that.defaultMessage != null) {
            return false;
        }
        if (bundleCode != null ? !bundleCode.equals(that.bundleCode) : that.bundleCode != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = defaultMessage != null ? defaultMessage.hashCode() : 0;
        result = 31 * result + (bundleCode != null ? bundleCode.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }


    /**
     * Формирование плейсхолдера для имени типа доменного объекта.
     *
     * @param typeName тип (полное или коротокое имя типа)
     * @return плейсхолдер вида [ru.croc.ctp.domain.User]
     */
    public static String formatTypeNamePlaceholder(String typeName) {
        return MessageFormat.format("[{0}]", typeName);
    }

    /**
     * Формирование плейсхолдера для имени поля доменного объекта.
     *
     * @param typeName     тип (полное или коротокое имя типа)
     * @param propertyName имя поля доменного объекта
     * @return плейсхолдер вида [ru.croc.ctp.domain.User#login]
     */
    public static String formatPropertyNamePlaceholder(String typeName, String propertyName) {
        return MessageFormat.format("[{0}#{1}]", typeName, propertyName);
    }


    @Override
    public String toString() {
        return "XfwMessageTemplate{"
                + "defaultMessage='" + defaultMessage + '\''
                + ", bundleCode='" + bundleCode + '\''
                + ", arguments=" + Arrays.toString(arguments)
                + '}';
    }
}
