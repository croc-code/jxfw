package ru.croc.ctp.jxfw.metamodel.runtime;

import java.util.Map;
import java.util.List;

/**
 * Класс в метамодели.
 */
public interface XfwClass extends XfwLocalizable, XfwClassifier, XfwFieldQualifier {

    /* EClass methods*/


    /**
     * Абстрактный класс.
     * @return да\нет
     */
    boolean isAbstract();


    /**
     * Метаданные непосредственных предков класса.
     * @return список
     */
    List<XfwClass> getESuperTypes();

    /**
     * Метаданные всех предков класса.
     * @return список
     */
    List<XfwClass> getEAllSuperTypes();

    //EAttribute getEIDAttribute();

    /**
     * Свойства, объявленные непосредственно в классе.
     * @return список
     */
    List<XfwStructuralFeature> getEStructuralFeatures();

    // EList<EGenericType> getEGenericSuperTypes();

    // EList<EGenericType> getEAllGenericSuperTypes();

    /**
     * Примитивные свойства, объявленные непосредственно в классе.
     * @return список
     */
    List<XfwAttribute> getEAttributes();

    /**
     * Все примитивные свойства класса.
     * @return список
     */
    List<XfwAttribute> getEAllAttributes();

    /**
     * Навигируемые и массивные навигируемые свойства, объявленные непосредственно в классе.
     * @return список
     */
    List<XfwReference> getEReferences();

    /**
     * Все навигируемые и массивные навигируемые свойства класса.
     * @return список
     */
    List<XfwReference> getEAllReferences();

    // EList<EReference> getEAllContainments();

    /**
     * Все свойства класса.
     * @return список
     */
    List<XfwStructuralFeature> getEAllStructuralFeatures();

    //  EList<EOperation> getEOperations();

    //  EList<EOperation> getEAllOperations();

    /**
     * Класс является предком переданного класса.
     * @param var1 потенциальный наследник
     * @return да\нет
     */
    boolean isSuperTypeOf(XfwClass var1);

    /**
     * Общее количество свойств.
     * @return количество
     */
    int getFeatureCount();

    //XfwStructuralFeature getEStructuralFeature(int var1);

    /**
     * Найти свойство по имени.
     * @param var1 имя
     * @return метаданные совйства или null
     */
    XfwStructuralFeature getEStructuralFeature(String var1);

    //  int getOperationCount();

    //  EOperation getEOperation(int var1);

    //   int getOperationID(EOperation var1);

    //  EOperation getOverride(EOperation var1);

    //   EGenericType getFeatureType(EStructuralFeature var1);

    //    int getFeatureID(EStructuralFeature var1);




    /* XFWClass methods*/


    /**
     * Returns the value of the '<em><b>Persistence Module</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     *
     * @return the value of the '<em>Persistence Module</em>' attribute list.
     */
    List<String> getPersistenceModule();

    /**
     * Комплексный тип.
     * @return да\нет
     */
    boolean isComplexType();

    /**
     * Returns the value of the '<em><b>Key Type Name</b></em>' attribute.
     *
     * @return the value of the '<em>Key Type Name</em>' attribute.
     */
    String getKeyTypeName();


    /**
     * Returns the value of the '<em><b>Persistence Type</b></em>' attribute.
     *
     * @return the value of the '<em>Persistence Type</em>' attribute.
     */
    String getPersistenceType();


    /**
     * Ищет атрибут по  имени.
     *
     * @param name - имя
     * @return - атрибут
     */
    XfwAttribute findAttribute(String name);

    /**
     * Транзиентный тип.
     * @return да\нет
     */
    boolean isTransientType();

    /**
     * Хранимый тип.
     * @return да\нет
     */
    boolean isPersistentType();


    /**
     * Найти свойство по имени. Имя может быть цепочкой свойств через точку.
     * @param propNameChain имя
     * @return метаданные свойства или null
     */
    XfwStructuralFeature getChainingEStructuralFeature(String propNameChain);


    /**
     * Построить мапу имя свойста - описание этого свойства так, что для свойств комплексных типов
     * описание разворачивается вплоть до примитивных типов.
     *
     * @return мапа
     */
    Map<String, XfwStructuralFeature> getFeaturesFlatMap();



}
