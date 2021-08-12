/**
 */
package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XFW Data Source</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWDataSourceImpl#getRequestMapping <em>Request Mapping</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWDataSourceImpl#isGeneral <em>General</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XFWDataSourceImpl extends EClassImpl implements XFWDataSource {
    /**
	 * The default value of the '{@link #getRequestMapping() <em>Request Mapping</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getRequestMapping()
	 * @generated
	 * @ordered
	 */
    protected static final String REQUEST_MAPPING_EDEFAULT = null;
    /**
	 * The cached value of the '{@link #getRequestMapping() <em>Request Mapping</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getRequestMapping()
	 * @generated
	 * @ordered
	 */
    protected String requestMapping = REQUEST_MAPPING_EDEFAULT;

    /**
	 * The default value of the '{@link #isGeneral() <em>General</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGeneral()
	 * @generated
	 * @ordered
	 */
	protected static final boolean GENERAL_EDEFAULT = false;
				/**
	 * The cached value of the '{@link #isGeneral() <em>General</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isGeneral()
	 * @generated
	 * @ordered
	 */
	protected boolean general = GENERAL_EDEFAULT;

				/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected XFWDataSourceImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return XFWMMPackage.Literals.XFW_DATA_SOURCE;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getRequestMapping() {
		return requestMapping;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setRequestMapping(String newRequestMapping) {
		String oldRequestMapping = requestMapping;
		requestMapping = newRequestMapping;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XFWMMPackage.XFW_DATA_SOURCE__REQUEST_MAPPING, oldRequestMapping, requestMapping));
	}

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGeneral() {
		return general;
	}

				/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGeneral(boolean newGeneral) {
		boolean oldGeneral = general;
		general = newGeneral;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XFWMMPackage.XFW_DATA_SOURCE__GENERAL, oldGeneral, general));
	}

				/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case XFWMMPackage.XFW_DATA_SOURCE__REQUEST_MAPPING:
				return getRequestMapping();
			case XFWMMPackage.XFW_DATA_SOURCE__GENERAL:
				return isGeneral();
		}
		return super.eGet(featureID, resolve, coreType);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case XFWMMPackage.XFW_DATA_SOURCE__REQUEST_MAPPING:
				setRequestMapping((String)newValue);
				return;
			case XFWMMPackage.XFW_DATA_SOURCE__GENERAL:
				setGeneral((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public void eUnset(int featureID) {
		switch (featureID) {
			case XFWMMPackage.XFW_DATA_SOURCE__REQUEST_MAPPING:
				setRequestMapping(REQUEST_MAPPING_EDEFAULT);
				return;
			case XFWMMPackage.XFW_DATA_SOURCE__GENERAL:
				setGeneral(GENERAL_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public boolean eIsSet(int featureID) {
		switch (featureID) {
			case XFWMMPackage.XFW_DATA_SOURCE__REQUEST_MAPPING:
				return REQUEST_MAPPING_EDEFAULT == null ? requestMapping != null : !REQUEST_MAPPING_EDEFAULT.equals(requestMapping);
			case XFWMMPackage.XFW_DATA_SOURCE__GENERAL:
				return general != GENERAL_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (requestMapping: ");
		result.append(requestMapping);
		result.append(", general: ");
		result.append(general);
		result.append(')');
		return result.toString();
	}

} //XFWDataSourceImpl
