package ru.croc.ctp.jxfw.fulltext.generator.solr

import java.util.ArrayList
import java.util.Arrays
import java.util.stream.Collectors
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.file.Path
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import org.springframework.data.annotation.Id
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import java.util.List
import com.google.common.base.CaseFormat
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EReference
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference
import ru.croc.ctp.jxfw.metamodel.XFWConstants
import org.eclipse.emf.ecore.EcoreFactory
import org.springframework.data.solr.core.mapping.SolrDocument
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory
import org.slf4j.Logger

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import ru.croc.ctp.jxfw.fulltext.generator.XFWSearchClass
import org.springframework.data.solr.core.mapping.Indexed
import org.apache.commons.lang3.StringUtils
import ru.croc.ctp.jxfw.core.generator.Constants
import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToOne
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWJoinTable
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToMany
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToOne
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWBasic
import ru.croc.ctp.jxfw.metamodel.XfwValueType


/**
 * @author SMufazzalov
 * @since 1.4
 */
class GeneratorHelperSolr {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorHelperSolr);

    static val SOLR_PACKAGE = "solr"

    //получить имя entity, для класса (можно в своем пакете)
    static def getEntityQName(ClassDeclaration clazz, boolean useSeparatePkg) {
        getEntityQName(clazz.qualifiedName, useSeparatePkg)
    }

    //получить имя entity, для класса (можно в своем пакете)
    static def getEntityQName(String qualifiedName, boolean useSeparatePkg) {
        addPackage(qualifiedName, useSeparatePkg)
    }
    
    //получить имя сервиса, для класса (можно в своем пакете)
    static def getServiceQName(ClassDeclaration clazz, boolean useSeparatePkg) {
        if (useSeparatePkg)
            clazz.compilationUnit.packageName + "." + SOLR_PACKAGE + servicePackageSuffix + clazz.simpleName + "Service"
        else
            getServiceQName(clazz)
    }

    //интерфейс репозитория
    static def getRepositoryQName(ClassDeclaration clazz, boolean useSeparatePkg) {
        if (useSeparatePkg)
            clazz.compilationUnit.packageName + "." + SOLR_PACKAGE + repositoryPackageSuffix + clazz.simpleName + "SolrRepository"
        else
            getRepositoryQName(clazz)
    }

    //реализация репозитория
    static def getRepositoryImplQName(ClassDeclaration clazz, boolean useSeparatePkg) {
        if (useSeparatePkg)
            clazz.compilationUnit.packageName + "." + SOLR_PACKAGE + repositoryPackageSuffix + clazz.simpleName + "SolrRepositoryImpl"
        else
            getRepositoryImplQName(clazz)
    }

    //имя ядра solr
    static def getSolrCore(ClassDeclaration clazz) {
        var name = ""
        val search = ClassUtil.getAnnotation(clazz, XFWSearchClass)
        if (search !== null) {
            name = search.getStringValue("name")
            if (StringUtils.isEmpty(name)) {
                val solrDocument = ClassUtil.getAnnotation(clazz, SolrDocument)
                if (solrDocument !== null) {
                    name = solrDocument.getStringValue("solrCoreName")
                }
            }
        }
        if (StringUtils.isEmpty(name)) name = clazz.simpleName
        name
    }

    //проставит пакет SOLR_PACKAGE в qualifiedName
    public static def addPackage(String path, boolean usePckg) {
        if(!usePckg) return path

        val split = path.split("\\.")
        val stringList = new ArrayList(Arrays.asList(split))

        if(path.endsWith(".java")) {
            stringList.add(stringList.size() - 2, SOLR_PACKAGE)
        } else {
            stringList.add(stringList.size() - 1, SOLR_PACKAGE)
        }

        stringList.stream().collect(Collectors.joining("."))
    }

    //вызывается только на стадии doRegisteGlobals
    static def providedOrTransient(Path p, ClassDeclaration clazz, RegisterGlobalsContext ctx) {
        //registerGlobalsContext содержит вспомогательные методы для работы с файловой системой
        if(p!==null && ctx.exists(p)) {
            return true
        }

        if (isTransient(clazz)) {
              return true
        }
        return false
    }

    /**
     * определения составного ключа
     */
    public static def isSimplePK (MutableClassDeclaration clazz, extension TransformationContext context) {
        if (clazz.isApacheSolr) {
            return false
        } else {
            return !clazz.useSyntheticKey(context)
        }
    }

    //проверка что движок голый solr а не dse-solr например
    static def isApacheSolr (MutableClassDeclaration clazz) {
        val searchAnn = ClassUtil.getAnnotation(clazz, XFWSearchClass)
        if (searchAnn === null) {
            throw new RuntimeException("Passed class, not annotated with XFWSearchClass annotation: "
            + clazz.qualifiedName)
        }

        searchAnn.getEnumValue(Constants.FULLTEXT_DB_SEARCH_TYPE).simpleName.equals(SearchType.SOLR.toString)
    }

    //проверка что движок dse-solr
    static def isDseSolr (MutableClassDeclaration clazz) {
        val searchAnn = ClassUtil.getAnnotation(clazz, XFWSearchClass)
        if (searchAnn === null) {
            throw new RuntimeException("Passed class, not annotated with XFWSearchClass annotation: "
            + clazz.qualifiedName)
        }

        searchAnn.getEnumValue(Constants.FULLTEXT_DB_SEARCH_TYPE).simpleName.equals(SearchType.DSE.toString)
    }
    
    public static def getKeyType(MutableClassDeclaration clazz, extension TransformationContext context) {
        if (!clazz.useSyntheticKey(context) && clazz.dseSolr) {
        	clazz.getIdField(context).type
        } else {
        	String.newTypeReference
        }
    }

    public static def addIdFieldsAndMethods(MutableClassDeclaration clazz, extension TransformationContext context) {
        //тип ключа простой/комплексный
        val isSimplePK = clazz.isSimplePK(context)

        val idAnn = context.idAnnotation

        var bodyGet = ""
        var bodySet = ""

        if(isSimplePK) {
            var idField = clazz.getIdField(context)
            idField.addAnnotation(idAnn)
            bodyGet = '''return «idField.simpleName»;'''
            bodySet = '''«idField.simpleName» = id;'''
        } else {
            // Важно. Поле должно называться именно _uniqueKey
            val uKey = clazz.addField("_uniqueKey") [
                visibility = Visibility::PRIVATE
                type = String.newTypeReference
            ]
            uKey.addAnnotation(idAnn)

            bodyGet = '''return _uniqueKey;'''
            bodySet = '''_uniqueKey = id;'''
        }
        val bodySetL = bodySet
        val bodyGetL = bodyGet
        val idType = clazz.getKeyType(context)
        clazz.addMethod('setId') [
            visibility = Visibility::PUBLIC
            addParameter('id', idType)
            body = [bodySetL]
        ]
        clazz.addMethod('getId') [
            visibility = Visibility::PUBLIC
            returnType = idType
            body = [bodyGetL]
        ]
    }

    public static def MutableFieldDeclaration getIdField(MutableClassDeclaration clazz, extension TransformationContext context) {
        val key = clazz.declaredFields.findFirst [ClassUtil.existAnnotation(it, XFWPrimaryKey)]
        if(key === null) addError(clazz, "No XFWPrimaryKey defined")
        return key
    }

    /**
    * dse solr использует синтетический ключ, при работе с композитными ключами, для нашей модели
    * наличие более одного поля аннотацией XFWPrimaryKey, означает работу с композитным ключом
    */
    def static useSyntheticKey(ClassDeclaration clazz, extension TypeReferenceProvider typeRefProvider) {
        val found = clazz.declaredFields.filter[ClassUtil.existAnnotation(it, XFWPrimaryKey)]
        found.size > 1
    }

    def static getIdAnnotation(extension TransformationContext context) {
        Id.newAnnotationReference
    }

    def static String getIdFromAnnotatatedManyToOne(FieldDeclaration field) {
        if (ClassUtil.existAnnotation(field, XFWManyToOne)) {
            field.simpleName + "_id"
        }
    }

    def static List<FieldDeclaration> getManyToOneList(MutableClassDeclaration clazz, extension TransformationContext context) {
        // получим поля модели с аннотациями @XFWManyToOne
        val clzes = clazz.parents
        clzes.add(clazz)
        clzes.map[it.declaredFields].flatten.filter [ClassUtil.existAnnotation(it, XFWManyToOne)].toList
    }

    // спиок полей проаннотированных @XFWManyToMany
    def static List<FieldDeclaration> getXFWManyToManyAnnotatedFields(ClassDeclaration clazz) {
        val clzes = clazz.parents
        clzes.add(clazz)
        clzes.map[declaredFields].flatten.filter [ClassUtil.existAnnotation(it, XFWManyToMany)].toList
    }

    // кросс таблица
    def static String getCrossTableName(FieldDeclaration field, extension TransformationContext context) {
        val annotation = field.findAnnotation(XFWJoinTable.newAnnotationReference.getAnnotationTypeDeclaration)
        annotation?.getStringValue("name")
    }

    // искомое поле searchField в аннотации @XFWJoinTable
    def static String getSearchFieldName(FieldDeclaration field, extension TransformationContext context) {
        val annotation = field.findAnnotation(XFWJoinTable.newAnnotationReference.getAnnotationTypeDeclaration)
        annotation?.getAnnotationArrayValue("joinColumns").get(0).getStringValue("name")
    }

    def static String getIdFromAnnotatedManyToOne(FieldDeclaration field, extension TransformationContext context) {
        val annotation = field.findAnnotation(XFWManyToOne.newAnnotationReference.getAnnotationTypeDeclaration)
        if (annotation !== null) {
            field.simpleName + "_id"
        }
    }

    // результирующие поле resultField в аннотации @XFWJoinTable
    def static String getResultFieldName(FieldDeclaration field, extension TransformationContext context) {
        val annotation = field.findAnnotation(XFWJoinTable.newAnnotationReference.getAnnotationTypeDeclaration)
        annotation?.getAnnotationArrayValue("inverseJoinColumns").get(0).getStringValue("name")
    }

    def static String toUnderscored(String value) {
        val sb = new StringBuilder()
        var str = value
        if(str.indexOf(".") != -1) {
            val arr = str.split("\\.")
            sb.append(arr.get(0))
            sb.append(".")
            str = arr.get(1)
        }
        val res = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str)
        sb.append(res)
        sb.toString
    }

    def static ecoreGetOpposite(FieldDeclaration field, EClass refClass,
    List<MutableClassDeclaration> mutableClasses, extension TypeReferenceProvider context) {
        var result = null as EReference;
        var opposite_attr = field.oppositeFromAnnotatdetXFWOneToMany(mutableClasses)
        if(opposite_attr !== null) {
            result = refClass.getEStructuralFeature(opposite_attr.toFirstLower) as EReference;
        }
        return result;
    }

    def static String oppositeFromAnnotatdetXFWOneToMany(FieldDeclaration field,
    List<MutableClassDeclaration> mutableClasses) {
        val annotation = field.annotations.findFirst[annotationTypeDeclaration.simpleName.equals("XFWOneToMany")]
        if (annotation !== null) {
            val cls = annotation.getClassValue("targetEntity")
            val by = annotation.getStringValue("mappedBy")
            mutableClasses.findFirst[mCls|cls.simpleName.equals(mCls.simpleName)].declaredFields.findFirst [ f |
                (f.simpleName.equals(by)
                )
            ].type.simpleName
        }
    }

    def static void ecoreAddColumnProps(EStructuralFeature e,
    Iterable<? extends AnnotationReference> annotations, extension TypeReferenceProvider context, XfwValueType xfwValueType) {
        val columnAnnotation = EcoreFactory.eINSTANCE.createEAnnotation
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

    // изменить автоматическое формирование названий свойств и таблиц при генерации javа-классов доменной модели на основе xtend-модели terra-88
    def static void changeAnnotationsCamelCase(MutableClassDeclaration clazz, extension TransformationContext context) {
        var solrDoc = clazz.findAnnotation(SolrDocument.newTypeReference.type)
        if(solrDoc !== null) {
            val sCoreName = solrDoc.getStringValue("solrCoreName")
            clazz.removeAnnotation(solrDoc)
            var nAnn = SolrDocument.newAnnotationReference [
                set("solrCoreName", sCoreName.toUnderscored)
            ]
            clazz.addAnnotation(nAnn)
        }
        clazz.declaredFields.filter[ f |
            val indAnn = f.findAnnotation(Indexed.newTypeReference.type)
            indAnn !== null
        ].filter [ f |
            val indAnn = f.findAnnotation(Indexed.newTypeReference.type)
            val name = indAnn.getStringValue("name")
            StringUtils.isEmpty(name) && !f.simpleName.equals(f.simpleName.toLowerCase)
        ].forEach [ f |
            val indAnn = f.findAnnotation(Indexed.newTypeReference.type)
            f.removeAnnotation(indAnn)
            f.addAnnotation(Indexed.newAnnotationReference [
                set("name", f.simpleName.toUnderscored)
                setBooleanValue("stored", indAnn.getBooleanValue("stored"))
                setBooleanValue("searchable", indAnn.getBooleanValue("searchable"))
                setStringValue("type", indAnn.getStringValue("type"))
                //TODO поддержать прочие значения аннотации Indexed
            ])
        ]
    }

    def static isTransient(ClassDeclaration clazz) {
        val searchClassAnn = clazz.annotations.findFirst[annotationTypeDeclaration.simpleName.equals(XFWSearchClass.simpleName)]
        if (searchClassAnn === null) {
            throw new RuntimeException("@XFWSearchClass not declared on " + clazz.qualifiedName)
        }

        searchClassAnn.getEnumValue("persistence").simpleName == XFWSearchClass.PersistenceType.TRANSIENT.toString
    }
}