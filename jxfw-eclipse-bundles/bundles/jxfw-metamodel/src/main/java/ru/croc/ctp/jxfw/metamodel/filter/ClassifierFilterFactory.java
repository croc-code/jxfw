package ru.croc.ctp.jxfw.metamodel.filter;

import java.util.Map;
import java.util.Set;

/**
 * Фабрика фильтров метаданных.
 */
public interface ClassifierFilterFactory {

    /**
     * Построить фильтр.
     *
     * @param type  тип фильтра @see ClassifierFilterType
     * @param value фильтрующее значение
     * @return фильтр\
     * @throws IllegalArgumentException если получен неизвестный тип фильтра.
     */
    ClassifierFilter createFilter(String type, String value);

    /**
     * Построить набор фильтров.
     *
     * @param filters - map:  тип фильтра @see ClassifierFilterType - фильтрующее значение
     * @return набор фильтров
     * @throws IllegalArgumentException если получен неизвестный тип фильтра.
     */
    Set<ClassifierFilter> createFilters(Map<String, String> filters);


    /**
     * Построить набор фильтров.
     *
     * @param classes       имена классов для фильтрации
     * @param models        имена файлов моделей для фильтрации
     * @param regexps       регулярные выражения для фильтрации
     * @param defaultFilter фильтр по умолчанию, используется, если по предыдущим параметрам никакие фильтры не созданы
     * @return набор фильтров
     */
    Set<ClassifierFilter> createFilters(String[] classes, String[] models, String[] regexps,
                                        ClassifierFilter defaultFilter);

}
