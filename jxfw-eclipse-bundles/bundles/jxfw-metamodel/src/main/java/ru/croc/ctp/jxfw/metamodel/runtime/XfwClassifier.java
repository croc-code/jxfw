package ru.croc.ctp.jxfw.metamodel.runtime;


/**
 * Классификатор в метамодели.
 */
public interface XfwClassifier extends XfwNamedElement {


    /* EClassifier methods*/

    /**
     * Полное имя реализующего класса.
     * @return имя
     */
    String getInstanceClassName();


    /**
     * Реализующий класс.
     * @return класс
     */
    Class<?> getInstanceClass();


    /**
     * Значение по умолчанию.
     * @return объект
     */
    Object getDefaultValue();

    /**
     * Имя реализующего типа.
     * @return имя
     */
    String getInstanceTypeName();

    //EList<ETypeParameter> getETypeParameters();

    /**
     * Объект реализует тип данного классификатора.
     * @param var1 объект
     * @return да\нет
     */
    boolean isInstance(Object var1);

    //int getClassifierID();


}
