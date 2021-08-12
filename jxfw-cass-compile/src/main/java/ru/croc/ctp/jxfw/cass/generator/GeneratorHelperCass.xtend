package ru.croc.ctp.jxfw.cass.generator

import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory
import org.slf4j.Logger
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import java.util.Collections
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey
import ru.croc.ctp.jxfw.core.domain.XFWPrimaryKeyType
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.cql.Ordering
import org.springframework.data.cassandra.core.mapping.Column

/**
 * @author SMufazzalov
 * @since 1.4
 */
class GeneratorHelperCass {

    private static final Logger logger = LoggerFactory.getLogger(GeneratorHelperCass);

    //нужна ли генерация комплексного ключа
    static def isNeedForComplexKey(ClassDeclaration clazz) {
        val src = clazz.findSourceRepresentaion
        //на этапе doRegisterGlobals еще не доступен transformationContext (нельзя через newAnnotationReference)
        val pkc = PrimaryKeyColumn.name
        val xpkc = XFWPrimaryKey.name

        //если поля модели содержат аннотации PrimaryKeyColumn или XFWPrimaryKey, то считаем что ключ надо cгенерить
        src.declaredFields.map[annotations].flatten.findFirst[
            annotationTypeDeclaration.qualifiedName.equals(pkc) || annotationTypeDeclaration.qualifiedName.equals(xpkc)
        ] != null
    }

    //найти первоначальное описание класса (до модификаций средствами генерации)
    static def findSourceRepresentaion(ClassDeclaration clazz) {
        val src = clazz.compilationUnit.getSourceTypeDeclarations.findFirst[
            //среди исходников по совпадению полного наименования
            clazz.getQualifiedName.equals(it.getQualifiedName)
        ]

        if(src != null) return src

        throw new RuntimeException("no source representation found for: " + clazz.getQualifiedName)
    }

    //достать значение из аннотации
    static def ordinal(FieldDeclaration field, extension TransformationContext ctx) {
        val pka = field.findAnnotation(ctx.pk)
        if(pka != null) {
            return pka.getIntValue("ordinal")
        }

        val xpka = field.findAnnotation(ctx.xpk)
        if(xpka != null) {
            return xpka.getIntValue("order")
        }

        throw new RuntimeException(field.simpleName + " couldnt resolve order for key")
    }

    //достать значение из аннотации
    static def type(FieldDeclaration field, extension TransformationContext ctx) {
        val primaryKeyTypeEnum = PrimaryKeyType.newTypeReference.getType as EnumerationTypeDeclaration
        val pkClustered = primaryKeyTypeEnum.findDeclaredValue(PrimaryKeyType.CLUSTERED.name)
        val pkPartitioned = primaryKeyTypeEnum.findDeclaredValue(PrimaryKeyType.PARTITIONED.name)

        val pka = field.findAnnotation(ctx.pk)
        if(pka != null) {
            val t = pka.getEnumValue('type')
            if(t.simpleName.equals(PrimaryKeyType.CLUSTERED.name)) {
                return pkClustered
            } else {
                return pkPartitioned
            }
        }

        val xpka = field.findAnnotation(ctx.xpk)
        if(xpka != null) {
            val t = xpka.getEnumValue('type')
            if(t.simpleName.equals(XFWPrimaryKeyType.CLUSTERED.name)) {
                return pkClustered
            } else {
                return pkPartitioned
            }
        }

        throw new RuntimeException(field.simpleName + " couldnt resolve column type for key")
    }

	//получить направление сортировки из аннотации
	static def orderingType(FieldDeclaration field, extension TransformationContext ctx) {
		val enumType = Ordering.newTypeReference.type as EnumerationTypeDeclaration
		val dsc = enumType.findDeclaredValue(Ordering.DESCENDING.name)
		val asc = enumType.findDeclaredValue(Ordering.ASCENDING.name)
		
		val pka = field.findAnnotation(ctx.pk)
        if(pka != null) {
            val t = pka.getEnumValue('ordering')
            if(t.simpleName.equals(Ordering.DESCENDING.name)) {
                return dsc
            } else {
                return asc
            }
        }

        val xpka = field.findAnnotation(ctx.xpk)
        if(xpka != null) {
            val t = xpka.getEnumValue('ordering')

            if(t.simpleName.equals(XFWPrimaryKey.Ordering.DESCENDING.name)) {
                return dsc
            } else {
                return asc
            }
        }
	}
	
    //достать значение из аннотации
    static def name(FieldDeclaration field, extension TransformationContext ctx) {
        val xpka = field.findAnnotation(ctx.xpk)
        if(xpka != null) {
            return xpka.getStringValue('name')
        }
        val pka = field.findAnnotation(ctx.pk)
        if(pka != null) {
            return pka.getStringValue('name')
        }
        val columnAnn = field.findAnnotation(Column.newAnnotationReference.annotationTypeDeclaration)
        if (columnAnn != null) {
            return columnAnn.getStringValue("value")
        }
    }

    //поиск признаков того, что поле входит в составной ключ
    static def isKeyField(FieldDeclaration field, extension TransformationContext ctx) {
        field.findAnnotation(ctx.pk) != null || field.findAnnotation(ctx.xpk) != null
    }

    private static def pk(extension TransformationContext ctx) {
        PrimaryKeyColumn.newAnnotationReference.annotationTypeDeclaration
    }

    private static def xpk(extension TransformationContext ctx) {
        XFWPrimaryKey.newAnnotationReference.annotationTypeDeclaration
    }

    static def partiotionKeysSorted(TypeDeclaration type) {
        val list = type.declaredFields.filter [ isPartitionedKey(it) ].toList
        Collections.sort(list) [ a, b |
            Integer.compare(orderForKey(a), orderForKey(b))
        ]
        list
    }

    static def clusteringKeysSorted(TypeDeclaration type) {
        val list = type.declaredFields.filter [ isClusteredKey(it) ].toList
        Collections.sort(list) [ a, b |
            Integer.compare(orderForKey(a), orderForKey(b))
        ]
        list
    }

    //порядок ключа
    static def orderForKey(FieldDeclaration field) {
        val keyAnnotation = field.annotations.findFirst [ ann |
            ann.getAnnotationTypeDeclaration.simpleName.equals(XFWPrimaryKey.simpleName) ||
                    ann.getAnnotationTypeDeclaration.simpleName.equals(PrimaryKeyColumn.simpleName)
        ]

        if(keyAnnotation == null) throw new RuntimeException("Attempt to get key order? from non-key field")

        val t = keyAnnotation.getEnumValue('type')

        if(keyAnnotation.getAnnotationTypeDeclaration.simpleName.equals(XFWPrimaryKey.simpleName) ) {
            return keyAnnotation.getIntValue("order")
        } else {
            return keyAnnotation.getIntValue("ordinal")
        }
    }
    //поле - кластерный ключ
    static def isClusteredKey(FieldDeclaration field) {
        val keyAnnotation = field.annotations.findFirst [ ann |
            ann.getAnnotationTypeDeclaration.simpleName.equals(XFWPrimaryKey.simpleName) ||
                    ann.getAnnotationTypeDeclaration.simpleName.equals(PrimaryKeyColumn.simpleName)
        ]

        if(keyAnnotation == null) return false

        val t = keyAnnotation.getEnumValue('type')

        if(keyAnnotation.getAnnotationTypeDeclaration.simpleName.equals(XFWPrimaryKey.simpleName) ) {
            if(t.simpleName.equals(XFWPrimaryKeyType.CLUSTERED.name)) {
                return true;
            }
        } else {
            if(t.simpleName.equals(PrimaryKeyType.CLUSTERED.name)) {
                return true
            }
        }
        return false
    }
    //поле - партиционный ключ
    static def isPartitionedKey(FieldDeclaration field) {
        val keyAnnotation = field.annotations.findFirst [ ann |
            ann.getAnnotationTypeDeclaration.simpleName.equals(XFWPrimaryKey.simpleName) ||
                    ann.getAnnotationTypeDeclaration.simpleName.equals(PrimaryKeyColumn.simpleName)
        ]

        if(keyAnnotation == null) return false

        val t = keyAnnotation.getEnumValue('type')

        if(keyAnnotation.getAnnotationTypeDeclaration.simpleName.equals(XFWPrimaryKey.simpleName) ) {
            if(t.simpleName.equals(XFWPrimaryKeyType.PARTITIONED.name)) {
                return true;
            }
        } else {
            if(t.simpleName.equals(PrimaryKeyType.PARTITIONED.name)) {
                return true
            }
        }
        return false
    }
}