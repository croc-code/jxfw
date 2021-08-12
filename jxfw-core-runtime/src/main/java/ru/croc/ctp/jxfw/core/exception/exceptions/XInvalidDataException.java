package ru.croc.ctp.jxfw.core.exception.exceptions;

import static ru.croc.ctp.jxfw.core.localization.XfwMessageTemplate.formatTypeNamePlaceholder;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.ClassUtils;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;
import ru.croc.ctp.jxfw.core.domain.Identity;
import ru.croc.ctp.jxfw.core.exception.dto.XExceptionTo;
import ru.croc.ctp.jxfw.core.exception.dto.XInvalidDataExceptionTo;
import ru.croc.ctp.jxfw.core.localization.XfwMessageTemplateResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Исключение генерируется в случаях некорректных входных данных.
 * Например попытка сохранить readOnly доменный объект.
 *
 * @author SMufazzalov от 07.04.2016.
 * @author OKrutova
 * @since jXFW 1.2.0
 */
public class XInvalidDataException extends XException {

    private static final long serialVersionUID = 7795721024795411818L;

    private Map<String, String> identities = new HashMap<>();

    /**
     * Конструктор.
     *
     * @param message сообщение.
     */
    public XInvalidDataException(String message) {
        this(message, null);
    }

    /**
     * Конструктор.
     *
     * @param message сообщение.
     * @param cause   причина которая сохраняется для последуещего извлечения через {@link #getCause()}
     */
    public XInvalidDataException(String message, Throwable cause) {
        super(message, cause, true, "", "");
    }

    /**
     * Создать новый XInvalidDataException.
     *
     * @param typeName Тип потомка DomainObject
     * @param id       Идентификатор объекта
     * @param message  сообщение. В данном случае сообщение не локализуется,
     *                 а подставляется как аргумент в стандартный шаблон
     *                 ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.message.
     *                 Для обратной совместимости.
     */
    public XInvalidDataException(String typeName, Object id, String message) {

        this(new Builder<>(
                "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.message", null)
                .identity(typeName, id)
                .addArgument(message));
    }

    /**
     * Конструктор.
     *
     * @param builder - билдер.
     */
    protected XInvalidDataException(Builder<?> builder) {
        super(builder);
        this.identities = builder.identities;
    }

    /**
     * Получить человеко-читабельную информацию о ключе.
     *
     * @param id Идентификатор
     * @return String строковый вид идентификатора
     */
    private static String getStringRepresentationOfId(Object id) {
        if (id instanceof String) {
            return (String) id;
        } else if (ClassUtils.isPrimitiveOrWrapper(id.getClass())) {
            return id.toString();
        } else {
            return ToStringBuilder.reflectionToString(id);
        }
    }

    public Map<String, String> getIdentities() {
        return identities;
    }

    @Override
    public XExceptionTo toTo(XfwMessageTemplateResolver resolver, Locale locale) {
        return new XInvalidDataExceptionTo(this, resolver, locale);
    }

    /**
     * Билдер исключения XInvalidDataException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XException.Builder<T> {
        private boolean identitySet = false;
        private Map<String, String> identities = new HashMap<>();

        /**
         * Конструткор с сообщение по умолчанию.
         */
        public Builder() {
            super("ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.message.default",
                    "Incorrect input for object {0} with identifier {1}.");
        }


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
         * Установить идентифицирующую информацию доменного объекта.
         * Имя типа и идентификатор всегда становятся первым и вторым аргументами для предоставленного шаблона.
         * Необходимо это учитывать при построениее шаблона сообщений.
         *
         * @param typeName имя типа
         * @param id       идентификатор
         * @return билдер
         */
        public T identity(String typeName, Object id) {
            if (identitySet) {
                popArgument();
                popArgument();
            }
            identitySet = true;
            // тип и ид всегда!! должны быть первыми параметрами в списке
            pushArgument(getStringRepresentationOfId(id));
            pushArgument(formatTypeNamePlaceholder(typeName));
            identities.put("type", typeName);
            identities.put("id", getStringRepresentationOfId(id));
            return getThis();
        }

        /**
         * Установить идентифицирующую информацию доменного объекта.
         * Имя типа и идентификатор всегда становятся первым и вторым аргументами для предоставленного шаблона.
         * Необходимо это учитывать при построениее шаблона сообщений.
         *
         * @param identity идентифицирующая информация доменного объекта
         * @return билдер
         */
        public T identity(Identity<?> identity) {
            identity(identity.getTypeName(), identity.getId());
            return getThis();
        }

        /**
         * Установить идентифицирующую информацию доменного объекта.
         * Имя типа и идентификатор всегда становятся первым и вторым аргументами для предоставленного шаблона.
         * Необходимо это учитывать при построениее шаблона сообщений.
         *
         * @param domainObject доменный объект
         * @return билдер
         */
        public T identity(DomainObject<?> domainObject) {
            identity(new DomainObjectIdentity<>(domainObject));
            return getThis();
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }

        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XInvalidDataException build() {
            return new XInvalidDataException(this);
        }

    }

}
