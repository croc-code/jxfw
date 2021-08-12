/**
 */
package ru.croc.ctp.jxfw.metamodel;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XFW Package</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWPackage()
 * @model
 * @generated
 */
public interface XFWPackage extends EObject, EPackage {
    /**
     * Базовый URI для всех пакетов. К нему добавляется суффикс из имени пакета.
     * Например: "http://ru.croc.ctp.jxfw/XFWPackage/org/demo/test"
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated NOT
     */
    String eNS_URI_PREFIX = "http://ru.croc.ctp.jxfw/XFWPackage";


    /**
     * Ищет классификатор по простому имени и заданному типу.
     *
     * @param name - Имя искомого классификатора
     * @param type - тип
     * @param <T>  - искомый тип
     * @return Найденный классификатор или null
     */
    <T extends EClassifier> T find(String name, Class<T> type);

    /**
     * Возвращает все классификаторы данного типа в пакете
     *
     * @param type - тип
     * @param <T>  - искомый тип
     * @return набор найденных объектов
     */
    <T extends EClassifier> Set<T> getAll(Class<T> type);


} // XFWPackage
