package ru.croc.ctp.jxfw.jpa.generator

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Predicate
import java.util.List
import java.util.Set
import java.util.Objects
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.Transient
import javax.persistence.Id
import javax.persistence.Version
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWBasic
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToOne
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToOne
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException
import ru.croc.ctp.jxfw.core.generator.EcoreModelEmitter
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext
import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil
import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.ReadOnlyType
import ru.croc.ctp.jxfw.core.generator.impl.EcoreModelEmitterBaseImpl
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper
import ru.croc.ctp.jxfw.core.generator.impl.PersistenceModuleBaseImpl
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject.PersistenceType
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker
import ru.croc.ctp.jxfw.jpa.converter.PeriodPersistenceConverter
import ru.croc.ctp.jxfw.jpa.domain.DomainJpaObject
import ru.croc.ctp.jxfw.jpa.domain.DomainJpaService
import ru.croc.ctp.jxfw.metamodel.XFWConstants
import ru.croc.ctp.jxfw.metamodel.XfwValueType

import javax.persistence.OneToOne
import javax.persistence.Access
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import javax.persistence.AccessType

import static org.eclipse.emf.ecore.EcorePackage.Literals.ESTRING
import static ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import javax.persistence.IdClass
import java.util.Optional
import javax.persistence.MappedSuperclass
import ru.croc.ctp.jxfw.core.domain.DomainObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaContext
import javax.persistence.EntityManager
import java.util.LinkedList
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToMany
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToMany
import com.querydsl.jpa.impl.JPAQuery
import java.util.Map
import java.util.HashMap
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver
import ru.croc.ctp.jxfw.jpa.generator.dto.PreloadDto
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisherAware
import java.util.stream.Collectors
import ru.croc.ctp.jxfw.core.domain.meta.XFWServerOnly
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWTransient

class PersistenceModuleJpa extends PersistenceModuleBaseImpl {

	new(MutableClassDeclaration clazz, PersistenceModuleContext moduleContext) {
		super(clazz, moduleContext)
	}

	new(PersistenceModuleContext moduleContext) {
		super(moduleContext)
	}

	protected override EcoreModelEmitter createEcoreModelEmitter() {
		return new EcoreModelEmitterBaseImpl(getClazz, getContext)
	}

	protected override TypeReference getQueryPredicateType() {
		Predicate.newTypeReference
	}

    override extendMappedSuperclass() {
    	
    	clazz.addAnnotation(moduleContext.transformationContext.newAnnotationReference(MappedSuperclass));
    	
        val relationProcessor = new XFWRelationProcessor()
        relationProcessor.processRelation("ManyToOne", clazz, this, transformationContext)
        relationProcessor.processRelation("ManyToMany", clazz, this, transformationContext)
        relationProcessor.processRelation("OneToMany", clazz, this, transformationContext)
        relationProcessor.processRelation("OneToOne", clazz, this, transformationContext)
        
        
        super.extendMappedSuperclass
    }

    /** Список недопустимых для совместного использования с XFWServerOnly аннотаций. */
    protected override getInadmissibleWithServerOnlyAnnotation() {
        super.getInadmissibleWithServerOnlyAnnotation() + #[Id, Version]
    }

	override extendClazz() {

		clazz.declaredFields.filter[systemFields.contains(simpleName)].forEach [
			addError("Conflicts with the system field name")
		]

		if (clazz.findAnnotation(Entity.newTypeReference.type) === null &&
			findXFWObjectAnnotation(clazz).getEnumValue("persistence").simpleName !=
				PersistenceType.TRANSIENT.toString) {
					// сущности с установленным параметром @XFWObject(persistence = PersistenceType.TRANSIENT) не могут быть @Entity
					clazz.addAnnotation(Entity.newAnnotationReference)
				}

				clazz.declaredFields.forEach [ field |
					if (field.findAnnotation(Convert.newTypeReference.type) === null) {
						// В Hibernate 5 добавлена поддержка типов JSR-310
						/* if("LocalDateTime".equals(field.type.simpleName)) {
						 *     field.addAnnotation(Convert.newAnnotationReference [
						 *         setClassValue("converter", LocalDateTimeConverter.newTypeReference)
						 *     ])
						 * } else if("LocalDate".equals(field.type.simpleName)) {
						 *     field.addAnnotation(Convert.newAnnotationReference [
						 *         setClassValue("converter", LocalDateConverter.newTypeReference)
						 *     ])
						 } else */
						if ("Period".equals(field.type.simpleName)) {
							field.addAnnotation(Convert.newAnnotationReference [
								setClassValue("converter", PeriodPersistenceConverter.newTypeReference)
							])
						}
					}

					if (field.findAnnotation(XFWBasic.newTypeReference.type) !== null) {
						field.addAnnotation(Basic.newAnnotationReference [
							setBooleanValue("optional",
								field.findAnnotation(XFWBasic.newTypeReference.type).getBooleanValue("optional"))
							setEnumValue("fetch",
								field.findAnnotation(XFWBasic.newTypeReference.type).getEnumValue("fetch"))
						])

						// JXFW-953 переопределение нулабельности полей внутри комплесного поля
						if (field.findAnnotation(XFWBasic.newTypeReference.type).getBooleanValue("optional") &&
							new GeneratorHelper(transformationContext).isComplex(field.type) &&
							(field.type.type instanceof ClassDeclaration) &&
							field.findAnnotation(AttributeOverride.newTypeReference.type) === null &&
							field.findAnnotation(AttributeOverrides.newTypeReference.type) === null) {
							field.addAnnotation(AttributeOverrides.newAnnotationReference [
								setAnnotationValue("value", nullableColumnAnnotationList("", field))
							])
						}

					}
				]
				if (isPersistence(clazz)) {
					val relationProcessor = new XFWRelationProcessor()
					relationProcessor.processRelation("ManyToOne", clazz, this, transformationContext)
					relationProcessor.processRelation("ManyToMany", clazz, this, transformationContext)
					relationProcessor.processRelation("OneToMany", clazz, this, transformationContext)
					relationProcessor.processRelation("OneToOne", clazz, this, transformationContext)
				}

				super.extendClazz
			}
			// JXFW-1183 Учет атрибута nullable для свойств комплексных типов с вложенным комплексным свойством
			private def List<AnnotationReference> nullableColumnAnnotationList(String prefix,FieldDeclaration field) {
				val annList = (field.type.type as ClassDeclaration).declaredFields.filter [
					it.findAnnotation(Column.newTypeReference.type) !== null &&
						!it.findAnnotation(Column.newTypeReference.type).getBooleanValue("nullable")
				].map [ complexInnerField |
					nullableColumnAnnotation(prefix, complexInnerField)
				].toList() 
				val nestedAnnList = (field.type.type as ClassDeclaration).declaredFields.filter [
					new GeneratorHelper(transformationContext).isComplex(it.type)
					// это чтобы не уходить в бесконечную рекурсию по полю savedState
					&& !field.type.type.equals(it.type.type)
				].map[ 
					logger.debug("nullableColumnAnnotationList" + (if(prefix.empty) "" else prefix + "." )+ it.simpleName)
					nullableColumnAnnotationList((if(prefix.empty) "" else prefix + "." )+ it.simpleName, it)
				].flatten
				annList.addAll(nestedAnnList)
				return annList
			}

			private def AnnotationReference nullableColumnAnnotation(String prefix,
				FieldDeclaration complexInnerField) {
				val column = complexInnerField.findAnnotation(Column.newTypeReference.type)
				return AttributeOverride.newAnnotationReference [
					setStringValue("name", (if(prefix.empty) "" else prefix + "." )+ complexInnerField.simpleName)
					setAnnotationValue("column", Column.newAnnotationReference [
						// nullable выставляем в true
						setBooleanValue("nullable", true)
						// все остальные поля переносим как было на поле комплексного типа
						// если значение отличается от дефолтного
						// @AttributeOverrides(value = @AttributeOverride(name = "house", column = @Column(nullable = true, name = "", columnDefinition = "", table = "", unique = false, insertable = true, updatable = true, length = 20, precision = 0, scale = 0)))
						if (!column.getStringValue("name").isEmpty) {
							setStringValue("name", column.getStringValue("name"))
						}
						if (!column.getStringValue("columnDefinition").isEmpty) {
							setStringValue("columnDefinition", column.getStringValue("columnDefinition"))
						}
						if (!column.getStringValue("table").isEmpty) {
							setStringValue("table", column.getStringValue("table"))
						}
						if (column.getBooleanValue("unique")) {
							setBooleanValue("unique", column.getBooleanValue("unique"))
						}
						if (!column.getBooleanValue("insertable")) {
							setBooleanValue("insertable", column.getBooleanValue("insertable"))

						}
						if (!column.getBooleanValue("updatable")) {
							setBooleanValue("updatable", column.getBooleanValue("updatable"))

						}
						if (column.getIntValue("length") != 255) {
							setIntValue("length", column.getIntValue("length"))

						}
						if (column.getIntValue("precision") != 0) {
							setIntValue("precision", column.getIntValue("precision"))
						}
						if (column.getIntValue("scale") != 0) {
							setIntValue("scale", column.getIntValue("scale"))
						}
					])
				]

			}

			override protected extendTopParentOnly() {
				if (DomainClassCompileUtil.isReadOnlyEntity(clazz)) {
					clazz.implementedInterfaces = clazz.implementedInterfaces
				} else {
					if (isPersistence(clazz)) {
						// сущности с установленным параметром @XFWObject(persistence = PersistenceType.TRANSIENT) не могут быть DomainJpaObject
						clazz.implementedInterfaces = clazz.implementedInterfaces + #[DomainJpaObject.newTypeReference]
					}
				}

				// TODO Почему-то перестает работать AccessorsProcessor, если добавить конструктор
				// clazz.addConstructor [
				// addParameter('id', String.newTypeReference)
				// body = ['''this.id = id;''']
				// ]
				// xfwRelProc.processJoinTable(clazz, this, context)
				val columnAnnotation = eFactory.createEAnnotation
				columnAnnotation.setSource(XFWConstants.COLUMN_ANNOTATION_SOURCE.getUri())

				super.extendTopParentOnly
			}

			override protected addOptimisticLockField() {
				clazz.addOptimisticLockField(Version, moduleContext.transformationContext)
			}

			override protected getTransientAnnotation() {
				Transient.newAnnotationReference
			}
			
			override addIdFieldsAndMethods() {
				super.addIdFieldsAndMethods()
				var idField = findId(clazz)
				if (idField !== null && idField instanceof MutableFieldDeclaration) {
					if (clazz.findAnnotation(IdClass.newTypeReference.type) === null 
						&& idField.findAnnotation(XFWPrimaryKey.newTypeReference.type) === null 
						&& idField.findAnnotation(Access.newTypeReference.type) === null
					) {
						val mutableIdField =  idField as MutableFieldDeclaration
						mutableIdField.addAnnotation(Access.newAnnotationReference [
							setEnumValue("value", getEnumValue(AccessType.newTypeReference, AccessType.PROPERTY.name))
						])
					}
				}
			}

			override getRepositoryExtendedInterfaces() {
				#[
					JpaRepository.newTypeReference(clazz.newTypeReference, getKeyTypeType(clazz)), // String.newTypeReference()),
					QuerydslPredicateExecutor.newTypeReference(clazz.newTypeReference)
				]
			}

			override getKeyType(ClassDeclaration clazz) {
				getKeyTypeType(clazz).name
			}

			override ecoreAddColumnProps(EStructuralFeature e, Iterable<? extends AnnotationReference> annotations,
				extension TypeReferenceProvider context, XfwValueType xfwValueType) {

				val columnAnnotation = eFactory.createEAnnotation
				columnAnnotation.setSource(XFWConstants.COLUMN_ANNOTATION_SOURCE.getUri())

				val columnAnnRef = Column.newTypeReference
				val joinColumnAnnRef = JoinColumn.newTypeReference

				val a = annotations.findFirst [
					getAnnotationTypeDeclaration() == columnAnnRef.getType() ||
						getAnnotationTypeDeclaration() == joinColumnAnnRef.getType()
				]

				val length = if (a !== null && a.getAnnotationTypeDeclaration() != joinColumnAnnRef.getType())
						a.getIntValue("length")
					else
						255
				if (ESTRING.equals(e.getEType())) {
					columnAnnotation.getDetails().put("length", length.toString())
				}

				// информация из аттрибута Column#name 
				if (a !== null) {
					val String name = a.getStringValue("name");
					if (StringUtils.isNotEmpty(name)) {
						columnAnnotation.getDetails().put("name", name);
					}
				}

				val relationAnnotation = annotations.findFirst [
					annotationTypeDeclaration == XFWManyToOne.newTypeReference.type ||
						annotationTypeDeclaration == XFWOneToOne.newTypeReference.type
				]

				val basicAnnotation = annotations.findFirst [
					annotationTypeDeclaration == XFWBasic.newTypeReference.type
				]

				// JXFW-953 Для комплексных типов по умолчанию unsettable=false
				// иначе веб-клиент не валидирует отдельные поля комплексного типа
				var unsettable = xfwValueType != XfwValueType.COMPLEX

				if(basicAnnotation !== null) unsettable = basicAnnotation.getBooleanValue("optional")

				if(relationAnnotation !== null) unsettable = relationAnnotation.getBooleanValue("optional")
				if(a !== null) unsettable = a.getBooleanValue("nullable")

				e.setUnsettable(unsettable)
				e.getEAnnotations().add(columnAnnotation)
			}

			override EReference ecoreGetOpposite(FieldDeclaration field, EClass refClass,
				List<MutableClassDeclaration> mutableClasses, extension TypeReferenceProvider context) {
				var result = null as EReference;
				val aref = field.annotations.findFirst [ a |
					a.getAnnotationTypeDeclaration.isAssignableFrom(OneToMany.newTypeReference.getType()) ||
						a.getAnnotationTypeDeclaration.isAssignableFrom(ManyToMany.newTypeReference.getType()) ||
						a.getAnnotationTypeDeclaration.isAssignableFrom(OneToOne.newTypeReference.getType())
				]
				if (aref !== null) {
					val opposite_attr = aref.getStringValue("mappedBy");
					if (opposite_attr !== null) {
						result = refClass.getEStructuralFeature(opposite_attr) as EReference;
					}
				}
				return result;
			}

			override createRepositoryImpl() {
			}

			override addReadConveyerAnnotations(MutableMethodDeclaration serviceMethod) {
				serviceMethod.addAnnotation(
					Transactional.newAnnotationReference [
						setBooleanValue("readOnly", true)
						if ("getObjectById".equals(serviceMethod.simpleName)) {
							setClassValue("noRollbackFor", XObjectNotFoundException.newTypeReference())
						}
					]
				)
			}

			override protected checkModuleFields(List<FieldDeclaration> uncheckedFields,
				extension ValidationContext context) {
				uncheckedFields.forEach [ field |
					field.checkJpaField(clazz, context)
				]
			}

			/**
			 * 	Проверка типов полей в доменном объекте.
			 * 	В доменном объекте разрешены свойства комплексного типа
			 * 	и навигируемые (массивные навигируемые) свойста персистентных и временных доменных типов
			 *
			 * 	
			 */
			private def checkJpaField(FieldDeclaration field, ClassDeclaration clazz,
				extension ValidationContext context) {

				val GeneratorHelper generatorHelper = new GeneratorHelper(context);
				if (generatorHelper.isComplex(field.type)) {
					// Do nothing ComplexType is allowed
				} else if (!generatorHelper.isDomain(field.type) 
					&& !generatorHelper.isDomainCollection(field.type)
					&& !isTransientAndServerOnly(field)) {
					field.addError("Field type is not supported in this case - " + field.type);
				}

			}
			
			private def isTransientAndServerOnly(FieldDeclaration field) {
				return field.findAnnotation(XFWServerOnly.newAnnotationReference.annotationTypeDeclaration) !== null 
				&& (field.findAnnotation(XFWTransient.newTypeReference.type) !== null 
				|| field.findAnnotation(getTransientAnnotation.annotationTypeDeclaration) !== null)
			}

			override doValidate() {
				if (!isPersistence(clazz)) {
					if (clazz.findAnnotation(Entity.newTypeReference.type) !== null) {
						clazz.addError("Transient entity must not contain @Entity annotation")
					}
				}

				super.doValidate()
			}

            override checkIdField() {
                //Поле id должно быть помечено аннотацией javax.persistence.Id
                clazz.declaredFields.forEach [
                    if (simpleName.equals("id")
                            &&  findAnnotation(Id.newAnnotationReference.annotationTypeDeclaration) === null) {
                        addError("Field " + simpleName + " has to be marked by annotation  @javax.persistence.Id")
                    }
                ]
            }

			override createServiceMethodValidate(MutableClassDeclaration service) {
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

			override createServiceGetObjectByIdMethod(MutableClassDeclaration service) {
				super.createServiceGetObjectByIdMethod(service)
				
				service.addMethod('getObjectById') [
					visibility = Visibility::PUBLIC
					addParameter("predicate", Predicate.newTypeReference)
					addParameter("id", getKeyTypeType(clazz))
					addParameter("saveState", primitiveBoolean)
					returnType = clazz.newTypeReference
					body = [
						'''
							«toJavaCode(Optional.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> entity = repository.findOne(predicate);
							if (!entity.isPresent()) {
							    throw new «toJavaCode(XObjectNotFoundException.newTypeReference)»("«clazz.newTypeReference.name»", id);
							}
							
							«clazz.newSelfTypeReference» result = entity.get();
							
							log.debug("Fetch object «clazz.simpleName» by predicate={} entity={}", predicate, result);
							
							if (saveState && result.getSavedState() == null && result instanceof «toJavaCode(SelfDirtinessTracker.newTypeReference)» && !((SelfDirtinessTracker) result).hasDirtyAttributes()) {
							    result.saveState();
							}
							
							return result;
						'''
					]
					it.addReadConveyerAnnotations // добавить аннотации методам read сессии (напр. Transactional readOnly для jpa) 
				]

			}
			
			def createGetPreloads() {
				service.addMethod('getPreloads') [
				visibility = Visibility::PUBLIC
				addAnnotation(Override.newAnnotationReference())
				addParameter("domainObjects", Iterable.newTypeReference(newWildcardTypeReference(clazz.newTypeReference())))
				addParameter("preloads", List.newTypeReference(List.newTypeReference(String.newTypeReference())))
				returnType = List.newTypeReference(DomainObject.newTypeReference(newWildcardTypeReference()))
				body = [
						'''
						final int MAX_ID_WHEREIN = 1000;
						«toJavaCode(List.newTypeReference(clazz.newTypeReference()))» domainObjectsList = new LinkedList<>();
						domainObjects.forEach(domainObjectsList::add);
						
						«toJavaCode(EntityManager.newTypeReference())» entityManager = jpaContext.getEntityManagerByManagedType(«toJavaCode(clazz.newTypeReference())».class); 

						«getQEntityPath(clazz)» q«clazz.simpleName» = new «getQEntityPath(clazz)»("«clazz.simpleName.toFirstLower»" + 0);
						
						«toJavaCode(List.newTypeReference(DomainObject.newTypeReference(newWildcardTypeReference)))» result = new «toJavaCode(LinkedList.newTypeReference())»<>();
						
						for (int i = 0; i < domainObjectsList.size(); i+=MAX_ID_WHEREIN) {
							JPAQuery<«clazz.simpleName»> query = new JPAQuery<«clazz.simpleName»>(entityManager).from(q«clazz.simpleName»);
							
							if(getMaxObjects()>0) {
								query.limit(getMaxObjects());	
							}
							
						    query.where(q«clazz.simpleName».in(domainObjectsList.subList(i, Math.min(i+MAX_ID_WHEREIN, i+domainObjectsList.size()))));
						    result.addAll(computePreload(query, preloads, 0).stream().distinct().filter(«toJavaCode(Objects.newTypeReference())»::nonNull).collect(«toJavaCode(Collectors.newTypeReference())».toList()));
						}
						
						return result;
						'''
					]
				]
					
			}
			
			
			def createComputePreloads() {
				service.addMethod('computePreload') [
					visibility = Visibility::PUBLIC
					addAnnotation(Override.newAnnotationReference())
					addParameter("query", JPAQuery.newTypeReference(newWildcardTypeReference(DomainObject.newTypeReference(newWildcardTypeReference))))
					addParameter("preloads", List.newTypeReference(List.newTypeReference(String.newTypeReference())))
					addParameter("i", primitiveInt)
					returnType = List.newTypeReference(DomainObject.newTypeReference(newWildcardTypeReference()))
					body = [
						'''
							«toJavaCode(EntityManager.newTypeReference())» entityManager = jpaContext.getEntityManagerByManagedType(«toJavaCode(clazz.newTypeReference())».class); 
							
							«getQEntityPath(clazz)» q«clazz.simpleName» = new «getQEntityPath(clazz)»("«clazz.simpleName.toFirstLower»" + i);
							final int j = i+1; 
							
							«toJavaCode(List.newTypeReference(DomainObject.newTypeReference(newWildcardTypeReference)))» result = new «toJavaCode(LinkedList.newTypeReference())»<>();
							
							«toJavaCode(Map.newTypeReference(String.newTypeReference(), PreloadDto.newTypeReference()))» pathMap = new «toJavaCode(HashMap.newTypeReference())»<>();

							for (List<String> preload: preloads) {
								String firstPreload = preload.remove(0);
							
								PreloadDto preloadDto = new PreloadDto();
								«FOR field : clazz.getAllFieldsIncludeSuperClasses.filter([ f | ((f.findAnnotation(XFWOneToOne.newTypeReference.type) !== null 
									|| f.findAnnotation(XFWManyToOne.newTypeReference.type) !== null) && isPersistence(f.type.type as TypeDeclaration)
								)])»
								
								if(firstPreload.equalsIgnoreCase("«field.simpleName»") && !pathMap.containsKey(firstPreload)) {
									«getQEntityPath(field.type)» q«field.simpleName» = new «getQEntityPath(field.type)»("«field.type.simpleName.toFirstLower»" + j);

									«toJavaCode(JPAQuery.newTypeReference(newWildcardTypeReference(DomainObject.newTypeReference(newWildcardTypeReference))))» cloneQuery = query.clone(entityManager).leftJoin(q«clazz.simpleName».«field.simpleName», q«field.simpleName»);
												
									for(«toJavaCode(clazz.newTypeReference())» domainObject: cloneQuery.clone(entityManager).select(q«clazz.simpleName»).fetchJoin().fetch().stream().collect(«toJavaCode(Collectors.newTypeReference())».toSet())) {
										if(domainObject.get«field.simpleName.toFirstUpper»() != null) {
											result.add(domainObject.get«field.simpleName.toFirstUpper»());	
										}
									}
									preloadDto.setTypeName("«field.type»");
									preloadDto.setQuery(cloneQuery);
								}
								«ENDFOR»
								«FOR field : clazz.getAllFieldsIncludeSuperClasses.filter([ f | ((f.findAnnotation(XFWOneToMany.newTypeReference.type) !== null 
									|| f.findAnnotation(XFWManyToMany.newTypeReference.type) !== null) && isPersistence(f.type.actualTypeArguments.get(0).type as TypeDeclaration)
								)])»
								
								if(firstPreload.equalsIgnoreCase("«field.simpleName»") && !pathMap.containsKey(firstPreload)) {
									«getQEntityPath(field.type.actualTypeArguments.get(0))» q«field.simpleName» = new «getQEntityPath(field.type.actualTypeArguments.get(0))»("«field.type.actualTypeArguments.get(0).type.simpleName.toFirstLower»" + j);
										
									«toJavaCode(JPAQuery.newTypeReference(newWildcardTypeReference(DomainObject.newTypeReference(newWildcardTypeReference))))» cloneQuery = query.clone(entityManager).leftJoin(q«clazz.simpleName».«field.simpleName», q«field.simpleName»);
										
									for(«toJavaCode(clazz.newTypeReference())» domainObject: cloneQuery.clone(entityManager).select(q«clazz.simpleName»).fetchJoin().fetch().stream().collect(«toJavaCode(Collectors.newTypeReference())».toSet())) {
										result.addAll(domainObject.get«field.simpleName.toFirstUpper»());
									}
									preloadDto.setTypeName("«field.type.actualTypeArguments.get(0)»");
									preloadDto.setQuery(cloneQuery);
								}
								«ENDFOR»	
								
								if(preloadDto.getTypeName() == null && !pathMap.containsKey(firstPreload)) {
									query.clone(entityManager).select(q«clazz.simpleName»).fetch().forEach(domainObject -> result.addAll(domainObject.obtainValueByPropertyName(firstPreload)));
								} else if (!preload.isEmpty()) { 
									pathMap.putIfAbsent(firstPreload, preloadDto);
									pathMap.get(firstPreload).getPreloads().add(preload);
								}							
							}
							
							pathMap.forEach((loaded, preloadDto) -> {
								«toJavaCode(DomainJpaService.newTypeReference())» service 
									= domainServiceResolver.resolveDomainService(preloadDto.getTypeName(), "Service");
								result.addAll(service.computePreload(preloadDto.getQuery(), preloadDto.getPreloads(), j));	
							});
							
							return result;
						'''
					] 
				]				
			}
			
			static def getQEntityPath(ClassDeclaration clazz) {
				return clazz.compilationUnit.packageName + ".Q" +clazz.simpleName
			}
			
			static def getQEntityPath(TypeReference type) {
				return type.name.substring(0, type.name.length-type.simpleName.length) + "Q" + type.simpleName 
			}
			
			override createService() {
				super.createService()
				
				service.implementedInterfaces = #[serviceInterface, ApplicationEventPublisherAware.newTypeReference, ApplicationContextAware.newTypeReference, DomainJpaService.newTypeReference]
				
				service.addField("domainServiceResolver") [
            		visibility = Visibility::PRIVATE
            		addAnnotation(Autowired.newAnnotationReference())
            		type = DomainServicesResolver.newTypeReference()
        		]
				
				service.addField("jpaContext") [
            		visibility = Visibility::PRIVATE
            		addAnnotation(Autowired.newAnnotationReference())
            		type = JpaContext.newTypeReference()
        		]
				
				createComputePreloads()
				
				createGetPreloads()
			}

			override createGetPredicateToAffectAllReadMethods(MutableClassDeclaration service) {
				service.addMethod('getPredicateToAffectAllReadMethods') [
					visibility = Visibility::PUBLIC
					returnType = BooleanBuilder.newTypeReference
					body = [
						'''
							return new BooleanBuilder();
						'''
					]
				]
			}
		}
		
