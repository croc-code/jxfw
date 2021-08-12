package ru.croc.ctp.jxfw.core.domain;

import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Интерфейс для правильного преобразования JSON -> Java Object
 * комплексного типа. Используется при генерации объектов доменной модели из xtend
 *
 * @author Nosov Alexander
 * @see ru.croc.ctp.jxfw.core.generator.XFWComplexType
 */
public interface ComplexType extends Serializable {

    /**
     * Получить все поля комплексного класса, по префиксу.
     * Это необходимо так как все поля храняться в плоской Map.
     *
     * @param prefix - префикс поля.
     * @return отображение с полями и значениями этих полей.
     */
    Map<String, Object> getAllFields(String prefix);

    /**
     * Установить свойства во внутренную Map.
     *
     * @param prefix - префикс для установки свойства
     * @param name   - имя свойства
     * @param value  - значение свойства
     */
    void setProperty(final String prefix, final String name, final Object value);

    /**
     * Создать комплексный тип.
     *
     * @param prefix - префикс для установки свойства
     * @param props свойства комплексного типа, объект класса {@link Map}
     * @return объект класса {@link ComplexType}.
     */
    @Nonnull
    ComplexType createComplexType(@Nonnull String prefix, @Nonnull HashMap<String, Object> props);

    /**
     * Метаданные типа.
     * @return метаданные.
     */
    XfwClass getMetadata();

}
