package ru.croc.ctp.jxfw.metamodel.filter;

/**
 * Типы фильтров метаданных.
 *
 * @author OKrutova
 * @since 1.6
 */
public enum ClassifierFilterType {
    /**
     * Фильтр по полному или короткому имени класса.
     */
    className,


    /**
     * Фильтр по имени файла модели.
     */
    modelName,

    /**
     * Фильтр по маске, применяемой к полному имени класса.
     */
    regexp
}
