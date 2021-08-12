package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;

import java.util.HashSet;
import java.util.Set;

public class LocalizationUtils {


    static Set<String> getAvailableLanguages(EModelElement modelElement) {
        Set<String> availableLanguages = new HashSet<>();
        for (EAnnotation eAnnotation : modelElement.getEAnnotations()) {
            if (eAnnotation.getSource().equals(XFWConstants.I18N_ANNOTATION_SOURCE.getUri())
                    && (eAnnotation.getDetails().size() > 0)) {
                if (eAnnotation.getDetails().get("propName") == null) {// oldstyle ecore
                    availableLanguages.addAll(eAnnotation.getDetails().keySet());
                } else {
                    availableLanguages.add(eAnnotation.getDetails().get("lang"));
                }
            }
        }

        return availableLanguages;

    }

    static String getLocalizedName(ENamedElement modelElement, String lang) {
        if (modelElement == null) {
            return null;
        }

        String result = modelElement.getName();
        for (EAnnotation eAnnotation : modelElement.getEAnnotations()) {
            if (eAnnotation.getSource().equals(XFWConstants.I18N_ANNOTATION_SOURCE.getUri())
                    && (eAnnotation.getDetails().size() > 0)) {
                if (eAnnotation.getDetails().get("propName") == null) {// oldstyle ecore
                    result = eAnnotation.getDetails().get(lang);
                } else {
                    if (eAnnotation.getDetails().get("lang").equals(lang)
                            && eAnnotation.getDetails().get("propName").isEmpty()) {
                        result = eAnnotation.getDetails().get("value");
                    }
                }
            }
        }

        return result;

    }

    static String getLocalizedPropName(EClass eclass, String propName, String lang) {
        String result = null;
        for (EAnnotation eAnnotation : eclass.getEAnnotations()) {
            if (eAnnotation.getSource().equals(XFWConstants.I18N_ANNOTATION_SOURCE.getUri())
                    && (eAnnotation.getDetails().size() > 0)
                    && eAnnotation.getDetails().get("propName") != null // newstyle ecore
                    && eAnnotation.getDetails().get("propName").equals(propName)
                    && eAnnotation.getDetails().get("lang").equals(lang)) {
                return eAnnotation.getDetails().get("value");
            }
        }

        return result;

    }

}
