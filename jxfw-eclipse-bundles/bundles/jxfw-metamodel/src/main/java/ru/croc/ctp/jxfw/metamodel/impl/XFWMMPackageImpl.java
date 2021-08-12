/**
 */
package ru.croc.ctp.jxfw.metamodel.impl;

import com.querydsl.core.Tuple;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWMMFactory;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.XFWOperation;
import ru.croc.ctp.jxfw.metamodel.XFWPackage;
import ru.croc.ctp.jxfw.metamodel.XFWReference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XFWMMPackageImpl extends EPackageImpl implements XFWMMPackage {

    /**
     * Имя пакета, в котором хранится файл модели в jar-модулях jXFW.
     */
    private static final String XFW_MODEL_FILE_PACKAGE_NAME = "model";

    /**
     * Имя файла модели.
     */
    private static final String XFW_MODEL_FILE_NAME = "XFWModel";

    /**
     * Расширение файла модели.
     */
    private static final String XFW_MODEL_FILE_EXTENSION = "ecore";

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xfwPackageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xfwClassEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xfwAttributeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xfwReferenceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xfwDataSourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass xfwOperationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType localDateTimeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType blobEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType zonedDateTimeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType periodEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType uuidEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType localDateTime_Java7EDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType zonedDateTime_Java7EDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType period_Java7EDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType tupleEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType objectFilterEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType durationEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType duration_Java7EDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType localDateEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType localDate_Java7EDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType localTimeEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType localTime_Java7EDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType setEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType listEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType timestampEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType domainObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType mapEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see ru.croc.ctp.jxfw.metamodel.XFWMMPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private XFWMMPackageImpl() {
		super(eNS_URI, XFWMMFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link XFWMMPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static XFWMMPackage init() {
		if (isInited) return (XFWMMPackage)EPackage.Registry.INSTANCE.getEPackage(XFWMMPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredXFWMMPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		XFWMMPackageImpl theXFWMMPackage = registeredXFWMMPackage instanceof XFWMMPackageImpl ? (XFWMMPackageImpl)registeredXFWMMPackage : new XFWMMPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theXFWMMPackage.createPackageContents();

		// Initialize created meta-data
		theXFWMMPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theXFWMMPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(XFWMMPackage.eNS_URI, theXFWMMPackage);
		return theXFWMMPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXFWPackage() {
		return xfwPackageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXFWClass() {
		return xfwClassEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWClass_PersistenceModule() {
		return (EAttribute)xfwClassEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWClass_ComplexType() {
		return (EAttribute)xfwClassEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWClass_KeyTypeName() {
		return (EAttribute)xfwClassEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWClass_PersistenceType() {
		return (EAttribute)xfwClassEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXFWAttribute() {
		return xfwAttributeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWAttribute_MaxLength() {
		return (EAttribute)xfwAttributeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXFWReference() {
		return xfwReferenceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXFWDataSource() {
		return xfwDataSourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWDataSource_RequestMapping() {
		return (EAttribute)xfwDataSourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getXFWDataSource_General() {
		return (EAttribute)xfwDataSourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getXFWOperation() {
		return xfwOperationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLocalDateTime() {
		return localDateTimeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getBlob() {
		return blobEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getZonedDateTime() {
		return zonedDateTimeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPeriod() {
		return periodEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getUUID() {
		return uuidEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLocalDateTime_Java7() {
		return localDateTime_Java7EDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getZonedDateTime_Java7() {
		return zonedDateTime_Java7EDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPeriod_Java7() {
		return period_Java7EDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTuple() {
		return tupleEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getObjectFilter() {
		return objectFilterEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDuration() {
		return durationEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDuration_Java7() {
		return duration_Java7EDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLocalDate() {
		return localDateEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLocalDate_Java7() {
		return localDate_Java7EDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLocalTime() {
		return localTimeEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getLocalTime_Java7() {
		return localTime_Java7EDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getSet() {
		return setEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getList() {
		return listEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTimestamp() {
		return timestampEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getDomainObject() {
		return domainObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getMap() {
		return mapEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public XFWMMFactory getXFWMMFactory() {
		return (XFWMMFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		xfwPackageEClass = createEClass(XFW_PACKAGE);

		xfwClassEClass = createEClass(XFW_CLASS);
		createEAttribute(xfwClassEClass, XFW_CLASS__PERSISTENCE_MODULE);
		createEAttribute(xfwClassEClass, XFW_CLASS__COMPLEX_TYPE);
		createEAttribute(xfwClassEClass, XFW_CLASS__KEY_TYPE_NAME);
		createEAttribute(xfwClassEClass, XFW_CLASS__PERSISTENCE_TYPE);

		xfwAttributeEClass = createEClass(XFW_ATTRIBUTE);
		createEAttribute(xfwAttributeEClass, XFW_ATTRIBUTE__MAX_LENGTH);

		xfwReferenceEClass = createEClass(XFW_REFERENCE);

		xfwDataSourceEClass = createEClass(XFW_DATA_SOURCE);
		createEAttribute(xfwDataSourceEClass, XFW_DATA_SOURCE__REQUEST_MAPPING);
		createEAttribute(xfwDataSourceEClass, XFW_DATA_SOURCE__GENERAL);

		xfwOperationEClass = createEClass(XFW_OPERATION);

		// Create data types
		localDateTimeEDataType = createEDataType(LOCAL_DATE_TIME);
		blobEDataType = createEDataType(BLOB);
		zonedDateTimeEDataType = createEDataType(ZONED_DATE_TIME);
		periodEDataType = createEDataType(PERIOD);
		uuidEDataType = createEDataType(UUID);
		localDateTime_Java7EDataType = createEDataType(LOCAL_DATE_TIME_JAVA7);
		zonedDateTime_Java7EDataType = createEDataType(ZONED_DATE_TIME_JAVA7);
		period_Java7EDataType = createEDataType(PERIOD_JAVA7);
		tupleEDataType = createEDataType(TUPLE);
		objectFilterEDataType = createEDataType(OBJECT_FILTER);
		durationEDataType = createEDataType(DURATION);
		duration_Java7EDataType = createEDataType(DURATION_JAVA7);
		localDateEDataType = createEDataType(LOCAL_DATE);
		localDate_Java7EDataType = createEDataType(LOCAL_DATE_JAVA7);
		localTimeEDataType = createEDataType(LOCAL_TIME);
		localTime_Java7EDataType = createEDataType(LOCAL_TIME_JAVA7);
		setEDataType = createEDataType(SET);
		listEDataType = createEDataType(LIST);
		timestampEDataType = createEDataType(TIMESTAMP);
		domainObjectEDataType = createEDataType(DOMAIN_OBJECT);
		mapEDataType = createEDataType(MAP);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		xfwPackageEClass.getESuperTypes().add(ecorePackage.getEPackage());
		xfwClassEClass.getESuperTypes().add(ecorePackage.getEClass());
		xfwAttributeEClass.getESuperTypes().add(ecorePackage.getEAttribute());
		xfwReferenceEClass.getESuperTypes().add(ecorePackage.getEReference());
		xfwDataSourceEClass.getESuperTypes().add(ecorePackage.getEClass());
		xfwOperationEClass.getESuperTypes().add(ecorePackage.getEOperation());

		// Initialize classes, features, and operations; add parameters
		initEClass(xfwPackageEClass, XFWPackage.class, "XFWPackage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(xfwClassEClass, XFWClass.class, "XFWClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getXFWClass_PersistenceModule(), ecorePackage.getEString(), "persistenceModule", null, 0, 10, XFWClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXFWClass_ComplexType(), ecorePackage.getEBoolean(), "complexType", null, 0, 1, XFWClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXFWClass_KeyTypeName(), ecorePackage.getEString(), "keyTypeName", null, 0, 1, XFWClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXFWClass_PersistenceType(), ecorePackage.getEString(), "persistenceType", null, 0, 1, XFWClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(xfwAttributeEClass, XFWAttribute.class, "XFWAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getXFWAttribute_MaxLength(), ecorePackage.getEInt(), "maxLength", null, 0, 1, XFWAttribute.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(xfwReferenceEClass, XFWReference.class, "XFWReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(xfwDataSourceEClass, XFWDataSource.class, "XFWDataSource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getXFWDataSource_RequestMapping(), ecorePackage.getEString(), "requestMapping", null, 0, 1, XFWDataSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getXFWDataSource_General(), ecorePackage.getEBoolean(), "general", null, 0, 1, XFWDataSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(xfwOperationEClass, XFWOperation.class, "XFWOperation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Initialize data types
		initEDataType(localDateTimeEDataType, LocalDateTime.class, "LocalDateTime", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(blobEDataType, Blob.class, "Blob", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(zonedDateTimeEDataType, ZonedDateTime.class, "ZonedDateTime", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(periodEDataType, Period.class, "Period", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(uuidEDataType, java.util.UUID.class, "UUID", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(localDateTime_Java7EDataType, org.threeten.bp.LocalDateTime.class, "LocalDateTime_Java7", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(zonedDateTime_Java7EDataType, org.threeten.bp.ZonedDateTime.class, "ZonedDateTime_Java7", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(period_Java7EDataType, org.threeten.bp.Period.class, "Period_Java7", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(tupleEDataType, Tuple.class, "Tuple", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		/*
		  JXFW-1153 instance class передан null, чтобы Убрать модуль jxfw-datatypes.
        */
		initEDataType(objectFilterEDataType, null, "ObjectFilter", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "ru.croc.ctp.jxfw.core.facade.webclient.ObjectFilter");
		initEDataType(durationEDataType, Duration.class, "Duration", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(duration_Java7EDataType, org.threeten.bp.Duration.class, "Duration_Java7", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(localDateEDataType, LocalDate.class, "LocalDate", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(localDate_Java7EDataType, org.threeten.bp.LocalDate.class, "LocalDate_Java7", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(localTimeEDataType, LocalTime.class, "LocalTime", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(localTime_Java7EDataType, org.threeten.bp.LocalTime.class, "LocalTime_Java7", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(setEDataType, Set.class, "Set", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(listEDataType, List.class, "List", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(timestampEDataType, Timestamp.class, "Timestamp", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		/*
		  JXFW-987 instance class передан null, чтобы не тащить  DomainObject, а за ним и спринг, внутрь бандла.
		 */
		initEDataType(domainObjectEDataType, null, "DomainObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "ru.croc.ctp.jxfw.core.domain.DomainObject");
		initEDataType(mapEDataType, Map.class, "Map", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}
	
    @Override
    public Path getModelFilePath() {
        return Paths.get(getModelFolderPath().toString(), XFW_MODEL_FILE_NAME + "." + XFW_MODEL_FILE_EXTENSION);
    }

    @Override
    public URI getModelFileUri(URI sorceFolderUri) {
        String sourceFolderPath = sorceFolderUri.getPath();
        return sorceFolderUri.resolve(sourceFolderPath + (sourceFolderPath.endsWith("/") ? "" : "/") + XFW_MODEL_FILE_PACKAGE_NAME.replaceAll("\\.", "/") + "/")
                .resolve(XFW_MODEL_FILE_NAME + "." + XFW_MODEL_FILE_EXTENSION);
    }

    @Override
    public String getModelFileExtension() {
        return XFW_MODEL_FILE_EXTENSION;
    }

    @Override
    public Path getModelFolderPath() {
        return Paths.get(XFW_MODEL_FILE_PACKAGE_NAME.replaceAll("\\.", File.separator) + File.separator);
    }

	public String getModelFileName() {
		return XFW_MODEL_FILE_NAME;
	}

	
} //XFWMMPackageImpl
