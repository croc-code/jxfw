package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import ru.croc.ctp.jxfw.metamodel.XFWAttribute;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.impl.XfwLocalizableAdapterFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAttribute;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwLocalizable;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwReference;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwStructuralFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Реализация XfwClass, оборачивающая XFWClass.
 */
public class XfwClassImpl extends XfwClassifierImpl implements XfwClass {

    private final XfwAttribute idField;
    private final XfwAttribute versionField;


    /**
     * Конструктор.
     *
     * @param xfwClass делегат
     */
    public XfwClassImpl(XFWClass xfwClass) {
        super(xfwClass);
        XfwAttribute idCandidate = null;
        XfwAttribute versionCandidate = null;
        for (XfwAttribute attribute : getEAllAttributes()) {
            if (attribute.isIdField()) {
                idCandidate = attribute;
            }
            if (attribute.isVersionField()) {
                versionCandidate = attribute;
            }
        }
        this.idField = idCandidate;
        this.versionField = versionCandidate;
    }


    @Override
    public List<String> getPersistenceModule() {
        return ((XFWClass) target).getPersistenceModule();
    }

    @Override
    public boolean isComplexType() {
        return ((XFWClass) target).isComplexType();
    }

    @Override
    public String getKeyTypeName() {
        return ((XFWClass) target).getKeyTypeName();
    }

    @Override
    public String getPersistenceType() {
        return ((XFWClass) target).getPersistenceType();
    }

    @Override
    public XfwAttribute findAttribute(String name) {
        XFWAttribute result = ((XFWClass) target).findAttribute(name);
        return result == null ? null : (XfwAttribute) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(result, XfwAttribute.class);
    }


    @Override
    public boolean isTransientType() {
        return ((XFWClass) target).isTransientType();
    }

    @Override
    public boolean isPersistentType() {
        return ((XFWClass) target).isPersistentType();
    }

    @Override
    public XfwStructuralFeature getChainingEStructuralFeature(String propNameChain) {

        List<String> properties = Arrays.asList(propNameChain.split("\\.", 2));
        switch (properties.size()) {
            case 0:
                return null;
            case 1:
                return getEStructuralFeature(propNameChain);
            default:
                XfwStructuralFeature feature = getEStructuralFeature(properties.get(0));
                if (feature.getEType() instanceof XfwClass) {
                    return ((XfwClass) feature.getEType()).getChainingEStructuralFeature(properties.get(1));
                }
                return null;
        }
    }

    @Override
    public boolean isAbstract() {
        return ((XFWClass) target).isAbstract();
    }

    @Override
    public List<XfwClass> getESuperTypes() {
        List<XfwClass> result = new ArrayList<>();
        for (EClass eClass : ((XFWClass) target).getESuperTypes()) {
            result.add((XfwClass) XfwRuntimeAdapterFactory.INSTANCE.adapt(eClass, XfwClass.class));
        }
        return result;
    }

    @Override
    public List<XfwClass> getEAllSuperTypes() {
        List<XfwClass> result = new ArrayList<>();
        for (EClass eClass : ((XFWClass) target).getEAllSuperTypes()) {
            result.add((XfwClass) XfwRuntimeAdapterFactory.INSTANCE
                    .adapt(eClass, XfwClass.class));
        }
        return result;
    }

    @Override
    public List<XfwStructuralFeature> getEStructuralFeatures() {
        List<XfwStructuralFeature> result = new ArrayList<>();
        for (EStructuralFeature feature : ((XFWClass) target).getEStructuralFeatures()) {
            result.add((XfwStructuralFeature) XfwRuntimeAdapterFactory.INSTANCE
                    .adapt(feature, XfwStructuralFeature.class));
        }
        return result;
    }

    @Override
    public List<XfwAttribute> getEAttributes() {
        List<XfwAttribute> result = new ArrayList<>();
        for (EAttribute attribute : ((XFWClass) target).getEAttributes()) {
            result.add((XfwAttribute) XfwRuntimeAdapterFactory.INSTANCE
                    .adapt(attribute, XfwAttribute.class));
        }
        return result;
    }

    @Override
    public List<XfwAttribute> getEAllAttributes() {
        List<XfwAttribute> result = new ArrayList<>();
        for (EAttribute attribute : ((XFWClass) target).getEAllAttributes()) {
            result.add((XfwAttribute) XfwRuntimeAdapterFactory.INSTANCE
                    .adapt(attribute, XfwAttribute.class));
        }
        return result;
    }

    @Override
    public List<XfwReference> getEReferences() {
        List<XfwReference> result = new ArrayList<>();
        for (EReference ref : ((XFWClass) target).getEReferences()) {
            result.add((XfwReference) XfwRuntimeAdapterFactory.INSTANCE.adapt(ref, XfwReference.class));
        }
        return result;
    }

    @Override
    public List<XfwReference> getEAllReferences() {
        List<XfwReference> result = new ArrayList<>();
        for (EReference ref : ((XFWClass) target).getEAllReferences()) {
            result.add((XfwReference) XfwRuntimeAdapterFactory.INSTANCE.adapt(ref, XfwReference.class));
        }
        return result;
    }

    @Override
    public List<XfwStructuralFeature> getEAllStructuralFeatures() {
        List<XfwStructuralFeature> result = new ArrayList<>();
        for (EStructuralFeature feature : ((XFWClass) target).getEAllStructuralFeatures()) {
            result.add((XfwStructuralFeature) XfwRuntimeAdapterFactory.INSTANCE
                    .adapt(feature, XfwStructuralFeature.class));
        }
        return result;
    }


    @Override
    public boolean isSuperTypeOf(XfwClass eclass) {
        return ((XFWClass) target).isSuperTypeOf((XFWClass) ((XfwClassImpl) eclass).target);
    }

    @Override
    public int getFeatureCount() {
        return ((XFWClass) target).getFeatureCount();
    }


    @Override
    public XfwStructuralFeature getEStructuralFeature(String var1) {
        EStructuralFeature result = ((XFWClass) target).getEStructuralFeature(var1);
        return result == null ? null : (XfwStructuralFeature) XfwRuntimeAdapterFactory.INSTANCE
                .adapt(result, XfwStructuralFeature.class);
    }


    @Override
    public String getLocalizedTypeName(String lang) {
        return ((XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE.adapt(target, XfwLocalizable.class))
                .getLocalizedTypeName(lang);
    }

    @Override
    public String getLocalizedFieldName(String fieldName, String lang) {
        return ((XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE.adapt(target, XfwLocalizable.class))
                .getLocalizedFieldName(fieldName, lang);
    }

    @Override
    public Set<String> getAvailableLanguages() {
        return ((XfwLocalizable) XfwLocalizableAdapterFactory.INSTANCE.adapt(target, XfwLocalizable.class))
                .getAvailableLanguages();
    }


    @Override
    public Set<XfwStructuralFeature> getScalarFieldsOfType(Class<?> clazz) {
        return getFieldsOfType(clazz, false);
    }


    @Override
    public Set<XfwStructuralFeature> getMassiveFieldsOfType(Class<?> clazz) {
        return getFieldsOfType(clazz, true);
    }

    @Override
    public Set<XfwStructuralFeature> getComplexFields() {
        Set<XfwStructuralFeature> result = new HashSet<>();
        for (XfwReference ref : getEAllReferences()) {
            if (ref.getEReferenceType().isComplexType()) {
                result.add(ref);
            }
        }
        return result;
    }

    @Override
    public Set<XfwStructuralFeature> getEnumFields() {
        return getFieldsOfType(Enum.class, false);
    }

    @Override
    public Set<XfwStructuralFeature> getEnumFlagsFields() {
        return getFieldsOfType(Enum.class, true);
    }


    @Override
    public XfwAttribute getIdField() {
        return idField;
    }

    @Override
    public XfwAttribute getVersionField() {
        return versionField;
    }

    private Set<XfwStructuralFeature> getFieldsOfType(Class<?> clazz, boolean isMany) {
        Set<XfwStructuralFeature> result = new HashSet<>();
        for (XfwStructuralFeature xfwStructuralFeature : getEAllStructuralFeatures()) {
            if (xfwStructuralFeature.isMany() == isMany
                    && xfwStructuralFeature.isFieldOfType(clazz)) {
                result.add(xfwStructuralFeature);
            }
        }
        return result;

    }

    @Override
    public Map<String, XfwStructuralFeature> getFeaturesFlatMap(){
        Map<String, XfwStructuralFeature> result = new HashMap<>();
        for (Map.Entry<String, EStructuralFeature>  entry : ((XFWClass) target).getFeaturesFlatMap().entrySet()) {
            result.put(entry.getKey(), (XfwStructuralFeature) XfwRuntimeAdapterFactory.INSTANCE
                    .adapt(entry.getValue(), XfwStructuralFeature.class));
        }
        return result;
    }



}
