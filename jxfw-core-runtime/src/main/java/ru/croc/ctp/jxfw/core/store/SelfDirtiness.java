package ru.croc.ctp.jxfw.core.store;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Интерфейс, реализуемый классами доменных объектов, 
 * позволяет получить данные о значении измененных 
 * в процессе работы конвейера сохранения полей доменного объекта.
 * 
 * Реализация применяется при проверке состояния ReadOnly доменных объектов.
 * 
 * {@link ru.croc.ctp.jxfw.core.domain.DomainObject}, 
 * {@link StoreService}, 
 * {@link StoreContext}
 * 
 * @author AKogun
 * @since 1.6
 *
 */
public interface SelfDirtiness extends Serializable {
            
    /**
     * Получение свойств, значения которых были изменены.
     * @return неизменяемый набор наименований свойств
     */
    Set<String> getDirtyAttributes();
    
    /**
     * Проверка наличия изменений в доменном объекте.
     * @return {@code true} в случае, если есть хотя бы одно свойство, значение которого было измененно
     */
    boolean hasDirtyAttributes();
        
    /**
     * Получение исходного значения свойства по его наименованию, 
     * в случае, если значение установлено,
     * для скалярных свойств возвращается список из одного элемента.
     * 
     * @param attribute наименование свойства
     * @return исходное состояние свойства или пустой список
     */
    <T extends Serializable> List<T> getOriginalValue(String attribute);
    
    /**
     * Получение текущего значения свойства по его наименованию, 
     * в случае, если значение установлено,
     * для скалярных свойств возвращается список из одного элемента.
     * 
     * @param attribute наименование свойства
     * @return текущее состояние свойства или пустой список
     */
    <T extends Serializable> List<T> getCurrentValue(String attribute);
}