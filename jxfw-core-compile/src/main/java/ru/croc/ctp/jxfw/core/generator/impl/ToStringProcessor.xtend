package ru.croc.ctp.jxfw.core.generator.impl

import java.util.ArrayList
import java.util.Collection
import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import org.eclipse.xtend.lib.macro.AbstractClassProcessor
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableTypeDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString

/**
 * Процессор для active annotation {@link XFWToString}. 
 * Генерирует метод toString() для объектов доменных сущностей.
 * 
 * @author Nosov Alexander
 * @since 1.3
 */
class ToStringProcessor extends AbstractClassProcessor {
    
    override doTransform(MutableClassDeclaration annotatedClass, extension TransformationContext context) {
        // получаем аннотацию ToString
        val annotation = annotatedClass.findAnnotation(typeof(XFWToString).findTypeGlobally)

        //  получаем список имен полей которые попадают в toString
        val of = newArrayList(annotation.getStringArrayValue("of"))
        //  получаем список имен полей которые НЕ попадают в toString
        val exclude = annotation.getStringArrayValue("exclude")
        //  получаем флаг необходимо ли вызавать ToString у родителя
        val callSuper = annotation.getValue('callSuper') as Boolean
        //  получаем флаг необходимо ли вызавать ToString у родителя
        val includeFieldNames = annotation.getValue('includeFieldNames') as Boolean
        //  получаем флаг необходимо ли печатать имена полей объекта
        val includeHashCode = annotation.getValue('hashcode') as Boolean
        //  получаем флаг необходимо ли печатать identityHashcode объекта
        val includeIdentityHashCode = annotation.getValue('identityHashcode') as Boolean

        if(of.size > 0 && exclude.size > 0) {
            annotatedClass.addError("ToString.of and ToString.exclude are mutually exclusive")
            return
        }

        for(name : of) {
            if(name.empty) {
                annotatedClass.addError("The name in ToString.of must not be empty")
                return
            }
        }

        val fields = annotatedClass.declaredFields
                                    .filter[ !type.isAssignableFrom(Collection.newTypeReference)]
                                    .map[simpleName].toSet

        for (name : exclude) {
            if (name.empty) {
                annotatedClass.addError("The name in ToString.exclude must not be empty")
                return
            } else if (!fields.contains(name)) {
                annotatedClass.addError('''The field «name» is not a member of «annotatedClass.qualifiedName»''')
            }
        }

        val valuesMap = annotatedClass.getValuesMap(callSuper, of, exclude, context)

        val values = new ArrayList<String>
        valuesMap.forEach [valueName, valueExpression |
            if (!values.empty) {
                values.add('builder.append(", ");')
            }
            if (includeFieldNames) {
                values.add('''builder.append("«valueName»=").append(«valueExpression»);''')
            } else {
                values.add('''builder.append(«valueExpression»);''')
            }
        ]

        val methodToString = annotatedClass.getDeclaredMethods.findFirst [simpleName.equals("toString")]
        
        if (methodToString === null) {
            annotatedClass.addMethod('toString') [
                returnType = typeof(String).newTypeReference
                body = ['''
                    final StringBuilder builder = new StringBuilder();
                    builder.append("«annotatedClass.simpleName»");
                    «IF includeHashCode»
                        builder.append("@").append(Integer.toString(hashCode(), 16));
                    «ENDIF»
                    «IF includeIdentityHashCode»
                        builder.append("#").append(Integer.toString(System.identityHashCode(this), 16));
                    «ENDIF»
  		            «IF !(new GeneratorHelper(context).isComplex(annotatedClass.newTypeReference))»
                        builder.append("(id = ").append(getId()).append(")");
                    «ENDIF»
                    «IF !values.empty»
                        builder.append("[");
                        «FOR value : values»
                            «value»
                        «ENDFOR»
                        builder.append("]");
                    «ENDIF»
                    return builder.toString();
                ''']
            ]
        }
    }

    private def static Map<String, String> getValuesMap(MutableTypeDeclaration annotatedClass, boolean callSuper, List<String> of, List<String> exclude, extension TransformationContext context) {
        val values = new LinkedHashMap

        if(!of.empty) {
                of
                    .filter[ToStringProcessor.isBaseTypeField(annotatedClass, it, context)]
                    .forEach[values.put(it, getFieldValueExpression(annotatedClass, it))]
        } else {
            getFields(annotatedClass, exclude, context)
                        .forEach[values.put(simpleName, getFieldValueExpression(annotatedClass, it))]
        }

        if(callSuper) {
            values.put('super', 'super.toString()')
        }

        values
    }

    private def static boolean isBaseTypeField(MutableTypeDeclaration annotatedClass, String fieldName, extension TransformationContext context) {
        val field = annotatedClass.findDeclaredField(fieldName)
        isBaseTypeField(annotatedClass, field, context)
    }
    
    private def static boolean isBaseTypeField(MutableTypeDeclaration annotatedClass, FieldDeclaration field, extension TransformationContext context) {
        if (field === null || DomainClassCompileUtil.getSupportedBaseFieldTypes(context).exists[it.isAssignableFrom(field.type)]) {
           true 
        } else {
           false
        }
    }
    
    private def static String getFieldValueExpression(MutableTypeDeclaration annotatedClass, String fieldName) {
        val field = annotatedClass.findDeclaredField(fieldName)

        if(field === null) {
            fieldName
        } else {
            getFieldValueExpression(annotatedClass, field)
        }
    }

    private def static String getFieldValueExpression(MutableTypeDeclaration annotatedClass, MutableFieldDeclaration field) {
        val it = field
        switch (it) {
            default : simpleName
        }
    }

    private def static getFields(MutableTypeDeclaration annotatedClass, List<String> exclude, extension TransformationContext context) {
        annotatedClass.declaredFields
                        .filter[!static && !exclude.contains(simpleName)]
                        .filter[isBaseTypeField(annotatedClass, it, context)]
    }
}