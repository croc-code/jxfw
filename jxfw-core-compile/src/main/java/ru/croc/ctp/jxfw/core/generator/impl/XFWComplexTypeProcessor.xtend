package ru.croc.ctp.jxfw.core.generator.impl

import java.util.HashMap
import java.util.List
import java8.util.Maps
import javax.annotation.Nonnull
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable
import org.eclipse.xtend.lib.macro.AbstractClassProcessor
import org.eclipse.xtend.lib.macro.CodeGenerationContext
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.slf4j.Logger
import org.springframework.data.annotation.Transient
import org.springframework.util.StringUtils
import ru.croc.ctp.jxfw.core.domain.ComplexType
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWBasic
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory

import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.EnumCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*

class XFWComplexTypeProcessor extends AbstractClassProcessor {

	private static final Logger logger = LoggerFactory.getLogger(XFWComplexTypeProcessor);

	val accessorsProcessor = new AccessorsProcessor

	List<MutableClassDeclaration> mutableClasses
	PersistenceModulesManager persistenceModulesManager = null
	PersistenceModuleContext moduleContext = null

	override doRegisterGlobals(List<? extends ClassDeclaration> annotatedClasses,
		extension RegisterGlobalsContext context) {
		logger.debug("XFWComplexTypeProcessor doRegisterGlobals started")
		val cu = annotatedClasses.get(0).compilationUnit
		logger.debug("XFWComplexTypeProcessor Compilation Unit: " + cu.simpleName)

		moduleContext = new PersistenceModuleContext(loadProperties(cu, context), cu)
		persistenceModulesManager = new PersistenceModulesManager(moduleContext)
		moduleContext.registerGlobalsContext = context

		super.doRegisterGlobals(annotatedClasses, context)
		val filteredElms = annotatedClasses.filter(typeof(ClassDeclaration)).toList()
		filteredElms.forEach [
			context.registerClass(getPropertyInnerClassQName(it))
		]

		persistenceModulesManager.clearModulesMap()
	}

	override doTransform(List<? extends MutableClassDeclaration> annotatedClasses,
		extension TransformationContext context) {
		mutableClasses = annotatedClasses.filter(typeof(MutableClassDeclaration)).toList()

		moduleContext.mutableClasses = mutableClasses
		moduleContext.transformationContext = context

		super.doTransform(annotatedClasses, context)
		mutableClasses.forEach [
			// TODO удалить после деприкации XFWEnumerated
			it.processXFWEnumeratedField(context)
            it.addSerialVersionUID(context) // Добавление поля serialVersionUID в класс доменной модели
			it.addPropertyConstants(context)

			it.addSavedState(Transient.newAnnotationReference, context)

		]
		accessorsProcessor.doTransform(annotatedClasses, context)

		mutableClasses.forEach [
			it.processEnumFields(context)
		]
	}

	override doTransform(MutableClassDeclaration clazz, extension TransformationContext context) {
		clazz.addAnnotation(Embeddable.newAnnotationReference)

		/*
		 *   добавляем аннотацию @Access(value = AccessType.FIELD)
		 *   без нее apt-maven-plugin генерирует для комплексных типов  (Embeddable)
		 *  пути в q-Типах по типу геттеров. а нам надо по типу полей.
		 */
		clazz.addAnnotation( Access.newAnnotationReference [
			setEnumValue("value", getEnumValue(AccessType.newTypeReference, AccessType.FIELD.name))
		])

		val fields = clazz.getDeclaredFields()

		clazz.implementedInterfaces = #[ComplexType.newTypeReference]
		/*
		 * Доступ к полям осуществляется напрямую,а не через геттеры, что вообще-то неверно.
		 * Но пока это так, для перечислений работает без конвертеров.
		 */
		clazz.addMethod("getAllFields") [
			visibility = Visibility::PUBLIC
			returnType = HashMap.newTypeReference(String.newTypeReference, Object.newTypeReference)
			addAnnotation(Override.newAnnotationReference)
			addParameter('prefix', String.newTypeReference)
			body = [
				'''
					    «toJavaCode(HashMap.newTypeReference(String.newTypeReference, Object.newTypeReference))» fields = new «toJavaCode(HashMap
                .newTypeReference)»<>();
					«FOR field : fields»
						«IF (!field.isFinal() || !field.isStatic())»
							«IF ComplexType.newTypeReference.isAssignableFrom(field.type.type.newTypeReference)»
								if( get«field.simpleName.toFirstUpper»()!=null ){
								    fields.putAll(get«field.simpleName.toFirstUpper»().getAllFields(prefix + ".«field.simpleName»"));
								}
							«ELSE»
								if («toJavaCode(StringUtils.newTypeReference)».isEmpty(prefix)) {
								    fields.put("«field.simpleName»", «field.simpleName»);
								} else {
								    fields.put(prefix + "." + "«field.simpleName»", «field.simpleName»);
								}
							«ENDIF»
						«ENDIF»
					«ENDFOR»
					return fields;
				'''
			]
		]

		clazz.addMethod("setProperty") [
			visibility = Visibility::PUBLIC
			addAnnotation(Override.newAnnotationReference)
			addParameter('prefix', String.newTypeReference)
			addParameter('name', String.newTypeReference)
			addParameter('value', Object.newTypeReference)
			body = [
				'''
					«FOR field : fields»
						«IF (!field.isFinal() || !field.isStatic())»
							«IF ComplexType.newTypeReference.isAssignableFrom(field.type.type.newTypeReference)»
								if(get«field.simpleName.toFirstUpper»() == null) {
								   set«field.simpleName.toFirstUpper»(new «field.type»());
								}
								get«field.simpleName.toFirstUpper»().setProperty(prefix + ".«field.simpleName»", name, value);
							«ELSE»
								if («toJavaCode(StringUtils.newTypeReference)».isEmpty(prefix)) {
								    if ("«field.simpleName.toFirstLower»".equals(name)) {
								        set«field.simpleName.toFirstUpper»(«fromToExpr(field, "value", context)»);
								    }
								} else {
								    if ((prefix + "." + "«field.simpleName.toFirstLower»").equals(name)) {
								        set«field.simpleName.toFirstUpper»(«fromToExpr(field, "value", context)»);
								    }
								}
							«ENDIF»
						«ENDIF»
					«ENDFOR»
				'''
			]
		]

		clazz.addMethod("createComplexType") [
			visibility = Visibility::PUBLIC
			returnType = clazz.newTypeReference
			addAnnotation(Override.newAnnotationReference)
            addAnnotation(Nonnull.newAnnotationReference)
            val prefix = addParameter('prefix', String.newTypeReference)
            val props = addParameter('props', HashMap.newTypeReference(String.newTypeReference, Object.newTypeReference))
            prefix.addAnnotation(Nonnull.newAnnotationReference)
            props.addAnnotation(Nonnull.newAnnotationReference)
			body = [
				'''
					«toJavaCode(Maps.newTypeReference)».forEach(props, (String key, Object value) -> setProperty(prefix, key, value));
					return this;
				'''
			]
		]

		clazz.addMetadataMethod(context)
	}

	override doValidate(ClassDeclaration annotatedClass, extension ValidationContext context) {
		super.doValidate(annotatedClass, context)

		checkClassPackage(annotatedClass, context)

		annotatedClass.checkCoreFields(context).forEach [ field |
			field.checkComplexTypeField(annotatedClass, context)
		]
		annotatedClass.declaredFields.forEach [ field |
			if (field.findAnnotation(XFWBasic.newTypeReference.type) !== null) {
				field.addError("Annotation XFWBasic not allowed in complex types");
			}
		]

	}

	override doGenerateCode(List<? extends ClassDeclaration> annotatedSourceElements,
		extension CodeGenerationContext context) {
		moduleContext.setCodeGenerationContext(context)

		super.doGenerateCode(annotatedSourceElements, context)

		if (mutableClasses === null || mutableClasses.size == 0) {
			return
		}

		val generator = getEcoreGenerator(persistenceModulesManager, mutableClasses, context)

		generator.generate
	}
}
