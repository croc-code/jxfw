package ru.croc.ctp.jxfw.metamodel.impl;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * URI аннотаций и методы для работы с аннотациями.
 * <p/>
 * Created by SPlaunov on 27.07.2016.
 */
class AnnotationsSources {

    /**
     * Параметры jXFW аннотации {@code XFWBlobInfo}.
     */
    static class BlobAdditionalFields {
        /**
         * Имя ecore-аннотации.
         */
        static final String SOURCE = "http://www.croc.ru/ctp/model/blobAdditionalFields";
        /**
         * Имя параметра для хранения значения {@code XFWBlobInfo.value}.
         */
        static final String GENERATE_FIELDS_PARAM = "value";
        /**
         * Имя параметра для хранения {@code XFWBlobInfo.contentTypeFieldNameSuffix}.
         */
        static final String CONTENT_TYPE_SUFFIX_PARAM = "contenTypeSuffix";
        /**
         * Имя параметра для хранения {@code XFWBlobInfo.sizeFieldNameSuffix}.
         */
        static final String CONTENT_SIZE_SUFFIX_PARAM = "contentSizeSuffix";
        /**
         * Имя параметра для хранения {@code XFWBlobInfo.fileNameFieldNameSuffix}.
         */
        static final String FILE_NAME_SUFFIX_PARAM = "fileNameSuffix";
    }

    /**
     * Ищет аннотацию с заданным URI среди аннотаций переданного в параметре {@code feature} элемента.
     * Если аннотация не найдена, то создает новую аннотацию и добавляет её к переданному элементу.
     *
     * @param annotationSource URI аннотации, которую нужно найти или создать
     * @param feature          Элемент, у которого нужно найти или создать аннотацию
     * @return Найденная или созданная аннотация
     */
    public static EAnnotation findOrCreateAnnotation(String annotationSource, EStructuralFeature feature) {
        EAnnotation annotation = feature.getEAnnotation(annotationSource);
        if (annotation == null) {
            annotation = EcoreFactory.eINSTANCE.createEAnnotation();
            annotation.setSource(annotationSource);
            feature.getEAnnotations().add(annotation);
        }
        return annotation;
    }
}
