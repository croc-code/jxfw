package ru.croc.ctp.jxfw.core.exception.exceptions;


/**
 * Описание ошибки отсутствия доменного объекта в хранилище.
 * должно генерироваться.
 * <ol>
 * <li>не найден объект
 * <li>сохранется навигируемое свойство которого нет в базе (значение навигируемого свойства это oid или список oid)
 * <li>попытка достать файл с несуществующим id (_file/binaryPropValue)
 * </ol>
 *
 * @author SMufazzalov от 04.03.2016.
 * @author OKrutova
 * @since 1.1
 */
public class XObjectNotFoundException extends XInvalidDataException {

    private static final long serialVersionUID = -3063475015606605620L;


    /**
     * Конструктор.
     *
     * @param typeName Тип потомка DomainObject.
     * @param id       Идентификатор объекта
     */
    public XObjectNotFoundException(String typeName, Object id) {
        super(new Builder<>()
                .identity(typeName, id));
    }

    /**
     * Конструктор.
     *
     * @param builder - билдер.
     */
    protected XObjectNotFoundException(Builder<?> builder) {
        super(builder);
    }


    /**
     * Билдер исключения XObjectNotFoundException.
     *
     * @param <T> билдер исключений.
     */
    public static class Builder<T extends Builder<T>> extends XInvalidDataException.Builder<T> {


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
            super("ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException.message",
                    "Object {0} with identifier {1} not found.");
        }


        /**
         * Построить исключние.
         *
         * @return исключение
         */
        public XObjectNotFoundException build() {
            return new XObjectNotFoundException(this);
        }

        @Override
        @SuppressWarnings("unchecked")
        protected T getThis() {
            return (T) this;
        }


    }


}
