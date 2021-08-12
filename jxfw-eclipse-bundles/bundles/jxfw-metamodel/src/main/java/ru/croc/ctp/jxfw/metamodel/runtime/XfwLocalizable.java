package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.Set;

/**
 * Локализуемое представление классификатора метамодели.
 */

public interface XfwLocalizable {


    /**
     * Получить локализованное имя доменного типа на заданном языке.
     * @param lang - код языка
     * @return - локализованное имя.
     */
    String getLocalizedTypeName(String lang);




    /**
     * Получить локализованное имя свойства на заданном языке.
     * @param fieldName - имя поля
     * @param lang - код языка
     * @return - локализованное имя.
     */
    String getLocalizedFieldName(String fieldName, String lang);

    /**
     * Набор языков, существующих в данной модели.
     * @return - набор языков.
     */
    Set<String> getAvailableLanguages();


}
