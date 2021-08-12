package ru.croc.ctp.jxfw.cass.generator

import java.util.List
import org.eclipse.emf.ecore.EClass
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import org.springframework.data.annotation.Transient
import ru.croc.ctp.jxfw.cass.predicate.CassandraQueryContext
import ru.croc.ctp.jxfw.core.generator.impl.PersistenceModuleBaseImpl
import ru.croc.ctp.jxfw.core.generator.EcoreModelEmitter
import ru.croc.ctp.jxfw.core.generator.PersistenceModuleContext
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory
import org.slf4j.Logger
import com.querydsl.core.types.Predicate

import static extension ru.croc.ctp.jxfw.cass.generator.GeneratorHelperCass.*
import ru.croc.ctp.jxfw.cass.repo.CassandraQueryRepository
import org.springframework.data.cassandra.core.CassandraOperations
import com.datastax.driver.core.querybuilder.Select
import javax.annotation.Resource
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.apache.commons.lang.NotImplementedException
import org.springframework.beans.factory.annotation.Autowired
import ru.croc.ctp.jxfw.cass.facade.webclient.SelectComposer
import org.springframework.data.annotation.Version
import ru.croc.ctp.jxfw.core.load.LoadContext
import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil
import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil
import ru.croc.ctp.jxfw.core.exception.exceptions.XObjectNotFoundException
import java.util.ArrayList
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.core.mapping.PrimaryKey

class PersistenceModuleCass extends PersistenceModuleBaseImpl {

    private static final Logger logger = LoggerFactory.getLogger(PersistenceModuleCass);

    new(MutableClassDeclaration clazz, PersistenceModuleContext moduleContext) {
        super(clazz, moduleContext)
    }

    new(PersistenceModuleContext moduleContext) {
        super(moduleContext)
    }

    protected override EcoreModelEmitter createEcoreModelEmitter() {
        return new EcoreModelEmitterCass(getClazz, getContext)
    }


    override checkIdField() {
        //Поле id должно быть помечено аннотацией org.springframework.data.cassandra.core.mapping.PrimaryKey, если не требуется комплексный ключ
        if(!clazz.isNeedForComplexKey) {
            clazz.declaredFields.forEach [
                if (simpleName.equals("id")
                        &&  findAnnotation(PrimaryKey.newAnnotationReference.annotationTypeDeclaration) === null) {
                    addError("Field "
                            + simpleName
                            + " has to be marked by annotation  @org.springframework.data.cassandra.core.mapping.PrimaryKey"
                    )
                }
            ]
        }
    }

    override createComplexKey() {
        new ComplexKeyPopulator(moduleContext.transformationContext).populate(clazz)
    }

    override extendClazz() {
        super.extendClazz
    }

    override protected extendTopParentOnly() {
        super.extendTopParentOnly
    }

    override protected addOptimisticLockField() {
        super.addOptimisticLockField
        DomainClassCompileUtil.getOptimisticLockField(clazz, Version)?.addAnnotation(Transient.newAnnotationReference)
    }

    /**
     * Тип которым будет проаннотирован первичный ключ
     */
    override def getIdType() {
        PrimaryKey.newTypeReference
    }

    override protected addIdFieldsAndMethods() {
        //проверка что для модели нужен комплескный ключ
        if (clazz.isNeedForComplexKey) {
            val keyClassName = GeneratorHelper.getComplexKeyQName(clazz)
            //операция по замене колонок и добавлению комплексного ключа
            swapKeyColumns(keyClassName)
        }
        //в остальном вполне подходит родительский метод по генерации setter/getter
        super.addIdFieldsAndMethods
    }

    override getKeyType(ClassDeclaration clazz) {
        val keyField = clazz.declaredFields.findFirst[findAnnotation(PrimaryKey.newTypeReference.type) != null]
        return keyField?.type?.getName() ?: String.newTypeReference.name
    }

    override getKeyType() {
        val keyField = clazz.declaredFields.findFirst[findAnnotation(PrimaryKey.newTypeReference.type) != null]
        return keyField?.type ?: String.newTypeReference
    }

    private def String getKeyName() {
        val keyField = clazz.declaredFields.findFirst[findAnnotation(PrimaryKey.newTypeReference.type) != null]
        if(keyField != null) return keyField.simpleName;
        null
    }

    override registerClasses(ClassDeclaration clz) {
        super.registerClasses(clz)

        //регистрация комплексного ключа
        if (clz.isNeedForComplexKey) {
            val ctx = moduleContext.registerGlobalsContext
            val keyClassName = GeneratorHelper.getComplexKeyQName(clz)
            ctx.registerClass(keyClassName);
        }
    }


    override ecoreGetOpposite(FieldDeclaration field, EClass refClass, List<MutableClassDeclaration> mutableClasses,
    extension TypeReferenceProvider context) {
    }

    override getFieldsList() {
        //т.к. генерируемый MutableClassDeclaration класс изменяется в сторону замены ключевых полей
        //на тип ключа (комплексный), то получается что для ecore модели нехватает полей, поэтому подставляем исходный класс
        val src = findSourceRepresentaion(clazz)
        ClassUtil.getFieldsList(src)
    }

    protected override getRepositoryExtendedInterfaces() {
        #[
            CassandraRepository.newTypeReference(clazz.newTypeReference, keyType),
            CassandraQueryRepository.newTypeReference(clazz.newTypeReference, keyType)
        ]
    }

    override createRepositoryImpl() {
        val iRepositoryImpl = repositoryImpl as MutableClassDeclaration

        iRepositoryImpl.implementedInterfaces = #[
            CassandraQueryRepository.newTypeReference(clazz.newTypeReference, keyType)
        ]

        val cassOperations = iRepositoryImpl.addField("operations") [
            visibility = Visibility::PRIVATE
            type = CassandraOperations.newTypeReference
        ]

        cassOperations.addAnnotation(Resource.newAnnotationReference)

        val findAll = iRepositoryImpl.addMethod('findAll') [
            visibility = Visibility::PUBLIC
            addParameter('query', Select.newTypeReference)
            returnType = List.newTypeReference(
                    clazz.
                    newTypeReference)
            body = ['''
            return operations.select(query, «toJavaCode(clazz.newTypeReference)».class);
            '''
            ]
        ]

        findAll.addAnnotation(Override.newAnnotationReference)
    }

    protected override createServiceGetObjectsMethods(MutableClassDeclaration service) {

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(Iterable.newTypeReference(clazz.newTypeReference))» result;
            if (getMaxObjects() > 0) {
                «toJavaCode(Pageable.newTypeReference)» limit =  «toJavaCode(PageRequest.newTypeReference)».of(0, getMaxObjects());
                result = repository.findAll(limit);
            } else {
                result =  repository.findAll();
            }
            return result;
            ''']
        ]

        service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", queryPredicateType)
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            «toJavaCode(CassandraQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = («toJavaCode(CassandraQueryContext.newTypeReference(newWildcardTypeReference))») query;
            «toJavaCode(Select.newTypeReference)» select;
            if (getMaxObjects() > 0) {
                select = queryContext.getStatement().limit(getMaxObjects());
            } else {
                select = queryContext.getStatement();
            }
            «toJavaCode(Iterable.newTypeReference(clazz.newTypeReference))» result = repository.findAll(select);
            return result;
            ''']
        ]

		service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", queryPredicateType)
            addParameter("pageable", Pageable.newTypeReference)
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            throw new «toJavaCode(NotImplementedException.newTypeReference)»("getObjects(Predicate query, Pageable pageable)");
            ''']
        ]

		service.addMethod('getObjects') [
            visibility = Visibility::PUBLIC
            addParameter("query", queryPredicateType)
            addParameter("sort", org.springframework.data.domain.Sort.newTypeReference)
            returnType = Iterable.newTypeReference(clazz.newTypeReference)
            body = ['''
            throw new «toJavaCode(NotImplementedException.newTypeReference)»("getObjects(Predicate query, Sort sort)");
            ''']
        ]
    }

	override protected createServiceGetObjectByIdMethod(MutableClassDeclaration service) {
		super.createServiceGetObjectByIdMethod(service)

		service.addMethod('getObjectById') [
			visibility = Visibility::PUBLIC
			addParameter("predicate", Predicate.newTypeReference)
			addParameter("id", getKeyTypeType(clazz))
			addParameter("saveState", primitiveBoolean)
			returnType = clazz.newTypeReference
			body = [
				'''
	            «toJavaCode(List.newTypeReference)»<«toJavaCode(clazz.newTypeReference)»> uow = new «toJavaCode(ArrayList.newTypeReference)»<>();
	            «toJavaCode(CassandraQueryContext.newTypeReference(newWildcardTypeReference))» queryContext = (CassandraQueryContext<?>) predicate;
	            uow.addAll(repository.findAll(queryContext.getStatement()));

	            if (uow.size() < 1) {
	                throw new «toJavaCode(XObjectNotFoundException.newTypeReference)»("«clazz.newTypeReference.name»", id);
	            }
	            
	            «clazz.newSelfTypeReference» result = uow.get(0);
	            
	            log.debug("Fetch object «clazz.simpleName» by predicate={} entity={}", predicate, result);

	            if (saveState && result.getSavedState() == null && result instanceof «toJavaCode(SelfDirtinessTracker.newTypeReference)» && !((SelfDirtinessTracker) result).hasDirtyAttributes()) {
	                result.saveState();
	            }

	            return result;
	            '''
			]
		]
	}

    override protected createServiceMethodCount(MutableClassDeclaration service) {
    }

    override doGenerateCode() {
    }
    
	override protected getQueryPredicateType() {
		com.querydsl.core.types.Predicate.newTypeReference
	}

    //колонки ключа в моделе, заменим на комплексный ключ
    private def swapKeyColumns(String keyClassName) {
        val ctx = moduleContext.transformationContext
        //удаление из модели всех полей с аннотациями PrimaryKeyColumn или XFWPrimaryKey
        clazz.declaredFields.filter[
            it.isKeyField(ctx)
        ].forEach[
            it.remove
        ]

        //добавление комплексного ключа, вместо удаленных полей
        clazz.addField('id') [
            visibility = Visibility::PRIVATE
            type = ctx.newTypeReference(keyClassName)
            addAnnotation(PrimaryKey.newAnnotationReference)
        ]
    }

}