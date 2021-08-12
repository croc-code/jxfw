package ru.croc.ctp.jxfw.metamodel.impl

import org.eclipse.emf.ecore.EcoreFactory
import ru.croc.ctp.jxfw.metamodel.XFWMMFactory
import spock.lang.Specification

/**
 * Тесты для {@link AnnotationsSources}.
 * <p/>
 * Created by SPlaunov on 27.07.2016.
 */
class AnnotationsSourcesSpec extends Specification {
    def "create annotation"() {
        given:
        def attribute = XFWMMFactory.eINSTANCE.createXFWAttribute()
        def annotationSource = "testURI"

        when:
        def annotation = AnnotationsSources.findOrCreateAnnotation(annotationSource, attribute)

        then:
        annotation
        annotation.source == annotationSource
        attribute.getEAnnotation(annotationSource) == annotation
    }

    def "find existing annotation"() {
        given:
        def attribute = XFWMMFactory.eINSTANCE.createXFWAttribute()
        def annotationSource = "testURI"
        def annotationCreated = EcoreFactory.eINSTANCE.createEAnnotation()
        annotationCreated.setSource(annotationSource)
        attribute.EAnnotations.add(annotationCreated)

        when:
        def annotationFound = AnnotationsSources.findOrCreateAnnotation(annotationSource, attribute)

        then:
        annotationFound
        annotationFound == annotationCreated
    }
}
