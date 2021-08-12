/**
 */
package ru.croc.ctp.jxfw.metamodel.impl;

import com.querydsl.core.Tuple;

import java.sql.Blob;
import java.sql.Timestamp;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import ru.croc.ctp.jxfw.metamodel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XFWMMFactoryImpl extends EFactoryImpl implements XFWMMFactory {
	/**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     */
    public static XFWMMFactory init() {
        try {
            XFWMMFactory theXFWMMFactory = (XFWMMFactory)EPackage.Registry.INSTANCE.getEFactory(XFWMMPackage.eNS_URI);
            if (theXFWMMFactory != null) {
                return theXFWMMFactory;
            }
        } catch (ClassCastException ignore) {
            // игнорируем исключение - оно не влияет на сборку
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
		return new XFWMMFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public XFWMMFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case XFWMMPackage.XFW_PACKAGE: return createXFWPackage();
			case XFWMMPackage.XFW_CLASS: return createXFWClass();
			case XFWMMPackage.XFW_ATTRIBUTE: return createXFWAttribute();
			case XFWMMPackage.XFW_REFERENCE: return createXFWReference();
			case XFWMMPackage.XFW_DATA_SOURCE: return createXFWDataSource();
			case XFWMMPackage.XFW_OPERATION: return createXFWOperation();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case XFWMMPackage.LOCAL_DATE_TIME:
				return createLocalDateTimeFromString(eDataType, initialValue);
			case XFWMMPackage.BLOB:
				return createBlobFromString(eDataType, initialValue);
			case XFWMMPackage.ZONED_DATE_TIME:
				return createZonedDateTimeFromString(eDataType, initialValue);
			case XFWMMPackage.PERIOD:
				return createPeriodFromString(eDataType, initialValue);
			case XFWMMPackage.UUID:
				return createUUIDFromString(eDataType, initialValue);
			case XFWMMPackage.LOCAL_DATE_TIME_JAVA7:
				return createLocalDateTime_Java7FromString(eDataType, initialValue);
			case XFWMMPackage.ZONED_DATE_TIME_JAVA7:
				return createZonedDateTime_Java7FromString(eDataType, initialValue);
			case XFWMMPackage.PERIOD_JAVA7:
				return createPeriod_Java7FromString(eDataType, initialValue);
			case XFWMMPackage.TUPLE:
				return createTupleFromString(eDataType, initialValue);
			case XFWMMPackage.DURATION:
				return createDurationFromString(eDataType, initialValue);
			case XFWMMPackage.DURATION_JAVA7:
				return createDuration_Java7FromString(eDataType, initialValue);
			case XFWMMPackage.LOCAL_DATE:
				return createLocalDateFromString(eDataType, initialValue);
			case XFWMMPackage.LOCAL_DATE_JAVA7:
				return createLocalDate_Java7FromString(eDataType, initialValue);
			case XFWMMPackage.LOCAL_TIME:
				return createLocalTimeFromString(eDataType, initialValue);
			case XFWMMPackage.LOCAL_TIME_JAVA7:
				return createLocalTime_Java7FromString(eDataType, initialValue);
			case XFWMMPackage.SET:
				return createSetFromString(eDataType, initialValue);
			case XFWMMPackage.LIST:
				return createListFromString(eDataType, initialValue);
			case XFWMMPackage.TIMESTAMP:
				return createTimestampFromString(eDataType, initialValue);
			case XFWMMPackage.MAP:
				return createMapFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case XFWMMPackage.LOCAL_DATE_TIME:
				return convertLocalDateTimeToString(eDataType, instanceValue);
			case XFWMMPackage.BLOB:
				return convertBlobToString(eDataType, instanceValue);
			case XFWMMPackage.ZONED_DATE_TIME:
				return convertZonedDateTimeToString(eDataType, instanceValue);
			case XFWMMPackage.PERIOD:
				return convertPeriodToString(eDataType, instanceValue);
			case XFWMMPackage.UUID:
				return convertUUIDToString(eDataType, instanceValue);
			case XFWMMPackage.LOCAL_DATE_TIME_JAVA7:
				return convertLocalDateTime_Java7ToString(eDataType, instanceValue);
			case XFWMMPackage.ZONED_DATE_TIME_JAVA7:
				return convertZonedDateTime_Java7ToString(eDataType, instanceValue);
			case XFWMMPackage.PERIOD_JAVA7:
				return convertPeriod_Java7ToString(eDataType, instanceValue);
			case XFWMMPackage.TUPLE:
				return convertTupleToString(eDataType, instanceValue);
			case XFWMMPackage.DURATION:
				return convertDurationToString(eDataType, instanceValue);
			case XFWMMPackage.DURATION_JAVA7:
				return convertDuration_Java7ToString(eDataType, instanceValue);
			case XFWMMPackage.LOCAL_DATE:
				return convertLocalDateToString(eDataType, instanceValue);
			case XFWMMPackage.LOCAL_DATE_JAVA7:
				return convertLocalDate_Java7ToString(eDataType, instanceValue);
			case XFWMMPackage.LOCAL_TIME:
				return convertLocalTimeToString(eDataType, instanceValue);
			case XFWMMPackage.LOCAL_TIME_JAVA7:
				return convertLocalTime_Java7ToString(eDataType, instanceValue);
			case XFWMMPackage.SET:
				return convertSetToString(eDataType, instanceValue);
			case XFWMMPackage.LIST:
				return convertListToString(eDataType, instanceValue);
			case XFWMMPackage.TIMESTAMP:
				return convertTimestampToString(eDataType, instanceValue);
			case XFWMMPackage.MAP:
				return convertMapToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWPackage createXFWPackage() {
		XFWPackageImpl xfwPackage = new XFWPackageImpl();
		return xfwPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWClass createXFWClass() {
		XFWClassImpl xfwClass = new XFWClassImpl();
		return xfwClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWAttribute createXFWAttribute() {
		XFWAttributeImpl xfwAttribute = new XFWAttributeImpl();
		return xfwAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWReference createXFWReference() {
		XFWReferenceImpl xfwReference = new XFWReferenceImpl();
		return xfwReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWDataSource createXFWDataSource() {
		XFWDataSourceImpl xfwDataSource = new XFWDataSourceImpl();
		return xfwDataSource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWOperation createXFWOperation() {
		XFWOperationImpl xfwOperation = new XFWOperationImpl();
		return xfwOperation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalDateTime createLocalDateTimeFromString(EDataType eDataType, String initialValue) {
		return (LocalDateTime)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLocalDateTimeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Blob createBlobFromString(EDataType eDataType, String initialValue) {
		return (Blob)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertBlobToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ZonedDateTime createZonedDateTimeFromString(EDataType eDataType, String initialValue) {
		return (ZonedDateTime)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertZonedDateTimeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Period createPeriodFromString(EDataType eDataType, String initialValue) {
		return (Period)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPeriodToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UUID createUUIDFromString(EDataType eDataType, String initialValue) {
		return (UUID)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertUUIDToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public org.threeten.bp.LocalDateTime createLocalDateTime_Java7FromString(EDataType eDataType, String initialValue) {
		return (org.threeten.bp.LocalDateTime)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLocalDateTime_Java7ToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public org.threeten.bp.ZonedDateTime createZonedDateTime_Java7FromString(EDataType eDataType, String initialValue) {
		return (org.threeten.bp.ZonedDateTime)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertZonedDateTime_Java7ToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public org.threeten.bp.Period createPeriod_Java7FromString(EDataType eDataType, String initialValue) {
		return (org.threeten.bp.Period)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPeriod_Java7ToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Tuple createTupleFromString(EDataType eDataType, String initialValue) {
		return (Tuple)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTupleToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Duration createDurationFromString(EDataType eDataType, String initialValue) {
		return (Duration)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDurationToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public org.threeten.bp.Duration createDuration_Java7FromString(EDataType eDataType, String initialValue) {
		return (org.threeten.bp.Duration)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDuration_Java7ToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalDate createLocalDateFromString(EDataType eDataType, String initialValue) {
		return (LocalDate)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLocalDateToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public org.threeten.bp.LocalDate createLocalDate_Java7FromString(EDataType eDataType, String initialValue) {
		return (org.threeten.bp.LocalDate)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLocalDate_Java7ToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalTime createLocalTimeFromString(EDataType eDataType, String initialValue) {
		return (LocalTime)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLocalTimeToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public org.threeten.bp.LocalTime createLocalTime_Java7FromString(EDataType eDataType, String initialValue) {
		return (org.threeten.bp.LocalTime)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLocalTime_Java7ToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("rawtypes")
	public Set createSetFromString(EDataType eDataType, String initialValue) {
		return (Set)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSetToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("rawtypes")
	public List createListFromString(EDataType eDataType, String initialValue) {
		return (List)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertListToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Timestamp createTimestampFromString(EDataType eDataType, String initialValue) {
		return (Timestamp)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTimestampToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("rawtypes")
	public Map createMapFromString(EDataType eDataType, String initialValue) {
		return (Map)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertMapToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWMMPackage getXFWMMPackage() {
		return (XFWMMPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static XFWMMPackage getPackage() {
		return XFWMMPackage.eINSTANCE;
	}

} //XFWMMFactoryImpl
