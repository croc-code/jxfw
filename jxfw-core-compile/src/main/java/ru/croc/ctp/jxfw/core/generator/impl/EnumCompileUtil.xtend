package ru.croc.ctp.jxfw.core.generator.impl;

import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.services.Tracability
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.ValidationContext
import java.util.EnumSet
import java.util.Collection
import java.util.HashSet
import ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId
import ru.croc.ctp.jxfw.core.domain.meta.XFWDefaultValue
import java.util.List
import java.util.Arrays
import javax.persistence.Access
import javax.persistence.AccessType

import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import java.util.stream.StreamSupport
import org.apache.commons.lang.StringUtils
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MethodDeclaration

/**
 * Вспомогательные методы для работы с Enum на этапе компиляции.
 * <p/>
 * Created by SPlaunov on 18.06.2016.
 */
public class EnumCompileUtil {

	/**
	 * Ищем конкретный тип Перечисления.
	 * 
	 * @param field - поле
	 * @return Type - класс Enum если нашли, null - если нет
	 */
	public static def EnumerationTypeDeclaration getEnumTypeForField(FieldDeclaration field,
		extension Tracability context) {

		// TODO удалить после деприкации XFWEnumerated
		val enumType = field.annotations.filter [
			"XFWEnumerated".equals(it.getAnnotationTypeDeclaration().getSimpleName())
		].head?.getClassValue("value")?.type as EnumerationTypeDeclaration
		if (enumType !== null) {
			return enumType
		}

		if (field.type.isEnumField) {
			return field.type.type as EnumerationTypeDeclaration
		}
		if (field.type.isEnumSetField) {
			return field.type.getActualTypeArguments().get(0).type as EnumerationTypeDeclaration
		}

		// бывшее поле-пересчиление, преобразованное в Int.
		val primarySource = (field.primarySourceElement as FieldDeclaration)?.type;

		if (primarySource !== null) {
			if (primarySource.isEnumField) {
				return primarySource.type as EnumerationTypeDeclaration;
			}
			if (primarySource.isEnumSetField) {
				return primarySource.getActualTypeArguments().get(0).type as EnumerationTypeDeclaration;
			}
			
		}
		
		return null
	}
	
	/**
	 * Ищем конкретный тип Перечисления по классу.
	 * 
	 * @param field - поле
	 * @param clazz - класс содержащий поле
	 * @return Type - класс Enum если нашли, null - если нет
	 */
	public static def EnumerationTypeDeclaration getEnumTypeForField(FieldDeclaration field,
		extension Tracability context, ClassDeclaration clazz) {
		
		val foundByType =  getEnumTypeForField(field, context);
		
		if (foundByType !== null) {
			return foundByType;
		}
		
		val findByGetter = getEnumTypeRefForFieldByClazzGetter(field, clazz);
		
		if (findByGetter !== null) {
			if (findByGetter.isEnumField) {
				return findByGetter.type as EnumerationTypeDeclaration;
			}
			if (findByGetter.isEnumSetField) {
				return findByGetter.getActualTypeArguments().get(0).type as EnumerationTypeDeclaration;
			}
		}
		
		return null
	}
	
	public static def TypeReference getEnumTypeRefForFieldByClazzGetter(FieldDeclaration field, ClassDeclaration clazz) {
		
		val findByGetter = StreamSupport.stream(
			clazz.getDeclaredMethods().spliterator(), false
		)
		.filter[(it as MethodDeclaration).getSimpleName().equals("get"+StringUtils.capitalize(field.getSimpleName()))]
		.map[(it as MethodDeclaration).returnType]
		.findFirst()
		.orElse(null);
		return if(findByGetter!==null) findByGetter as TypeReference else null;
	}

	public static def boolean isEnumMany(FieldDeclaration field, extension Tracability context) {

		// TODO удалить после деприкации XFWEnumerated
		val enumType = field.annotations.filter [
			"XFWEnumerated".equals(it.getAnnotationTypeDeclaration().getSimpleName())
		].head?.getClassValue("value")?.type as EnumerationTypeDeclaration
		if (enumType !== null) {
			return enumType.isFlags
		}

		if (field.type.isEnumField) {
			return false
		}
		if (field.type.isEnumSetField) {
			return true
		}

		// бывшее поле-пересчиление, преобразованное в Int.
		val primarySource = (field.primarySourceElement as FieldDeclaration)?.type;

		if (primarySource !== null) {
			if (primarySource.isEnumField) {
				return false;
			}
			if (primarySource.isEnumSetField) {
				return true;
			}
		}
	}
	
	public static def boolean isEnumMany(FieldDeclaration field, extension Tracability context, ClassDeclaration clazz) {
		if (isEnumMany(field, context)) {
			return true
		} else {
			val typeRefEnum = getEnumTypeRefForFieldByClazzGetter(field, clazz);
			if (typeRefEnum !== null) {
				if (typeRefEnum.isEnumField) {
					return false;
				}
				if (typeRefEnum.isEnumSetField) {
					return true;
				}
			}
		}
	}

	public static def boolean isFlags(TypeDeclaration enumz) {

		for (AnnotationReference ar : enumz.getAnnotations()) {
			if (ar.getAnnotationTypeDeclaration().getSimpleName().equals(
				typeof(ru.croc.ctp.jxfw.core.domain.meta.XFWEnum).getSimpleName())) {
				return ar.getBooleanValue("isFlags");
			}
		}

	}

	// заменяем перечисления и наборы перечислений на целые
	// меняем геттеры и сеттеры соответственно.
	public static def processEnumFields(MutableClassDeclaration clazz, extension TransformationContext context) {

		clazz.declaredFields.filter[it.type.isEnumField].forEach[it.processEnum(clazz, false, context)]

		clazz.declaredFields.filter[it.type.isEnumSetField].forEach[it.processEnum(clazz, true, context)]

	}

	/**
	 * Поле -перечисление.
	 */
	static def boolean isEnumField(TypeReference typeReference) {
		return typeReference.type instanceof EnumerationTypeDeclaration;
	}

	/**
	 * Поле - множественное перечисление.
	 */
	static def boolean isEnumSetField(TypeReference typeReference) {
		return (typeReference.getActualTypeArguments().size() > 0 &&
			typeReference.getActualTypeArguments().get(0).type instanceof EnumerationTypeDeclaration);
	}

	private static def initializerToValueList(EnumerationTypeDeclaration fieldType, String initializer) {
		if (initializer.contains("EnumSet.noneOf")) {
			return null;
		}
		var List<String> items = Arrays.asList(
			initializer.replace(fieldType.simpleName + ".", "").replace("(", "").replace(")", "").replace("EnumSet.of",
				"").split("\\s*,\\s*"));
		return String.join(",", items);

	}

	private static def checkDefault(MutableFieldDeclaration field, EnumerationTypeDeclaration fieldType, boolean isMany,
		extension TransformationContext context) {
		val ann = field.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration)
		if (ann === null) {
			return
		}
		val annValue = ann.getStringValue("value").trim()
		if (annValue.nullOrEmpty) {
			return
		}
		if (!isMany && Arrays.asList(annValue.split("\\s*,\\s*")).size() > 1) {
			field.addError("Only one value allowed for XFWDefaultValue attribute used on enumeration")
		}
		Arrays.asList(annValue.split("\\s*,\\s*")).forEach [ annItem |
			if (fieldType.declaredValues.filter [
				it.simpleName.equals(annItem)
			].size === 0) {
				field.addError("XFWDefaultValue attribute " + annItem + " not found in enumeration " +
					fieldType.simpleName)
			}
		]

	}

	public static def createNewMethodBody(String annValue, FieldDeclaration field,EnumerationTypeDeclaration fieldType, boolean isEnumMany) {
		val sb = new StringBuilder();
		if (isEnumMany) {
			sb.append("EnumSet.of(").append(
				Arrays.asList(annValue.split("\\s*,\\s*")).map [
					fieldType.simpleName + "." + it
				].join(",")
			).append(")")

		} else {
			sb.append(fieldType.simpleName + "." + annValue)
		}
		return sb.toString()
	}

	private static def processEnum(MutableFieldDeclaration field, MutableClassDeclaration clazz, boolean isMany,
		extension TransformationContext context) {

		val fieldType = field.getEnumTypeForField(context);
		val fieldTypeName = fieldType.simpleName
		field.type = Integer.newTypeReference
		
		/*
		 * добавляем аннотацию @Access(value = AccessType.FIELD)
		 * без нее apt-maven-plugin генерирует в некоторых случаях
		 * пути в q-Типах по типу геттеров. а нам надо по типу полей.
		 * JXFW-1452
		 */
		if (field.findAnnotation(Access.newAnnotationReference.annotationTypeDeclaration) === null) { 
			field.addAnnotation( Access.newAnnotationReference [
				setEnumValue("value", getEnumValue(AccessType.newTypeReference, AccessType.FIELD.name))
		])
		
		}

		if (field.initializer !== null &&
			field.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration) === null) {
			val annotationValue = initializerToValueList(fieldType, field.initializer.toString)
			if (annotationValue !== null) {
				field.addAnnotation(XFWDefaultValue.newAnnotationReference [
					setStringValue("value", annotationValue)
				])

			}
		}

		field.checkDefault(fieldType, isMany, context)

		val initBody = field.initializer?.toString?.trim()?.replace(";", "")
		if (initBody !== null) {
			if (initBody.contains("EnumSet.noneOf")) {
				field.initializer = [
					'''  «fieldTypeName».METADATA.convertToInt(EnumSet.noneOf(«fieldTypeName».class))'''
				]
			} else {
				field.initializer = [
					'''  «fieldTypeName».METADATA.convertToInt(«initBody»)'''
				]
			}

		}


		//  геттер и сеттер модифицируeтся в AccessorsProcessor
	}

	/**
	 * Заменяем целые поля проаннотарованные XFWEnumerated на перечисления.
	 * В зависимости от isFlags на типе пересичления делаем
	 * либо Enum, либо EnumSet поле. Т.к. больше множественность в данном случае взять неоткуда.
	 * 
	 */
	// TODO удалить после деприкации XFWEnumerated
	public static def processXFWEnumeratedField(MutableClassDeclaration clazz,
		extension TransformationContext context) {

		clazz.declaredFields.filter [
			it.annotations.exists["XFWEnumerated".equals(it.getAnnotationTypeDeclaration().getSimpleName())]
		].forEach [
			val enumType = it.annotations.filter [
				"XFWEnumerated".equals(it.getAnnotationTypeDeclaration().getSimpleName())
			].head?.getClassValue("value").type as EnumerationTypeDeclaration
			if (enumType !== null) {
				if (enumType.isFlags) {
					it.type = EnumSet.newTypeReference(enumType.newSelfTypeReference)
				} else {
					it.type = enumType.newSelfTypeReference
				}
			}
		]

	}

	static def boolean validateEnumField(FieldDeclaration field, extension ValidationContext context) {

		val annotatedEnumerationType = field.getEnumTypeForField(context)

		if (annotatedEnumerationType === null) {
			return false;
		}

        if (annotatedEnumerationType.findDeclaredField("METADATA") === null) {
			field.addError(String.format(
					"METADATA field is missing in enum %s. Set annotation XFWEnum or create field METADATA manually.",
					annotatedEnumerationType.simpleName));
		}

		val xfwEnumIdSet = new HashSet<Integer>();
		annotatedEnumerationType.declaredValues.forEach [ declaredValue |
			val xfwEnumIdAnn = declaredValue.findAnnotation(
				XFWEnumId.newAnnotationReference.annotationTypeDeclaration
			)
			if (xfwEnumIdAnn !== null) {
				// проверяем, что все численные значения разные.
				if (!xfwEnumIdSet.add(xfwEnumIdAnn.getIntValue("value"))) {
					field.addError("Duplicate XFWEnumId value on " + declaredValue.simpleName);
				}
			} else {
				field.addError("XFWEnumId annotation is missing on " + declaredValue.simpleName);
			}
		]
		// для множественных перечислений проверяем, что все значения - степени двойки.
		if (field.isEnumMany(context)) {

		    val fieldType = if (Collection.newTypeReference.isAssignableFrom(field.type)) field.type else (field.primarySourceElement as FieldDeclaration)?.type
		    if (Collection.newTypeReference.isAssignableFrom(fieldType) && !(EnumSet.newTypeReference.isAssignableFrom(fieldType))) {
		        field.addError("Do not use " + fieldType.toString + ", only EnumSet")
		    }

			xfwEnumIdSet.forEach [ value |
				if (value.bitwiseAnd(value - 1) !== 0) {
					field.addError("XFWEnumId value must be the power of 2: " + value);
				}
			]
		}
		return true;
	}

	/**
	 * формирует выражение, которое подставляется в сеттер поля. 
	 * Выражение expr пришло с вебклиента.
	 */
	public static def String fromToExpr(FieldDeclaration field, String expr, extension Tracability context) {
		val enumDeclaration = field.getEnumTypeForField(context);
		if (enumDeclaration === null) {
			return "(" + field.type.simpleName + ") " + expr;
		}
		if (field.isEnumMany(context)) {
			return "(" + expr + " instanceof Integer) ? "
					+ enumDeclaration.simpleName + ".METADATA.convertToEnumSet((Integer) value, " + enumDeclaration.simpleName + ".class)"
					+ " : (EnumSet<" + enumDeclaration.simpleName + ">)" + expr;
		} else {
			return "(" + expr + " instanceof Integer) ? "
					+ enumDeclaration.simpleName + ".METADATA.convertToEnum((Integer) value, " + enumDeclaration.simpleName + ".class)"
					+ " : (" + enumDeclaration.simpleName + ")" + expr;
		}
	}
}
