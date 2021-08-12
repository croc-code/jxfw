package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreSwitch;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWReference;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwNamedElement;
import ru.croc.ctp.jxfw.metamodel.util.XFWMMSwitch;

/**
 * Фабрика адаптеров к runtime API метамодели.
 */
public class XfwRuntimeAdapterFactory extends AdapterFactoryImpl {

    /**
     * Статический экземпляр.
     */
    public static final XfwRuntimeAdapterFactory INSTANCE = new XfwRuntimeAdapterFactory();

    /**
     * Конструктор.
     */
    public XfwRuntimeAdapterFactory() {
    }

    @Override
    public boolean isFactoryForType(Object type) {
        return XfwNamedElement.class.isAssignableFrom((Class<?>) type)
                || type == XfwAnnotation.class;
    }

    protected XFWMMSwitch<Adapter> modelSwitch =
            new XFWMMSwitch<Adapter>() {
                @Override
                public Adapter caseXFWClass(XFWClass object) {
                    return createXFWClassAdapter(object);
                }

                @Override
                public Adapter caseXFWAttribute(XFWAttribute object) {
                    return createXFWAttributeAdapter(object);
                }

                @Override
                public Adapter caseXFWReference(XFWReference object) {
                    return createXFWReferenceAdapter(object);
                }
            };


    protected EcoreSwitch<Adapter> ecoreModelSwitch = new EcoreSwitch<Adapter>() {
        @Override
        public Adapter caseENamedElement(ENamedElement object) {
            return createENamedElementAdapter(object);
        }

        @Override
        public Adapter caseEClassifier(EClassifier object) {
            return createEClassifierAdapter(object);
        }

        @Override
        public Adapter caseEStructuralFeature(EStructuralFeature object) {
            return createEStructuralFeatureAdapter(object);
        }

        @Override
        public Adapter caseEAnnotation(EAnnotation object) {
            return createEAnnotation(object);
        }

        @Override
        public Adapter caseEEnum(EEnum object) {
            return createEEnum(object);
        }

        @Override
        public Adapter caseEEnumLiteral(EEnumLiteral object) {
            return createEEnumLiteral(object);
        }

    };

    private Adapter createEEnumLiteral(EEnumLiteral object) {
        return new XfwEnumLiteralImpl(object);
    }

    private Adapter createEEnum(EEnum object) {
        return new XfwEnumerationImpl(object);
    }

    private Adapter createEAnnotation(EAnnotation object) {
        return new XfwAnnotationImpl(object);
    }


    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        Adapter result = modelSwitch.doSwitch((EObject) target);
        return result == null ? ecoreModelSwitch.doSwitch((EObject) target) : result;
    }


    /**
     * Creates a new adapter for an object of class '{@link XFWClass <em>XFW Class</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     * @see XFWClass
     */
    public Adapter createXFWClassAdapter(XFWClass object) {
        return new XfwClassImpl(object);
    }

    /**
     * Creates a new adapter for an object of class '{@link XFWAttribute <em>XFW Attribute</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     * @see XFWAttribute
     */
    public Adapter createXFWAttributeAdapter(XFWAttribute object) {
        return new XfwAttributeImpl(object);
    }

    /**
     * Creates a new adapter for an object of class '{@link XFWReference <em>XFW Reference</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     * @see XFWReference
     */
    public Adapter createXFWReferenceAdapter(XFWReference object) {
        return new XfwReferenceImpl(object);
    }


    /**
     * Creates a new adapter for an object of class '{@link ENamedElement <em>ENamed Element</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     * @see ENamedElement
     */
    public Adapter createENamedElementAdapter(ENamedElement target) {
        return new XfwNamedElementImpl(target);
    }


    /**
     * Creates a new adapter for an object of class '{@link EClassifier <em>EClassifier</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     * @see EClassifier
     */
    public Adapter createEClassifierAdapter(EClassifier object) {
        return new XfwClassifierImpl(object);
    }


    /**
     * Creates a new adapter for an object of class '{@link EStructuralFeature <em>EStructural Feature</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     *
     * @return the new adapter.
     * @generated
     * @see EStructuralFeature
     */
    public Adapter createEStructuralFeatureAdapter(EStructuralFeature object) {
        return new XfwStructuralFeatureImpl(object);
    }


} //XFWMMAdapterFactory
