/**
 */
package ru.croc.ctp.jxfw.metamodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage
 * @generated
 */
public interface XFWMMFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	XFWMMFactory eINSTANCE = ru.croc.ctp.jxfw.metamodel.impl.XFWMMFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>XFW Package</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XFW Package</em>'.
	 * @generated
	 */
	XFWPackage createXFWPackage();

	/**
	 * Returns a new object of class '<em>XFW Class</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XFW Class</em>'.
	 * @generated
	 */
	XFWClass createXFWClass();

	/**
	 * Returns a new object of class '<em>XFW Attribute</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XFW Attribute</em>'.
	 * @generated
	 */
	XFWAttribute createXFWAttribute();

	/**
	 * Returns a new object of class '<em>XFW Reference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XFW Reference</em>'.
	 * @generated
	 */
	XFWReference createXFWReference();

	/**
	 * Returns a new object of class '<em>XFW Data Source</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XFW Data Source</em>'.
	 * @generated
	 */
	XFWDataSource createXFWDataSource();

	/**
	 * Returns a new object of class '<em>XFW Operation</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>XFW Operation</em>'.
	 * @generated
	 */
	XFWOperation createXFWOperation();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	XFWMMPackage getXFWMMPackage();

} //XFWMMFactory
