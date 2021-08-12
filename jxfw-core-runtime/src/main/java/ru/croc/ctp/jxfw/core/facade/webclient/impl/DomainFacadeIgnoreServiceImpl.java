package ru.croc.ctp.jxfw.core.facade.webclient.impl;

import static ru.croc.ctp.jxfw.metamodel.XFWConstants.FACADE_IGNORE_ANNOTATION;
import static ru.croc.ctp.jxfw.metamodel.XFWConstants.SERVER_ONLY_ANNOTATION;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainFacadeIgnoreService;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwAnnotation;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwNamedElement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация: сервис позволяющий определить включенно ли игнорирование
 * для указанной связки фасада и типа доменного объекта.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Service
public class DomainFacadeIgnoreServiceImpl implements DomainFacadeIgnoreService {
    private List<String> facadesOfDefaultEnable;


    /**
     * Конструктор.
     *
     * @param facadesOfDefaultEnable список фасадов для которых включенно игнорирование
     *                                 (аннотацией {@link XFWFacadeIgnore}) по умолчанию
     */
    public DomainFacadeIgnoreServiceImpl(@Value("${domain.ignore.facades:#{null}}") String[] facadesOfDefaultEnable) {
        this.facadesOfDefaultEnable = facadesOfDefaultEnable == null ? null : Arrays.asList(facadesOfDefaultEnable);
    }

    @Override
    public boolean isIgnore(String typeName, String facadeName) {
        final XfwClass clazz = XfwModelFactory.getInstance().findBySimpleName(typeName, XfwClass.class);
        if (clazz == null || facadeName == null) {
            return false;
        }
        final XfwAnnotation facadeIgnoreAnnotation = clazz.getEAnnotation(FACADE_IGNORE_ANNOTATION.getUri());
        final XfwAnnotation serverOnlyAnnotation = clazz.getEAnnotation(SERVER_ONLY_ANNOTATION.getUri());
        if (serverOnlyAnnotation != null) {
            return true;
        }
        if (facadeIgnoreAnnotation == null) {
            return false;
        }
        if (isIncludeFacadeList(facadeIgnoreAnnotation, facadeName)) {
            return true;
        }
        return isFacadeListEmpty(facadeIgnoreAnnotation) && isIgnoreFacadeByDefault(facadeName);
    }

    /**
     * Проверяет игнорируется ли фасад по умолчанию(глобальная настройка).
     *
     * @param facadeName имя фасада
     * @return true, если фасад игнорируется по умолчанию.
     */
    protected boolean isIgnoreFacadeByDefault(String facadeName) {
        return facadesOfDefaultEnable == null || facadesOfDefaultEnable.contains(facadeName);
    }

    @Override
    public boolean isIgnore(String typeName, String fieldName, String facadeName) {
        return getIgnoredFields(typeName, facadeName).contains(fieldName);
    }

    @Override
    public List<String> getIgnoredFields(String typeName, String facadeName) {
        final XfwClass xfwClass = XfwModelFactory.getInstance().findBySimpleName(typeName, XfwClass.class);
        if (xfwClass == null || facadeName == null) {
            return Collections.emptyList();
        }
        return xfwClass.getEAllStructuralFeatures().stream()
                .filter(field -> {
                    final XfwAnnotation xfwFacadeIgnore = field.getEAnnotation(FACADE_IGNORE_ANNOTATION.getUri());
                    final XfwAnnotation xfwServerOnly = field.getEAnnotation(SERVER_ONLY_ANNOTATION.getUri());
                    if (xfwServerOnly != null) {
                        return true;
                    }
                    if (xfwFacadeIgnore == null) {
                        return false;
                    }
                    final List<String> facades = getFacadeList(xfwFacadeIgnore);
                    return facades.contains(facadeName) || (facades.isEmpty() && isIgnoreFacadeByDefault(facadeName));
                })
                .map(XfwNamedElement::getName)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет включен ли фасад в список фасадов {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore}.
     *
     * @param facadeIgnoreAnnotation аннотация
     * @param facadeName название фасада
     * @return true если пуст
     */
    protected boolean isIncludeFacadeList(XfwAnnotation facadeIgnoreAnnotation, String facadeName) {
        return getFacadeList(facadeIgnoreAnnotation).contains(facadeName);
    }

    /**
     * Проверяет пуст ли список фасадов у анатации {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore}.
     *
     * @param facadeIgnoreAnnotation аннотация
     * @return true если пуст
     */
    protected boolean isFacadeListEmpty(XfwAnnotation facadeIgnoreAnnotation) {
        return getFacadeList(facadeIgnoreAnnotation).isEmpty();
    }

    /**
     * Достаёт из {@link ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore} список фасадов.
     *
     * @param facadeIgnoreAnnotation аннотация
     * @return список фасадов
     */
    protected List<String> getFacadeList(XfwAnnotation facadeIgnoreAnnotation) {
        String facades = facadeIgnoreAnnotation.getDetails().get("facades");
        // парсим строковое пердаставление массива строк
        facades = facades.substring(1, facades.length() - 1);
        if (facades.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(facades.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
