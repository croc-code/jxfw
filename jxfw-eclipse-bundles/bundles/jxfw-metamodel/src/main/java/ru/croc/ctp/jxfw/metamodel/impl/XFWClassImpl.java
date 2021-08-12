/**
 */
package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;
import ru.croc.ctp.jxfw.metamodel.PersistenceType;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XFW Class</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWClassImpl#getPersistenceModule <em>Persistence Module</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWClassImpl#isComplexType <em>Complex Type</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWClassImpl#getKeyTypeName <em>Key Type Name</em>}</li>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWClassImpl#getPersistenceType <em>Persistence Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XFWClassImpl extends EClassImpl implements XFWClass {
    /**
	 * The cached value of the '{@link #getPersistenceModule() <em>Persistence Module</em>}' attribute list.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPersistenceModule()
	 * @generated
	 * @ordered
	 */
    protected EList<String> persistenceModule;

    /**
	 * The default value of the '{@link #isComplexType() <em>Complex Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #isComplexType()
	 * @generated
	 * @ordered
	 */
    protected static final boolean COMPLEX_TYPE_EDEFAULT = false;
    /**
	 * The cached value of the '{@link #isComplexType() <em>Complex Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #isComplexType()
	 * @generated
	 * @ordered
	 */
    protected boolean complexType = COMPLEX_TYPE_EDEFAULT;

    /**
	 * The default value of the '{@link #getKeyTypeName() <em>Key Type Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getKeyTypeName()
	 * @generated
	 * @ordered
	 */
    protected static final String KEY_TYPE_NAME_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getKeyTypeName() <em>Key Type Name</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getKeyTypeName()
	 * @generated
	 * @ordered
	 */
    protected String keyTypeName = KEY_TYPE_NAME_EDEFAULT;

    /**
	 * The default value of the '{@link #getPersistenceType() <em>Persistence Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPersistenceType()
	 * @generated
	 * @ordered
	 */
    protected static final String PERSISTENCE_TYPE_EDEFAULT = null;

    /**
	 * The cached value of the '{@link #getPersistenceType() <em>Persistence Type</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getPersistenceType()
	 * @generated
	 * @ordered
	 */
    protected String persistenceType = PERSISTENCE_TYPE_EDEFAULT;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected XFWClassImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return XFWMMPackage.Literals.XFW_CLASS;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EList<String> getPersistenceModule() {
		if (persistenceModule == null) {
			persistenceModule = new EDataTypeEList<String>(String.class, this, XFWMMPackage.XFW_CLASS__PERSISTENCE_MODULE);
		}
		return persistenceModule;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public boolean isComplexType() {
		return complexType;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setComplexType(boolean newComplexType) {
		boolean oldComplexType = complexType;
		complexType = newComplexType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XFWMMPackage.XFW_CLASS__COMPLEX_TYPE, oldComplexType, complexType));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getKeyTypeName() {
		return keyTypeName;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setKeyTypeName(String newKeyTypeName) {
		String oldKeyTypeName = keyTypeName;
		keyTypeName = newKeyTypeName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XFWMMPackage.XFW_CLASS__KEY_TYPE_NAME, oldKeyTypeName, keyTypeName));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public String getPersistenceType() {
		return persistenceType;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setPersistenceType(String newPersistenceType) {
		String oldPersistenceType = persistenceType;
		persistenceType = newPersistenceType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XFWMMPackage.XFW_CLASS__PERSISTENCE_TYPE, oldPersistenceType, persistenceType));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_MODULE:
				return getPersistenceModule();
			case XFWMMPackage.XFW_CLASS__COMPLEX_TYPE:
				return isComplexType();
			case XFWMMPackage.XFW_CLASS__KEY_TYPE_NAME:
				return getKeyTypeName();
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_TYPE:
				return getPersistenceType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_MODULE:
				getPersistenceModule().clear();
				getPersistenceModule().addAll((Collection<? extends String>)newValue);
				return;
			case XFWMMPackage.XFW_CLASS__COMPLEX_TYPE:
				setComplexType((Boolean)newValue);
				return;
			case XFWMMPackage.XFW_CLASS__KEY_TYPE_NAME:
				setKeyTypeName((String)newValue);
				return;
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_TYPE:
				setPersistenceType((String)newValue);
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
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_MODULE:
				getPersistenceModule().clear();
				return;
			case XFWMMPackage.XFW_CLASS__COMPLEX_TYPE:
				setComplexType(COMPLEX_TYPE_EDEFAULT);
				return;
			case XFWMMPackage.XFW_CLASS__KEY_TYPE_NAME:
				setKeyTypeName(KEY_TYPE_NAME_EDEFAULT);
				return;
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_TYPE:
				setPersistenceType(PERSISTENCE_TYPE_EDEFAULT);
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
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_MODULE:
				return persistenceModule != null && !persistenceModule.isEmpty();
			case XFWMMPackage.XFW_CLASS__COMPLEX_TYPE:
				return complexType != COMPLEX_TYPE_EDEFAULT;
			case XFWMMPackage.XFW_CLASS__KEY_TYPE_NAME:
				return KEY_TYPE_NAME_EDEFAULT == null ? keyTypeName != null : !KEY_TYPE_NAME_EDEFAULT.equals(keyTypeName);
			case XFWMMPackage.XFW_CLASS__PERSISTENCE_TYPE:
				return PERSISTENCE_TYPE_EDEFAULT == null ? persistenceType != null : !PERSISTENCE_TYPE_EDEFAULT.equals(persistenceType);
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
		result.append(" (persistenceModule: ");
		result.append(persistenceModule);
		result.append(", complexType: ");
		result.append(complexType);
		result.append(", keyTypeName: ");
		result.append(keyTypeName);
		result.append(", persistenceType: ");
		result.append(persistenceType);
		result.append(')');
		return result.toString();
	}

    @Override
    public XFWAttribute findAttribute(String name) {
        for (EAttribute eAttribute : getEAllAttributes()) {
            if (eAttribute instanceof XFWAttribute && eAttribute.getName().equals(name)) {
                return (XFWAttribute) eAttribute;
            }
        }
        return null;
    }


    @Override
    public boolean isTransientType() {

        final String persistenceType = getPersistenceType();
        return persistenceType != null
                && PersistenceType.valueOf(persistenceType) == PersistenceType.TRANSIENT;
    }

    @Override
    public boolean isPersistentType() {
        final String persistenceType = getPersistenceType();
        return persistenceType != null
                && PersistenceType.valueOf(persistenceType) == PersistenceType.FULL;
    }


    public Iterable<EStructuralFeature> getOwnAndOverridenStructuralFeatures() {
        List<EStructuralFeature> features = new ArrayList<>();
        // св-ва из данного класса, кроме системных
        for (EStructuralFeature feature : getEStructuralFeatures()) {
            if (feature instanceof XFWAttribute
                    && ((XFWAttribute) feature).isSystemField()) {
                continue;
            }
            features.add(feature);
        }

        // переопределенные св-ва
        for (EAnnotation eAnnotation : getEAnnotations()) {
            if (eAnnotation.getSource().equals(XFWConstants.I18N_ANNOTATION_SOURCE.getUri())
                    && (eAnnotation.getDetails().size() > 0)
                    && eAnnotation.getDetails().get("propName") != null) { // переопределение
                if (!features.contains(getEStructuralFeature(eAnnotation.getDetails().get("propName"))))
                    features.add(getEStructuralFeature(eAnnotation.getDetails().get("propName")));
            }
        }
        features.remove(null);
        return features;
    }

    @Override
    public Map<String, EStructuralFeature> getFeaturesFlatMap() {

        return getFeaturesFlatMap("");
    }

    private Map<String, EStructuralFeature> getFeaturesFlatMap(String prefix) {
        Map<String, EStructuralFeature> result = new HashMap<>();

        for (EReference reference : getEAllReferences()) {
            if (((XFWClass) reference.getEReferenceType()).isComplexType()) {
                result.putAll(((XFWClassImpl) reference.getEReferenceType())
                        .getFeaturesFlatMap(prefix + reference.getName() + "."));
            } else {
                result.put(prefix + reference.getName(), reference);
            }
        }

        for (EAttribute attribute : getEAllAttributes()) {
            if (!((XFWAttribute) attribute).isSystemField()
                    && ! attribute.getEType().equals(XFWMMPackage.eINSTANCE.getBlob())) {
                result.put(prefix + attribute.getName(), attribute);
            }

        }
        return result;

    }


} //XFWClassImpl
