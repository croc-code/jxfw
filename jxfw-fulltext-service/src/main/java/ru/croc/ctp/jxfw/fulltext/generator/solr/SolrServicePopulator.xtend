package ru.croc.ctp.jxfw.fulltext.generator.solr

import javax.validation.ConstraintValidatorContext;

import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.slf4j.LoggerFactory
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.ApplicationEventPublisherAware
import java.time.ZonedDateTime
import java.time.LocalDateTime
import org.apache.commons.lang3.StringUtils
import java.time.format.DateTimeFormatter
import java.util.Objects
import org.springframework.stereotype.Service
import ru.croc.ctp.jxfw.solr.services.DomainSolrService
import ru.croc.ctp.jxfw.solr.predicate.SolrQueryContext
import javax.annotation.Nonnull
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.solr.core.query.SimpleQuery
import org.springframework.data.solr.core.query.Criteria
import ru.croc.ctp.jxfw.solr.utils.XfwSolrUtils
import java.util.ArrayList
import java.util.List
import java.util.UUID
import java8.lang.Iterables
//import ru.croc.ctp.jxfw.fulltext.generator.XFWSearchClass
import org.springframework.data.solr.core.mapping.Indexed
import com.querydsl.core.types.Predicate

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.fulltext.generator.solr.GeneratorHelperSolr.*

import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.ReadOnlyType
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import ru.croc.ctp.jxfw.core.domain.DomainService
//import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.meta.XFWDefaultValue
import ru.croc.ctp.jxfw.core.load.LoadContext
import ru.croc.ctp.jxfw.core.load.events.PreCheckSecurityEvent
import ru.croc.ctp.jxfw.core.load.events.BeforeLoadEvent
import ru.croc.ctp.jxfw.core.load.events.AfterLoadEvent
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException
import ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException
import ru.croc.ctp.jxfw.core.validation.meta.XFWReadOnly
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker
import java.util.Optional
import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil

/**
 * @author SMufazzalov
 * @since 1.4
 */
class SolrServicePopulator {

    private static final Logger logger = LoggerFactory.getLogger(SolrServicePopulator);

    val extension TransformationContext ctx
    val Iterable<MutableClassDeclaration> mutableClasses

    new(@Nonnull TransformationContext context, @Nonnull Iterable<MutableClassDeclaration> mClasses) {
        ctx = context
        mutableClasses = mClasses
    }

    private def getServiceInterfaces(MutableClassDeclaration clazz, boolean needForExtraMethods) {
        val list = newArrayList(
            DomainService.newTypeReference(clazz.newTypeReference, clazz.getKeyType(ctx), queryPredicateType),
            ApplicationEventPublisherAware.newTypeReference, ApplicationContextAware.newTypeReference
        )

        // когда solr не базовое хранилище, добавляем интерфейс, который сигнализирует о наличие метода
        //который принимая на вход допустим jpa доменный объект отдаст solr доменный объект (адаптер)
        if (needForExtraMethods) list += DomainSolrService.newTypeReference(clazz.newTypeReference)

        return list

    }

    protected def getQueryPredicateType() {
        com.querydsl.core.types.Predicate.newTypeReference
    }

    public def populate(MutableClassDeclaration clazz, boolean dual) {
        val sName = clazz.getServiceQName(dual)

        logger.debug(sName + " dual " + dual)

        val service = findClass(sName)

        service.addAnnotation(Service.newAnnotationReference[
            if (dual) setStringValue("value", "solr" + service.simpleName)
        ])

        //val searchAnn = ClassUtil.getAnnotation(clazz, XFWSearchClass)

        //метод проверяет что solr НЕ базовое хранилище и БЕЗ DSE под капотом
        val needForExtraMethods = clazz.isApacheSolr && dual

        service.implementedInterfaces = clazz.getServiceInterfaces(needForExtraMethods)

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

        //TODO искать правильно
        val repoInterface = ctx.findInterface(clazz.getRepositoryQName(dual))

        service.addField("repository") [
            visibility = Visibility::PRIVATE
            type = repoInterface.newTypeReference
        ]
        
        service.addMethod("setRepository") [
        	visibility = Visibility::PUBLIC
        	addParameter("repository", repoInterface.newTypeReference)
        	addAnnotation(Autowired.newAnnotationReference)
        	body = [
                "this.repository = repository;"
            ]
        ]

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

        //связи нужны только для базового хранилища(CASANDRA), не для jpa решения
        if (needForExtraMethods) {
            val domainClazz =  findClass(clazz.qualifiedName.replace(".solr", ""))

            service.addMethod('createNew') [
                visibility = Visibility::PUBLIC
                addParameter("domainObject", DomainObject.newTypeReference)
                returnType = clazz.
                newTypeReference
                body = ['''
                «IF clazz.abstract == true»
                	throw new «toJavaCode(UnsupportedOperationException.newTypeReference)»("Abstract Type Service");
                «ELSE»
		            log.debug("Create new object «clazz.simpleName» with entity={}", domainObject);
		            «domainClazz.qualifiedName» entity = («domainClazz.qualifiedName»)domainObject;
		            «toJavaCode(clazz.newTypeReference)» o = new «toJavaCode(clazz.newTypeReference)»();
		            o.setId(entity.getId());
		            o.setNew(true);
		            «FOR field : ClassUtil.getAllFieldsList(clazz).filter[it.findAnnotation(Indexed.newAnnotationReference.annotationTypeDeclaration) !== null]»
		                «val upper = field.simpleName.toFirstUpper»
		                o.set«upper»(entity.get«upper»());
		            «ENDFOR»
		            return o;
	            «ENDIF»
                '''
                ]
            ]
        }

        service.addMethod('createNew') [
            visibility = Visibility::PUBLIC
            addParameter("id", clazz.getKeyType(ctx))
            returnType = clazz.
            newTypeReference
            body = ['''
            «IF clazz.abstract == true»
            	throw new «toJavaCode(UnsupportedOperationException.newTypeReference)»("Abstract Type Service");
            «ELSE»
	            log.debug("Create new object «clazz.simpleName» with id={}", id);
	            «toJavaCode(clazz.newTypeReference)» o = new «toJavaCode(clazz.newTypeReference)»();
	            o.setId(id);
	            o.setNew(true);
	            «FOR field : clazz.declaredFields.filter[it.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration) !== null]»
	                «val value = field.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration).getStringValue("value")»
	                «val upper = field.simpleName.toFirstUpper»
	                «IF field.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration).getBooleanValue("asCurrent")»
	                    «IF field.type == ZonedDateTime.newTypeReference»
	                        o.set«upper»(«toJavaCode(ZonedDateTime.newTypeReference)».now());
	                    «ELSEIF field.type == LocalDateTime.newTypeReference»
	                        o.set«upper»(«toJavaCode(LocalDateTime.newTypeReference)».now());
	                    «ELSE»
	                        «field.addError("cant pass 'now' to not-time types")»
	                    «ENDIF»
	                «ELSEIF StringUtils.isEmpty(value)»
	                    «field.addError("length of value must be > 0")»
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
	            return o;
            «ENDIF»
                '''
            ]
        ]

        clazz.createServiceGetObjectsMethods(service)
        
        service.addMethod('getObjectById') [
            visibility = Visibility::PUBLIC
            addParameter("id", clazz.getKeyType(ctx))
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

            return result;
            '''
            ]
        ]
        
        service.addMethod('getObjectById') [
            visibility = Visibility::PUBLIC
            addParameter("id", clazz.getKeyType(ctx))
            returnType = clazz.newTypeReference
            body = ['''
            return getObjectById(id, false);
            '''
            ]
        ]
        
        service.addMethod('getObjectById') [
			visibility = Visibility::PUBLIC
			addParameter("predicate", Predicate.newTypeReference)
			addParameter("id", clazz.getKeyType(ctx))
			returnType = clazz.newTypeReference
			body = [
				'''
					return getObjectById(predicate, id, false);
				'''
			]
		]
		
        service.addMethod('getObjectById') [
			visibility = Visibility::PUBLIC
			addParameter("query", Predicate.newTypeReference)
			addParameter("id", clazz.getKeyType(ctx))
			addParameter("saveState", primitiveBoolean)
			returnType = clazz.newTypeReference
			body = [
				'''
	            «toJavaCode(SolrQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = (SolrQueryContext<?>) query;
	            «toJavaCode(List.newTypeReference(clazz.newTypeReference))» uow = new «toJavaCode(ArrayList.newTypeReference)»<>(repository.findAll(queryContext.getQuery()).getContent());
		
	            if (uow.size() < 1) {
	                throw new «toJavaCode(XObjectNotFoundException.newTypeReference)»("«clazz.newTypeReference.name»", id);
	            }
	            
	            «clazz.newSelfTypeReference» result = uow.get(0);
	            
	            log.debug("Fetch object «clazz.simpleName» by query={} entity={}", query, result);
	                        
	            return result;
	            '''
			]
		]

        service.addMethod('delete') [
            visibility = Visibility::PUBLIC
            addParameter("id", clazz.getKeyType(ctx))
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
            body = ['''
                «IF clazz.needToAddValidationLogic(ReadOnlyType.ALL, ctx)»
                    boolean validationPassed = true;
                    «IF isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                        «val facadeConditionForMethod = if (isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.FACADE)) "facade" else "!facade"»
                    if («facadeConditionForMethod») {
                    «ENDIF»
                        if (entity.isNew()) {
                            final String msgCode = "ru.croc.ctp.jxfw.core.exception.exceptions.XInvalidDataException.readonly.message";
                             «IF isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                                if (context != null) {
                                    context.disableDefaultConstraintViolation();
                                    context.buildConstraintViolationWithTemplate(msgCode).addConstraintViolation();
                                }
                                return false;
                            «ELSE»
                                «val xFWReadOnly = XFWReadOnly.newAnnotationReference.annotationTypeDeclaration»
                                «FOR field : clazz.declaredFields.filter[it.findAnnotation(xFWReadOnly) !== null && !it.isStatic]»
                                    «val upper = field.simpleName.toFirstUpper»
                                    «val facadeConditionForField = if (field.findAnnotation(xFWReadOnly).getBooleanValue("facade")) "facade" else "!facade"»
                                    if («facadeConditionForField» && !«toJavaCode(Objects.newTypeReference())».isNull(entity.get«upper»()) && !entity.get«upper»().equals(createNew("").get«upper»())) {
                                    if (context != null) {
                                        context.disableDefaultConstraintViolation();
                                        context.buildConstraintViolationWithTemplate(msgCode).addConstraintViolation();
                                    }
                                    return false;
                                }
                                «ENDFOR»
                            «ENDIF»
                        } else {
                            «toJavaCode(clazz.newTypeReference)» stored = getObjectById(entity.getId());
                            validationPassed = (stored != null); if (!validationPassed) return validationPassed;
                            «IF isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                                «FOR field : clazz.declaredFields.filter[!systemFields.contains(simpleName) && !it.isStatic]»
                                    «val upper = field.simpleName.toFirstUpper»
                                    validationPassed = («toJavaCode(Objects.newTypeReference())».equals(stored.get«upper»(), entity.get«upper»())); if (!validationPassed) return validationPassed;
                                «ENDFOR»
                            «ELSE»
                                «val xFWReadOnly = XFWReadOnly.newAnnotationReference.annotationTypeDeclaration»
                                «FOR field : clazz.declaredFields.filter[it.findAnnotation(xFWReadOnly) !== null && !it.isStatic]»
                                    «val upper = field.simpleName.toFirstUpper»
                                    «val facadeConditionForField = if (field.findAnnotation(xFWReadOnly).getBooleanValue("facade")) "facade" else "!facade"»
                                    validationPassed = «facadeConditionForField» && («toJavaCode(Objects.newTypeReference())».equals(stored.get«upper»(), entity.get«upper»()));
                                    if (!validationPassed) return validationPassed;
                                «ENDFOR»
                            «ENDIF»
                        }
                    «IF isReadOnlyEntityIncludeSuperClasses(clazz, ReadOnlyType.ALL)»
                    }
                    «ENDIF»
                    return validationPassed;
                «ELSE»
                    return true;
                «ENDIF»
                '''
            ]
        ]

        clazz.createServiceMethodSave(service, dual)

        clazz.createServiceMethodGetMaxObject(service)

        clazz.createServiceMethodCount(service)
    }

    //TODO query нужно заменить на Predicate и передовать в конструкторы событий

    private def createServiceGetObjectsMethods(MutableClassDeclaration clazz, MutableClassDeclaration service) {
        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            returnType = Page.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Page.newTypeReference(clazz.newTypeReference))» result;
                if (getMaxObjects() > 0) {
                    «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects());
                    result = repository.findAll(limit);
                } else {
                    result = («toJavaCode(Page.newTypeReference(clazz.newTypeReference))») repository.findAll();
                }
                return result;
            ''']
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", queryPredicateType)
            returnType = Page.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(SolrQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = (SolrQueryContext<?>) query;
            «toJavaCode(Page.newTypeReference(clazz.newTypeReference))» result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects());
                result = repository.findAll(queryContext.getQuery(), limit);
            } else {
                result = repository.findAll(queryContext.getQuery());
            }
            return result;
                '''
            ]
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            returnType = Page.newTypeReference(clazz.newTypeReference)
            addParameter("sort", Sort.newTypeReference)
            body = ['''
            «toJavaCode(Page.newTypeReference(clazz.newTypeReference))» result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects(), sort);
                result = repository.findAll(limit);
            } else {
                result = («toJavaCode(Page.newTypeReference(clazz.newTypeReference))») repository.findAll(sort);
            }
            return result;
                '''
            ]
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", queryPredicateType)
            addParameter("sort", Sort.newTypeReference)
            returnType = Page.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(SolrQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = (SolrQueryContext<?>) query;
            if (queryContext != null) {
            	queryContext.setSort(sort);
            } else {
            	return getObjects(sort);
            }
            «toJavaCode(Page.newTypeReference(clazz.newTypeReference))» result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects(), sort);
                result = repository.findAll(queryContext.getQuery(), limit);
            } else {
                result = repository.findAll(queryContext.getQuery());
            }
            return result;
                '''
            ]
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("pageable", Pageable.newTypeReference)
            returnType = Page.newTypeReference(clazz.newTypeReference)
            body = ['''
            return repository.findAll(pageable);
                '''
            ]
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", queryPredicateType)
            addParameter("pageable", Pageable.newTypeReference)
            returnType = Page.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(SolrQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = (SolrQueryContext<?>) query;
            return repository.findAll(queryContext.getQuery(), pageable);
                '''
            ]

        ]
    }


    protected def createServiceMethodGetMaxObject(MutableClassDeclaration clazz, MutableClassDeclaration service) {
        service.addMethod('getMaxObjects') [
            visibility = Visibility::PUBLIC
            returnType = primitiveInt
            body = ['''
            return maxObjects;
                '''
            ]
        ]
    }

    private def createServiceMethodSave(MutableClassDeclaration clazz, MutableClassDeclaration service, boolean dual) {
        var List<FieldDeclaration> vManyToOneList = new ArrayList<FieldDeclaration>
        var List<FieldDeclaration> vManyToManyFields = new ArrayList<FieldDeclaration>

        //связи нужны только для базового хранилища(CASANDRA), не для jpa решения
        if (clazz.isDseSolr) {
            vManyToOneList = getManyToOneList(clazz, ctx)
            vManyToManyFields = clazz.XFWManyToManyAnnotatedFields
            // заинжектим репозитарии кросс таблиц
            vManyToManyFields.forEach [ field |
                mutableClasses.forEach [ mCls |
                    if(getCrossTableName(field, ctx) == mCls.simpleName) {
                        val repo = ctx.findInterface(mCls.getRepositoryQName(dual))
                        service.addField(mCls.simpleName.toFirstLower + "Repository") [
                            addAnnotation(Autowired.newAnnotationReference)
                            type = repo.newTypeReference
                        ]
                    }
                ]
                //подключаем сервисы для связей
                //рабочее решение ClassUtil.getAnnotation(clazz, XFWSearchClass) != null
                if(dual) {
                    logger.debug("Import autowired services for " + clazz.simpleName + "." + field.simpleName + " for ManyToMany relation")
                    var crossTableName = getCrossTableName(field, ctx)
                    if (crossTableName !== null) {
                        val crossTable = mutableClasses.findFirst[(getCrossTableName(field, ctx)).equals(simpleName)].newTypeReference
                        val serviceManyInterface = findClass(crossTable.type.qualifiedName.replaceAll(crossTable.simpleName, "service." + crossTable.simpleName + "Service"))

                        service.addField(crossTable.simpleName.toFirstLower + "Service") [
                            visibility = Visibility::PRIVATE
                            type = serviceManyInterface.newTypeReference
                            addAnnotation(Autowired.newAnnotationReference)
                        ]
                    }

                }
            ]
        }

        // айдишники которым надо поменять при сохранении
        val manyToOneList = vManyToOneList

        val manyToManyFields = vManyToManyFields

        service.addMethod("save") [
            visibility = Visibility::PUBLIC
            returnType = clazz.newTypeReference
            addParameter("entity", clazz.newTypeReference)
            body = ['''
            return save(entity, false);
            ''']
        ]

        service.addMethod("save") [
            visibility = Visibility::PUBLIC
            returnType = clazz.newTypeReference
            addParameter("entity", clazz.newTypeReference)
            addParameter("saveState", boolean.newTypeReference)
            body = ['''log.debug("Save object «clazz.simpleName» id={}, state={}", entity.getId(), entity.toString());
                    if (entity.isRemoved()) {
                        log.debug("Delete object «clazz.simpleName» id={}, state={}", entity.getId(), entity.toString());
                        repository.delete(entity);
                        «FOR field : manyToManyFields»
                            {
                                «val searchField = getSearchFieldName(field, ctx)»
                                «toJavaCode(SimpleQuery.newTypeReference)» sq = new SimpleQuery(new «toJavaCode(Criteria.newTypeReference)»("«searchField»").is(entity.getId()));
                                log.debug("Delete for crosstable objects «getCrossTableName(field, ctx)» by query={}", sq);
                                «getCrossTableName(field, ctx).toFirstLower»Repository.delete(sq);
                            }
                        «ENDFOR»
                        return null;
                    }
                    «FOR f : manyToOneList»
                        {
                            «IF (dual)»
                            «val mapped = findTypeGlobally(f.type.name)»
                            «val fk = getIdFromAnnotatedManyToOne(f, ctx)»
                            «toJavaCode(mapped.newTypeReference)» «mapped.simpleName.toFirstLower» = entity.get«f.simpleName.toFirstUpper»();
                            if («mapped.simpleName.toFirstLower» != null) {
                            String changedId = «toJavaCode(XfwSolrUtils.newTypeReference)».serializeKey(«mapped.simpleName.toFirstLower».getId(), «clazz.newTypeReference».class);
                            entity.set«fk.toFirstUpper»(changedId);
                            }
                            «ELSE»
                            «val mapped = findTypeGlobally(f.type.simpleName)»
                            «val fk = getIdFromAnnotatedManyToOne(f, ctx)»
                            «toJavaCode(mapped.newTypeReference)» «mapped.simpleName.toFirstLower» = entity.get«mapped.simpleName»();
                            if («mapped.simpleName.toFirstLower» != null) {
                            String changedId = «toJavaCode(XfwSolrUtils.newTypeReference)».serializeKey(«mapped.simpleName.toFirstLower».getId(), «clazz.newTypeReference».class);
                            entity.set«fk.toFirstUpper»(changedId);
                            }
                            «ENDIF»
                        }
                    «ENDFOR»
                    «FOR field : manyToManyFields»
                        «val crossTable = mutableClasses.findFirst[(getCrossTableName(field, ctx)).equals(simpleName)].newTypeReference»
                        {
                            «toJavaCode(field.type)» «field.simpleName» = entity.get«field.simpleName.toFirstUpper»();
                            if («field.simpleName» != null && «field.simpleName».size() > 0) {
                                «toJavaCode(List.newTypeReference(crossTable))» navigables = new «toJavaCode(ArrayList.newTypeReference(crossTable))»();
                                «toJavaCode(Iterables.newTypeReference)».forEach(«field.simpleName», o -> {
                                    «IF (dual)»
                                    «toJavaCode(crossTable)» navigable = «crossTable.simpleName.toFirstLower»Service.createNew(«toJavaCode(UUID.newTypeReference)».randomUUID().toString());
                                    «ELSE»
                                    «toJavaCode(crossTable)» navigable = new «toJavaCode(crossTable)»();
                                    «ENDIF»
                                    navigable.set«getSearchFieldName(field, ctx).toFirstUpper»(entity.getId());
                                    navigable.set«getResultFieldName(field, ctx).toFirstUpper»(o.getId());
                                    navigables.add(navigable);
                                });
                                «crossTable.simpleName.toFirstLower»Repository.saveAll(navigables);
                            }
                        }
                    «ENDFOR»
                    
                    «toJavaCode(clazz.newTypeReference)» result = repository.save(entity);
                    
                    «createSaveStateForSaveMethod»
                    
                    return result;
            ''']
        ]
    }
    
    protected def createSaveStateForSaveMethod() {
    	return '''
    	result.setNew(false);
    	'''
    }
    
    private def createServiceMethodCount(MutableClassDeclaration clazz, MutableClassDeclaration service) {
        service.addMethod('count') [
            visibility = Visibility::PUBLIC
            addParameter("query", getQueryPredicateType())
            returnType = primitiveLong
            body = ['''
            «toJavaCode(SolrQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = (SolrQueryContext<?>) query;
            long result = repository.count(queryContext.getQuery());
            log.debug("Count objects «clazz.simpleName» by predicate={}, count={}", query.toString(), result);
            return result;
                '''
            ]
        ]
    }
    
}