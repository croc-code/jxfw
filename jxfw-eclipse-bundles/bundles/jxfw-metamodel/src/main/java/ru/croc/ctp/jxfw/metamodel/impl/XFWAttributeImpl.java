/**
 */
package ru.croc.ctp.jxfw.metamodel.impl;

import static ru.croc.ctp.jxfw.metamodel.impl.AnnotationsSources.*;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;

import javax.annotation.Nonnull;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XFW Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link ru.croc.ctp.jxfw.metamodel.impl.XFWAttributeImpl#getMaxLength <em>Max Length</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XFWAttributeImpl extends EAttributeImpl implements XFWAttribute {

    /**
	 * The default value of the '{@link #getMaxLength() <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getMaxLength()
	 * @generated
	 * @ordered
	 */
    protected static final int MAX_LENGTH_EDEFAULT = 0;

    /**
	 * The cached value of the '{@link #getMaxLength() <em>Max Length</em>}' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getMaxLength()
	 * @generated
	 * @ordered
	 */
    protected int maxLength = MAX_LENGTH_EDEFAULT;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected XFWAttributeImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return XFWMMPackage.Literals.XFW_ATTRIBUTE;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public int getMaxLength() {
		return maxLength;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setMaxLength(int newMaxLength) {
		int oldMaxLength = maxLength;
		maxLength = newMaxLength;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, XFWMMPackage.XFW_ATTRIBUTE__MAX_LENGTH, oldMaxLength, maxLength));
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case XFWMMPackage.XFW_ATTRIBUTE__MAX_LENGTH:
				return getMaxLength();
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
			case XFWMMPackage.XFW_ATTRIBUTE__MAX_LENGTH:
				setMaxLength((Integer)newValue);
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
			case XFWMMPackage.XFW_ATTRIBUTE__MAX_LENGTH:
				setMaxLength(MAX_LENGTH_EDEFAULT);
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
			case XFWMMPackage.XFW_ATTRIBUTE__MAX_LENGTH:
				return maxLength != MAX_LENGTH_EDEFAULT;
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
		result.append(" (maxLength: ");
		result.append(maxLength);
		result.append(')');
		return result.toString();
	}

    @Override
    public void setGenerateBlobInfoFields(boolean generate) {
        checkIsBlob();

        EAnnotation annotation = findOrCreateAnnotation(BlobAdditionalFields.SOURCE, this);
        annotation.getDetails().put(BlobAdditionalFields.GENERATE_FIELDS_PARAM, String.valueOf(generate));
    }

    @Override
    public boolean getGenerateBlobInfoFields() {
        checkIsBlob();

        boolean defaultResult = false;

        EAnnotation annotation = getEAnnotation(BlobAdditionalFields.SOURCE);
        if (annotation == null) {
            return defaultResult;
        }
        String generate = annotation.getDetails().get(BlobAdditionalFields.GENERATE_FIELDS_PARAM);
        if (generate != null) {
            return Boolean.valueOf(generate);
        }
        return defaultResult;
    }

    @Override
    public void setContentSizeSuffix(String sizeSuffix) {
        checkIsBlob();

        EAnnotation annotation = findOrCreateAnnotation(BlobAdditionalFields.SOURCE, this);
        annotation.getDetails().put(BlobAdditionalFields.CONTENT_SIZE_SUFFIX_PARAM, sizeSuffix);
    }

    @Nonnull
    @Override
    public String getContentSizeSuffix() {
        checkIsBlob();

        EAnnotation annotation = getEAnnotation(BlobAdditionalFields.SOURCE);
        if (annotation == null) {
            throw new IllegalStateException("Annotation not found");
        }
        String suffix = annotation.getDetails().get(BlobAdditionalFields.CONTENT_SIZE_SUFFIX_PARAM);
        if (suffix == null) {
            throw new IllegalStateException("Parameter not set in the model");
        }
        return suffix;
    }

    @Override
    public void setFileNameSuffix(String fileNameSuffix) {
        checkIsBlob();

        EAnnotation annotation = findOrCreateAnnotation(BlobAdditionalFields.SOURCE, this);
        annotation.getDetails().put(BlobAdditionalFields.FILE_NAME_SUFFIX_PARAM, fileNameSuffix);
    }

    @Nonnull
    @Override
    public String getFileNameSuffix() {
        checkIsBlob();

        EAnnotation annotation = getEAnnotation(BlobAdditionalFields.SOURCE);
        if (annotation == null) {
            throw new IllegalStateException("Annotation not found");
        }
        String suffix = annotation.getDetails().get(BlobAdditionalFields.FILE_NAME_SUFFIX_PARAM);
        if (suffix == null) {
            throw new IllegalStateException("Parameter not set in the model");
        }
        return suffix;
    }

    @Override
    public void setContentTypeSuffix(String contentTypeSuffix) {
        checkIsBlob();

        EAnnotation annotation = findOrCreateAnnotation(BlobAdditionalFields.SOURCE, this);
        annotation.getDetails().put(BlobAdditionalFields.CONTENT_TYPE_SUFFIX_PARAM, contentTypeSuffix);
    }

    @Nonnull
    @Override
    public String getContentTypeSuffix() {
        checkIsBlob();

        EAnnotation annotation = getEAnnotation(BlobAdditionalFields.SOURCE);
        if (annotation == null) {
            throw new IllegalStateException("Annotation not found");
        }
        String suffix = annotation.getDetails().get(BlobAdditionalFields.CONTENT_TYPE_SUFFIX_PARAM);
        if (suffix == null) {
            throw new IllegalStateException("Parameter not set in the model");
        }
        return suffix;
    }

    @Override
    public boolean isSystemField() {
        return isIdField() || isVersionField();
    }

    @Override
    public boolean isIdField() {
        return getEAnnotation(XFWConstants.getUri("Id")) != null;
    }

    @Override
    public boolean isVersionField() {
        return getEAnnotation(XFWConstants.getUri("Version")) != null;
    }

    private void checkIsBlob() {
        if (getEAttributeType() == null || !getEAttributeType().getInstanceTypeName().equals("java.sql.Blob")) {
            throw new IllegalStateException("Тип атрибута должен быть java.sql.Blob");
        }
    }

} //XFWAttributeImpl
