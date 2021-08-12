/**
 */
package ru.croc.ctp.jxfw.XtendMetaModel.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import ru.croc.ctp.jxfw.XtendMetaModel.XMMFactory;
import ru.croc.ctp.jxfw.XtendMetaModel.XMMPackage;
import ru.croc.ctp.jxfw.XtendMetaModel.XtendClass;

/**
 * This is the item provider adapter for a {@link ru.croc.ctp.jxfw.XtendMetaModel.XtendClass} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class XtendClassItemProvider extends ItemProviderAdapter {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XtendClassItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addNamePropertyDescriptor(object);
            addInstanceClassNamePropertyDescriptor(object);
            addInstanceClassPropertyDescriptor(object);
            addDefaultValuePropertyDescriptor(object);
            addInstanceTypeNamePropertyDescriptor(object);
            addAbstractPropertyDescriptor(object);
            addInterfacePropertyDescriptor(object);
            addESuperTypesPropertyDescriptor(object);
            addEAllAttributesPropertyDescriptor(object);
            addEAllReferencesPropertyDescriptor(object);
            addEReferencesPropertyDescriptor(object);
            addEAttributesPropertyDescriptor(object);
            addEAllContainmentsPropertyDescriptor(object);
            addEAllOperationsPropertyDescriptor(object);
            addEAllStructuralFeaturesPropertyDescriptor(object);
            addEAllSuperTypesPropertyDescriptor(object);
            addEIDAttributePropertyDescriptor(object);
            addEAllGenericSuperTypesPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ENamedElement_name_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_ENamedElement_name_feature", "_UI_ENamedElement_type"),
                 EcorePackage.Literals.ENAMED_ELEMENT__NAME,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Instance Class Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInstanceClassNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClassifier_instanceClassName_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClassifier_instanceClassName_feature", "_UI_EClassifier_type"),
                 EcorePackage.Literals.ECLASSIFIER__INSTANCE_CLASS_NAME,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Instance Class feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInstanceClassPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClassifier_instanceClass_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClassifier_instanceClass_feature", "_UI_EClassifier_type"),
                 EcorePackage.Literals.ECLASSIFIER__INSTANCE_CLASS,
                 false,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Default Value feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addDefaultValuePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClassifier_defaultValue_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClassifier_defaultValue_feature", "_UI_EClassifier_type"),
                 EcorePackage.Literals.ECLASSIFIER__DEFAULT_VALUE,
                 false,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Instance Type Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInstanceTypeNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClassifier_instanceTypeName_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClassifier_instanceTypeName_feature", "_UI_EClassifier_type"),
                 EcorePackage.Literals.ECLASSIFIER__INSTANCE_TYPE_NAME,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Abstract feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAbstractPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_abstract_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_abstract_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__ABSTRACT,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Interface feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInterfacePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_interface_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_interface_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__INTERFACE,
                 true,
                 false,
                 false,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the ESuper Types feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addESuperTypesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eSuperTypes_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eSuperTypes_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__ESUPER_TYPES,
                 true,
                 false,
                 true,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll Attributes feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllAttributesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllAttributes_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllAttributes_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_ATTRIBUTES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll References feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllReferencesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllReferences_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllReferences_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_REFERENCES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EReferences feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEReferencesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eReferences_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eReferences_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EREFERENCES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAttributes feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAttributesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAttributes_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAttributes_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EATTRIBUTES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll Containments feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllContainmentsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllContainments_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllContainments_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_CONTAINMENTS,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll Operations feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllOperationsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllOperations_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllOperations_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_OPERATIONS,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll Structural Features feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllStructuralFeaturesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllStructuralFeatures_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllStructuralFeatures_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_STRUCTURAL_FEATURES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll Super Types feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllSuperTypesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllSuperTypes_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllSuperTypes_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_SUPER_TYPES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EID Attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEIDAttributePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eIDAttribute_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eIDAttribute_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EID_ATTRIBUTE,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the EAll Generic Super Types feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addEAllGenericSuperTypesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_EClass_eAllGenericSuperTypes_feature"),
                 getString("_UI_PropertyDescriptor_description", "_UI_EClass_eAllGenericSuperTypes_feature", "_UI_EClass_type"),
                 EcorePackage.Literals.ECLASS__EALL_GENERIC_SUPER_TYPES,
                 false,
                 false,
                 false,
                 null,
                 null,
                 null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS);
            childrenFeatures.add(EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS);
            childrenFeatures.add(EcorePackage.Literals.ECLASS__EOPERATIONS);
            childrenFeatures.add(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES);
            childrenFeatures.add(EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES);
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature(Object object, Object child) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns XtendClass.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/XtendClass"));
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((XtendClass)object).getName();
        return label == null || label.length() == 0 ?
            getString("_UI_XtendClass_type") :
            getString("_UI_XtendClass_type") + " " + label;
    }
    

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        updateChildren(notification);

        switch (notification.getFeatureID(XtendClass.class)) {
            case XMMPackage.XTEND_CLASS__NAME:
            case XMMPackage.XTEND_CLASS__INSTANCE_CLASS_NAME:
            case XMMPackage.XTEND_CLASS__INSTANCE_CLASS:
            case XMMPackage.XTEND_CLASS__DEFAULT_VALUE:
            case XMMPackage.XTEND_CLASS__INSTANCE_TYPE_NAME:
            case XMMPackage.XTEND_CLASS__ABSTRACT:
            case XMMPackage.XTEND_CLASS__INTERFACE:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case XMMPackage.XTEND_CLASS__EANNOTATIONS:
            case XMMPackage.XTEND_CLASS__ETYPE_PARAMETERS:
            case XMMPackage.XTEND_CLASS__EOPERATIONS:
            case XMMPackage.XTEND_CLASS__ESTRUCTURAL_FEATURES:
            case XMMPackage.XTEND_CLASS__EGENERIC_SUPER_TYPES:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
     * that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add
            (createChildParameter
                (EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
                 XMMFactory.eINSTANCE.createXtendAttribute()));
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return XtendMetaModelEditPlugin.INSTANCE;
    }

}
