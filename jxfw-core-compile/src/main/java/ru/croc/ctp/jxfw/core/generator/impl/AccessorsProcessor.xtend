package ru.croc.ctp.jxfw.core.generator.impl

import java.util.List
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.springframework.util.ObjectUtils
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker

import static extension ru.croc.ctp.jxfw.core.generator.impl.EnumCompileUtil.*

class AccessorsProcessor implements TransformationParticipant<MutableClassDeclaration> {

	override doTransform(List<? extends MutableClassDeclaration> elements, extension TransformationContext context) {
		elements.forEach [
			declaredFields.filter[!static && thePrimaryGeneratedJavaElement].forEach [
				transform(context)
			]
		]
	}

	def void transform(MutableFieldDeclaration it, extension TransformationContext context) {
		extension val util = new org.eclipse.xtend.lib.annotations.AccessorsProcessor.Util(context)

		val field = it

		val generatorHelper = new GeneratorHelper(context)
		val enum = it.type.isEnumField
		val enumMany = it.type.isEnumSetField
		val fieldTypeName = if(enum || enumMany) field.getEnumTypeForField(context).simpleName else null;

		if (shouldAddGetter) {
			// TODO: взять из аннотации
			addGetter(Visibility.PUBLIC)
			if (field.getterName.startsWith("is")) {
				field.declaringType.addMethod(field.possibleGetterNames.tail.head) [
					primarySourceElement = field.primarySourceElement
					addAnnotation(newAnnotationReference(Pure))
					returnType = field.type ?: object
					body = '''return this.«field.simpleName»;'''
					static = field.static
					it.visibility = visibility
				]
			}
			if (enum || enumMany) {
				field.declaringType.findDeclaredMethod(field.getterName).body = [
					'''return «fieldTypeName».METADATA.convertToEnum«if(enumMany)"Set" else ""»(«field.simpleName», «fieldTypeName».class);'''
				]
			}
		}

		if (SelfDirtinessTracker.newTypeReference.isAssignableFrom(field.declaringType.newTypeReference) && generatorHelper.isDomainCollection(field.type)) {
			field.declaringType.findDeclaredMethod(field.getterName).body = [
				'''
                	return («field.simpleName» == null) ? null : new «generatorHelper.getNameObservableCollection(field.type).name»<>(«field.simpleName», (changes) -> trackChange("«field.simpleName»"));
				'''
			]
		}
		
		if (shouldAddSetter) {
			// addSetter(setterType.toVisibility)
			// TODO: добавить проверку, является ли тип с поддержкой изменений в противном случае, генерировать стандартный сеттер
			field.declaringType.addMethod(field.setterName) [
				primarySourceElement = field.primarySourceElement
				returnType = primitiveVoid
				val param = addParameter(field.simpleName, (field.type ?: object))
				val input = if (enum ||
						enumMany) '''«fieldTypeName».METADATA.convertToInt(«param.simpleName»)''' else param.simpleName
				body = '''
					«IF SelfDirtinessTracker.newTypeReference.isAssignableFrom(field.declaringType.newTypeReference)»
						if (!«ObjectUtils».nullSafeEquals(this.«field.simpleName», «input»)) {
						    trackChange("«field.simpleName»");
						}
					«ENDIF»
					this.«field.simpleName» = «input»;
					
				'''
				static = field.static
				it.visibility = visibility
			]	
		}

	}
}
