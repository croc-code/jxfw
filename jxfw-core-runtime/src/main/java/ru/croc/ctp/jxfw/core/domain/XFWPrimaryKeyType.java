package ru.croc.ctp.jxfw.core.domain;

/**
 * Тип ключа.
 */
public enum XFWPrimaryKeyType {
    /**
     * для колонки, которая является частью партиционного ключа.
     */
    PARTITIONED,

    /**
     * для колонки, которая является частью кластерного ключа.
     */
    CLUSTERED;
}
