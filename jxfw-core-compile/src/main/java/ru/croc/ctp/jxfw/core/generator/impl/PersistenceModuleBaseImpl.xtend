package ru.croc.ctp.jxfw.core.generator.impl

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.List
import java8.util.Objects
import javax.annotation.Nonnull
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.validation.ConstraintValidatorContext;
import ru.croc.ctp.jxfw.core.validation.meta.XFWFacadeValidationGroup
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWFacadeReadOnlyCheck
import org.eclipse.emf.ecore.EcoreFactory
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableInterfaceDeclaration
import org.eclipse.xtend.lib.macro.declaration.Type
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.eclipse.xtend.lib.macro.file.Path
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import org.springframework.data.annotation.Transient
import org.springframework.data.annotation.Version
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.DomainService
import ru.croc.ctp.jxfw.core.domain.Editable
import ru.croc.ctp.jxfw.core.domain.meta.XFWDefaultValue
import ru.croc.ctp.jxfw.core.domain.meta.XFWServerOnly
import ru.croc.ctp.jxfw.core.domain.meta.XFWElementLabel
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey
import ru.croc.ctp.jxfw.core.domain.meta.XFWFacadeIgnore
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToMany
import ru.croc.ctp.jxfw.core.generator.meta.XFWProtected
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException
import ru.croc.ctp.jxfw.core.generator.EcoreModelEmitter
import ru.croc.ctp.jxfw.core.generator.PersistenceModule
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWReadOnlyCheck
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration

import static ru.croc.ctp.jxfw.core.generator.Constants.*

import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.ReadOnlyType
import java.util.EnumSet
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker
import org.eclipse.emf.ecore.EStructuralFeature
import ru.croc.ctp.jxfw.metamodel.XfwValueType
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import ru.croc.ctp.jxfw.metamodel.XFWConstants
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToOne
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToOne
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWBasic
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWTransient
import java.util.Set
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWNewRemoveCheck
import java.util.Optional
import com.querydsl.core.types.Predicate
import ru.croc.ctp.jxfw.core.domain.SaveStateManager
import java.util.stream.Stream
import java.util.Collections
import java.util.stream.Collectors
import java.time.LocalDate
import java.time.LocalTime

public abstract class PersistenceModuleBaseImpl implements PersistenceModule {

    protected static final Logger logger
        = ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory.getLogger(GeneratorHelper);
    private final MutableClassDeclaration clazz;

    protected extension TransformationContext transformationContext;

    protected final EcoreFactory eFactory = EcoreFactory.eINSTANCE
    protected final PersistenceModuleContext moduleContext
    protected final String defaultLang

    private EcoreModelEmitter ecoreModelEmitter;

    private Type repository;

    private Type repositoryImpl;

    private MutableClassDeclaration service;

    private MutableClassDeclaration dtoService;

    private MutableClassDeclaration controller;

    private final static String unsupportedOperationBody =
    "throw new UnsupportedOperationException(\"Abstract Type Service\");";

    new(@Nonnull MutableClassDeclaration clazz, @Nonnull PersistenceModuleContext moduleContext) {
        this.clazz = clazz
        this.moduleContext = moduleContext
        this.transformationContext = moduleContext.transformationContext
        defaultLang = context.properties.getProperty(LANG_PROP_NAME)
    }

    new(@Nonnull PersistenceModuleContext moduleContext) {
        this.clazz = null
        this.moduleContext = moduleContext
        defaultLang = context.properties.getProperty(LANG_PROP_NAME)
    }

    public override getEcoreModelEmitter() {
        if(ecoreModelEmitter === null) {
            ecoreModelEmitter = createEcoreModelEmitter();
        }
        return ecoreModelEmitter;
    }

    protected def abstract EcoreModelEmitter createEcoreModelEmitter();

    protected def void checkIdField(){}

    protected def TypeReference getQueryPredicateType() {
        Object.newTypeReference
    }

    protected def getContext() {
        moduleContext
    }

    protected def getClazz() {
        clazz
    }

    override registerClasses(ClassDeclaration clazz) {

        val serviceQName = getServiceQName(clazz)
        val repositoryQName = getRepositoryQName(clazz)
        val repositoryImplQName = getRepositoryImplQName(clazz)

        val context = moduleContext.getRegisterGlobalsContext

        context.registerClass(serviceQName)

        if(!providedOrTransient(getRepositoryJavaFile(clazz), clazz)) {
            context.registerInterface(repositoryQName)
        }

        if(!providedOrTransient(getRepositoryImplJavaFile(clazz), clazz)) {
            context.registerClass(repositoryImplQName)
        }

     }

    //вызывается только на стадии doRegisteGlobals
    private def providedOrTransient(Path p, ClassDeclaration clazz) {
        //registerGlobalsContext содержит вспомогательные методы для работы с файловой системой
        if(p!==null && moduleContext.registerGlobalsContext.exists(p)) {
            return true
        }

        if(!isPersistence(clazz)) {
            return true
        }
        return false
    }

    override extendClazz() {

        val context = moduleContext.transformationContext

        clazz.setI18NAnnotationDefaults(defaultLang, context)

        extendTransientFields()

        if(clazz.isTopParent) {
            extendTopParentOnly()
        } else {
            extendNotTopParent()
        }

        clazz.addObtainValueByPropertyNameField(keyType, context)


        clazz.addMethod('getTypeName') [
            visibility = Visibility::PUBLIC
            returnType = String.newTypeReference
            body = ['''return TYPE_NAME;''']
        ]

        clazz.addMetadataMethod(context)

        if(clazz.needToAddValidationLogic(ReadOnlyType.SERVER, context)) {
            clazz.addAnnotation(XFWReadOnlyCheck.newAnnotationReference)
        }
        
        if(clazz.needToAddValidationLogic(ReadOnlyType.FACADE, context)) {
            clazz.addAnnotation(XFWFacadeReadOnlyCheck.newAnnotationReference [
                setClassValue("groups", XFWFacadeValidationGroup.newTypeReference)
            ])
        }

        clazz.addAnnotation(XFWNewRemoveCheck.newAnnotationReference)

        addBlobInfo()
    }

    override createComplexKey() {
    }

    protected def void addBlobInfo() {
        clazz.addBlobInfo(moduleContext.properties, moduleContext.transformationContext)
    }

    /**
    * Добавляем аннотации на временные поля.
    */
    protected def void extendTransientFields() {
        if (isPersistence(clazz)) {
            val context = moduleContext.transformationContext
            val GeneratorHelper generatorHelper = new GeneratorHelper(context);

            clazz.declaredFields.forEach [ field |
                // навигируемые свойства на временные типы помечаем временными
                if (generatorHelper.isComplex(field.type)) {
                    // Do nothing
                } else if (generatorHelper.isDomain(field.type)) {
                    if (!isPersistence(field.type.type as TypeDeclaration)) {
                	    field.addAnnotation(XFWTransient.newAnnotationReference)
                	}
                } else if (generatorHelper.isDomainCollection(field.type)) {
                  	val argType = generatorHelper.getDomainCollectionArgument(field.type);
                    if (!isPersistence(argType?.type as TypeDeclaration)) {
                		field.addAnnotation(XFWTransient.newAnnotationReference)
                	}
                }

                if (field.findAnnotation(XFWTransient.newTypeReference.type) !== null
            	        && field.findAnnotation(getTransientAnnotation.annotationTypeDeclaration) === null) {
            	    field.addAnnotation(getTransientAnnotation)
               	}
            ]
        }
    }

    /**
     * Extends only top parent class in the hierarchy
     */
    protected def void extendTopParentOnly() {
        
        if (!clazz.isMappedBySuperclass){
        	addIdFieldsAndMethods	
        }
        
        clazz.implementedInterfaces = clazz.implementedInterfaces + #[DomainObject.newTypeReference(getKeyTypeType(clazz))] //keyType)]
        if(!clazz.isReadOnlyEntity) {  // если класс доменной модели НЕ readOnly то наследуемся от интерфейса Editable
            clazz.implementedInterfaces = clazz.implementedInterfaces + #[Editable.newTypeReference]
        }

        val context = moduleContext.transformationContext

        clazz.addLoggerField(transientAnnotation, context)
        clazz.addIsNewField(transientAnnotation, context)
        clazz.addIsRemovedField(transientAnnotation, context)



        if(!clazz.isReadOnlyEntity && !clazz.isMappedBySuperclass) {
            addOptimisticLockField
        }
        
        if(clazz.isPersistence) {
        	clazz.addSavedState(transientAnnotation, context)
        }
        
        if (!clazz.isPersistence) {
			clazz.declaredFields.forEach [ f |
				if (f.initializer === null) {
					if (List.newTypeReference.isAssignableFrom(f.type)) {
						f.initializer = '''new java.util.ArrayList<>()'''
					} else if (Set.newTypeReference.isAssignableFrom(f.type) 
						&& !EnumSet.newTypeReference.isAssignableFrom(f.type)
					) {
						f.initializer = '''new java.util.HashSet<>()'''
					}
				}
			]
		}

		//TODO: разобраться, надо ли добавлять, убрать аннотацию transient, как специфичную для JPA
        clazz.addOriginalField(transientAnnotation, context)
    }
	

    /**
    * Extends not top parent class in the hierarchy
    */
    protected def void extendNotTopParent() {
        val context = moduleContext.transformationContext

        if(clazz.isPersistence) {
            clazz.overrideGetCurrentValueMethodForNotTopParent(transientAnnotation, context)
        }
    }

    protected def void addOptimisticLockField() {
        clazz.addOptimisticLockField(Version, moduleContext.transformationContext)
    }

    protected def void addIdFieldsAndMethods() {
        val idFieldCandidate = findId(clazz)
        val idField = if(idFieldCandidate === null) {
            clazz.addField("id") [
                type = keyType
                addAnnotation(idType.getType.newAnnotationReference)
            ]
        } else {
            idFieldCandidate
        }
        if (idField instanceof MutableFieldDeclaration) {
            (idField as MutableFieldDeclaration).markAsRead // избавляет от WARNING, что поле не используется
        }

        clazz.addMethod('getId') [
            visibility = Visibility::PUBLIC
            returnType = idField.type //keyType
            body = ['''return «idField.simpleName»;''']
        ]

        clazz.addMethod('setId') [
            visibility = Visibility::PUBLIC
            addParameter('id', idField.type) // keyType)
            body = ['''this.«idField.simpleName» = id;''']
        ]
    }


    protected def getKeyTypeType(ClassDeclaration clazz) {
     	val idField = findId(clazz)
    	if (idField === null) {
    		getKeyType()
    	} else {
    		idField.type
    	}
  }
    
    override getKeyType(){
    	return String.newTypeReference()
    }

    /*
     * Компиляция xtend происходит по разному в eclipse И maven-ом.
     * eclipse смотрит на xtend-файлы, мавен - на java. Поэтому свойства, сгенерированные
     * процессорами активных аннотаций, могут быть не видны, если сборка происходит eclipse-ом.
     * Отсюда следует, что поле - идентификатор может не найтись при сборке в eclipse-м и результат данной функции
     * надо обязательно проверять на null.
     * null означает, что идентификатор не задавался явно в модели и будет сгенерирован автоматически.
     * Т.е. это будет String id;
     */
    protected final def findId(ClassDeclaration clazz) {
    	clazz.allFieldsIncludeSuperClasses.findFirst[findAnnotation(idType.type) !== null]
    }
     
    
    protected def getKeyName(ClassDeclaration clazz) {
    	val idField = findId(clazz)
    	if (idField === null) {
    		"id"
    	} else {
    		idField.simpleName
    	}
    }
    
    protected def getTransientAnnotation() {
        Transient.newAnnotationReference
    }

    /**
     * Тип которым будет проаннотирован первичный ключ
     */
    override def getIdType() {
        Id.newTypeReference
    }

    protected def getServiceInterface() {
        DomainService.newTypeReference(clazz.newTypeReference, getKeyTypeType(clazz), //keyType, 
        	queryPredicateType
        )
    }

    override createRepository() {
        if(!isPersistence(clazz)) {
            return;
        }

        // Если репозиторий был определен разработчиком, то не нужно его генерировать
        if(getRepository() instanceof MutableInterfaceDeclaration) {
            val rep = getRepository() as MutableInterfaceDeclaration
            rep.extendedInterfaces = repositoryExtendedInterfaces
        }
    }

    override extendMappedSuperclass() {
    	
		val context = moduleContext.transformationContext

        clazz.setI18NAnnotationDefaults(defaultLang, context)

        extendTransientFields()

        //добавляем id поле и ts(если требуется) только верхнему уровню иерархии
        if(!clazz.isMappedBySuperclass && clazz.isTopParent) {
            addIdFieldsAndMethods

            if(!clazz.isReadOnlyEntity) {
                addOptimisticLockField
            }

        }

        clazz.addObtainValueByPropertyNameField(keyType, context)

		if(clazz.needToAddValidationLogic(ReadOnlyType.SERVER, context)) {
            clazz.addAnnotation(XFWReadOnlyCheck.newAnnotationReference)
        }

        addBlobInfo()
    	    
    }

    protected def Iterable<? extends TypeReference> getRepositoryExtendedInterfaces()

    override createService() {
        val service = findClass(clazz.serviceQName)
        service.addAnnotation(Service.newAnnotationReference[])
        service.implementedInterfaces = #[serviceInterface, ApplicationEventPublisherAware.newTypeReference, ApplicationContextAware.newTypeReference]

        service.addField("log") [
            visibility = Visibility::PRIVATE
            static = true
            final = true
            initializer = [
                '''«toJavaCode(LoggerFactory.newTypeReference)».getLogger(«service.simpleName».class)'''
            ]
            type = Logger.newTypeReference
        ]

        service.addField("maxObjects") [
            visibility = Visibility::PRIVATE
            addAnnotation(Value.newAnnotationReference [setStringValue("value", "${ru.croc.ctp.jxfw.load.maxObjects:-1}")])
            type = primitiveInt
        ]

        createServiceAddRepositoryField(service)
        
        service.addField("applicationContext") [
            visibility = Visibility::PRIVATE
            type = ApplicationContext.newTypeReference
        ]
        
        service.addField("publisher") [
            visibility = Visibility::PRIVATE
            type = ApplicationEventPublisher.newTypeReference
        ]

        service.addField("saveStateManager") [
            visibility = Visibility::PROTECTED
            type = SaveStateManager.newTypeReference
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

        service.addMethod("setSaveStateManager") [
            visibility = Visibility::PUBLIC
            addParameter("saveStateManager", SaveStateManager.newTypeReference)
            addAnnotation(Autowired.newAnnotationReference)
            body = [
                "this.saveStateManager = saveStateManager;"
            ]
        ]

        service.addMethod('isSaveState') [
            visibility = Visibility::PROTECTED
            returnType = primitiveBoolean
            body = ['''
            return saveStateManager.isEnable(«clazz.newTypeReference».class).orElse(«isSaveState»);
            '''
            ]
        ]

        createServiceCreateNewMethod(service)

        createServiceGetObjectByIdMethod(service)
        
        createServiceGetObjectsMethods(service)

        createServiceDeleteMethods(service)

		createServiceMethodValidate(service)

        createServiceMethodSave(service)

        createServiceMethodCount(service)

        createServiceMethodGetMaxObject(service)

        createGetPredicateToAffectAllReadMethods(service)

    }
    protected def createServiceAddRepositoryField(MutableClassDeclaration service) {
        service.addField("repository") [
            visibility = Visibility::PRIVATE
            type = getRepository().newTypeReference
        ]
        
        service.addMethod("setRepository", [
        	visibility = Visibility::PUBLIC
        	addParameter("repository", getRepository().newTypeReference)
        	addAnnotation(Autowired.newAnnotationReference)
        	body = ['''
        	this.repository = repository; 
        	''']
        ])

    }

    protected def createServiceCreateNewMethod(MutableClassDeclaration service) {
		service.addMethod('createNew') [
			visibility = Visibility::PUBLIC
			addParameter("id", getKeyTypeType(clazz)) // keyType)
			returnType = clazz.newTypeReference
			body = if (clazz.isAbstract)
				[unsupportedOperationBody]
			else
				[
					'''
						log.debug("Create new object «clazz.simpleName» with id={}", id);
						«toJavaCode(clazz.newTypeReference)» o = new «toJavaCode(clazz.newTypeReference)»();
						o.setId(id);
						o.setNew(true);
						
						«FOR clazz : Stream.concat(clazz.parents.stream, Collections.singletonList(clazz).stream).collect(Collectors.toList)»				
							«FOR field : clazz.declaredFields.filter[it.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration) !== null]»
								«val value = field.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration).getStringValue("value")»
								«val upper = field.simpleName.toFirstUpper»
								«val enumTypeRef = EnumCompileUtil.getEnumTypeForField(field, transformationContext, clazz)»
								
							    «IF field.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration).getBooleanValue("asCurrent")»
							    	«IF field.type == ZonedDateTime.newTypeReference»
										o.set«upper»(«toJavaCode(ZonedDateTime.newTypeReference)».now());
							    	«ELSEIF field.type == LocalDateTime.newTypeReference»
										o.set«upper»(«toJavaCode(LocalDateTime.newTypeReference)».now());
							    	«ELSEIF field.type == LocalTime.newTypeReference»
										o.set«upper»(«toJavaCode(LocalTime.newTypeReference)».now());
							    	«ELSEIF field.type == LocalDate.newTypeReference»
										o.set«upper»(«toJavaCode(LocalDate.newTypeReference)».now());
							    	«ENDIF»
							    «ELSEIF StringUtils.isEmpty(value)»
									«field.addError("length of value must be > 0")»
							    «ELSEIF enumTypeRef !== null»
							    	«IF !value.contains("EnumSet.noneOf")»
							    		«IF EnumCompileUtil.isEnumMany(field, transformationContext, clazz)»
											o.set«upper»((«toJavaCode(EnumSet.newTypeReference)»<«toJavaCode(enumTypeRef.newTypeReference)»>)«EnumCompileUtil.createNewMethodBody(value, field, enumTypeRef, true)»);
							    		«ELSE»
											o.set«upper»((«toJavaCode(enumTypeRef.newTypeReference)»)«EnumCompileUtil.createNewMethodBody(value, field, enumTypeRef, false)»);
							    		«ENDIF»
							    	«ENDIF»
							    «ELSEIF field.type == Boolean.newTypeReference»
									o.set«upper»(new «toJavaCode(Boolean.newTypeReference)»("«value»"));
							    «ELSEIF field.type == Long.newTypeReference»
									o.set«upper»(«toJavaCode(Long.newTypeReference)».parseLong("«value»"));
							    «ELSEIF field.type == String.newTypeReference»
									o.set«upper»(new «toJavaCode(String.newTypeReference)»("«value»"));
							    «ELSEIF field.type == Double.newTypeReference»
									o.set«upper»(«toJavaCode(Double.newTypeReference)».parseDouble("«value»"));
							    «ELSEIF field.type == Integer.newTypeReference»
									o.set«upper»(«toJavaCode(Integer.newTypeReference)».parseInt("«value»"));
							    «ELSEIF field.type == ZonedDateTime.newTypeReference»
									o.set«upper»(«toJavaCode(ZonedDateTime.newTypeReference)».parse("«value»", «toJavaCode(DateTimeFormatter.newTypeReference)».ISO_OFFSET_DATE_TIME));
							    «ELSEIF field.type == LocalDateTime.newTypeReference»
									o.set«upper»(«toJavaCode(LocalDateTime.newTypeReference)».parse("«value»", «toJavaCode(DateTimeFormatter.newTypeReference)».ISO_LOCAL_DATE_TIME));
							    «ENDIF»
							«ENDFOR»
						«ENDFOR»
						return o;
					    '''
				]
		]

	}

    protected def createServiceGetObjectByIdMethod(MutableClassDeclaration service) {
    	
        service.addMethod('getObjectById') [
            visibility = Visibility::PUBLIC
            addParameter("id", getKeyTypeType(clazz))
            addParameter("saveState", primitiveBoolean)
            returnType = clazz.newTypeReference
            body = ['''

            «toJavaCode(List.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> uow = new «toJavaCode(ArrayList.newTypeReference)»<>();

            «toJavaCode(Optional.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> entity = repository.findById(id);
            if (entity.isPresent()) {
                uow.add(entity.get());
            }

            if (uow.size() < 1) {
                throw new «toJavaCode(XObjectNotFoundException.newTypeReference)»("«clazz.newTypeReference.name»", id);
            }
            
            «clazz.newSelfTypeReference» result = uow.get(0);
            
            log.debug("Fetch object «clazz.simpleName» by id={} entity={}", id, result);
            
            if (saveState && result.getSavedState() == null && result instanceof «toJavaCode(SelfDirtinessTracker.newTypeReference)» && !((SelfDirtinessTracker) result).hasDirtyAttributes()) {
                result.saveState();
            }

            return result;
            '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. @Transactional(readOnly=true) для JPA) 
        ]
        
        service.addMethod('getObjectById') [
            visibility = Visibility::PUBLIC
            addParameter("id", getKeyTypeType(clazz))
            returnType = clazz.newTypeReference
            body = ['''
            return getObjectById(id, isSaveState());
            '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transactional readOnly для jpa) 
        ]
        
        service.addMethod('getObjectById') [
	        visibility = Visibility::PUBLIC
	        addParameter("predicate", Predicate.newTypeReference)
	        addParameter("id", getKeyTypeType(clazz))
	        returnType = clazz.newTypeReference
	        body = ['''
	        return getObjectById(predicate, id, isSaveState());
	        '''
	        ]
	        it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transactional readOnly для jpa)
	    ]
    }
    
    protected final def boolean isSaveState() {
    	return Boolean.parseBoolean(context.properties.getProperty(SAVE_STATE_PROP_NAME)) || isSaveState(clazz)
    }

    protected def createServiceDeleteMethods(MutableClassDeclaration service) {
      service.addMethod('delete') [
            visibility = Visibility::PUBLIC
            addParameter("id", getKeyTypeType(clazz)) // keyType)
            body = ['''
            log.debug("Delete object «clazz.simpleName» by id={}", id);
            repository.deleteById(id);
                '''
            ]
        ]

        service.addMethod('deleteAll') [
            visibility = Visibility::PUBLIC
            body = ['''
            log.debug("Delete all objects «clazz.simpleName»");
            repository.deleteAll();
                '''
            ]
        ]

    }

    protected def createServiceMethodValidate(MutableClassDeclaration service) {
        service.addMethod("validate") [
            visibility = Visibility::PUBLIC
            returnType = primitiveBoolean
            addParameter("entity", clazz.newTypeReference)
            body = [
                '''
                return validate(entity, false, null);
                '''
            ]
        ]

        service.addMethod("validate") [
            visibility = Visibility::PUBLIC
            returnType = primitiveBoolean
            addParameter("entity", clazz.newTypeReference)
            addParameter("facade", primitiveBoolean)
            addParameter("context", ConstraintValidatorContext.newTypeReference)
            body = [
				'''
                «IF clazz.needToAddValidationLogic(ReadOnlyType.ALL, moduleContext.transformationContext)»
                    boolean validationPassed = true;
                    «IF DomainClassCompileUtil.isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                        «val facadeConditionForMethod = if (DomainClassCompileUtil.isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.FACADE)) "facade" else "!facade"»
                    if («facadeConditionForMethod») {
                    «ENDIF»
                        if (entity.isNew()) {
                            final String msgCode = "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.readonly.message";
                    «IF DomainClassCompileUtil.isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                            if (context != null) {
                                context.disableDefaultConstraintViolation();
                                context.buildConstraintViolationWithTemplate(msgCode).addConstraintViolation();
                            }
                            return false;
                    «ELSE»
                        «FOR fieldName : DomainClassCompileUtil.getAllReadOnlyFieldsIncludeSuperClasses(clazz, ReadOnlyType.ALL, transformationContext)
                                            .filter[!it.isStatic && !systemFields.contains(it.simpleName)]
                                            .map[it.simpleName.toFirstUpper].toSet»
                            if (!«toJavaCode(Objects.newTypeReference())».isNull(entity.get«fieldName»()) && !entity.get«fieldName»().equals(createNew("").get«fieldName»())) {
                                if (context != null) {
                                    context.disableDefaultConstraintViolation();
                                    context.buildConstraintViolationWithTemplate(msgCode).addConstraintViolation();
                                }
                                return false;
                            }
                        «ENDFOR»
                    «ENDIF»
                        } else {
                        «toJavaCode(Set.newTypeReference(String.newTypeReference))» dirtyAttributes = entity.getDirtyAttributes();
                        «IF DomainClassCompileUtil.isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                            if (!dirtyAttributes.isEmpty()) {
                                return false;
                            }
                        «ELSE»
                            if (facade) {
                            «FOR fieldName : DomainClassCompileUtil.getAllReadOnlyFieldsIncludeSuperClasses(clazz, ReadOnlyType.FACADE, transformationContext)
                                                .filter[!it.isStatic && !systemFields.contains(it.simpleName)].map[it.simpleName].toSet»
                                if (dirtyAttributes.contains("«fieldName»")) {
                                    return false;
                                }
                            «ENDFOR»
                            } else {
                            «FOR fieldName : DomainClassCompileUtil.getAllReadOnlyFieldsIncludeSuperClasses(clazz, ReadOnlyType.SERVER, transformationContext)
                                                .filter[!it.isStatic && !systemFields.contains(it.simpleName)].map[it.simpleName].toSet»
                                if (dirtyAttributes.contains("«fieldName»")) {
                                    return false;
                                }
                            «ENDFOR»
                            }
                        «ENDIF»
                        }
                    «IF DomainClassCompileUtil.isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                    }
                    «ENDIF»
                    return validationPassed;
                «ELSE»
                    return true;
                «ENDIF»
            '''
            ]
        ]
    }

    protected def createServiceGetObjectsMethods(MutableClassDeclaration service) {
        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects());
                result = repository.findAll(limit);
            } else {
                result = repository.findAll();
            }
            return result;
                '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transctional readOnly для jpa) 
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", getQueryPredicateType())
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects());
                result = repository.findAll(query, limit);
            } else {
                result = repository.findAll(query);
            }
            return result;
            '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transctional readOnly для jpa) 
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            addParameter("sort", Sort.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects(), sort);
                result = repository.findAll(limit);
            } else {
                result = repository.findAll(sort);
            }
            return result;
            '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transctional readOnly для jpa) 
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", getQueryPredicateType())
            addParameter("sort", Sort.newTypeReference)
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects(), sort);
                result = repository.findAll(query, limit);
            } else {
                result = repository.findAll(query, sort);
            }
            return result;
                '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transctional readOnly для jpa) 
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("pageable", Pageable.newTypeReference)
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> uow = repository.findAll(pageable);
            return uow;
                '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transctional readOnly для jpa) 
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", getQueryPredicateType())
            addParameter("pageable", Pageable.newTypeReference)
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> uow = repository.findAll(query, pageable);
            return uow;
                '''
            ]
            it.addReadConveyerAnnotations //добавить аннотации методам read сессии (напр. Transctional readOnly для jpa) 
        ]
    }

    protected def createServiceMethodSave(MutableClassDeclaration service) {
        service.addMethod("save") [
    	    addAnnotation(Override.newAnnotationReference)
            visibility = Visibility::PUBLIC
            returnType = clazz.newTypeReference
            addParameter("entity", clazz.newTypeReference)
            body = ['''
            return save(entity, isSaveState());
            ''']
        ]
    	
        service.addMethod("save") [
            addAnnotation(Override.newAnnotationReference)
            visibility = Visibility::PUBLIC
            returnType = clazz.newTypeReference
            addParameter("entity", clazz.newTypeReference)
            addParameter("saveState", boolean.newTypeReference)
            body = ['''
            log.debug("Save object «clazz.simpleName» id={}, state={}", entity.getId(), entity.toString());
            if(entity.isRemoved()){
                log.debug("Delete object «clazz.simpleName» id={}, state={}", entity.getId(), entity.toString());
                repository.delete(entity);
                return null;
            }
            
            «toJavaCode(clazz.newTypeReference)» result = repository.save(entity);
            
            «createSaveStateForSaveMethod»
            
            return result;
            ''']
        ]
    }
    
    protected def createSaveStateForSaveMethod() {
    	return '''
    	result.setNew(false);
    	                    
    	if (result instanceof «SelfDirtinessTracker.newTypeReference») {
    	    if (saveState) {
    	        result.saveState();
    	    } 
    	    result.clearDirtyAttributes();
    	}
    	'''
    }

    protected def createServiceMethodCount(MutableClassDeclaration service) {
        service.addMethod('count') [
            visibility = Visibility::PUBLIC
            addParameter("query", getQueryPredicateType())
            returnType = primitiveLong
            body = ['''
            long result = repository.count(query);
            log.debug("Count objects «clazz.simpleName» count={}", result);
            return result;
                '''
            ]
        ]
        
        service.addMethod('count') [
            visibility = Visibility::PUBLIC
            returnType = primitiveLong
            body = ['''
            return count(null);
                '''
            ]
        ]
    }

    protected def createServiceMethodGetMaxObject(MutableClassDeclaration service) {
        service.addMethod('getMaxObjects') [
            visibility = Visibility::PUBLIC
            returnType = primitiveInt
            body = ['''
            return maxObjects;
                '''
            ]
        ]
    }

    protected def createGetPredicateToAffectAllReadMethods(MutableClassDeclaration service) {}

    def addReadConveyerAnnotations(MutableMethodDeclaration serviceMethod) {}

    override produceRequiredFields(){
        val procedure = addAnnotationsToGeneratedField as Procedures.Procedure1<MutableFieldDeclaration>
        clazz.produceRequiredFields(procedure, moduleContext.transformationContext)
    }

    protected def addAnnotationsToGeneratedField() {
    }

    override validateBeforeTransform() {
        val context = moduleContext.transformationContext
        clazz.checkAnnotationsCombination(context)
        clazz.checkUnsupportedClassAnnotations(context)
        clazz.checkPublicAccessToMethodsOfSet(context)
        clazz.checkPublicAccessToMethodsOfGet(context)
        clazz.checkVersionField(context)
        clazz.checkClassPersistenceHierarchicalType(context)
        //TODO XFW1249 не получается проверить указан ли параметр аннотации
        clazz.checkTransientParams(context)
        checkManyToManyRelationshipWithOneJoinTable()
        checkServerOnly()
    }

    /** Проверяет ошибки конфигурации ServerOnly. */
    private def checkServerOnly() {
        for (field : clazz.declaredFields.filter[it.findAnnotation(XFWServerOnly.newAnnotationReference.annotationTypeDeclaration) !== null]) {
            getInadmissibleWithServerOnlyAnnotation()
                .filter[field.findAnnotation(it.newAnnotationReference.annotationTypeDeclaration) !== null]
                .forEach[field.addError(String.format(ERR_MSG_ANNOTATION_COMBINATION_IS_INADMISSIBLE, it.simpleName, "XFWServerOnly"))]

            getInefficientWithServerOnlyAnnotation()
                .filter[field.findAnnotation(it.newAnnotationReference.annotationTypeDeclaration) !== null]
                .forEach[field.addWarning(String.format(ERR_MSG_ANNOTATION_COMBINATION_IS_INEFFICIENT, it.simpleName, "XFWServerOnly"))]
        }
    }

    /** Список недопустимых для совместного использования с XFWServerOnly аннотаций. */
    protected def Iterable<Class<?>> getInadmissibleWithServerOnlyAnnotation() {
        #[XFWPrimaryKey, org.springframework.data.annotation.Id, Version]
    }

    /** Список неэффективных для совместного использования с XFWServerOnly аннотаций. */
    protected def Iterable<Class<?>> getInefficientWithServerOnlyAnnotation() {
        #[XFWProtected, XFWFacadeIgnore]
    }

    /** Проверяет ошибки конфигурации связи ManyToMany для класса, в случае если указана одна join таблица. */
    private def checkManyToManyRelationshipWithOneJoinTable() {
    	for (field : clazz.declaredFields) {
        	val annotationManyToMany = field.findAnnotation(XFWManyToMany.newTypeReference.type)
        	val annotationJoinTable = field.findAnnotation(JoinTable.newTypeReference.type)

        	if (annotationManyToMany !== null && annotationJoinTable !== null) {
                val joinTable = annotationJoinTable.getStringValue("name")

                if (joinTable !== null) {
                    val mappedBy = annotationManyToMany.getStringValue("mappedBy")
                    var relationClass = findRelationClass(field, annotationManyToMany)
                    if(relationClass === null) {
                        // если сюда попали, то тип еще не загружен. Но когда будет проверятся он, валидация произойдет
                        return
                    }
                    if (relationClass.qualifiedName.equals(clazz.qualifiedName)) {
                        // Когда объект ссылается на самого себя
                        return
                    }

                    if(mappedBy !== null && !mappedBy.isEmpty) {
                        if(isExistMappedByParamInFieldIfEqJoinTable(relationClass, mappedBy, joinTable)) {
                            field.addError(ERR_MSG_MANY_TO_MANY_MAPPEDBY_FOUND_TWICE)
                        }
                    } else {
                        if(!isExistFieldWithMappedByAndTypeAndNameIfEqJoinTable(relationClass, clazz.qualifiedName, field.simpleName, joinTable)) {
                            field.addError(ERR_MSG_MANY_TO_MANY_MAPPEDBY_IS_NOT_EXISTS)
                        }
                    }
                }
        	}
        }
    }
    
    /** Находит сущность с которой связано поле в отношеннии многие к многим.
     * @parem поле.
     * @parem описание аннотации ManyToMany.
     * @return описание связанного доменного объекта.
     */
    private def MutableClassDeclaration findRelationClass(FieldDeclaration field, AnnotationReference annotationManyToMany) {
    	val targetEntity = (annotationManyToMany.getValue("targetEntity") as TypeReference)?.name
    	var relationClass = findClassByName(targetEntity)
    	
        if(relationClass === null && !field.type.getActualTypeArguments.isEmpty) {
        	relationClass = findClassByName(field.type.getActualTypeArguments.get(0).name)
        }
        
        return relationClass
    }



    /** Находит класс по имени в muduleContext.
     * @parem name полное имя класса.
     * @return описание класса.
     */
    private def MutableClassDeclaration findClassByName(String name) {
    	 return moduleContext.mutableClasses.findFirst[it.qualifiedName.equals(name)]
    }

    /** Проверяет ниличие не пустого строкового указанного параметра аннотации в классе над полем, если связь использует такую же join таблицу.
     * @param clazz класс для поиска.
     * @param fieldName имя поля.
     * @param joinTable имя join таблицы.
     * @return true/false.
     */
    private def boolean isExistMappedByParamInFieldIfEqJoinTable(
            MutableClassDeclaration clazz,
            String fieldName,
            String joinTable) {
        val field = clazz.findDeclaredField(fieldName)
        if (field === null) {
            return false
        }
        val annotationManyToMany = field.findAnnotation(XFWManyToMany.newTypeReference.type)
        val annotationJoinTable = field.findAnnotation(JoinTable.newTypeReference.type)
        if (annotationManyToMany === null || annotationJoinTable === null
                || !joinTable.equalsIgnoreCase(annotationJoinTable.getStringValue("name"))) {
            return false
        }
        val paramValue = annotationManyToMany.getStringValue("mappedBy")
        return paramValue !== null && !paramValue.isEmpty()
    }

    /** Проверяет ниличие поля в указаном классе, которое имеет аннотацию ManyToMany и атрибут mappedBy указывающий
     * на указнное поле, если связь использует такую же join таблицу.
     * @param clazz класс для поиска.
     * @param typeName generic тип искомого поля.
     * @param fieldName имя поля на которое должен указывать mappedBy.
     * @param joinTable имя join таблицы.
     * @return true/false.
     */
    private def boolean isExistFieldWithMappedByAndTypeAndNameIfEqJoinTable(
            MutableClassDeclaration clazz,
            String typeName,
            String fieldName,
            String joinTable) {
        for (field : clazz.declaredFields) {
            val annotation = field.findAnnotation(XFWManyToMany.newTypeReference.type)

            if (annotation !== null && typeName.equals(field.type.getActualTypeArguments.get(0)?.name)) {
                val annotationJoinTable = field.findAnnotation(JoinTable.newTypeReference.type)
                val paramValue = annotation.getStringValue("mappedBy")

                if (annotationJoinTable !== null && joinTable.equalsIgnoreCase(annotationJoinTable.getStringValue("name"))) {
                	if (fieldName.equals(paramValue)) {
                		return true
                	} else {
                    	return false
                    }
                }
            }
        }

        return true
    }

    override doValidate() {
        val context = moduleContext.validationContext
        checkFields(context)
        checkIdField()
        clazz.checkLabels(context)
        clazz.checkProtectedType(context)
        clazz.checkNullable(context)
        clazz.checkFieldNames(context)
        clazz.checkBlobFieldsOnTransientObjects(context)
        clazz.checkShadowing(context)
        clazz.checkXfwDefaultValue(context)
    }

    override doGenerateCode() {
    }

    private def checkFields(extension ValidationContext context) {
        val uncheckedFields = clazz.checkCoreFields(context)
        uncheckedFields.checkModuleFields(context)
    }
    
    protected def checkModuleFields(List<FieldDeclaration> uncheckedFields, extension ValidationContext context) {
    	uncheckedFields.forEach[ field |
    		field.addWarning("Field type not checked - " + field.type);
    	]
    }

    override getFieldsList() {
        ClassUtil.getFieldsList(clazz)
    }

    /**
     * Получить репозиторий для класса.
     *
     * @return объект {@link Type}.
     */
    def Type getRepository() {
        if(repository !== null) {
            return repository;
        }

        val repFqn = GeneratorHelper.getRepositoryQName(clazz);

        repository = getContext().getTransformationContext().findInterface(repFqn);
        if(repository === null) {
            repository = getContext().getTransformationContext().findTypeGlobally(repFqn);
        }

        if(repository === null) {
            throw new IllegalStateException(
                    "Repository interface not found for "
                            + clazz.getSimpleName());
        }
        return repository;
    }

    /**
     * @return получить реализацию репозитория.
     */
    def Type getRepositoryImpl() {
        if(repositoryImpl !== null) {
            return repositoryImpl;
        }
        val repFqn = GeneratorHelper.getRepositoryImplQName(clazz);

        repositoryImpl = getContext().getTransformationContext().findClass(repFqn);
        if(repositoryImpl === null) {
            repositoryImpl = getContext().getTransformationContext().findTypeGlobally(repFqn);
        }

        if(repositoryImpl === null) {
            throw new IllegalStateException("Repository implementation class not found for " + clazz.getSimpleName());
        }
        return repositoryImpl;
    }

    /**
     * @return получить сервис.
     */
    def MutableClassDeclaration getService() {
        if(service !== null) {
            return service;
        }
        service = getContext().getTransformationContext().findClass(GeneratorHelper
        .getServiceQName(clazz));
        return service;
    }

    /**
     * @return получить сервис трансформации.
     */
    def MutableClassDeclaration getServiceTo() {
        if(dtoService !== null) {
            return dtoService;
        }
        dtoService = getContext().getTransformationContext().findClass(GeneratorHelper
        .getTOServiceQName(clazz));
        return dtoService;
    }

    /**
     * @return получить контроллер.
     */
    def MutableClassDeclaration getController() {
        if(controller !== null) {
            return controller;
        }
        controller = getContext().getTransformationContext().findClass(GeneratorHelper
        .getControllerQName(clazz));
        return controller;
    }


    override ecoreAddColumnProps(EStructuralFeature e, Iterable<? extends AnnotationReference> annotations,
    TypeReferenceProvider arg2, XfwValueType xfwValueType) {

        val columnAnnotation = eFactory.createEAnnotation
        columnAnnotation.setSource(XFWConstants.COLUMN_ANNOTATION_SOURCE.getUri())

        val relationAnnotation = annotations.findFirst [
            annotationTypeDeclaration == XFWManyToOne.newTypeReference.type ||
                    annotationTypeDeclaration == XFWOneToOne.newTypeReference.type
        ]

        val basicAnnotation = annotations.findFirst [
            annotationTypeDeclaration == XFWBasic.newTypeReference.type
        ]

        // JXFW-953 Для комплексных типов по умолчанию unsettable=false
        // иначе веб-клиент не валидирует отдельные поля комплексного типа
        var unsettable= xfwValueType!=XfwValueType.COMPLEX


        if(basicAnnotation !== null)  unsettable = basicAnnotation.getBooleanValue("optional")
        if(relationAnnotation !== null) unsettable = relationAnnotation.getBooleanValue("optional")

        e.setUnsettable(unsettable)
        e.getEAnnotations().add(columnAnnotation)

    }



}
