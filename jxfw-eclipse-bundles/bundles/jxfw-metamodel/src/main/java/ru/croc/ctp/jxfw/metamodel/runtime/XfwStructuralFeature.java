package ru.croc.ctp.jxfw.metamodel.runtime;


/**
 * Структурный элемент типа в метамодели.
 */
public interface XfwStructuralFeature extends XfwNamedElement {

    /* ETypedElement methods*/


    //boolean isOrdered();


    //boolean isUnique();


    /**
     * Нижняя количественная граница для множественного элемента.
     * @return нижняя граница
     */
    int getLowerBound();

    /**
     * Верхняя количественная граница для множественного элемента.
     * @return верхняя граница
     */
    int getUpperBound();


    /**
     * Элемент множественный.
     * @return да\нет
     */
    boolean isMany();

    /**
     * Элемент обязателен.
     * @return да\нет
     */
    boolean isRequired();

    /**
     * Метаданные типа структурного элемента.
     * @return метаданые
     */
    XfwClassifier getEType();


    //EGenericType getEGenericType();


    /* EStructuralFeature methods*/

    /**
     * Элемент транзиентный.
     * @return да\нет
     */
    boolean isTransient();


    //boolean isVolatile();


    //boolean isChangeable();


    /**
     * Значение по умолчанию.
     * @return стоковое представление
     */
    String getDefaultValueLiteral();


    /**
     * Значение по умолчанию.
     * @return объект
     */
    Object getDefaultValue();


    /**
     * Элемент допускает значение null.
     * @return да\нет
     */
    boolean isUnsettable();


    /**
     * Элемент производный.
     * @return да\нет
     */
    //boolean isDerived();


    /**
     * Класс, содержащий данный элемент.
     * @return метаданные класса
     */
    XfwClass getEContainingClass();


    /**
     * Является ли свойством данного типа, примитивные типы и их обертки считаются равными.
     * @param clazz тип
     * @return да\нет
     */
    boolean isFieldOfType(Class<?> clazz);


    //Class<?> getContainerClass();


}
