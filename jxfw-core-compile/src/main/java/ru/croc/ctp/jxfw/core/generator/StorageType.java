package ru.croc.ctp.jxfw.core.generator;

/**
 * Тип хранилища.
 *
 */
public enum StorageType {
    /**
     * Тип хранилища не определен.
     */
    NONE,
    /**
     * Реляционная СУБД с доступом через JPA.
     */
    JPA,
    /**
     * Cassandra.
     */
    CASS,
    /**
     * Solr.
     */
    SOLR,
    /**
     * CMIS.
     */
    CMIS
}
