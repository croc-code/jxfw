package ru.croc.ctp.jxfw.fulltext.generator.solr

import javax.annotation.Nonnull
import  org.springframework.data.annotation.Id
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import ru.croc.ctp.jxfw.fulltext.generator.FulltextService
import ru.croc.ctp.jxfw.fulltext.generator.FulltextServiceContext
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper
import java.util.List
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.springframework.data.solr.core.mapping.Indexed
import org.springframework.data.annotation.Transient
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.slf4j.Logger;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import ru.croc.ctp.jxfw.core.generator.impl.ToStringProcessor
import ru.croc.ctp.jxfw.metamodel.XfwValueType


import static ru.croc.ctp.jxfw.core.generator.Constants.*

import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.EnumCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import static extension ru.croc.ctp.jxfw.fulltext.generator.solr.GeneratorHelperSolr.*
import ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.ReadOnlyType
import ru.croc.ctp.jxfw.fulltext.generator.XFWSearchClass
import org.springframework.data.solr.core.mapping.SolrDocument
import java.util.HashMap
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference
import ru.croc.ctp.jxfw.core.generator.EcoreGenerator
import org.springframework.data.annotation.Version
import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil
import ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString
import ru.croc.ctp.jxfw.core.generator.StorageType
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWReadOnlyCheck
import ru.croc.ctp.jxfw.core.validation.meta.XFWFacadeValidationGroup
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.Editable
import ru.croc.ctp.jxfw.core.domain.meta.XFWSolrDocument
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWFacadeReadOnlyCheck
import ru.croc.ctp.jxfw.core.validation.impl.meta.XFWNewRemoveCheck

/**
 * @author SMufazzalov
 * @since 1.4
 *
 */
class FulltextServiceSolr implements FulltextService {

    private static final Logger logger = LoggerFactory.getLogger(FulltextServiceSolr);

    var List<MutableClassDeclaration> collectedMutableClasses

    var HashMap<MutableClassDeclaration, MutableClassDeclaration> mutableClzMap //ключ (класс который процесится) - значение предок из xtend
    val accessorsProcessor = new ru.croc.ctp.jxfw.core.generator.impl.AccessorsProcessor
    val toStringProcessor = new ToStringProcessor

    //проперти, transformationContext...
    var ctx = new FulltextServiceContext

    new(@Nonnull FulltextServiceContext ctx) {
        this.ctx = ctx
    }

    override register(List<? extends ClassDeclaration> elements) {
        elements.forEach[
            registerClasses(it)
        ]
    }

    override transform(List<? extends MutableClassDeclaration> elements) {
        val extension context = ctx.transformationContext

        //elements это прямое мутабельное представление классов описанных в xtend файле
        //в мапе ключи базовая модель либо полнотекстовая модель, значения всегда базовая.
        mutableClzMap = SourceUtil.getMutableClzMap(elements, context)

        //мутабельные классы, entity для работы с полнотекстом
        collectedMutableClasses = mutableClzMap.keySet.toList

        collectedMutableClasses.forEach[
            val clazz = it

            val dual = SourceUtil.dual(mutableClzMap.get(clazz), context)

            //возможно класс сгенерирован, и нужно его наполнить базовой информаций (поля и аннотации)
            new SolrEntityPopulator(context, clazz, elements, mutableClzMap, dual).populate()
            // Добавление поля serialVersionUID в класс доменной модели
            clazz.addSerialVersionUID(context)

            logger.debug(clazz.simpleName)
            clazz.annotations.forEach[
                logger.debug(it.getAnnotationTypeDeclaration.simpleName)
            ]
            logger.debug(" ------------------------------------- ")


            clazz.extendClazz(context)

            val org.eclipse.xtext.xbase.lib.Procedures.Procedure1 proc = [MutableFieldDeclaration field |

                if (!ClassUtil.existAnnotation(field, Indexed)) {
                    field.addAnnotation(Indexed.newAnnotationReference[
                        setStringValue("name", field.simpleName.toUnderscored)
                    ])

                }
            ]

            val producedFields = clazz.produceRequiredFields(proc, context)

            addAnnotationIfNotExist(XFWToString.findTypeGlobally, XFWToString.newAnnotationReference)

            if(!isTransient(clazz)) {
                new SolrRepositoryPopulator(context).populate(clazz, dual)
                new SolrRepositoryImplPopulator(context).populate(clazz, dual)
                new SolrServicePopulator(context, collectedMutableClasses).populate(clazz, dual)
            }

            //TODO фильтровать
             //TODO удалить после деприкации XFWEnumerated
            clazz.processXFWEnumeratedField(context)
            
            if(!dual){
       		   accessorsProcessor.doTransform(#[clazz], context)
       		} else {
       		 	/**
    			* генерация геттеров/сеттеров через хак (кишки AccessorProcessor), т.к. созданный при генерации класс не
    			* может пройти заложенные в AccessorProcessor проверки на исходный класс... т.к. его не было изначально
    			*/
       		 	clazz.declaredFields
       		 	.filter[!static && !producedFields.contains(it)]
       		 	.forEach[accessorsProcessor.transform(it, context)]
       		}
            
			//clazz.addPropertyConstants(context)
			clazz.addPropertyConstant(clazz,"TYPE_NAME", "solr." + clazz.simpleName, context)
  
        ]
		
        toStringProcessor.doTransform(collectedMutableClasses, context)
        
        collectedMutableClasses.forEach [
            it.processEnumFields(context)
        ]

    }

    override generate(List<? extends TypeDeclaration> elements){
        val extension context = ctx.codeGenerationContext

        //если полнотекст не основное NoSql хранилище то информацию о моделях в ecore НЕ храним
        val classesForEcoreProcessing = collectedMutableClasses.filter[!isGenerated(it)].toList
        if (classesForEcoreProcessing.size < 1) return

        val oppositeProvider = [
            FieldDeclaration field, EClass ref, List<MutableClassDeclaration> clazzez, TypeReferenceProvider typeRef |
            ecoreGetOpposite(field, ref, clazzez, typeRef)
        ]
        val ecoreModelEmitterProvider = [ ClassDeclaration clz, XFWModelGenerator modelEmfResource, TypeReferenceProvider typeRef |
            new EcoreModelEmitterSolr(clz, context, modelEmfResource, typeRef)
        ]
        val ecoreAddColumnPropsProcedure = [
            ClassDeclaration clz, EStructuralFeature feature, Iterable<? extends AnnotationReference> annotations, TypeReferenceProvider typeRef, XfwValueType xfwValueType |
            ecoreAddColumnProps(feature, annotations, typeRef, xfwValueType)
        ]
        val storageTypeProvider = [ClassDeclaration clz |
            StorageType.SOLR.name()
        ]
        val keyTypeProvider = [ClassDeclaration clz |
            String.name
        ]
        //TODO подумать возможно лучше перенести логику в базовые модули
        val useFulltextProvider = [ClassDeclaration clz |
            !isGenerated(clz)
        ]

        //список полей
        val fieldsListProvider = [ClassDeclaration clz |
            ClassUtil.getFieldsList(clz)
        ]

        val generator = new EcoreGenerator(
                classesForEcoreProcessing,
                oppositeProvider,
                ecoreModelEmitterProvider,
                ecoreAddColumnPropsProcedure,
                storageTypeProvider,
                keyTypeProvider,
                useFulltextProvider,
                fieldsListProvider,
                context
        )

        generator.generate

    }

    //использовать на или после стадии doTransform
    private def isGenerated(MutableClassDeclaration clz) {
        !mutableClzMap.get(clz).equals(clz)
    }

    private def extendClazz(MutableClassDeclaration clazz, extension TransformationContext context) {
        {
            if(getKeyName(clazz, context) == "_uniqueKey")
                clazz.addError("Should NOT define key field with '_uniqueKey' name")

            val idAnnotation = clazz.declaredFields.findFirst[findAnnotation(Id.newTypeReference.type) !== null]
            if(idAnnotation !== null) {
                clazz.addError("Should NOT annotate members with '@Id' annotation")
            }
        }

        clazz.swapKeySpaceAlias(context)

        clazz.changeAnnotationsCamelCase(context)

        val defaultLang = ctx.properties.getProperty(LANG_PROP_NAME)
        clazz.setI18NAnnotationDefaults(defaultLang, context)


        if(mutableClzMap.get(clazz).isTopParent) {
            clazz.extendTopParentOnly(context)
        }

        clazz.addObtainValueByPropertyNameField(String.newTypeReference, context)

        clazz.addMethod('getTypeName') [
            visibility = Visibility::PUBLIC
            returnType = String.newTypeReference
            body = ['''return TYPE_NAME;''']
        ]

       // clazz.addMetadataMethod(context)
       clazz.addField("METADATA") [
                   visibility = Visibility::PUBLIC
                   type = XfwClass.newTypeReference
                   static = true
                   final = true
                   initializer = ['''«toJavaCode(XfwModelFactory.newTypeReference)».getInstance().findThrowing("«clazz.simpleName»", XfwClass.class)''']
                   docComment = "Статические метаданные класса."
       ]
       clazz.addMethod('getMetadata') [
           visibility = Visibility::PUBLIC
           addAnnotation(Override.newAnnotationReference)
           returnType = XfwClass.newTypeReference
           body = ['''return  «toJavaCode(XfwModelFactory.newTypeReference)».getInstance().findThrowing("«clazz.simpleName»", XfwClass.class);''']
       ]


        if(clazz.needToAddValidationLogic(ReadOnlyType.SERVER, context)) {
            clazz.addAnnotation(XFWReadOnlyCheck.newAnnotationReference)
        }
        if(clazz.needToAddValidationLogic(ReadOnlyType.FACADE, context)) {
            clazz.addAnnotation(XFWFacadeReadOnlyCheck.newAnnotationReference [
                setClassValue("groups", XFWFacadeValidationGroup.newTypeReference)
            ])
        }

        clazz.addAnnotation(XFWNewRemoveCheck.newAnnotationReference)

        clazz.addBlobInfo(ctx.properties, context)

    }

    private def extendTopParentOnly(MutableClassDeclaration clazz, extension TransformationContext context) {
        //if solr is the only storage then XFWManyToOne
        //if jpa and solr then XFWManyToOne and XFWSearchField
        if (ClassUtil.existAnnotation(clazz, XFWSearchClass) && GeneratorHelperSolr.isApacheSolr(clazz)) {
            clazz.declaredFields.filter [
                annotations.findFirst[annotationTypeDeclaration.simpleName.equals("XFWManyToOne")] !== null
                annotations.findFirst[annotationTypeDeclaration.simpleName.equals("XFWSearchField")] !== null
            ].forEach [
                val id = getIdFromAnnotatatedManyToOne(it)
                val f = clazz.addField(id) [
                    visibility = Visibility::PUBLIC
                    type = String.newTypeReference
                    addAnnotation(Indexed.newAnnotationReference[
                        setStringValue("name", id.toUnderscored)
                    ])
                ]
                f.addGetterSetter(context)
            ]
        } else {
            clazz.declaredFields.filter [
                annotations.findFirst[annotationTypeDeclaration.simpleName.equals("XFWManyToOne")] !== null
            ].forEach [
                val id = getIdFromAnnotatatedManyToOne(it)
                val f = clazz.addField(id) [
                    visibility = Visibility::PUBLIC
                    type = String.newTypeReference
                    addAnnotation(Indexed.newAnnotationReference[
                        setStringValue("name", id.toUnderscored)
                    ])
                ]
                f.addGetterSetter(context)
            ]
        }

        clazz.addIdFieldsAndMethods(context)

        clazz.implementedInterfaces = clazz.implementedInterfaces + #[DomainObject.newTypeReference(clazz.getKeyType(context))]

        if(!clazz.isReadOnlyEntity) {  // если класс доменной модели НЕ readOnly то наследуемся от интерфейса Editable
            clazz.implementedInterfaces = clazz.implementedInterfaces + #[Editable.newTypeReference]
        }

        val transientAnnotation = Transient.newAnnotationReference
        clazz.addLoggerField(transientAnnotation, context)
        clazz.addIsNewField(transientAnnotation, context)
        clazz.addIsRemovedField(transientAnnotation, context)

        if(!clazz.isReadOnlyEntity) {
            clazz.addOptimisticLockField(Version, context)
            // TODO удалить, когда будет поддержка оптимистичных блокировок Solr
            val optimisticLockField = clazz.getOptimisticLockField(Version)
            for (annotationReference : optimisticLockField.annotations) {
                if (annotationReference.getAnnotationTypeDeclaration.getQualifiedName.equals(Version.getName)) {
                    optimisticLockField.removeAnnotation(annotationReference)
                }
            }
        }
        clazz.addOriginalField(transientAnnotation, context)
    }

    private def void swapKeySpaceAlias(MutableClassDeclaration clazz, extension TransformationContext context) {
        var xfwSolrDoc = clazz.findAnnotation(XFWSolrDocument.newTypeReference.type)
        if(xfwSolrDoc !== null) {
            

            val properties = loadProperties(KEYSPACE_PROPERTIES, clazz.compilationUnit, context)

            val alias = xfwSolrDoc.getStringValue("keySpaceAlias")
            val sCoreName = xfwSolrDoc.getStringValue("solrCoreName")
            

            val keySpace = properties.get(alias) ?: alias

            clazz.removeAnnotation(xfwSolrDoc)
            var solrDocument = SolrDocument.newAnnotationReference [
                set("solrCoreName", keySpace + "." + sCoreName)
            ]
            if(!isTransient(clazz)) {
                clazz.addAnnotation(solrDocument)
            }
        }
    }

    private def String getKeyName(MutableClassDeclaration clazz, extension TransformationContext context) {
        // FIXME непонятно что тут разобраться

        val keyField = clazz.declaredFields.findFirst[findAnnotation(Id.newTypeReference.type) !== null]
        keyField?.simpleName;
    }

    private def registerClasses(ClassDeclaration clazz) {
        val context = ctx.registerGlobalsContext
        val dual = SourceUtil.dual(clazz, context)
        //если используется единая модель, то нужно добавить entity в свой пакет
        if(dual) {
            val entityQName = clazz.getEntityQName(dual)

            logger.debug(entityQName.toUpperCase)

            context.registerClass(entityQName)
            //TODO: разобраться, как добавить внутренний класс
			//context.registerClass(getPropertyInnerClassQName(context.findSourceClass(entityQName)))
        }
        
        val serviceQName = clazz.getServiceQName(dual)
        val repositoryQName = clazz.getRepositoryQName(dual)
        val repositoryImplQName = clazz.getRepositoryImplQName(dual)

        {
            context.registerClass(serviceQName)

            //сработает если только как базовое хранилище
            if(!providedOrTransient(GeneratorHelper.getRepositoryJavaFile(clazz), clazz, context)) {
                context.registerInterface(repositoryQName)
            }

            if(!providedOrTransient(GeneratorHelper.getRepositoryJavaFile(clazz), clazz, context)) {
                context.registerClass(repositoryImplQName)
            }
        }
    }
}