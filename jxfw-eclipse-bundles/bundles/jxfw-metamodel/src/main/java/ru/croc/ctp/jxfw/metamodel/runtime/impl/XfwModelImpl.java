package ru.croc.ctp.jxfw.metamodel.runtime.impl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWModel;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClassifier;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Метамодель в рантайм.
 * Created by OKrutova on 18.07.2017.
 */
public class XfwModelImpl implements XfwModel {


    private static final Logger logger = LoggerFactory.getLogger(XfwModelImpl.class);
    // валидный java-идентификатор, точка или решетка в квадратных скобках
    private final Pattern pattern = Pattern.compile("\\[[\\w$.#]*\\]");

    private Map<String, Set<String>> simpleNameToFqNameMap = new HashMap<>();
    private Map<String, XfwClassifier> fqNameToClassifierMap = new HashMap<>();
    private final XFWModel xfwModel;


    /**
     * Конструктор загружает метамодель по набору URI
     * и сторит кэш имен классов.
     *
     * @param uris URI моделей.
     */
    public XfwModelImpl(URI... uris) {
        xfwModel = new XFWModelImpl(uris);
        initModel();
    }


    private void initModel() {
        for (EClassifier eclassifier : xfwModel.getAll(EClassifier.class)) {
            String simpleName = eclassifier.getName();
            String fqName = eclassifier.getInstanceClassName();

            if ((eclassifier instanceof XFWClass)
                    || (eclassifier instanceof EEnum)) {
                if (simpleNameToFqNameMap.get(simpleName) == null) {
                    simpleNameToFqNameMap.put(simpleName, new HashSet<String>());
                }
                Set<String> fqNames = simpleNameToFqNameMap.get(simpleName);
                fqNames.add(fqName);

                XfwClassifier xfwClassifier
                        = (XfwClassifier) XfwRuntimeAdapterFactory.INSTANCE.adapt(eclassifier, XfwClassifier.class);
                fqNameToClassifierMap.put(fqName, xfwClassifier);


               /* проинициализируем все адаптеры сразу, чтобы избежать проблем с многопоточностью
                при ленивой инициализации*/

                xfwClassifier.getEAnnotations();

                if (xfwClassifier instanceof XfwClass) {
                    ((XfwClass) xfwClassifier).getEAllStructuralFeatures();
                }
                if (xfwClassifier instanceof XfwEnumeration) {
                    ((XfwEnumeration) xfwClassifier).getELiterals();
                }

            }

        }


    }

    @Override
    public <T extends XfwClassifier> Set<T> getAll(Class<T> type) {
        Set<T> result = new HashSet<T>();
        for (XfwClassifier xfwClassifier : fqNameToClassifierMap.values()) {
            if (type.isAssignableFrom(xfwClassifier.getClass())) {
                result.add(type.cast(xfwClassifier));
            }
        }
        return result;
    }

    @Override
    public boolean isKnownType(String className) {
        return find(className, XfwClassifier.class) != null;
    }

    @Override
    public Set<String> getAvailableLanguages() {
        return xfwModel.getAvailableLanguages();
    }

    @Override
    public <T extends XfwClassifier> T findBySimpleName(String name, Class<T> type) {
        Set<String> fqNames = simpleNameToFqNameMap.get(name);
        if (fqNames == null) {
            return null;
        }
        if (fqNames.size() > 1) {
            throw new IllegalStateException("Several classes with same simpleName " + name + " : " + fqNames);
        }
        for (String fqName: fqNames) {
            T result = findByFqName(fqName, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
	@Override
    public <T extends XfwClassifier> T findByFqName(String name, Class<T> type) {
        XfwClassifier result = fqNameToClassifierMap.get(name);
        if (result == null) {
            return null;
        }
        if (type.isAssignableFrom(result.getClass())) {
            return (T) result;
        }
        return null;
    }

    @Override
    public <T extends XfwClassifier> T find(String name, Class<T> type) {

        T eclassifier = findByFqName(name, type);
        if (eclassifier != null) {
            return eclassifier;
        }
        eclassifier = findBySimpleName(name, type);
        return eclassifier;
    }


    @Override
    public <T extends XfwClassifier> T findBySimpleNameThrowing(String name, Class<T> type) {
        T obj = findBySimpleName(name, type);
        if (obj == null) {
            throw new IllegalArgumentException("Class not found in metamodel " + name);
        }
        return obj;

    }

    @Override
    public <T extends XfwClassifier> T findByFqNameThrowing(String name, Class<T> type) {
        T obj = findByFqName(name, type);
        if (obj == null) {
            throw new IllegalArgumentException("Class not found in metamodel " + name);
        }
        return obj;

    }


    @Override
    public <T extends XfwClassifier> T findThrowing(String name, Class<T> type) {
        T obj = find(name, type);
        if (obj == null) {
            throw new IllegalArgumentException("Class not found in metamodel " + name);
        }
        return obj;
    }

    @Override
    public String resolveMetadata(String input, String language) {

        Matcher matcher = pattern.matcher(input);

        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.start());
            ends.add(matcher.end());
        }
        String result = input;

        for (int i = starts.size() - 1; i >= 0; i--) {

            String placeholder = result.substring(starts.get(i) + 1, ends.get(i) - 1);
            int pos = placeholder.indexOf("#");
            String type = placeholder;
            String field = "";
            if (pos >= 0) {
                type = placeholder.substring(0, pos);
                field = placeholder.substring(pos + 1);
            }
            XfwClass xfwClass = find(type, XfwClass.class);
            if (xfwClass != null) {
                String i18n = xfwClass.getLocalizedTypeName(language);
                if (!field.isEmpty()) {
                    i18n = xfwClass.getLocalizedFieldName(field, language);
                }
                if (i18n != null) {
                    result = result.substring(0, starts.get(i))
                            + i18n
                            + result.substring(ends.get(i));
                }
            }
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
	@Override
    public XfwEnumeration findEnum(Class<? extends Enum> clazz) {
        return findByFqNameThrowing(clazz.getCanonicalName(), XfwEnumeration.class);
    }


}
