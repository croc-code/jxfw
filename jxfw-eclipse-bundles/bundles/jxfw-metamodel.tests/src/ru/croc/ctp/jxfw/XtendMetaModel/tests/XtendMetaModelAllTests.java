/**
 */
package ru.croc.ctp.jxfw.XtendMetaModel.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>Xtend Meta Model</b></em>' model.
 * <!-- end-user-doc -->
 * @generated
 */
public class XtendMetaModelAllTests extends TestSuite {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Test suite() {
        TestSuite suite = new XtendMetaModelAllTests("Xtend Meta Model Tests");
        suite.addTest(XMMTests.suite());
        return suite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XtendMetaModelAllTests(String name) {
        super(name);
    }

} //XtendMetaModelAllTests
