package ru.croc.ctp.jxfw.fulltext.generator

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.Target
import org.eclipse.xtend.lib.macro.Active
import ru.croc.ctp.jxfw.fulltext.generator.solr.SearchType

/**
 * FullText - Объект, сохраняемый в хранилище. Возможно описание как через единую модель, для работы c базовым
 * хранилищем + fulltext, так и для использование fulltext как базового noSql хранилища.
 * Автоматически генерируются Spring Data репозиторий, сервисы и контроллер.
 *
 * А также генерируются JavaScript модель и i18n-ресурсы.
 *
 * @author SMufazzalov
 * @since 1.4
 */
@Target(ElementType.TYPE)
@Active(XFWSearchClassProcessor)
@Retention(RUNTIME)
public annotation XFWSearchClass {

    boolean isReadonly = false

    PersistenceType persistence = PersistenceType.FULL
    /**
    * Имя таблицы (ядра), в полнотекстовом хранилище.
    *
    * @return идентификатор, например "calculated_data_view".
    */
    String name = ""

    /**
     * Выбор хранилища по умолчанию, на чтение.
     *
     * @return {@link SearchDataStore}
     */
    SearchDataStore defaultSearchDataStore = SearchDataStore.FULL_TEXT

    /**
     * Перечисление, выбор хранилища для чтения по умолчанию.
     */
    enum SearchDataStore {
        /**
         * Чтение из базового хранилищу по умолчанию.
         */
        BASE,
        /**
         * Чтение из полнотекстового хранилища по умолчанию.
         */
        FULL_TEXT
    }

    /**
      * FULL - объекты типа свойств загружаются и сохраняются в хранилище.
      * TRANSIENT - объекты типа свойств не загружаются и не сохраняются в хранилище.
      *             Также в хранилище не создаются соответствующие структуры хранения (таблицы и столбцы в БД).
      */
    enum PersistenceType {
        /**
         * объекты типа свойств загружаются и сохраняются в хранилище.
         */
        FULL,
        /**
         * объекты типа свойств не загружаются и не сохраняются в хранилище.
         * Также в хранилище не создаются соответствующие структуры хранения (таблицы и столбцы в БД).
         */
        TRANSIENT
    }

    SearchType dbSearchType = SearchType.DSE
    /**
   * Тип БД. Используем параметр для игнорирования @XFWPrimaryKey
   * в JPA. Обработкой занимается SolrEntityPopulator.
   * Временное решение.
   *
   * SOLR - игнорим XFWPrimaryKey - jpa бд
   * DSE - для касандры
    */
    /**
    * Выбор типа БД по умолчанию, касандра.
    *
    * @return {@link dbSearchType}
    */
    /*enum SearchType {
        DSE,
        SOLR
    }*/

    /**
    Номер версии для автоматизации переиндексации
    format x.xxx
    */
    double version = 0.1
}