package ru.croc.ctp.jxfw.core.domain;

import org.springframework.data.domain.Persistable;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Интерфейс для доменного объекта.
 *
 * @param <ID> - тип для первичного ключа.
 */
public interface DomainObject<ID extends Serializable> extends Persistable<ID>, Serializable { //, Identity<ID> {

    /**
     * @return Имя типа доменного объекта.
     */
    String getTypeName();

    /**
     * Установка идентификатора.
     *
     * @param id - идентификатор
     */
    void setId(ID id);

    /**
     * Установка параметра isNew.
     *
     * @param isnew - флаг новый ли объект.
     */
    void setNew(final boolean isnew);

    /**
     * Установка параметра removed.
     *
     * @param removed - флаг необходимости удаления объекта.
     */
    void setRemoved(Boolean removed);

    /**
     * @return Проверка, того что объект удаляется.
     */
    Boolean isRemoved();

    /**
     * @return Информация отправленная из WC, в которой хранятся измененные значения.
     */
    Map<String, Object> getPropChangedValues();

    /**
     * @param original - Информация отправленная из WC, в которой хранятся измененные значения.
     */
    void setPropChangedValues(Map<String, Object> original);

    /**
     * Получить значение навигируемого свойста от объекта по имени.
     *
     * @param name имя свойства (совпдаетает с названием field)
     * @return список значений
     */
    List<? extends DomainObject<?>> obtainValueByPropertyName(final String name);


    /**
     * Метаданные объекта.
     * @return метаданные.
     */
    XfwClass getMetadata();

}
