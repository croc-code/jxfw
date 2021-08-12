package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwLocalizable;

import java.util.Set;


/**
 * Реализация XfwLocalizable, оборачивающая EEnum.
 */
public class XFWLocalizableEnumerationAdapter extends AdapterImpl implements XfwLocalizable {

    /**
     * Конструктор.
     *
     * @param eenum делегат
     */
    public XFWLocalizableEnumerationAdapter(EEnum eenum) {
        this.target = eenum;

    }

    @Override
    public boolean isAdapterForType(Object type) {
        return type == XfwLocalizable.class;
    }



    @Override
    public String getLocalizedTypeName(String lang) {
        return LocalizationUtils.getLocalizedName( ((EEnum) target), lang);
    }


    @Override
    public String getLocalizedFieldName(String fieldName, String lang) {
        // у перечислений нет наследования => нет возможности переопределять лейблы
        return LocalizationUtils.getLocalizedName( ((EEnum) target).getEEnumLiteral(fieldName), lang);
    }

    @Override
    public Set<String> getAvailableLanguages() {
        Set<String> availableLanguages = LocalizationUtils.getAvailableLanguages( ((EEnum) target));

        for (EEnumLiteral feature :  ((EEnum) target).getELiterals()) {
            availableLanguages.addAll(LocalizationUtils.getAvailableLanguages(feature));
        }
        return availableLanguages;
    }

}
