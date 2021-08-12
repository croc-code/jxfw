package ru.croc.ctp.jxfw.fulltext.generator.solr

import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.slf4j.Logger
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils
import javax.annotation.Nonnull
import java.util.List
import java.util.HashMap
import org.springframework.data.solr.core.mapping.Indexed

import static extension ru.croc.ctp.jxfw.fulltext.generator.solr.GeneratorHelperSolr.*
import org.springframework.data.solr.core.mapping.SolrDocument
import ru.croc.ctp.jxfw.fulltext.generator.XFWSearchClass
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import ru.croc.ctp.jxfw.fulltext.generator.XFWSearchField
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration

import ru.croc.ctp.jxfw.core.generator.Constants
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey
import ru.croc.ctp.jxfw.core.generator.impl.ClassUtil
import org.apache.commons.lang3.StringUtils

/**
 * Для свеже зарегестрированного "сгенерированного, голого" класса, добавление полей и аннотаций.
 *
 * @author SMufazzalov
 * @since 1.4
 */
class SolrEntityPopulator {
    private static final Logger logger = LoggerFactory.getLogger(SolrEntityPopulator);

    val extension TransformationContext ctx
    val MutableClassDeclaration clazz
    var ClassDeclaration baseClass //самое близкое к начальному xtend описанию представление класса
    var HashMap<MutableClassDeclaration, MutableClassDeclaration> mutableClzMap
    var boolean dual

    new(@Nonnull TransformationContext context, MutableClassDeclaration clz,
    List<? extends MutableClassDeclaration> elements, HashMap<MutableClassDeclaration, MutableClassDeclaration> clazzMap, boolean isDual) {
        ctx = context
        clazz = clz //правильный мутабельный класс для solrEntity
        mutableClzMap = clazzMap
        //из xtend может быть получена базовая модель либо полнотекстовая, baseEntity это базовая
        val baseEntity = mutableClzMap.get(clazz)
        //исходный вариант (максимально близкий к xtend описанию)
        baseClass = context.getPrimarySourceElement(baseEntity) as ClassDeclaration
        dual = isDual
    }

    public def populate(){
        if(baseClass == null) {
            throw new RuntimeException("No xtend class found for " + clazz.getQualifiedName)
        }

        //добавить аннотацию XFWSearchClass
        addXFWSearchClassAnnotation
        //добавить аннотацию SolrDocument
        addSolrDocumentAnnotation
		//отнаследовать от родительских fulltext типов
		addParentsClassesInfo	
		
		if (baseClass.abstract) clazz.abstract = true
			
        //регистрация в контексте
        //добавить все поля и аннотации
        baseClass.declaredFields.forEach[ fromField |
            //добавить анологичное поле из исходной xtend модели в сгенерированный класс
            val toField = addFieldIfNotExistsOrGetExisting(fromField)
            //адаптировать аннотации полей из исходной xtend модели в сгенерированный класс
            addAnnotationsToField(fromField, toField)
        ]
        
        //TODO перенести методы из базового класса если таких нет в текущем
        //https://jira.croc.ru/browse/JXFW-1455
        baseClass.declaredMethods.forEach[fromMethod | 
        	//из-за свойств ленивого исполнения, старый таргет теряется.
        	//setBody
        ]
    }

    private def addFieldIfNotExistsOrGetExisting(FieldDeclaration fromField) {
        val fieldName = fromField.simpleName
        val alreadyExists = clazz.findDeclaredField(fieldName) //случай если в системе только solr модель
        if (alreadyExists == null) {
            return clazz.addField(fieldName) [
                type = fromField.type
            ]
        }
        alreadyExists
    }

    private def addAnnotationsToField(FieldDeclaration fromField, MutableFieldDeclaration toField) {
        val searchFieldAnn = fromField.findAnnotation(XFWSearchField.newAnnotationReference.annotationTypeDeclaration)
        //здесь могут быть системные поля jxfw, не предназначенные для индекса,
        //поэтому у них не должно быть @XFWSearchField и мы их пропускаем
        if(searchFieldAnn != null) {
            val indAnn = Indexed.newAnnotationReference[
                //поиск
                setBooleanValue(Constants.FULLTEXT_SEARCHABLE, searchFieldAnn.getBooleanValue(Constants.FULLTEXT_INDEXED))
                //хранение
                setBooleanValue(Constants.FULLTEXT_STORED, searchFieldAnn.getBooleanValue(Constants.FULLTEXT_STORED))
                //имя поля документа в Solr
                val n = searchFieldAnn.getStringValue("name")
                if(StringUtils.isNotEmpty(n)) {
                    setStringValue("name", n)
                }
                //тип поля в Solr
                val type = searchFieldAnn.getStringValue("type")
                if(StringUtils.isNotEmpty(type)) {
                    setStringValue("type", type)
                }
            ]
            (toField as MutableFieldDeclaration).addAnnotation(indAnn)

            //скопировать аннотации ключевые ключи
            val fromFkAnn = fromField.findAnnotation(XFWPrimaryKey.newAnnotationReference.getAnnotationTypeDeclaration)
            if(fromFkAnn != null) {
                val pkAnn = XFWPrimaryKey.newAnnotationReference[
                    setIntValue("order", fromFkAnn.getIntValue("order"))
                ]

                val toDelete = ClassUtil.getAnnotation(toField, XFWSearchClass)

                //удаляем если уже стояла над gjktv
                if (toDelete != null) toField.removeAnnotation(toDelete)
                toField.addAnnotation(pkAnn)
            }
        }
    }

	private def addParentsClassesInfo() {
		val extClass = baseClass.extendedClass
		if (extClass != null) {
			//имя прямого родителя базовой (не fulttext) сущности
			val parentName = extClass.name
			//найдем ее fulltext представление
			val qualifiedNameOfFulltextParent = GeneratorHelperSolr.getEntityQName(parentName, dual)
			if (StringUtils.isEmpty(qualifiedNameOfFulltextParent)) {
				clazz.addError("check parent entity has @XFWSearchClass annotation")
			}
			clazz.extendedClass = ctx.findTypeGlobally(qualifiedNameOfFulltextParent).newSelfTypeReference
		}
	}

    private def addSolrDocumentAnnotation() {
        var existingSolrDocAnn = ClassUtil.getAnnotation(clazz, SolrDocument)
        //удаляем если уже стояла над классом
        if (existingSolrDocAnn != null) clazz.removeAnnotation(existingSolrDocAnn)

        val solrDocAnn = SolrDocument.newAnnotationReference [
            setStringValue(Constants.FULLTEXT_SOLR_CORE_NAME, baseClass.getSolrCore)
        ]
        clazz.addAnnotation(solrDocAnn)

    }

    private def addXFWSearchClassAnnotation() {
        //XFWSearchClass над xtend классом
        val xtendsSearchClassAnn = ClassUtil.getAnnotation(baseClass, XFWSearchClass)
        
        val toDelete = ClassUtil.getAnnotation(clazz, XFWSearchClass)

        //удаляем если уже стояла над классом
        if (toDelete != null) clazz.removeAnnotation(toDelete)

        val searchAnn = XFWSearchClass.newAnnotationReference [
            setEnumValue(
                    Constants.FULLTEXT_DB_SEARCH_TYPE,
                    xtendsSearchClassAnn.getEnumValue(Constants.FULLTEXT_DB_SEARCH_TYPE)
            )
            setDoubleValue("version", xtendsSearchClassAnn.getDoubleValue("version"))
        ]
        clazz.addAnnotation(searchAnn)
    }

}