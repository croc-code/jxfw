package ru.croc.ctp.jxfw.facade;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.apache.commons.lang.ClassUtils;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Утильные методы общие для всех генераторов.
 *
 * @author Sergey Verkhushin
 * @since 1.6
 */
public class FacadeGeneratorUtils {

    /**
     * Получить имя класса первичного ключа в терминах javaPoet.
     *
     * @param xfwClass                      метаданные класса
     * @param generateControllerForFulltext generateControllerForFulltext
     * @return имя класса первичного ключа в терминах javaPoet
     */
    @SuppressWarnings("unchecked")
    public static <T extends TypeName> T getKeyClassName(XFWClass xfwClass, boolean generateControllerForFulltext) {
        String name = xfwClass.getKeyTypeName();
        if (generateControllerForFulltext) {
            List<EStructuralFeature> foundKeys = new ArrayList<>();
            for (EStructuralFeature feature : xfwClass.getEStructuralFeatures()) {
                for (EAnnotation annotation : feature.getEAnnotations()) {
                    if (XFWConstants.PRIMARY_KEY_ANNOTATION_SOURCE.getUri().equals(annotation.getSource())) {
                        foundKeys.add(feature);
                        break;
                    }
                }
            }

            if (foundKeys.size() > 1 || xfwClass.getPersistenceModule().contains("JPA")
                    || foundKeys.get(0).getEType().getName().equals("EString")) {
                return (T) ClassName.get(String.class);
            }

            name = foundKeys.get(0).getEType().getInstanceClassName();
        }

        return (T) ClassName.get(ClassUtils.getPackageName(name), ClassUtils.getShortClassName(name));
    }

    /**
     * Создает {@link FacadeGeneratorUtils}.
     */
    private FacadeGeneratorUtils() {
    }
}
