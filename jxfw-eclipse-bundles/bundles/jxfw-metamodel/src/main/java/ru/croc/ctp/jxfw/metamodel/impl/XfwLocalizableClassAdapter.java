package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwLocalizable;

import java.util.Set;


/**
 * Реализация XfwLocalizable, оборачивающая EClass.
 */
public class XfwLocalizableClassAdapter extends AdapterImpl implements XfwLocalizable {

    /**
     * Конструктор.
     *
     * @param eclass делегат
     */
    public XfwLocalizableClassAdapter(EClass eclass) {
        this.target = eclass;

    }

    @Override
    public boolean isAdapterForType(Object type) {
        return type == XfwLocalizable.class;
    }


    @Override
    public String getLocalizedTypeName(String lang) {
        return LocalizationUtils.getLocalizedName((EClass) target, lang);
    }


    @Override
    public String getLocalizedFieldName(String fieldName, String lang) {
        EClass eclass = (EClass) target;
        EStructuralFeature feature = eclass.getEStructuralFeature(fieldName);
        // сначала ищем переопределения
        do {

            String result = LocalizationUtils.getLocalizedPropName(eclass, fieldName, lang);
            if (result != null) {
                return result;
            }
            eclass = (eclass.getESuperTypes().size() > 0) ? eclass.getESuperTypes().get(0) : null;

        }
        while (eclass != null);
        return LocalizationUtils.getLocalizedName(feature, lang);
    }

    @Override
    public Set<String> getAvailableLanguages() {
        Set<String> availableLanguages = LocalizationUtils.getAvailableLanguages((EClass) target);

        for (EStructuralFeature feature : ((EClass) target).getEAllStructuralFeatures()) {
            availableLanguages.addAll(LocalizationUtils.getAvailableLanguages(feature));
        }
        return availableLanguages;
    }

}
