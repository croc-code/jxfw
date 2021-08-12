/**
 */
package ru.croc.ctp.jxfw.metamodel;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XFW Class</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWClass#getPersistenceModule <em>Persistence Module</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWClass#isComplexType <em>Complex Type</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWClass#getKeyTypeName <em>Key Type Name</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWClass#getPersistenceType <em>Persistence Type</em>}</li>
 * </ul>
 *
 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWClass()
 * @model
 * @generated
 */
public interface XFWClass extends EObject, EClass {

    /**
	 * Returns the value of the '<em><b>Persistence Module</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Persistence Module</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Persistence Module</em>' attribute list.
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWClass_PersistenceModule()
	 * @model unique="false" upper="10"
	 * @generated
	 */
    EList<String> getPersistenceModule();

    /**
	 * Returns the value of the '<em><b>Complex Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Complex Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Complex Type</em>' attribute.
	 * @see #setComplexType(boolean)
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWClass_ComplexType()
	 * @model
	 * @generated
	 */
    boolean isComplexType();

    /**
	 * Sets the value of the '{@link ru.croc.ctp.jxfw.metamodel.XFWClass#isComplexType <em>Complex Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Complex Type</em>' attribute.
	 * @see #isComplexType()
	 * @generated
	 */
    void setComplexType(boolean value);

    /**
	 * Returns the value of the '<em><b>Key Type Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Key Type Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Key Type Name</em>' attribute.
	 * @see #setKeyTypeName(String)
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWClass_KeyTypeName()
	 * @model
	 * @generated
	 */
    String getKeyTypeName();

    /**
	 * Sets the value of the '{@link ru.croc.ctp.jxfw.metamodel.XFWClass#getKeyTypeName <em>Key Type Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key Type Name</em>' attribute.
	 * @see #getKeyTypeName()
	 * @generated
	 */
    void setKeyTypeName(String value);

    /**
	 * Returns the value of the '<em><b>Persistence Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Persistence Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Persistence Type</em>' attribute.
	 * @see #setPersistenceType(String)
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWClass_PersistenceType()
	 * @model
	 * @generated
	 */
    String getPersistenceType();

    /**
	 * Sets the value of the '{@link ru.croc.ctp.jxfw.metamodel.XFWClass#getPersistenceType <em>Persistence Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Persistence Type</em>' attribute.
	 * @see #getPersistenceType()
	 * @generated
	 */
    void setPersistenceType(String value);


    /**
     * Ищет атрибут по  имени.
     *
     * @param name - имя
     * @return - атрибут
     */
    XFWAttribute findAttribute(String name);


    /**
     * Является ли тип транзиентным.
     *
     * @return да\нет.
     */
    boolean isTransientType();

    /**
     * Является ли тип хранимым.
     *
     * @return да\нет.
     */
    boolean isPersistentType();



    /**
     * Список свойств, объявленных непосредственно в данном классе,
     * или переопределенных в нем свойств родителя.
     * (аннотация XFWElementLabel со свойством propName)
     *
     * @return список свойств.
     */
    Iterable<EStructuralFeature> getOwnAndOverridenStructuralFeatures();



	/**
	 * Построить мапу имя свойста - описание этого свойства так, что для свойств комплексных типов
	 * описание разворачивается вплоть до примитивных типов.
	 * Без ситемных свойств( ид и версия) и блобов
	 *
	 * @return мапа
	 */
	Map<String, EStructuralFeature> getFeaturesFlatMap();




} // XFWClass
