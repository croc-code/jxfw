package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreSwitch;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwLocalizable;
import ru.croc.ctp.jxfw.metamodel.util.XFWMMSwitch;

/**
 * Фабрика адаптеров к интерфесу XfwLocalizable.
 */
public class XfwLocalizableAdapterFactory extends AdapterFactoryImpl {

    /**
     * Статический экземпляр.
     */
    public static final XfwLocalizableAdapterFactory INSTANCE = new XfwLocalizableAdapterFactory();

    /**
     * Констурктор.
     */
    public XfwLocalizableAdapterFactory() {
    }

    @Override
    public boolean isFactoryForType(Object type) {
        return type == XfwLocalizable.class;
    }


    protected XFWMMSwitch<Adapter> modelSwitch =
            new XFWMMSwitch<Adapter>() {
                @Override
                public Adapter caseXFWClass(XFWClass object) {
                    return createXFWClassAdapter(object);
                }
            };

    private Adapter createXFWClassAdapter(XFWClass object) {
        return new XfwLocalizableClassAdapter(object);

    }

    protected EcoreSwitch<Adapter> ecoreModelSwitch = new EcoreSwitch<Adapter>() {
        @Override
        public Adapter caseEEnum(EEnum object) {
            return createEEnum(object);
        }

    };

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


    private Adapter createEEnum(EEnum object) {
        return new XFWLocalizableEnumerationAdapter(object);
    }


} //XFWMMAdapterFactory
