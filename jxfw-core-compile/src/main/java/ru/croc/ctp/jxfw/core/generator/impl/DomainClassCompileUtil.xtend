package ru.croc.ctp.jxfw.core.generator.impl

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import java.io.Serializable
import java.math.BigDecimal
import java.math.BigInteger
import java.sql.Blob
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.List
import java.util.Map
import java.util.Objects
import java.util.Properties
import java.util.Set
import java.util.UUID
import java.util.function.Predicate
import javax.persistence.Column
import javax.persistence.JoinColumn
import javax.persistence.Version
import org.apache.commons.lang3.StringUtils
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtend.lib.macro.CodeGenerationContext
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableAnnotationTarget
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableMemberDeclaration
import org.eclipse.xtend.lib.macro.declaration.Type
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import org.slf4j.Logger
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.meta.XFWElementLabel
import ru.croc.ctp.jxfw.core.domain.meta.XFWElementLabels
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWBasic
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWManyToOne
import ru.croc.ctp.jxfw.core.domain.meta.persistence.XFWOneToOne
import ru.croc.ctp.jxfw.core.generator.Constants
import ru.croc.ctp.jxfw.core.generator.EcoreGenerator
import ru.croc.ctp.jxfw.core.generator.meta.XFWBlobInfo
import ru.croc.ctp.jxfw.core.generator.meta.XFWProtected
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker
import ru.croc.ctp.jxfw.core.validation.meta.XFWReadOnly
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass

import static ru.croc.ctp.jxfw.core.generator.Constants.*
import static ru.croc.ctp.jxfw.core.generator.impl.EnumCompileUtil.*

import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*
import ru.croc.ctp.jxfw.core.domain.impl.DomainObjectUtil
import org.eclipse.xtend.lib.macro.declaration.CompilationUnit
import org.eclipse.xtend.lib.macro.file.FileLocations
import org.eclipse.xtend.lib.macro.file.FileSystemSupport
import org.springframework.util.ObjectUtils
import ru.croc.ctp.jxfw.metamodel.XfwValueType
import javax.persistence.Transient
import java.util.Collections
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration
import ru.croc.ctp.jxfw.core.generator.meta.XFWMappedSuperclass
import ru.croc.ctp.jxfw.core.domain.meta.XFWDefaultValue

/**
 * Хелпер класс для работы с полями, енумами...
 *
 * @author SMufazzalov
 * @since 1.4
 */
class DomainClassCompileUtil {

    private static final Logger logger = LoggerFactory.getLogger(DomainClassCompileUtil);

    /**
    * сейчас обрабатывается только случай readOnly
    * 1) класс целиком readOnly @XFWObject(isReadonly = true)
    * 2) либо какоето поле readOnly, т.е. это поле помечено @XFWReadOnly
    */
    public static def needToAddValidationLogic(MutableClassDeclaration clazz, ReadOnlyType readOnlyType, extension TransformationContext ctx) {
        // класс полностью readonly
        if(isReadOnlyEntityIncludeSuperClasses(clazz, readOnlyType))
        	return true;
        
        return getAllReadOnlyFieldsIncludeSuperClasses(clazz, readOnlyType, ctx).size > 0;
    }
    
    public static def isFieldReadOnly(FieldDeclaration field, ReadOnlyType readOnlyType, extension TransformationContext ctx){
    	
    	val xFWReadOnly = XFWReadOnly.newAnnotationReference.annotationTypeDeclaration
    	val annotation = field.findAnnotation(xFWReadOnly);
    	return  annotation!== null
        	&& annotation.getValue("value").equals(true)
        	&& readOnlyType.filter.test(annotation);
    }

    /**
    * наличие поля
    */
    public static def hasField(ClassDeclaration clazz, String fieldName) {
        var hasField = false

        var cls = clazz
        do {
            if(cls.findDeclaredField(fieldName) !== null) hasField = true
            cls = cls.extendedClass?.type as ClassDeclaration
        } while(cls !== null && !hasField)

        hasField
    }

    /**
     * Проверка режима "Только для чтнеия" для сущности.
     *
     * @param clazz - проверяемый класс
     * @return да/нет
     */
    public static def isReadOnlyEntity(ClassDeclaration clazz) {
        ClassUtil.existAnnotation(clazz, XFWReadOnly)
    }
    
     /**
     * Проверка режима "Только для чтнеия" для сущности и его предков.
     *
     * @param clazz - проверяемый класс
     * @return да/нет
     */
    public static def boolean isReadOnlyEntityIncludeSuperClasses(ClassDeclaration clazz, ReadOnlyType readOnlyType) {
        if (ClassUtil.existAnnotation(clazz, XFWReadOnly, readOnlyType.filter)) {
        	return true;
        }
        
        for(ClassDeclaration parentClass : GeneratorHelper.getParents(clazz)) {
        	if (ClassUtil.existAnnotation(parentClass, XFWReadOnly, readOnlyType.filter)) {
        		return true;
        	}
        }
    }
    
     /**
     * Возвращает поля класса и его предков помеченные "Только для чтнеия".
     *
     * @param clazz - класс для поиска полей.
     * @return список полей.
     */
    public static def FieldDeclaration[] getAllReadOnlyFieldsIncludeSuperClasses(ClassDeclaration clazz, ReadOnlyType readOnlyType, TransformationContext ctx) {
       	return getAllFieldsIncludeSuperClasses(clazz).filter[isFieldReadOnly(it, readOnlyType, ctx)];
    }
    
         /**
     * Возвращает все поля класса и его предков помеченные.
     * Note: наследуемое поле дублируется, но анотация присутсвует не на всех полях.
     *
     * @param clazz - класс для поиска полей.
     * @return список полей.
     */
    public static def FieldDeclaration[] getAllFieldsIncludeSuperClasses(ClassDeclaration clazz) {
    	val List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
    	
        for (FieldDeclaration field : clazz.declaredFields) {
        	fields.add(field);
        }
        
        for(ClassDeclaration parentClass : GeneratorHelper.getParents(clazz)) {
        	for (FieldDeclaration field : parentClass.declaredFields) {
        		fields.add(field);
        	}
        }
        
       	return fields;
    }
    

    /*
    *  Добавляем <code>private static final long serialVersionUID = 1L;</code> поле.
    *  Значение поля берется из поля serialVersionUID аннотации @XFWObject
    */
    public static def addSerialVersionUID(MutableClassDeclaration classType,
    extension TransformationContext context) {

        if(classType.findDeclaredField(SERIAL_VERSION_UID_LABEL) !== null) {
            return;
        }

        classType.addField(SERIAL_VERSION_UID_LABEL) [
            type = primitiveLong
            static = true
            final = true
            initializer = [SERIAL_VERSION_UID_EXPR]
        ]
    }
        
     
    /**
     * Добавляем  строковые константы с именами свойств сущностей и самих сущностей
     */
    public static def addPropertyConstants(MutableClassDeclaration clazz,
    extension TransformationContext context) {
   
        // внутренний статический класс с константами имен свойств
        val property = findClass(getPropertyInnerClassQName(clazz))

        if(property === null) throw new IllegalStateException("Inner property class not found for " + clazz.qualifiedName);

        // Константы имен свойств могут не использоваться прикладным кодом и это нормально.
        property.addAnnotation(SuppressWarnings.newAnnotationReference [
            setStringValue("value", "unused")
        ])

        property.docComment = "Класс строковых констант с именами свойств сущности."
        
        
        // внутренний статический класс родителя, если есть.
        // От него будем наследовать
        property.extendedClass = findTypeGlobally(getPropertyInnerClassQName(clazz.extendedClass?.type as ClassDeclaration))?.newTypeReference

    	// Во внутренний класс добавляем константы для всех полей
    	clazz.declaredFields.filter[ !it.isFinal() || !it.isStatic()]
    	.forEach[ field | 
    		val regex = "([a-z])([A-Z]+)";
            val replacement = "$1_$2";
    		val name = field.simpleName
                           .replaceAll(regex, replacement)
                           .toUpperCase();
    		property.addPropertyConstant(field,name,context)
    	]
   
   	   // В основной класс добавляем константу с его именем	
       clazz.addPropertyConstant(clazz,"TYPE_NAME",context)       
       
     }     
     
     public static def addPropertyConstant(MutableClassDeclaration clazz, MutableMemberDeclaration member,
		String constName, extension TransformationContext context) {
		clazz.addPropertyConstant(member, constName, null, context)
	 }
     
     public static def addPropertyConstant(MutableClassDeclaration clazz, MutableMemberDeclaration member,
		String constName, String constValue, extension TransformationContext context) {

		if (clazz.findDeclaredField(constName) !== null) {
			clazz.addError("Duplicate field name: " + constName)
		} else {
			clazz.addField(constName) [
				type = string
				visibility = Visibility::PUBLIC
				static = true
				final = true
				// TODO JXFW-777 Правильно определять, какой XFWElementLabel использовать. 
				// переопределять константу в наследнике, если XFWElementLabel переопределен
				docComment = member.annotations.findFirst [
					annotationTypeDeclaration == XFWElementLabel.newTypeReference.type
				]?.getStringValue("value")?.concat(".")
				setConstantValueAsString = constValue?:member.simpleName
			]
		}
	}

    /**
    * Догенерация аннотаций XFWElementLabel
    */
    public static def setI18NAnnotationDefaults(MutableClassDeclaration clazz, String defaultLang, extension TransformationContext ctx) {
        clazz.annotations.filter[annotationTypeDeclaration == XFWElementLabel.newTypeReference.type].forEach(
                a | doProcessi18nDefaults(clazz, a, defaultLang, ctx)
        )


        clazz.declaredFields.forEach [ f |
            f.annotations.filter[annotationTypeDeclaration == XFWElementLabel.newTypeReference.type].forEach [ a |
                doProcessi18nDefaults(f, a, defaultLang, ctx)
            ]
        ]
    }

    private static def doProcessi18nDefaults(MutableAnnotationTarget target, AnnotationReference a, String defaultLang, extension TransformationContext ctx) {
        if(a.getStringValue(LANG).isNullOrEmpty && defaultLang.isNullOrEmpty)
            a.addError(ERR_MSG_NO_DEFAULT)
        else if(a.getStringValue(LANG).isNullOrEmpty) {
            target.removeAnnotation(a)

            target.addAnnotation(
                    XFWElementLabel.newAnnotationReference [
                        setStringValue("value", a.getStringValue("value"))
                        setStringValue(LANG, defaultLang)
                        setStringValue(PROP_NAME, a.getStringValue(PROP_NAME))
                    ])
        }
    }

	//TODO: перейти на AccessorsProcessor
    /**
    * генерация геттеров/сеттеров
    */
    public static def addGetterSetters(
            Iterable<? extends MutableFieldDeclaration> fieldList,
            extension TransformationContext ctx
    ) {
        val util = new org.eclipse.xtend.lib.annotations.AccessorsProcessor.Util(ctx)

        fieldList.forEach[ field |
            field.addGetterSetter(ctx)
        ]
    }

    /**
    * генерация геттеров/сеттеров через хак (кишки AccessorProcessor), т.к. созданный при генерации класс не
    * может пройти заложенные в AccessorProcessor проверки на исходный класс... т.к. его не было изначально
    */
    public static def addGetterSetter(
            MutableFieldDeclaration field,
            extension TransformationContext ctx
    ) {
        val util = new org.eclipse.xtend.lib.annotations.AccessorsProcessor.Util(ctx)

        //геттер
        if(util.shouldAddGetter(field)) {
            util.addGetter(field, Visibility::PUBLIC);
        }
        //сеттер
        if(util.shouldAddSetter(field)) {
            util.addSetter(field, Visibility::PUBLIC);
        }
    }

    public static def addLoggerField(MutableClassDeclaration clazz, AnnotationReference transientAnn, extension TransformationContext context) {
        clazz.addField("logger") [
            visibility = Visibility::PRIVATE
            type = Logger.newTypeReference
            addAnnotation(transientAnn)
            static = true
            final = true
            initializer = [
                '''«toJavaCode(org.slf4j.LoggerFactory.newTypeReference)».getLogger(«toJavaCode(clazz.newTypeReference)».class)'''
            ]
        ]
    }
    public static def addMetadataMethod(MutableClassDeclaration clazz, extension TransformationContext context) {
        clazz.addField("METADATA") [
            visibility = Visibility::PUBLIC
            type = XfwClass.newTypeReference
            static = true
            final = true
            initializer = ['''«toJavaCode(XfwModelFactory.newTypeReference)».getInstance().findByFqNameThrowing("«clazz.qualifiedName»", XfwClass.class)''']
            docComment = "Статические метаданные класса."
        ]
        clazz.addMethod('getMetadata') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            returnType = XfwClass.newTypeReference
            body = ['''return  «toJavaCode(XfwModelFactory.newTypeReference)».getInstance().findByFqNameThrowing("«clazz.qualifiedName»", XfwClass.class);''']
        ]
    }

    public static def addIsNewField(MutableClassDeclaration clazz, AnnotationReference transientAnn, extension TransformationContext context) {
        clazz.addField("isNew") [
            visibility = Visibility::PRIVATE
            type = boolean.newTypeReference
            addAnnotation(transientAnn)
            initializer = ['''false''']
        ]
        clazz.addMethod('isNew') [
            visibility = Visibility::PUBLIC
            returnType = boolean.newTypeReference
            body = ['''return isNew;''']
        ]
        clazz.addMethod('setNew') [
            visibility = Visibility::PUBLIC
            addParameter('isnew', boolean.newTypeReference)
            body = ['''this.isNew = isnew;''']
        ]
    }

    public static def addIsRemovedField(MutableClassDeclaration clazz, AnnotationReference transientAnn, extension TransformationContext context) {
        clazz.addField("isRemoved") [
            visibility = Visibility::PRIVATE
            type = Boolean.newTypeReference
            addAnnotation(transientAnn)
            initializer = ['''false''']
        ]
        clazz.addMethod('isRemoved') [
            visibility = Visibility::PUBLIC
            returnType = Boolean.newTypeReference
            body = ['''return isRemoved;''']
        ]
        clazz.addMethod('setRemoved') [
            visibility = Visibility::PUBLIC
            addParameter('removed', Boolean.newTypeReference)
            body = ['''this.isRemoved = removed;''']
        ]
    }


    /**
     * Добавляет getVersion/setVersion для поля с версией.
     * Если поле отсутствует, то создаётся поле по умолчанию.
     *
     * @param clazz - класс для добавления методов(поля).
     */
    public static def addOptimisticLockField(MutableClassDeclaration clazz, Class<?> annotation, extension TransformationContext context) {
        var FieldDeclaration field = getOptimisticLockField(clazz, annotation);
        if (field === null) {
            // если поле с версией не найдено, то создаём поле по умолчанию
            field = clazz.addField('ts') [
                type = Long.newTypeReference
                initializer = ['''-1L''']
                addAnnotation(annotation.newAnnotationReference)
            ]
        }

        val String fieldName = field.simpleName;
        if (isFieldTimestamp(field)) {
            clazz.addMethod('getVersion') [
                visibility = Visibility::PUBLIC
                returnType = Long.newTypeReference
                body = [String.format("return %s.getTime();", fieldName)]
            ]
            clazz.addMethod('setVersion') [
                visibility = Visibility::PUBLIC
                addParameter(fieldName, Long.newTypeReference)
                body = [String.format("this.%s = new Timestamp(%s);", fieldName, fieldName)]
            ]
        } else {
            clazz.addMethod('getVersion') [
                visibility = Visibility::PUBLIC
                returnType = Long.newTypeReference
                body = [String.format("return %s;", fieldName)]
            ]
            clazz.addMethod('setVersion') [
                visibility = Visibility::PUBLIC
                addParameter(fieldName, Long.newTypeReference)
                body = [String.format("this.%s = %s;", fieldName, fieldName)]
            ]
        }
        clazz.addMethod('getNameOfVersionField') [
            visibility = Visibility::PUBLIC
            returnType = String.newTypeReference
            body = [String.format("return \"%s\";", fieldName)]
        ]

    }

    /**
	 * Возвращает поле с версией(помеченное аннотацией {@param annotation}) доменного объекта,
	 * если такого поля нету, то null.
	 *
	 * @param clazz - проверяемый класс
	 * @param annotation - тип аннотации
	 * @return да/нет
	 */
    public static def MutableFieldDeclaration getOptimisticLockField(MutableClassDeclaration clazz, Class<?> annotation) {
        for (MutableFieldDeclaration field : clazz.declaredFields) {
            if (ClassUtil.existAnnotation(field, annotation)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Проверяет существование явно указанного поля версии(JPA)
     *
     * @param clazz - проверяемый класс
     * @return да/нет
     */
    public static def boolean isFieldTimestamp(FieldDeclaration field) {
        if(field !== null) {
            return "java.sql.Timestamp".equals(field.type.name);
        }
        return false;
    }


    public static def addOriginalField(MutableClassDeclaration clazz, AnnotationReference transientAnn, extension TransformationContext context) {
        clazz.addField("original") [
            visibility = Visibility::PRIVATE
            type = Map.newTypeReference(String.newTypeReference, Object.newTypeReference)
            initializer = ['''new «toJavaCode(HashMap.newTypeReference)»<>()''']
            addAnnotation(transientAnn)
        ]

        clazz.addMethod('setPropChangedValues') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            addParameter('values', Map.newTypeReference(String.newTypeReference, Object.newTypeReference))
            body = ['''this.original = values;''']
        ]

        clazz.addMethod('getPropChangedValues') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            returnType = Map.newTypeReference(String.newTypeReference, Object.newTypeReference)
            body = ['''return original;''']
        ]
    }
    
    public static def addSavedState(MutableClassDeclaration clazz, AnnotationReference transientAnn, extension TransformationContext context) {

		clazz.implementedInterfaces = clazz.implementedInterfaces + #[SelfDirtinessTracker.newTypeReference]
        
        clazz.addField("savedState") [
            visibility = Visibility::PRIVATE
            transient = true
            type = clazz.newSelfTypeReference
            addAnnotation(transientAnn)
        ]
        
        clazz.addField("dirtyAttributes") [
            visibility = Visibility::PRIVATE
            transient = true
            type = Set.newTypeReference(String.newTypeReference)
            initializer = ['''new «toJavaCode(HashSet.newTypeReference)»<>()''']
            addAnnotation(transientAnn)
        ]
        
        //TODO: исключить метод из obtainValueByPropertyName
        clazz.addMethod('getSavedState') [
            visibility = Visibility::PUBLIC
            //addAnnotation(Override.newAnnotationReference)
            returnType = clazz.newSelfTypeReference
            body = ['''return savedState;''']
        ]
        
        //получить поля свои + родительские
        val allFields = clazz.declaredFields + clazz.getParents.map [ parent | parent.declaredFields ].flatten
        
        //val blobFields = allFields.filter[type.isAssignableFrom(Blob.newTypeReference)]
               
        clazz.addMethod('saveState') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            returnType = primitiveVoid
            body = ['''
            this.savedState = «toJavaCode(DomainObjectUtil.newTypeReference)».copyDomainObjectState(this);            
            ''']
        ]
        
        clazz.addMethod('clearDirtyAttributes') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            returnType = primitiveVoid
            body = ['''
            dirtyAttributes.clear();
            ''']
        ]
		
		clazz.addMethod('getDirtyAttributes') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            returnType = Set.newTypeReference(String.newTypeReference)
            body = ['''
                return «toJavaCode(Collections.newTypeReference)».unmodifiableSet(dirtyAttributes);
            ''']
        ]
        
        clazz.addMethod('trackChange') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            addParameter("attribute", string)
            returnType = primitiveVoid
            body = ['''
                dirtyAttributes.add(attribute);
            ''']
        ]
        
        clazz.addMethod('hasDirtyAttributes') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            returnType = primitiveBoolean
            body = ['''
                return !dirtyAttributes.isEmpty();
            ''']
        ]
        
        clazz.addMethod('getOriginalValue') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            var typeParameter = addTypeParameter("T", Serializable.newTypeReference)
            addParameter("attribute", String.newTypeReference)
            returnType = List.newTypeReference(typeParameter.newTypeReference)
            body = ['''
                return savedState != null ? savedState.getCurrentValue(attribute) : getCurrentValue(attribute);
            ''']
        ]
        
        clazz.addMethod('getCurrentValue') [
            visibility = Visibility::PUBLIC
            addAnnotation(Override.newAnnotationReference)
            // Преобразование к generic <T> выдает предупреждение о непроверяемом пробразовании типов. Т.к. в данном
            // случае невозможно избавится от предупреждения, то подавляем его
            addAnnotation(SuppressWarnings.newAnnotationReference [
                setStringValue("value", "unchecked")
            ])

            val typeParameter = addTypeParameter("T", Serializable.newTypeReference)
            addParameter("attribute", String.newTypeReference)
            returnType = List.newTypeReference(typeParameter.newTypeReference)

            body = ['''«FOR field : allFields.filter[!static && !type.isAssignableFrom(Blob.newTypeReference) && thePrimaryGeneratedJavaElement]»
            		«IF (DomainObject.newTypeReference.isAssignableFrom(field.type) ||
                            (field.type.actualTypeArguments.size() > 0 &&
                                    DomainObject.newTypeReference.isAssignableFrom(field.type.actualTypeArguments.get(0))))»
                     if("«field.simpleName»".equalsIgnoreCase(attribute)) {
                        return («toJavaCode(List.newTypeReference(typeParameter.newTypeReference))») obtainValueByPropertyName(attribute);
                     }
                    «ELSE»
                     if("«field.simpleName»".equalsIgnoreCase(attribute)) {
                     	«IF field.type.isPrimitive»
                     	return («toJavaCode(List.newTypeReference(typeParameter.newTypeReference))») «toJavaCode(Lists.newTypeReference)».newArrayList(this.get«field.simpleName.toFirstUpper»());
                     	«ELSE»
                        return this.get«field.simpleName.toFirstUpper»() != null
                            ? «toJavaCode(Lists.newTypeReference)».newArrayList((«toJavaCode(typeParameter.newTypeReference)») this.get«field.simpleName.toFirstUpper»())
                            : «toJavaCode(Lists.newTypeReference)».newArrayList();
                        «ENDIF»
                     }
                    «ENDIF»

                    «ENDFOR»
                    return «Lists.newTypeReference».newArrayList();
                    '''
            ]
        ]
        
       // переопределение сеттеров, которые определены в исходном коде xtend
        clazz.declaredMethods.filter[
            simpleName.startsWith("set")
                    && simpleName.length >=4
                    && simpleName.charAt(3) == simpleName.toUpperCase().charAt(3)
                    && parameters.length() == 1
                    && body.source
        ].forEach [ method |

        val setterName = method.simpleName
       	val fieldName = setterName.substring(3).toFirstLower
       	val field = allFields.findFirst[simpleName == fieldName]
        	
        	if (field!==null) {
				val parameter = method.parameters.get(0)


				val enum = EnumCompileUtil.isEnumField(field.type)
				val enumMany = EnumCompileUtil.isEnumSetField(field.type)
				val fieldTypeName = if(enum || enumMany) EnumCompileUtil.getEnumTypeForField(field, context).simpleName else null;
				clazz.addMethod("_" + setterName) [
					visibility = Visibility::PRIVATE
					static = method.static
					addParameter(parameter.simpleName, parameter.type)
					body = method.body
				]
				val input = if (enum ||
						enumMany) '''«fieldTypeName».METADATA.convertToInt(«parameter.simpleName»)''' else parameter.simpleName
				method.body = [
					'''
						«IF SelfDirtinessTracker.newTypeReference.isAssignableFrom(clazz.newTypeReference)»
							if (!«toJavaCode(ObjectUtils.newTypeReference)».nullSafeEquals(this.«fieldName», «input»)) {
								trackChange("«fieldName»");
							}
						«ENDIF»
						_«setterName»(«parameter.simpleName»);
					'''
				]
			}	
        ]        
    }


    /**
    * Расширяет метод getCurrentValue родительского класса доменного объекта.
    */
    public static def overrideGetCurrentValueMethodForNotTopParent(MutableClassDeclaration clazz, AnnotationReference transientAnn, extension TransformationContext context) {
        clazz.addMethod('getCurrentValue') [
                visibility = Visibility::PUBLIC
                addAnnotation(Override.newAnnotationReference)
                // Преобразование к generic <T> выдает предупреждение о непроверяемом пробразовании типов. Т.к. в данном
                // случае невозможно избавится от предупреждения, то подавляем его
                addAnnotation(SuppressWarnings.newAnnotationReference [
                    setStringValue("value", "unchecked")
                ])

                val typeParameter = addTypeParameter("T", Serializable.newTypeReference)
                addParameter("attribute", String.newTypeReference)
                returnType = List.newTypeReference(typeParameter.newTypeReference)

                body = ['''
                    «FOR field : clazz.declaredFields.filter[!static && !type.isAssignableFrom(Blob.newTypeReference) && thePrimaryGeneratedJavaElement]»
                 	«IF (DomainObject.newTypeReference.isAssignableFrom(field.type) || (field.type.actualTypeArguments.size() > 0 && DomainObject.newTypeReference.isAssignableFrom(field.type.actualTypeArguments.get(0))))»
                    if("«field.simpleName»".equalsIgnoreCase(attribute)) {
                        return («toJavaCode(List.newTypeReference(typeParameter.newTypeReference))») obtainValueByPropertyName(attribute);
                    }
                    «ELSE»
                    if("«field.simpleName»".equalsIgnoreCase(attribute)) {
                    «IF field.type.isPrimitive»
                        return («toJavaCode(List.newTypeReference(typeParameter.newTypeReference))») «toJavaCode(Lists.newTypeReference)».newArrayList(this.get«field.simpleName.toFirstUpper»());
                    «ELSE»
                        return this.get«field.simpleName.toFirstUpper»() != null
                            ? «toJavaCode(Lists.newTypeReference)».newArrayList((«toJavaCode(typeParameter.newTypeReference)») this.get«field.simpleName.toFirstUpper»())
                            : «toJavaCode(Lists.newTypeReference)».newArrayList();
                    «ENDIF»
                    }
                    «ENDIF»
                    «ENDFOR»
                    return super.getCurrentValue(attribute);
                    ''']
             ]
     }

    /**
    * Метод генерит логику подгрузки навигируемых свойств по имени поля.
    * умеет для дочерних объектов грузить как свои так и навигируемые родительские свойства
    */
    public static def addObtainValueByPropertyNameField(MutableClassDeclaration clazz, TypeReference keyType, extension TransformationContext context) {
        val mName = 'obtainValueByPropertyName'
        //ищем, не определен ли уже такой метод в классе xtend
        val found = (clazz.findDeclaredMethod(mName, String.newTypeReference) !== null)
        if(found) {
            return //чтобы не генерить метод когда уже его написали прикладные разработчики
        }
        clazz.addMethod(mName) [
            visibility = Visibility::PUBLIC
            addParameter("name", String.newTypeReference)
            returnType = List.newTypeReference(newWildcardTypeReference(DomainObject.newTypeReference(
                    newWildcardTypeReference)))

            //получить поля свои + родительские
            val allFields = clazz.declaredFields + clazz.getParents.map [ parent | parent.declaredFields ].flatten

            body = ['''«FOR field : allFields.filter([ f |
                    DomainObject.newTypeReference.isAssignableFrom(f.type) ||
                            (f.type.actualTypeArguments.size() > 0 &&
                                    DomainObject.newTypeReference.isAssignableFrom(f.type.actualTypeArguments.get(0)))])»
                    if("«field.simpleName»".equalsIgnoreCase(name)) {
                        return this.get«field.simpleName.toFirstUpper»() != null ? «toJavaCode(Lists.newTypeReference)».newArrayList(this.get«field.simpleName.toFirstUpper»()) : «toJavaCode(Lists.newTypeReference)».newArrayList();
                    }
                    «ENDFOR»
                    return «toJavaCode(Lists.newTypeReference)».newArrayList();
                    '''
            ]
        ]
    }

    public static def addBlobInfo(MutableClassDeclaration clazz, Properties props, extension TransformationContext context) {
        var temp = true
        val propVal = props.getProperty(ADDITIONAL_FIELDS_FOR_BLOB);
        // по наличию флага в xtend.properties, если стоит false то не генерим доп. поля под Blob
        if(!Objects.isNull(propVal)) {
            temp = Boolean.valueOf(propVal)
        }
        val boolean isGenerateAdditionalBlobFields = temp
        
        val blobFields = clazz.declaredFields.filter [Blob.newTypeReference.isAssignableFrom(type)]
        
        /*blobFields.forEach[
        	transient = true

        ]*/
        
        blobFields.filter [
            findAnnotation(XFWBlobInfo.newTypeReference.type) === null
        ].forEach [
            addAnnotation(XFWBlobInfo.newAnnotationReference[setBooleanValue("value", isGenerateAdditionalBlobFields)])
        ]
    }
    
    public static def List<TypeReference> getSupportedBaseFieldTypes(extension TypeReferenceProvider context) {
    	
    	val supportedBaseFieldTypes = #[
            String.newTypeReference,
            Byte.newTypeReference,
            Short.newTypeReference,
            Integer.newTypeReference,
            Long.newTypeReference,
            Boolean.newTypeReference,
            Double.newTypeReference,
            Blob.newTypeReference,
            LocalDateTime.newTypeReference,
            ZonedDateTime.newTypeReference,
            UUID.newTypeReference,
            BigDecimal.newTypeReference,
            BigInteger.newTypeReference,
            LocalDate.newTypeReference,
            LocalTime.newTypeReference,
            Duration.newTypeReference,
            Timestamp.newTypeReference
        ]
        
        supportedBaseFieldTypes
    }

    public static def List<FieldDeclaration> checkCoreFields(ClassDeclaration clazz, extension ValidationContext context) {
       
        val List<FieldDeclaration> uncheckedFields = new ArrayList

        clazz.declaredFields.filter[!systemFields.contains(simpleName)].forEach [ field |
            if(!validateEnumField(field,context) && !getSupportedBaseFieldTypes(context).exists[it.isAssignableFrom(field.type)]) {
	              uncheckedFields.add(field)
             }
        ]

        clazz.checkUnusedCascadeTypes(context)
        
		uncheckedFields
    }
    
	
	/**
		Проверка типов полей в объекте комплексного типа.
		В комплексном типе разрешены только поля  комплексных типов.
	*/
	public static def checkComplexTypeField(FieldDeclaration field, ClassDeclaration clazz, extension ValidationContext context) {
		val GeneratorHelper generatorHelper = new GeneratorHelper(context);

		if(generatorHelper.isComplex(field.type)) {
				// Do nothing ComplexType is allowed
		} else {
			field.addError("Only complex fields allowed in complex types - " + field.type);
		}

	}


    private static def checkUnusedCascadeTypes(ClassDeclaration clazz, extension ValidationContext context) {
        clazz.declaredFields.forEach [ field |
            field.annotations.filter [
                annotationTypeDeclaration.simpleName.endsWith("OneToMany") ||
                        annotationTypeDeclaration.simpleName.endsWith("ManyToOne")
            ].forEach [ annotation |
                val cascadeParams = annotation.getEnumArrayValue("cascade")
                if(cascadeParams.size() > 0) {
                    field.addError(ERR_MSG_CASCADE)
                }
            ]
        ]
    }

     /**
    * lang должен быть 2х-символный
    * XFWElementLabel с атрибутом propName может быть только на классе и св-во propName должно существовать у класса
    * или его предков.
    */
    public static def checkLabels(ClassDeclaration clazz, extension ValidationContext context) {
        
        val fieldNames = ClassUtil.getAllFieldsList(clazz).map[simpleName].toList

        clazz.annotations.filter[annotationTypeDeclaration == XFWElementLabel.newTypeReference.type].forEach [
          checkTypeLabels( clazz,context,fieldNames,it )
        ]
        clazz.annotations.filter[annotationTypeDeclaration == XFWElementLabels.newTypeReference.type]
        .map[getAnnotationArrayValue("value") as List<AnnotationReference>].flatten.forEach [
          checkTypeLabels( clazz,context,fieldNames,it )
        ]
 
        clazz.declaredFields.map[annotations].flatten.filter [
            annotationTypeDeclaration == XFWElementLabel.newTypeReference.type
        ].forEach [
            checkFieldLabels( clazz,context,it)
        ]
        clazz.declaredFields.map[annotations].flatten.filter [
            annotationTypeDeclaration == XFWElementLabels.newTypeReference.type
        ].map[getAnnotationArrayValue("value") as List<AnnotationReference>].flatten.forEach [
            checkFieldLabels( clazz,context,it)
        ]
    }

    private static def checkTypeLabels(ClassDeclaration clazz, extension ValidationContext context,
		List<String> fieldNames, AnnotationReference ann) {
		if(ann.getStringValue(LANG).isNullOrEmpty || ann.getStringValue(LANG).length != 2) addError(clazz,
			ERR_MSG_LENGTH)
		val propName = ann.getStringValue(PROP_NAME)
		if (!propName.isNullOrEmpty) {
			if (!fieldNames.contains(propName))
				addError(
					clazz,
					String.format("Property %s not found in class %s", propName, clazz.simpleName)
				)
		}
	}

    private static def checkFieldLabels(ClassDeclaration clazz, extension ValidationContext context,
		AnnotationReference ann) {
		if(ann.getStringValue(LANG).isNullOrEmpty || ann.getStringValue(LANG).length != 2) addError(clazz,
			ERR_MSG_LENGTH)
		if (!ann.getStringValue(PROP_NAME).isNullOrEmpty)
			addError(
				clazz,
				"Annotation @XFWElementLabel with propName attribute unsupported on fields in class " + clazz.simpleName
			)
	}
    
    /**
    * Именование полей должно соответствовать паттерну "[A-Z0-9._$]+" или начинаться с маленькой буквы.
    * см. JXFW-695, org.springframework.data.mapping.PropertyPath spring-data-commons.
    */
    public static def checkFieldNames(ClassDeclaration clazz, extension ValidationContext context) {
        
  
        clazz.declaredFields.filter [
            ! (simpleName.matches("[A-Z0-9._$]+") || Character.isLowerCase(simpleName.charAt(0)))
        ].forEach [
              addError("Field name must follow pattern [A-Z0-9._$]+ or start with lowercase. Field " + simpleName)
             
        ]
    }

    public static def checkProtectedType(ClassDeclaration clazz, extension ValidationContext context) {
        clazz.declaredFields.forEach [ field |
            if(ClassUtil.existAnnotation(field, XFWProtected)) {
                if(field.type != String.newTypeReference) {
                    field.addError(ERR_MSG_STRING_TYPE_EXPECTED)
                }
            }
        ]
    }

    public static def checkNullable(ClassDeclaration clazz, extension ValidationContext context) {
        val columnAnnRef = Column.newTypeReference
        val joinColumnAnnRef = JoinColumn.newTypeReference

        clazz.declaredFields.forEach [ field |
            val joinColumn = field.annotations.findFirst[annotationTypeDeclaration == columnAnnRef.getType() ||
                        annotationTypeDeclaration == joinColumnAnnRef.getType()]

            val relationAnnotation = field.annotations.findFirst [
                annotationTypeDeclaration == XFWManyToOne.newTypeReference.type ||
                        annotationTypeDeclaration == XFWOneToOne.newTypeReference.type
            ]

            val basicAnnotation = field.annotations.findFirst [
                annotationTypeDeclaration == XFWBasic.newTypeReference.type
            ]

            val eqSet = Sets.newHashSet()

            if(joinColumn !== null) {eqSet.add(joinColumn.getBooleanValue("nullable"))}
            if(basicAnnotation !== null) {eqSet.add(basicAnnotation.getBooleanValue("optional"))}
            if(relationAnnotation !== null) {eqSet.add(relationAnnotation.getBooleanValue("optional"))}

            val boolean allEqual = eqSet.size() == 1 || eqSet.size() == 0
            if(!allEqual) {
                addError(field, ERR_MSG_DIFFERENT_PARAMS)
            }
        ]
    }

    /**
    * Проверяет наличие неподдерживаемых аннотаций на классе и его полях. Если хотя бы одна аннотация присутствует,
    * тогда для каждой формируется и добавляется в список сообщение об ошибке.
    */
    public static def checkUnsupportedClassAnnotations(ClassDeclaration clazz, extension TransformationContext context) {
        if(ClassUtil.existAnnotation(clazz, Accessors)) {
            addError(clazz, String.format(ERR_MSG_UNSUPPORTED_ANNOTATION_FOR_CLASS, "@Accessors", clazz.simpleName));
        }

        clazz.declaredFields.forEach [ field |
            if(ClassUtil.existAnnotation(field, Accessors)) {
                addError(field, String.format(ERR_MSG_UNSUPPORTED_ANNOTATION_FOR_FIELD_OF_CLASS,
                "@Accessors", clazz.simpleName, field.simpleName));
            }
        ]
    }
    
    

    /**
    * Проверяет видемость public для существующих set методов.
    */
    public static def checkPublicAccessToMethodsOfSet(ClassDeclaration clazz, extension TransformationContext context) {
        clazz.declaredMethods.filter[
            simpleName.startsWith("set")
                    && simpleName.length >=4
                    && simpleName.charAt(3) == simpleName.toUpperCase().charAt(3)
                    && parameters.length() == 1
        ].forEach [ method |
            if(method.visibility != Visibility.PUBLIC) {
                addError(method, String.format(ERR_MSG_NOT_PUBLIC_SET_METHOD, method.simpleName, clazz.simpleName));
            }
        ]
    }

    /**
    * Проверяет видемость public для существующих get методов.
    */
    public static def checkPublicAccessToMethodsOfGet(ClassDeclaration clazz, extension TransformationContext context) {
        clazz.declaredMethods.filter[
            simpleName.startsWith("get")
                    && simpleName.length >=4
                    && simpleName.charAt(3) == simpleName.toUpperCase().charAt(3)
                    && parameters.length() == 0
        ].forEach [ method |
            if(method.visibility != Visibility.PUBLIC) {
                addError(method, String.format(ERR_MSG_NOT_PUBLIC_GET_METHOD, method.simpleName, clazz.simpleName));
            }
        ]
    }

    /**
    * Проверяет чтобы не объявлялись поля с именем version без аннотации Version.
    */
    public static def checkVersionField(ClassDeclaration clazz, extension TransformationContext context) {
        for (field : clazz.declaredFields) {
            if ("version".equals(field.simpleName)) {
                val annotationJpa = field.findAnnotation(Version.newAnnotationReference.annotationTypeDeclaration)
                val annotation = field.findAnnotation(org.springframework.data.annotation.Version.newAnnotationReference.annotationTypeDeclaration)
                if (annotationJpa === null && annotation === null) {
                    field.addError(Constants.ERR_MSG_FIELD_NAME_IS_VERSION)
                }
                if (annotationJpa !== null && annotation !== null) {
                    field.addError(Constants.ERR_MSG_YOU_USE_TWO_ANNOTATION_VERSION_FOR_ONE_FILED)
                }
            }
        }
    }

    /**
    * Проверяет и корректирует соблюдение ограничений для признаков временности объектов.
    */
    static def checkTransientParams(MutableClassDeclaration clazz, extension TransformationContext context) {
        val annotation = clazz.findAnnotation(XFWObject.newAnnotationReference.annotationTypeDeclaration)
        
        if (annotation !== null && isPersistence(clazz)) {
        	clazz.removeAnnotation(annotation);
        	clazz.addAnnotation(XFWObject.newAnnotationReference [
        		annotation.annotationTypeDeclaration.declaredAnnotationTypeElements.forEach[param |
        			if ("temp".equals(param.simpleName)) {
        				setBooleanValue("temp", false)
        			} else {
        				set(param.simpleName, annotation.getValue(param.simpleName))
        			}
        		]
        	])
        }
    }

    /**
    * Проверка, что бы родительский класс иимел такое же значение persistence.
    * Запрещено в иерархии смешивать временные и постоянные объекты.
    */
    public static def checkClassPersistenceHierarchicalType(ClassDeclaration clazz, extension TransformationContext context) {
        val persistent = isPersistence(clazz)
        for(ClassDeclaration parent : GeneratorHelper.getParents(clazz)) {
            if (!parent.simpleName.equals(Object.simpleName) && persistent != isPersistence(parent) 
            	&& !(parent.isAbstract && ClassUtil.getAnnotation(parent, XFWMappedSuperclass) !== null)
			) {
                 clazz.addError(Constants.ERR_MSG_DIFFERENT_PERSISTENCE_TYPE_FOR_HIERARCHICAL)
            }
        }

    }

	/**
	 * @XFWObject(persistence = PersistenceType.TRANSIENT) таким объектам установлен запрет на поля типа Blob.
	 * JXFW-1274 Запретить поля типа Blob в транзиентных объектах
	 */
    static def checkBlobFieldsOnTransientObjects(
    	ClassDeclaration clazz, 
    	extension ValidationContext context
    ) {
        if(!isPersistence(clazz)) {
            clazz.declaredFields
            .filter [Blob.newTypeReference.isAssignableFrom(type)]
            .forEach[addError(clazz.simpleName + "#" + simpleName + " - " + Constants.ERR_BAN_BLOB_IN_TRANSIENT_MODEL)]
        }
    }
    
	/**
	 * JXFW-1460 JXFW-1460 Запретить перекрывать поля в наследниках.
	 */
    public static def checkShadowing(ClassDeclaration clazz, extension ValidationContext context) {

		val parentFields = clazz.getParents.map[declaredFields].flatten.map[simpleName].toList

		clazz.declaredFields.filter[!it.static && it.primarySourceElement !== null].filter[parentFields.contains(it.simpleName)].
			forEach [
				it.addError(Constants.ERR_SHADOWING_FORBIDDEN)
			]
	}

	/**
     * Проверяет сочетание аннотаций на типе
     */
    public static def checkAnnotationsCombination(ClassDeclaration clazz, extension TransformationContext context) {
        val mappedSuperclassAnn = XFWMappedSuperclass.newTypeReference.type
        val objectAnn = XFWObject.newTypeReference.type
        if(clazz.findAnnotation(mappedSuperclassAnn) !== null && clazz.findAnnotation(objectAnn) !== null) {
            clazz.addError(String.format(Constants.ERR_MSG_ANNOTATION_COMBINATION_IS_INADMISSIBLE,
                    mappedSuperclassAnn.simpleName, objectAnn.simpleName));
        }
    }

        /**
         * Проверка правильности установки значений по умолчанию
        */
        public static def checkXfwDefaultValue(ClassDeclaration clazz, extension ValidationContext context) {
                    clazz.declaredFields.filter [it.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration) !== null
                    	&& it.findAnnotation(XFWDefaultValue.newAnnotationReference.annotationTypeDeclaration).getBooleanValue("asCurrent")
                    	&& it.type != LocalDate.newTypeReference() && it.type != LocalTime.newTypeReference() && it.type != LocalDateTime.newTypeReference()
                    	&& it.type != ZonedDateTime.newTypeReference()
                    ].forEach [
                  addError("Cant pass 'now' to not-time types. Field " + simpleName)
            ]
        }
    
    public static def produceRequiredFields(MutableClassDeclaration clazz, Procedures.Procedure1<MutableFieldDeclaration> procedure, extension TransformationContext context) {

        val List<MutableFieldDeclaration> result = newArrayList()
        clazz.declaredFields.filter[Blob.newTypeReference.isAssignableFrom(type)].forEach [ blobField |
            var generate = false

            val blobInfoAnnotation = blobField.findAnnotation(XFWBlobInfo.newTypeReference.type)
            if(blobInfoAnnotation !== null) {
                generate = blobInfoAnnotation.getBooleanValue("value");
            } else {
                // Аннотация XFWBlobInfo добавляется автоматически ко всем полям Blob.
                // Поэтому эта ситуация не должна случиться.
                throw new IllegalStateException("Не найдена аннотация XFWBlobInfo:" + blobField.simpleName)
            }

            val fileNameSuffix = blobInfoAnnotation.getStringValue(FILE_NAME_SUFFIX)
            val sizeSuffix = blobInfoAnnotation.getStringValue(FILE_SIZE_SUFFIX)
            val contentTypeSuffix = blobInfoAnnotation.getStringValue(FILE_TYPE_SUFFIX)

            if(generate) {
                var blobFieldName = blobField.simpleName
                // На случай если указали аттрибут name в аннотации Column
                if(blobField.findAnnotation(Column.newTypeReference.type) !== null) {
                    val name = blobField.findAnnotation(Column.newTypeReference.type).getStringValue("name");
                    if(!StringUtils.isEmpty(name)) {
                        blobFieldName = toValidJavaIdentifier(name);
                    }
                }

                val blobFieldFileName = blobFieldName + fileNameSuffix

                val Procedures.Procedure2<MutableFieldDeclaration, TypeReference> init = [
                    field, type |
                    field.setVisibility(Visibility::PRIVATE)
                    field.setType(type)
                    procedure?.apply(field)
                    if(blobField.findAnnotation(Transient.newTypeReference.type) !== null) {
                    	field.addAnnotation(Transient.newAnnotationReference)
                    }
                ]

                var fieldBlobFieldFileName = clazz.addField(blobFieldFileName, [field |
						init.apply(field, String.newTypeReference)
					])

					val blobFieldMimeType = blobFieldName + contentTypeSuffix

					var fieldBlobFieldMimeType = clazz.addField(blobFieldMimeType, [field |
						init.apply(field, String.newTypeReference)
					])

					val blobFieldFileSize = blobFieldName + sizeSuffix

					var fieldBlobFieldFileSize = clazz.addField(blobFieldFileSize, [field |
						init.apply(field, Long.newTypeReference)
					])

                result.addAll(#[fieldBlobFieldFileName, fieldBlobFieldMimeType, fieldBlobFieldFileSize])
                addGetterSetters(
                        #[fieldBlobFieldFileName, fieldBlobFieldMimeType, fieldBlobFieldFileSize],
                        context
                )

            }
        ]
        return result
    }

    public static def addAnnotationIfNotExist(MutableClassDeclaration clazz, Type type, AnnotationReference annotation) {
        val clsAnnotation = clazz.getAnnotations.findFirst [annotationTypeDeclaration == annotation.annotationTypeDeclaration]
        if(clsAnnotation === null) {
            clazz.addAnnotation(annotation)
        }
    }

    public static def getEcoreGenerator(
            PersistenceModulesManager persistenceModulesManager,
            List<MutableClassDeclaration> mutableClasses,
            CodeGenerationContext context
    ) {
        val oppositeProvider = [
            FieldDeclaration field, EClass ref, List<MutableClassDeclaration> clazzez, TypeReferenceProvider typeRef |
            val module = persistenceModulesManager.getClassModule(field.getDeclaringType() as ClassDeclaration)
            module.ecoreGetOpposite(field, ref, clazzez, typeRef)
        ]
        val ecoreModelEmitterProvider = [ ClassDeclaration clz, XFWModelGenerator modelEmfResource, TypeReferenceProvider typeRef |
            val module = persistenceModulesManager.getClassModule(clz)
            persistenceModulesManager.getModuleContext.setModelEmfResource(modelEmfResource)
            module.getEcoreModelEmitter
        ]
        val ecoreAddColumnPropsProcedure = [
            ClassDeclaration clz, EStructuralFeature feature, Iterable<? extends AnnotationReference> annotations, 
            TypeReferenceProvider typeRef, XfwValueType xfwValueType |
            val module = persistenceModulesManager.getClassModule(clz)
            module.ecoreAddColumnProps(feature, annotations, typeRef, xfwValueType)
        ]
        val storageTypeProvider = [ClassDeclaration clz |
            persistenceModulesManager.getStorageType(clz)
        ]
        val keyTypeProvider = [ClassDeclaration clz |
            val module = persistenceModulesManager.getClassModule(clz)
            module.getKeyType(clz)
        ]
        val useFulltextProvider = [ClassDeclaration clz |
            clz
            .annotations
            .filter[it.annotationTypeDeclaration.simpleName.equalsIgnoreCase("XFWSearchClass")]
            .size > 0
        ]

        //список полей
        val fieldsListProvider = [ClassDeclaration clz |
            val module = persistenceModulesManager.getClassModule(clz)
            module.getFieldsList()
        ]
        val generator = new EcoreGenerator(
                mutableClasses,
                oppositeProvider,
                ecoreModelEmitterProvider,
                ecoreAddColumnPropsProcedure,
                storageTypeProvider,
                keyTypeProvider,
                useFulltextProvider,
                fieldsListProvider,
                context
        )

        generator
    }
    
    
    public static def <T extends FileLocations & FileSystemSupport> Properties loadProperties(CompilationUnit cu,
		extension T context) {
		return loadProperties(XTEND_PROPERTIES, cu, context)
	}

	public static def <T extends FileLocations & FileSystemSupport> Properties loadProperties(String propFileName,
		CompilationUnit cu, extension T context) {
		val properties = new Properties
		try {
			var jsFileFolder = cu.filePath.sourceFolder.append(cu.packageName?.replace('.', '/'))
			val propFile = jsFileFolder.append(propFileName)
			if (propFile.exists) {
				properties.load(propFile.contentsAsStream)
			}
		} catch (Exception ex) {
			logger.error("{}", ex)
		}
		return properties
	}
	
	static def getEnumValue(TypeReference enumTypeRef, String enumValueName) {
		(enumTypeRef.type as EnumerationTypeDeclaration).declaredValues.
		findFirst [
			simpleName.equals(enumValueName)
		]
	}

    /**
     * Класс констант для проверки режима read-only в зависимости от значения параметра {@link XFWReadOnly#facade}.
     *
     * @see XFWReadOnly
     */
    public static class ReadOnlyType {

        /**
         * Режим read-only независимо от признака {@link XFWReadOnly#facade}.
         */
        public static final ReadOnlyType ALL = new ReadOnlyType([true])

        /**
         * Режим read-only в случае, если {@link XFWReadOnly#facade} равно {@code true}.
         */
        public static final ReadOnlyType FACADE = new ReadOnlyType([it.getBooleanValue("facade")])

        /**
         * Режим read-only в случае, если {@link XFWReadOnly#facade} равно {@code false}.
         */
        public static final ReadOnlyType SERVER = new ReadOnlyType([!it.getBooleanValue("facade")])

        private final Predicate<AnnotationReference> filter;

        private new(Predicate<AnnotationReference> filter) {
            this.filter = filter
        }
    }
}
