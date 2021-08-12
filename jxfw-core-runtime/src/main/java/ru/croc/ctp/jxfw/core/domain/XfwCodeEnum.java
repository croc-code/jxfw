package ru.croc.ctp.jxfw.core.domain;

/**
 * Интерфейс, который реализует Enumeration с одним свойством code.
 *
 * @param <T> - Тип свойства code
 * @author AKogun
 */
public interface XfwCodeEnum<T> {

    /**
     * @return зачение code соответствующий константе.
     */
    T getCode();

}
