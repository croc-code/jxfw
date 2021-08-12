/**
 */
package ru.croc.ctp.jxfw.metamodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XFW Data Source</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWDataSource#getRequestMapping <em>Request Mapping</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.XFWDataSource#isGeneral <em>General</em>}</li>
 * </ul>
 *
 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWDataSource()
 * @model
 * @generated
 */
public interface XFWDataSource extends EObject, EClass {

    /**
	 * Returns the value of the '<em><b>Request Mapping</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Request Mapping</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Request Mapping</em>' attribute.
	 * @see #setRequestMapping(String)
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWDataSource_RequestMapping()
	 * @model
	 * @generated
	 */
    String getRequestMapping();

    /**
	 * Sets the value of the '{@link ru.croc.ctp.jxfw.metamodel.XFWDataSource#getRequestMapping <em>Request Mapping</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Request Mapping</em>' attribute.
	 * @see #getRequestMapping()
	 * @generated
	 */
    void setRequestMapping(String value);

				/**
	 * Returns the value of the '<em><b>General</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>General</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>General</em>' attribute.
	 * @see #setGeneral(boolean)
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#getXFWDataSource_General()
	 * @model
	 * @generated
	 */
	boolean isGeneral();

				/**
	 * Sets the value of the '{@link ru.croc.ctp.jxfw.metamodel.XFWDataSource#isGeneral <em>General</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>General</em>' attribute.
	 * @see #isGeneral()
	 * @generated
	 */
	void setGeneral(boolean value);
} // XFWDataSource
