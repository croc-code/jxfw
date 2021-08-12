/**
 */
package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;

import java.util.HashSet;
import java.util.Set;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XFW Package</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class XFWPackageImpl extends EPackageImpl implements XFWPackage {
    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected XFWPackageImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return XFWMMPackage.Literals.XFW_PACKAGE;
	}



	@Override
	public <T extends EClassifier> T find(String name, Class<T> type) {
		for(EClassifier eClassifier:getEClassifiers()){
			if(type.isAssignableFrom(eClassifier.getClass()) &&
					(eClassifier.getName().equals(name)
							|| eClassifier.getInstanceClassName().equals(name))){
				return type.cast(eClassifier);
			}
		}
		return null;
	}

	@Override
	public <T extends EClassifier> Set<T> getAll(Class<T> type) {
		Set<T> result = new HashSet<T>();
    	for(EClassifier eClassifier:getEClassifiers()){
    		if(type.isAssignableFrom(eClassifier.getClass())){
				result.add(type.cast(eClassifier));
			}
		}
		return result;
	}
} //XFWPackageImpl
