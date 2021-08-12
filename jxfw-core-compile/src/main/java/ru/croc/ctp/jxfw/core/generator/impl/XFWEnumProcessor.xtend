package ru.croc.ctp.jxfw.core.generator.impl

import org.slf4j.Logger
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory
import org.eclipse.xtend.lib.macro.AbstractEnumerationTypeProcessor
import org.eclipse.xtend.lib.macro.declaration.MutableEnumerationTypeDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.Visibility
import ru.croc.ctp.jxfw.metamodel.runtime.XfwEnumeration
import ru.croc.ctp.jxfw.core.generator.meta.XFWEnum
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory

import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import java.util.List
import org.eclipse.xtend.lib.macro.CodeGenerationContext

/**
 * Процессор перечислений в xtend-модели.
 * @since 1.6
 */
class XFWEnumProcessor extends AbstractEnumerationTypeProcessor {

	static final Logger logger = LoggerFactory.getLogger(XFWEnumProcessor);

	override doTransform(MutableEnumerationTypeDeclaration annotatedEnumerationType,
		extension TransformationContext context) {

		// удаляем аннотацию из runtime
		val xfwEnumAnn = annotatedEnumerationType.findAnnotation(
			XFWEnum.newAnnotationReference.annotationTypeDeclaration)

		annotatedEnumerationType.removeAnnotation(xfwEnumAnn)

		// добавляем метадату
		annotatedEnumerationType.addField("METADATA") [
			visibility = Visibility::PUBLIC
			type = XfwEnumeration.newTypeReference
			static = true
			final = true
			initializer = [
				'''«toJavaCode(XfwModelFactory.newTypeReference)».getInstance().findEnum(«annotatedEnumerationType.simpleName».class)'''
			]
			docComment = "Статические метаданные перечисления."
		]

		// добавляем метод локализации
		annotatedEnumerationType.addMethod('getLocalizedValue') [
			visibility = Visibility::PUBLIC
			addParameter('lang', String.newTypeReference)
			returnType = String.newTypeReference
			body = [''' return METADATA.getLocalizedValue(this, lang);''']
			docComment = "Получить локализованное именование."
		]

	}

	override doValidate(EnumerationTypeDeclaration annotatedEnumerationType, extension ValidationContext context) {
		checkClassPackage(annotatedEnumerationType, context);
	}

	override doGenerateCode(List<? extends EnumerationTypeDeclaration> annotatedSourceElements,
		@Extension CodeGenerationContext context) {
		super.doGenerateCode(annotatedSourceElements, context)
		
		new EnumEcoreGenerator(annotatedSourceElements, context).generate
	}

}
