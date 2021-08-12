/**
 */
package ru.croc.ctp.jxfw.XtendMetaModel.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>XtendMetaModel</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class XMMTests extends TestSuite {

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
        TestSuite suite = new XMMTests("XtendMetaModel Tests");
        suite.addTestSuite(XtendPackageTest.class);
        suite.addTestSuite(XtendClassTest.class);
        suite.addTestSuite(XtendAttributeTest.class);
        suite.addTestSuite(XtendPrimitiveTypeTest.class);
        return suite;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XMMTests(String name) {
        super(name);
    }

} //XMMTests
