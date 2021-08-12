package ru.croc.ctp.jxfw.core.facade.webclient;

/**
 * Информация о поле объекта {@link DomainTo}.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class DomainToProperty {

    private final String typeName;

    private final Type type;

    private DomainToProperty(String typeName, Type type) {
        this.typeName = typeName;
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public Type getType() {
        return type;
    }

    /**
     * @param typeName имя типа поля.
     * @param type     тип поля, простое или навигируемая ссылка на доменный объект.
     * @return объект с информацией о поле
     */
    public static DomainToProperty create(String typeName, Type type) {
        return new DomainToProperty(typeName, type);
    }

    /**
     * Тип поля.
     */
    public enum Type {
        /**
         * Поле простого типа.
         */
        Simple,
        /**
         * Полк доменного объекта.
         */
        DomainObject
    }
}
