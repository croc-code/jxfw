package ru.croc.ctp.jxfw.core.generator.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.List
import org.eclipse.xtend.lib.macro.CodeGenerationContext
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.generator.Constants
import ru.croc.ctp.jxfw.core.generator.StorageType
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import org.eclipse.xtend.lib.macro.declaration.MutableParameterDeclaration
import org.apache.commons.lang3.StringUtils

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import org.eclipse.xtend.lib.macro.CodeGenerationParticipant
import org.eclipse.xtend.lib.macro.ValidationParticipant
import org.eclipse.xtend.lib.macro.declaration.MutableDeclaration
import ru.croc.ctp.jxfw.core.datasource.DataSourceLoader
import ru.croc.ctp.jxfw.core.datasource.BaseDataSourceLoader
import org.eclipse.xtend.lib.macro.declaration.Declaration
import org.eclipse.xtend.lib.annotations.Accessors
import org.springframework.beans.factory.annotation.Autowired
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import javax.inject.Inject
import ru.croc.ctp.jxfw.core.datasource.DataSourceResult
import java.util.Map
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import javax.annotation.Nonnull
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject
import ru.croc.ctp.jxfw.core.domain.meta.XFWSolrDocument

/**
 * Процессор для генерации источников данных по XFWDataSource аннотации. 
 * 
 * @since 1.0
 */
class XFWDataSourceProcessor implements TransformationParticipant<MutableDeclaration>, CodeGenerationParticipant<Declaration>, ValidationParticipant<Declaration> {

	override doTransform(List<? extends MutableDeclaration> annotatedElements,
		@Extension TransformationContext context) {
		val annotatedMethods = annotatedElements.filter[it instanceof MutableMethodDeclaration].map [
			it as MutableMethodDeclaration
		].toList

		if (annotatedMethods.size() > 0) {
			doTransformMethods(annotatedMethods, context)
		}

		annotatedElements.filter[it instanceof MutableClassDeclaration].map[it as MutableClassDeclaration].forEach [
			doTransformClass(it, context)
		]
	}

	static final Logger logger = ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory.getLogger(
		XFWDataSourceProcessor);

	override doValidate(List<? extends Declaration> annotatedElements, @Extension ValidationContext context) {
		annotatedElements.filter[it instanceof MethodDeclaration].map[it as MethodDeclaration].forEach [
			doValidateMethod(it, context)
		]
		annotatedElements.filter[it instanceof ClassDeclaration].map[it as ClassDeclaration].forEach [ clazz |
			checkClassPackage(clazz, context);

		]
	}

	def doValidateMethod(MethodDeclaration annotatedMethod, ValidationContext context) {
		checkClassPackage(annotatedMethod.declaringType, context);

		for (parameter : annotatedMethod.getParameters()) {
			// TODO JXFW-861: Запрещаем использовать доменные объекты в качестве параметров.
			// т.к. не можем контролировать порядок запуска процессоров xtend.
			// На момент обработки параметра его тип может быть еще не создан.
			if (context.newTypeReference(DomainObject).isAssignableFrom(parameter.getType())) {
				context.addError(
					parameter,
					String.format(
						Constants.ERR_MSG_DATA_SOURCE_METHOD_PARAMETR_IS_DOMAIN_TYPE,
						parameter.simpleName,
						parameter.type.name
					)
				)
			}
		}
	}

	override doGenerateCode(List<? extends Declaration> annotatedSourceElements,
		extension CodeGenerationContext context) {
		val annotatedMethods = annotatedSourceElements.filter[it instanceof MethodDeclaration].map [
			it as MethodDeclaration
		].toList

		if (annotatedMethods.size() > 0) {
			doGenerateCodeMethods(annotatedMethods, context)
		}

		annotatedSourceElements.filter[it instanceof ClassDeclaration].map[it as ClassDeclaration].forEach [
			new DataSourceClassEcoreGenerator(it, getControllerFields(it), context).generate
		]
	}

	def doGenerateCodeMethods(List<? extends MethodDeclaration> annotatedSourceElements,
		extension CodeGenerationContext context) {
		new DataSourceEcoreGenerator(annotatedSourceElements, context).generate
	}

	def doTransformMethods(List<? extends MutableMethodDeclaration> annotatedMethods,
		extension TransformationContext context) {

		// Добавить аннотацию @Service
		addServiceAnnotation(annotatedMethods, context)

		// Добавить logger
		addLoggerField(annotatedMethods, context)

		// Добавить аннотацию @Transactional c пометкой readOnly
		addTransactionReadOnlyAnnotation(annotatedMethods, context)

		// Добавить ApplicatioEventPublisher
		addEventPublisherThroughSetter(annotatedMethods, context)
	}

	def addLoggerField(
		List<? extends MutableMethodDeclaration> annotatedMethods,
		extension TransformationContext context
	) {
		val service = annotatedMethods.get(0).declaringType as MutableClassDeclaration
		service.addField("log") [
			visibility = Visibility::PRIVATE
			static = true
			final = true
			initializer = [
				'''«toJavaCode(LoggerFactory.newTypeReference)».getLogger(«service.simpleName».class)'''
			]
			type = Logger.newTypeReference
		]
	}

	def concatParams(Iterable<? extends MutableParameterDeclaration> params) {
		StringUtils.join(params.map[it.simpleName].toList, ", ")
	}

	def findUow(TypeReference returnType, extension TransformationContext context) {
		if(returnType.isAssignableFrom(Iterable.newTypeReference)) return "result"
		if(returnType.isAssignableFrom(DataSourceResult.newTypeReference)) return "result.getData()"

		return "null"
	}

	def addEventPublisherThroughSetter(
		List<? extends MutableMethodDeclaration> annotatedMethods,
		extension TransformationContext context
	) {
		val service = annotatedMethods.get(0).declaringType as MutableClassDeclaration
		service.implementedInterfaces = #[ApplicationEventPublisherAware.newTypeReference,
			ApplicationContextAware.newTypeReference]

		service.addField("applicationContext") [
			visibility = Visibility::PRIVATE
			type = ApplicationContext.newTypeReference
		]

		service.addField("publisher") [
			visibility = Visibility::PRIVATE
			type = ApplicationEventPublisher.newTypeReference
		]

		service.addMethod("setApplicationContext") [
			visibility = Visibility::PUBLIC
			addParameter("applicationContext", ApplicationContext.newTypeReference)
			addAnnotation(Override.newAnnotationReference)
			body = [
				"this.applicationContext = applicationContext;"
			]
		]

		service.addMethod("setApplicationEventPublisher") [
			visibility = Visibility::PUBLIC
			addParameter("applicationEventPublisher", ApplicationEventPublisher.newTypeReference)
			addAnnotation(Override.newAnnotationReference)
			body = [
				"this.publisher = applicationEventPublisher;"
			]
		]
	}

	def addServiceAnnotation(
		List<? extends MutableMethodDeclaration> annotatedMethods,
		extension TransformationContext context
	) {
		val serviceAnnotation = annotatedMethods.get(0).declaringType.findAnnotation(Service.newTypeReference.type)
		if (serviceAnnotation === null) {
			annotatedMethods.get(0).declaringType.addAnnotation(Service.newAnnotationReference)
		}
	}

	def addTransactionReadOnlyAnnotation(
		List<? extends MutableMethodDeclaration> annotatedMethods,
		extension TransformationContext context
	) {
		// FIXME переделать проверку того, что хранилище поддерживает транзакционность.
		try {
			StorageType.classLoader.loadClass("ru.croc.ctp.jxfw.jpa.generator.PersistenceModuleFactoryJpa")
			val transactionalAnnotation = annotatedMethods.get(0).declaringType.findAnnotation(
				Transactional.newTypeReference.type)
			if (transactionalAnnotation === null) {
				annotatedMethods.get(0).declaringType.addAnnotation(Transactional.newAnnotationReference [
					set("readOnly", true)
				])
			}
		} catch (ClassNotFoundException e) {
			// Nothing to do
		}
	}

	/* Обработка аннотации на классе */
	def doTransformClass(MutableClassDeclaration clazz, @Extension TransformationContext context) {

		val implementsDataSourceLoader = DataSourceLoader.newTypeReference.isAssignableFrom(clazz.newTypeReference)
		val implementsBaseDataSourceLoader = BaseDataSourceLoader.newTypeReference.isAssignableFrom(
			clazz.newTypeReference)
		if (!implementsBaseDataSourceLoader) {
			clazz.addError("Datasource must extend BaseDataSourceLoader class")
			return
		}

		checkFields(clazz, context)

		// добавляем геттеры и сеттеры
		val accessorsProcessor = new org.eclipse.xtend.lib.annotations.AccessorsProcessor()
		clazz.declaredFields.forEach [
			it.addAnnotation(Accessors.newAnnotationReference)
			accessorsProcessor.transform(it, context)
		]

		if (implementsDataSourceLoader) {
			val generics =  clazz.extendedClass?.actualTypeArguments

			if (generics === null || generics.size < 1) {
				clazz.addError("Class must implement DataLoader interface with generic parameters")
				return
			}
			val domainTypeRef = generics.get(0)

			if (domainTypeRef.type.simpleName.equals(typeof(DomainObject).simpleName)
			|| ((domainTypeRef.type instanceof  ClassDeclaration)
					&& (domainTypeRef.type as ClassDeclaration).findXFWMappedSuperClassAnnotation !== null)) {
				clazz.addError("Generic type must be concrete domain object")
			}

			
			val idTypeRef = generics.get(1)

			generateSetPrimitiveValuesMethod(clazz, domainTypeRef, idTypeRef, context)
			generateCreateFilterMethod(clazz, domainTypeRef, idTypeRef, context)

			clazz.addMethod("getDomainObjectTypeName") [
				visibility = Visibility::PUBLIC
				returnType = String.newTypeReference
				addAnnotation(Override.newAnnotationReference)
				body = ['''return «domainTypeRef.simpleName».TYPE_NAME;''']

			]

		} else {
			generateSetPrimitiveValuesMethod(clazz, null, null, context)
			generateCreateFilterMethod(clazz, null, null, context)
		}

	}

	/* эти поля генерируются в котроллере. Их нельзя использовать в датасорсе. */
	static val forbiddenFields = newArrayList(
		'locale',
		'timeZone',
		'hints',
		'principal',
		'expand',
		'top',
		'skip',
		'orderby',
		'fetchTotal'
	)

	private def checkFields(MutableClassDeclaration clazz, @Extension TransformationContext context) {

		forbiddenFields.forEach [
			val field = clazz.findDeclaredField(it)
			if (field !== null) {
				field.addError("The use of this standard field name is forbidden")
			}
		]

		// проверяем, что имена полей не начинаются с $
		clazz.declaredFields.forEach [
			if (it.simpleName.startsWith('$')) {
				it.addError('Field name should not start with $')
			}
		]

		// проверяем, что поле доменного типа только одно,
		// и если есть называется filter
		val domainFields = getControllerFields(clazz).filter[it.isDomainField(context)].toList
		domainFields.filter[!it.simpleName.equals("filter")].forEach [
			it.addError("Domain field should be called \"filter\"")
		]

		getControllerFields(clazz).filter[!it.isDomainField(context)].forEach [ field |
			if (!DomainClassCompileUtil.getSupportedBaseFieldTypes(context).exists [
				it.isAssignableFrom(field.type)
			]) {
				// из непримитивов можно List<String>
				if (! field.type.equals(List.newTypeReference(String.newTypeReference))) {
					field.addError("Non domain fields has to be primitive")
				}
			}
		]

		// примитивные поля не могут называться также, как поля в составе фильтра
		// потому что мы не сможем выбрать правильный тип в контроллере.
		val domainField = getControllerFields(clazz).findFirst[it.isDomainField(context)]
		if (domainField !== null) {
			getControllerFields(clazz).filter[!it.isDomainField(context)].forEach [ field |
				if ((domainField.type.type as ClassDeclaration).declaredFields.exists [
					it.simpleName.equals(field.simpleName)
				]) {
					field.addError("Field with the same name exists in filter object")
				}
			]
		}

	}

	/**
	 * Прикладные поля, которые пойдут в метод контроллера
	 */
	private def getControllerFields(ClassDeclaration clazz) {
		clazz.declaredFields.filter [
			!it.annotations.exists [
				it.annotationTypeDeclaration.simpleName.equals(Autowired.simpleName)
			] && !it.annotations.exists [
				it.annotationTypeDeclaration.simpleName.equals(Inject.simpleName)
			]
		].toList
	}

	private def generateSetPrimitiveValuesMethod(MutableClassDeclaration clazz, TypeReference domainTypeRef,
		TypeReference idTypeRef, @Extension TransformationContext context) {

		clazz.addMethod("setPrimitiveValues") [ methodDeclaration |
			methodDeclaration.visibility = Visibility::PUBLIC
			methodDeclaration.addParameter("filterValues",
				Map.newTypeReference(String.newTypeReference, Object.newTypeReference)).addAnnotation(
				Nonnull.newAnnotationReference)

			val primitiveFields = getControllerFields(clazz).filter[!it.isDomainField(context)].toList
			methodDeclaration.body = [
				'''
					«FOR field : primitiveFields»
						if (filterValues.get("«field.simpleName»") != null) {
							set«field.simpleName.toFirstUpper»((«field.type.simpleName»)filterValues.get("«field.simpleName»"));
						}
					«ENDFOR»
					
				'''
			]

		]
	}

	private def generateCreateFilterMethod(MutableClassDeclaration clazz, TypeReference domainTypeRef,
		TypeReference idTypeRef, @Extension TransformationContext context) {
		val filterField = clazz.declaredFields.findFirst[it.isDomainField(context)]
		if (filterField !== null) {
			clazz.addMethod("createFilter") [
				visibility = Visibility::PUBLIC
				body = [
					'''
						if( filter == null){
							filter = new «filterField.type.simpleName»();
						}
					'''
				]
			]

		}
	}

	private def isDomainField(FieldDeclaration field, @Extension TransformationContext context) {
		new GeneratorHelper(context).isDomain(field.type)
	}

}
