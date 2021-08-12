package ru.croc.ctp.jxfw.cass.generator

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.cass.generator.GeneratorHelperCass.*

import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import java.io.Serializable
import org.eclipse.xtend.lib.macro.TransformationContext
import javax.annotation.Nonnull
import org.apache.commons.lang3.StringUtils
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import java.time.ZonedDateTime
import java.time.LocalDateTime
import java.util.UUID
import java.time.format.DateTimeFormatter
import java.util.Objects
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn

/**
 * @author SMufazzalov
 * @since 1.4
 */
class ComplexKeyPopulator {

    val extension TransformationContext ctx

    new(@Nonnull TransformationContext context) {
        ctx = context
    }

    def populate(MutableClassDeclaration clazz) {
        if (!clazz.isNeedForComplexKey) return

        val keyClassName = getComplexKeyQName(clazz)
        val found = ctx.findClass(keyClassName)

        if (found != null) {

            val keyClass = found as MutableClassDeclaration

            keyClass.implementedInterfaces = #[Serializable.newTypeReference]
            keyClass.addAnnotation(PrimaryKeyClass.newAnnotationReference)

            //вид полей до внесенных изменений генерацией в изменяемый класс
            val src = clazz.findSourceRepresentaion

            //поиск ключевых полей
            val keyCols = src.declaredFields.filter[
                it.isKeyField(ctx)
            ]

            //Добавление всех ключевых полей в класс ключа
            keyCols.forEach[
                val f = it;
                val typeName = f.type.name
                val keyFieldType = ctx.newTypeReference(typeName)
                val ord = f.ordinal(ctx)
                val primaryKeyType = f.type(ctx)
                val name = f.name(ctx)
				val orderingType = f.orderingType(ctx) //направление сортировки

                val ann = PrimaryKeyColumn.newAnnotationReference [
                    setIntValue("ordinal", ord)
                    setEnumValue("type", primaryKeyType)
                    if (StringUtils.isNotEmpty(name)) {
                        setStringValue("name", name)
                    }
                    setEnumValue("ordering", orderingType)
                ]
                keyClass.addField(f.simpleName)[
                    visibility = Visibility::PRIVATE
                    type = keyFieldType
                    addAnnotation(ann)
                ]
            ]

			//добавим конструкторы
			addConstructors(keyClass, keyCols)
            //метод позволяет получить экземпляр ключа, из строкового массива (приходит закодированным с клиента)
            addFromStringArrayMethod(keyClass, keyCols)
            //ключ в представление строкового массива
            addToStringArrayMethod(keyClass, keyCols)
            //добавление геттеров сеттеров филдам добавленным в класс ключа
            keyClass.declaredFields.addGetterSetters(ctx)
        } else {
            throw new RuntimeException("No complex key found registered for: " + clazz.qualifiedName + ", (" + keyClassName  +")")
        }
    }

	private def addConstructors(MutableClassDeclaration keyClass, Iterable<? extends FieldDeclaration> keyCols) {
		
		//с параметрами
		val cntr = keyClass.addConstructor[
			visibility = Visibility::PUBLIC	
		]
		
		keyCols.forEach[
			cntr.addParameter(it.simpleName, it.type.name.newTypeReference)
		]
		
		cntr.body = ['''
		«FOR fld : keyCols»
		«toJavaCode(Objects.newTypeReference())».requireNonNull(«fld.simpleName»);
		«ENDFOR»		
		
		«FOR fld : keyCols»
		this.«fld.simpleName» = «fld.simpleName»;
		«ENDFOR»
		''']
		
		//дефолтный (важно дефолтный добавлять в конце, иначе xtend его не добавит)
		keyClass.addConstructor[
			visibility = Visibility::PUBLIC
		]
	}

    private def addFromStringArrayMethod(MutableClassDeclaration keyClass, Iterable<? extends FieldDeclaration> keyCols) {
        val sortedKeys = sortedKeyList(keyCols)
        val idx = new AtomicInteger(0);
        keyClass.addMethod('fromStringArray') [
            visibility = Visibility::PUBLIC
            static = true
            addParameter("arr", newArrayTypeReference(String.newTypeReference))
            returnType = keyClass.newTypeReference
            body = ['''
            «toJavaCode(keyClass.newTypeReference)» key = new «toJavaCode(keyClass.newTypeReference)»();
            «FOR k : sortedKeys»
            «IF k.type == Boolean.newTypeReference»
            key.«k.simpleName» = «toJavaCode(Boolean.newTypeReference)».valueOf(arr[«idx.get()»]);
            «ELSEIF k.type == Long.newTypeReference»
            key.«k.simpleName» = «toJavaCode(Long.newTypeReference)».valueOf(arr[«idx.get()»]);
            «ELSEIF k.type == Integer.newTypeReference»
            key.«k.simpleName» = «toJavaCode(Integer.newTypeReference)».valueOf(arr[«idx.get()»]);
            «ELSEIF k.type == String.newTypeReference»
            key.«k.simpleName» = arr[«idx.get()»];
            «ELSEIF k.type == Double.newTypeReference»
            key.«k.simpleName» = «toJavaCode(Double.newTypeReference)».valueOf(arr[«idx.get()»]);
            «ELSEIF k.type == ZonedDateTime.newTypeReference»
            key.«k.simpleName» = «toJavaCode(ZonedDateTime.newTypeReference)».parse(arr[«idx.get()»], «toJavaCode(DateTimeFormatter.newTypeReference)».ISO_OFFSET_DATE_TIME);
            «ELSEIF k.type == LocalDateTime.newTypeReference»
            key.«k.simpleName» = «toJavaCode(LocalDateTime.newTypeReference)».parse(arr[«idx.get()»], «toJavaCode(DateTimeFormatter.newTypeReference)».ISO_LOCAL_DATE_TIME);
            «ELSEIF k.type == UUID.newTypeReference»
            key.«k.simpleName» = «toJavaCode(UUID.newTypeReference)».fromString(arr[«idx.get()»]);
            «ENDIF»
            «val someInt = idx.andIncrement»
            «ENDFOR»
            return key;
            ''']
        ]
    }

    private def addToStringArrayMethod(MutableClassDeclaration keyClass, Iterable<? extends FieldDeclaration> keyCols) {
        val sortedKeys = sortedKeyList(keyCols)
        val idx = new AtomicInteger(0);
        keyClass.addMethod('toStringArray') [
            visibility = Visibility::PUBLIC
            returnType = newArrayTypeReference(String.newTypeReference)
            body = ['''
            String[] strings = new String[«sortedKeys.size»];
            «FOR k : sortedKeys»
            strings[«idx.get()»] = "" + «k.simpleName»;
            «val someInt = idx.andIncrement»
            «ENDFOR»
            return strings;
            ''']
        ]
    }

    private def sortedKeyList(Iterable<? extends FieldDeclaration> keyCols) {
        val sortedKeys = keyCols.toList
        Collections.sort(sortedKeys) [ a, b |
            Integer.compare(a.ordinal(ctx), b.ordinal(ctx))
        ]
        sortedKeys
    }
}