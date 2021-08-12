package ru.croc.ctp.jxfw.metamodel.impl

import org.eclipse.emf.ecore.EcorePackage
import ru.croc.ctp.jxfw.metamodel.XFWMMFactory
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage
import spock.lang.Specification

import static ru.croc.ctp.jxfw.metamodel.impl.AnnotationsSources.*

/**
 * Тесты дополнительных (не сгенерированных EMF) методов в {@link XFWAttributeImpl}.
 * <p/>
 * Created by SPlaunov on 27.07.2016.
 */
class XFWAttributeImplSpec extends Specification {
    /**
     * Фабрика метамодели jXFW.
     */
    static final XFWMMFactory XFWMM_FACTORY = XFWMMFactory.eINSTANCE;

    /**
     * Пакет метамодели jXFW.
     */
    static final XFWMMPackage XFWMM_PACKAGE = XFWMMPackage.eINSTANCE;

    /**
     * Пакет метамодели ECORE.
     */
    static final EcorePackage ECORE_PACKAGE = EcorePackage.eINSTANCE;

    def "setGenerateBlobInfoFields attribute type not set"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()

        when:
        attribute.setGenerateBlobInfoFields(true)

        then:
        thrown(IllegalStateException)
    }

    def "setGenerateBlobInfoFields wrong attribute type"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(ECORE_PACKAGE.getEString())

        when:
        attribute.setGenerateBlobInfoFields(true)

        then:
        thrown(IllegalStateException)
    }

    def "setGenerateBlobInfoFields"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())

        when:
        attribute.setGenerateBlobInfoFields(true)

        then:
        attribute.EAnnotations.size() == 1
        def annotation = attribute.getEAnnotation(BlobAdditionalFields.SOURCE)
        annotation.details.containsKey(BlobAdditionalFields.GENERATE_FIELDS_PARAM)
        annotation.details.get(BlobAdditionalFields.GENERATE_FIELDS_PARAM) == "true"
    }

    def "getGenerateBlobInfoFields"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())

        when:
        attribute.setGenerateBlobInfoFields(true)
        def generate = attribute.getGenerateBlobInfoFields()

        then:
        generate

        when:
        attribute.setGenerateBlobInfoFields(false)
        generate = attribute.getGenerateBlobInfoFields()

        then:
        !generate

    }

    def "setContentSizeSuffix"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())
        def suffix = "Size"

        when:
        attribute.setContentSizeSuffix(suffix)

        then:
        attribute.EAnnotations.size() == 1
        def annotation = attribute.getEAnnotation(BlobAdditionalFields.SOURCE)
        annotation.details.containsKey(BlobAdditionalFields.CONTENT_SIZE_SUFFIX_PARAM)
        annotation.details.get(BlobAdditionalFields.CONTENT_SIZE_SUFFIX_PARAM) == suffix
    }

    def "getContentSizeSuffix"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())
        def suffixWrite = "sfx1"
        attribute.setContentSizeSuffix(suffixWrite)

        when:
        def suffixRead = attribute.getContentSizeSuffix()

        then:
        suffixRead == suffixWrite
    }

    def "setFileNameSuffix"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())
        def suffix = "FileName"

        when:
        attribute.setFileNameSuffix(suffix)

        then:
        attribute.EAnnotations.size() == 1
        def annotation = attribute.getEAnnotation(BlobAdditionalFields.SOURCE)
        annotation.details.containsKey(BlobAdditionalFields.FILE_NAME_SUFFIX_PARAM)
        annotation.details.get(BlobAdditionalFields.FILE_NAME_SUFFIX_PARAM) == suffix
    }

    def "getFileNameSuffix"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())
        def suffixWrite = "sfx1"
        attribute.setFileNameSuffix(suffixWrite)

        when:
        def suffixRead = attribute.getFileNameSuffix()

        then:
        suffixRead == suffixWrite
    }

    def "setContentTypeSuffix"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())
        def suffix = "ContentType"

        when:
        attribute.setContentTypeSuffix(suffix)

        then:
        attribute.EAnnotations.size() == 1
        def annotation = attribute.getEAnnotation(BlobAdditionalFields.SOURCE)
        annotation.details.containsKey(BlobAdditionalFields.CONTENT_TYPE_SUFFIX_PARAM)
        annotation.details.get(BlobAdditionalFields.CONTENT_TYPE_SUFFIX_PARAM) == suffix
    }

    def "getContentTypeSuffix"() {
        given:
        def attribute = XFWMM_FACTORY.createXFWAttribute()
        attribute.setEType(XFWMM_PACKAGE.getBlob())
        def suffixWrite = "sfx1"
        attribute.setContentTypeSuffix(suffixWrite)

        when:
        def suffixRead = attribute.getContentTypeSuffix()

        then:
        suffixRead == suffixWrite
    }
}
