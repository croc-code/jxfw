package ru.croc.ctp.jxfw.core.generator.impl

import java.util.ArrayList
import java.util.List
import java.util.Set
import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import org.eclipse.xtend.lib.macro.file.Path
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider
import org.slf4j.Logger;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.generator.meta.XFWComplexType
import ru.croc.ctp.jxfw.core.domain.ComplexType
import org.eclipse.xtend.lib.macro.ValidationContext
import java8.util.stream.Collectors
import java8.util.stream.StreamSupport
import javax.persistence.Entity

import ru.croc.ctp.jxfw.core.domain.impl.ObservableList
import ru.croc.ctp.jxfw.core.domain.impl.ObservableSet
import ru.croc.ctp.jxfw.core.generator.meta.XFWMappedSuperclass

class GeneratorHelper {

	final static String TEST_PROJECT_NAME = "myProject.src";

	extension TypeReferenceProvider context

	static final Logger logger = LoggerFactory.getLogger(GeneratorHelper);

	new(TypeReferenceProvider context) {
		this.context = context
	}

	static def getRepositoryQName(ClassDeclaration clazz) {
		clazz.compilationUnit.packageName + repositoryPackageSuffix + clazz.simpleName + "Repository"
	}

	static def Path getRepositoryJavaFile(ClassDeclaration clazz) {
		try{
			clazz.compilationUnit.filePath.parent.append(repositoryPackageName).append(clazz.simpleName + "Repository.java")
		}catch (Exception ex) {
			logger.error("compilationUnit.filePath fails in eclipse multimodule project {}", ex)
			//TODO: проблему починили в 2.14, разобраться с тем, актуально ли еще.
			/*
			 * JXFW-1174 Падение здесь приведет к проблемам, если в проекте есть явно определенный репозиторий
			 * и сборка делается эклипсом. В этом случае явно определенный репозиторий не будет найден и сгенерируется еще один.
			 * Это завалит сборку, но способа лечения не видно, пока не починят баг в xtend
			 * https://github.com/eclipse/xtext-xtend/issues/383
			 * Потому что явно определенный репозиторий - это java и он не виден в контексте регистрации классов xtend
			 * Узнать о его существовании можно только через файловую систему. А с эти как раз беда.
			 */
			return null
		}
		
	}

	static def getRepositoryImplQName(ClassDeclaration clazz) {
		clazz.compilationUnit.packageName + repositoryPackageSuffix + clazz.simpleName + "RepositoryImpl"
	}

	static def Path getRepositoryImplJavaFile(ClassDeclaration clazz) {
		try{
			clazz.compilationUnit.filePath.parent.append(repositoryPackageName).append(clazz.simpleName +
			"RepositoryImpl.java")
		}catch (Exception ex) {
			logger.error("compilationUnit.filePath fails in eclipse multimodule project {}", ex)
			/*
			 * JXFW-1174 Падение здесь приведет к проблемам, если в проекте есть явно определенный репозиторий
			 * и сборка делается эклипсом. В этом случае явно определенный репозиторий не будет найден и сгенерируется еще один.
			 * Это завалит сборку, но способа лечения не видно, пока не починят баг в xtend
			 * https://github.com/eclipse/xtext-xtend/issues/383
			 * Потому что явно определенный репозиторий - это java и он не виден в контексте регистрации классов xtend
			 * Узнать о его существовании можно только через файловую систему. А с эти как раз беда.
			 */
			return null
		}
		
			
	}

	static def getServiceQName(ClassDeclaration clazz) {
		clazz.compilationUnit.packageName + servicePackageSuffix + clazz.simpleName + "Service"
	}

	static def getComplexKeyQName(ClassDeclaration clazz) {
		clazz.qualifiedName + "Key"
	}

	static def getPropertyInnerClassQName(ClassDeclaration clazz) {
		clazz.qualifiedName + ".Property"
	}

	static def getControllerQName(ClassDeclaration clazz) {
		clazz.compilationUnit.packageName + facadePackageSuffix + clazz.simpleName + "Controller"
	}

	static def getControllerQName(TypeDeclaration type) {
		type.compilationUnit.packageName + facadePackageSuffix + type.simpleName + "Controller"
	}

	static def getTOServiceQName(ClassDeclaration clazz) {
		clazz.compilationUnit.packageName + facadePackageSuffix + clazz.simpleName + "ToService"
	}

	static def getTOServiceQName(ClassDeclaration clazz, String neighbourClassName) {
		clazz.compilationUnit.packageName + facadePackageSuffix + neighbourClassName + "ToService"
	}

	static def findXFWObjectAnnotation(ClassDeclaration clazz) {
		// Хак для доступа к TypeReferenceProvider. Иначе он недоступен в
		// CodeGenerationContext
		val getTypeReferenceProvider = clazz.compilationUnit.class.getDeclaredMethod("getTypeReferenceProvider");
		val typeReferenceProvider = getTypeReferenceProvider.invoke(clazz.compilationUnit) as TypeReferenceProvider

		return clazz.findAnnotation(typeReferenceProvider.newTypeReference(XFWObject).type)
	}
	
	static def findAnnotation(ClassDeclaration clazz, Class<?> annotation) {
		// Хак для доступа к TypeReferenceProvider. Иначе он недоступен в
		// CodeGenerationContext
		val getTypeReferenceProvider = clazz.compilationUnit.class.getDeclaredMethod("getTypeReferenceProvider");
		val typeReferenceProvider = getTypeReferenceProvider.invoke(clazz.compilationUnit) as TypeReferenceProvider

		return clazz.findAnnotation(typeReferenceProvider.newTypeReference(annotation).type)
	}
	
	static def findXFWMappedSuperClassAnnotation(ClassDeclaration clazz) {
		// Хак для доступа к TypeReferenceProvider. Иначе он недоступен в
		// CodeGenerationContext
		val getTypeReferenceProvider = clazz.compilationUnit.class.getDeclaredMethod("getTypeReferenceProvider");
		val typeReferenceProvider = getTypeReferenceProvider.invoke(clazz.compilationUnit) as TypeReferenceProvider

		return clazz.findAnnotation(typeReferenceProvider.newTypeReference(XFWMappedSuperclass).type)
	}
	
	static def isSaveState(TypeDeclaration clazz) {
		val ann = ClassUtil.getAnnotation(clazz, XFWObject);
		if (ann !== null) {
			return ann.getBooleanValue("saveState");
		} else {
			return false;
		}
	}

	private static def isPersistenceTransient(ClassDeclaration clazz) {
		isPersistenceTransient(findXFWObjectAnnotation(clazz))
	}
	
	private static def isPersistenceTransient(TypeDeclaration clazz) {
		isPersistenceTransient(ClassUtil.getAnnotation(clazz, XFWObject));
	}

	private static def isPersistenceTransient(AnnotationReference ann) {
		if (ann !== null) {
			ann.getEnumValue("persistence").simpleName == XFWObject.PersistenceType.TRANSIENT.toString
		} else {
			true
		}
	}

	static def isPersistence(ClassDeclaration clazz) {
		//Для класса правильно так аннотацию получать, иначе NPE
        findAnnotation(clazz, Entity) !== null || !isPersistenceTransient(clazz)
    }

	static def isPersistence(TypeDeclaration type) {
		logger.debug("isPersistence: " + type)
		logger.debug("isPersistence, getAnnotation: " + ClassUtil.getAnnotation(type, Entity))
		logger.debug("isPersistence, isPersistenceTransient: " + isPersistenceTransient(type))
        ClassUtil.getAnnotation(type, Entity) !== null || !isPersistenceTransient(type)
    }

	/*получить из строки валидный идентификатор*/
	static def toValidJavaIdentifier(String value) {
		val sb = new StringBuilder()
		value.toCharArray().forEach [
			if (Character.isJavaIdentifierPart(it)) {
				sb.append(it);
			}
		]

		sb.toString()
	}

	public static final List<String> systemFields = #["isNew", "ts", "logger", "isRemoved", "_uniqueKey", "original",
		"originalId", "METADATA", "dirtyAttributes"]
	public static final String repositoryPackageName = "repo"
	public static final String repositoryPackageSuffix = "." + repositoryPackageName + "."
	public static final String servicePackageSuffix = ".service."
	public static final String facadePackageSuffix = ".facade.webclient."

	static def boolean isFieldAddToModel(FieldDeclaration field, CodeGenerationContext context) {
		/* В модель попадают только поля, которые были объявлены в xtend, и
		 * id и ts
		 */
		return (context.isThePrimaryGeneratedJavaElement(field) || #["id", "ts"].contains(field.getSimpleName()))
	}

	static def String getFieldNameWithSuffix(FieldDeclaration field, String suffix) {
		field.simpleName + suffix
	}

	/**
	 * Возвращает перечень всех родительских классов вверх по дереву наследования
	 */
	static def List<ClassDeclaration> getParents(ClassDeclaration clazz) {
		val l = new ArrayList<ClassDeclaration>

		var cls = clazz.extendedClass?.type as ClassDeclaration
		while (cls !== null) {
			l.add(cls)
			cls = cls.extendedClass?.type as ClassDeclaration
		}

		return l
	}

	/**
	 * Проверка того что проверяемый класс, не содержит "родителей-наследников-от-ru.croc.ctp.jxfw.core.DomainObject"
	 */
	static def isTopParent(ClassDeclaration clazz) {
		// вся цепочка родителей (не интерфейсов)
		val parentsOfClass = getParents(clazz)

		// поиск родителя-реализующего DomainObject, Это работает, если компилятро смтоит на Java-код (c,jhrf xthtp vf
		// или зааннотированого @XFWObject
		val found = parentsOfClass.findFirst [ parent |
			val interfaces = parent.implementedInterfaces
			interfaces.findFirst[intf|intf.type.simpleName.equals(DomainObject.simpleName)] !== null ||
				findXFWObjectAnnotation(parent) !== null
		]

		val top = (found === null)

		// logger.info(clazz.simpleName+ " is top = " + top)
		return top // проверяемый класс - top parent
	}
	
	 /**
	 * Проверка того что проверяемый класс наследуется от абстрактного класса с маппингом. 
	 */
	static def isMappedBySuperclass(ClassDeclaration clazz) {
		// вся цепочка родителей (не интерфейсов)
		val parentsOfClass = getParents(clazz)

		// поиск родителя зааннотированого @XFWMappedSuperClass
		val found = parentsOfClass.findFirst [ parent | findXFWMappedSuperClassAnnotation(parent) !== null ]

		val top = (found !== null)

		return top // проверяемый класс - top parent
	}

	/**
	 * Имя типа связи OneToMany или ManyToOne
	 */
	def String getMappedTypeName(FieldDeclaration field) {
		field.mappedType.simpleName
	}

	def TypeReference getMappedType(FieldDeclaration field) {
		if (DomainObject.newTypeReference.isAssignableFrom(field.type)) {
			return field.type
		}

		val relationAnnotation = field.annotations.findFirst [
			annotationTypeDeclaration.simpleName.equals("XFWOneToMany") ||
				annotationTypeDeclaration.simpleName.equals("XFWManyToMany") ||
				annotationTypeDeclaration.simpleName.equals("OneToMany") ||
				annotationTypeDeclaration.simpleName.equals("ManyToMany")
		]
		if (relationAnnotation !== null) {
			return field.type.getActualTypeArguments().get(0)
		}
	}

	def boolean isDomain(TypeReference fieldType) {
		if (fieldType === null) {
			return false;
		}
		// это случай, когда тип поля - это класс, уже прошедший процессинг,
		// в нем нет XFWObject, зато он наследует от DomainObject
		if (DomainObject.newTypeReference.isAssignableFrom(fieldType)) {
			return true;
		}
		if (fieldType?.getType() instanceof ClassDeclaration) {
			val xfwObjectAnnotation = (fieldType.getType() as ClassDeclaration).annotations.findFirst [
				annotationTypeDeclaration.simpleName.equals(XFWObject.simpleName)
			]
			return xfwObjectAnnotation !== null
		}
		return false;
	}

	def getDomainCollectionArgument(TypeReference fieldType) {
		return if (fieldType?.getActualTypeArguments().size() == 1)
			fieldType.getActualTypeArguments().get(0)
		else
			null;
	}

	def boolean isDomainCollection(TypeReference fieldType) {
		var TypeReference fieldTypeArg = getDomainCollectionArgument(fieldType);
		return isDomain(fieldTypeArg);
	}

	def boolean isComplex(TypeReference fieldType) {
		// это случай, когда тип поля - это класс, уже прошедший процессинг,
		// в нем нет XFWComplexType, зато он наследует от ComplexType
		if (ComplexType.newTypeReference.isAssignableFrom(fieldType)) {
			return true;
		}
		if (fieldType !== null && fieldType.getType() instanceof ClassDeclaration) {
			val xfwObjectAnnotation = (fieldType.getType() as ClassDeclaration).annotations.findFirst [
				annotationTypeDeclaration.simpleName.equals(XFWComplexType.simpleName)
			]
			return xfwObjectAnnotation !== null
		}
		return false;
	}

	static def checkClassPackage(TypeDeclaration typeDeclaration, extension ValidationContext context) {
		try {
			val pathSegments = typeDeclaration.compilationUnit.filePath.parent.segments
			val stringPath = StreamSupport.stream(pathSegments).collect(Collectors.joining(".")) + "." +
				typeDeclaration.simpleName;
			// тестовая компиляция	
			if (stringPath.startsWith(TEST_PROJECT_NAME)) {
				return;
			}
			logger.debug("Dir: " + stringPath + " Package: " + typeDeclaration.qualifiedName);
			if (! stringPath.endsWith(typeDeclaration.qualifiedName)) {
				typeDeclaration.addError("Class' package and directory do not match: " + typeDeclaration.qualifiedName);
			}
		} catch (Exception ex) {
			logger.error("{}", ex)
		}
		
	}

	/**
	* Возвращает тип обертки отслеживания в зависимости от типа переданной коллекции.
	* Тип Collection должен быть запрещен и не приходить
	*/
	def TypeReference getNameObservableCollection(TypeReference type) {
		if(List.newTypeReference.isAssignableFrom(type)) {
			return ObservableList.newTypeReference
		} else if(Set.newTypeReference.isAssignableFrom(type)) {
			return ObservableSet.newTypeReference
		}
		throw new IllegalArgumentException("Not supported type -" + type)
	}

}
