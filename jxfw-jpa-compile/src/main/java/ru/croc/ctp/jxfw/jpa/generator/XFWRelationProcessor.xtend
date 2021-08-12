package ru.croc.ctp.jxfw.jpa.generator

import javax.persistence.JoinTable
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableFieldDeclaration
import org.eclipse.xtend.lib.macro.declaration.Type
import org.hibernate.annotations.NotFound
import java.util.List
import java.util.Set
import org.slf4j.Logger
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory

class XFWRelationProcessor {
	
	static final Logger logger = LoggerFactory.getLogger(XFWRelationProcessor);
	
	/**
	 * 
	 * 
	 */
	def processRelation(String relationName, MutableClassDeclaration clazz,
	extension PersistenceModuleJpa persistContext, extension TransformationContext transContext) {
		val xfwRelationName = "XFW" + relationName

		val fields = clazz.getDeclaredFields().filter [ f |
			f.annotations.exists[annotationTypeDeclaration.simpleName.equals(xfwRelationName)]
		]

		if("XFWManyToMany".equals(xfwRelationName) || "XFWOneToMany".equals(xfwRelationName)) {
			addNotFoundAnnotation(fields, transContext)
			fields.forEach[ f |
				if (f.initializer === null) {
					if (List.newTypeReference.isAssignableFrom(f.type)) {
						f.initializer = '''new java.util.ArrayList<>()'''
					} else if (Set.newTypeReference.isAssignableFrom(f.type)) {
						f.initializer = '''new java.util.HashSet<>()'''
					}
				}
			]
		}
		
		val jpaAnnotationTypeClass = findTypeGlobally("javax.persistence." + relationName).newTypeReference.type
		addJPARelationAnnotation(fields, jpaAnnotationTypeClass, xfwRelationName, transContext)

	}

	/**
	 * Добавляем аннотацию NotFound с полем IGNORE для того,
	 * чтобы не падало сохранение в Hibernate
	 */
	def addNotFoundAnnotation(Iterable<? extends MutableFieldDeclaration> fields,
	extension TransformationContext transContext) {
		val notFoundMethod = findTypeGlobally("org.hibernate.annotations.NotFoundAction") as EnumerationTypeDeclaration
		val actionIGNORE = notFoundMethod.findDeclaredValue("IGNORE")
		fields.forEach [ f |
			if(f.annotations.filter[annotationTypeDeclaration.simpleName.equals("NotFound")].size == 0) {
				f.addAnnotation(
						NotFound.newAnnotationReference [
							setEnumValue("action", actionIGNORE)
						])
			}
		]
	}

	def removeCustomAnnotation(Iterable<? extends MutableFieldDeclaration> fieldsManyToOne, String annotationName) {
		fieldsManyToOne.forEach [
			val manyToOne = annotations.filter[annotationTypeDeclaration.simpleName.equals(annotationName)].get(0)
			removeAnnotation(manyToOne)
		]
	}

	def addJPARelationAnnotation(Iterable<? extends MutableFieldDeclaration> fields, Type annotationType,
	String customAnnotationName, extension TransformationContext transContext) {
		fields.forEach [
			val customAnnotation = annotations.filter[annotationTypeDeclaration.simpleName.equals(customAnnotationName)].get(0)
			if (customAnnotation !== null) {
				addAnnotation(
						annotationType.newAnnotationReference [
							val targetEntity = customAnnotation.getClassValue("targetEntity")

							if(targetEntity !== null && !targetEntity.getSimpleName().equals("void")) {
								setClassValue("targetEntity", targetEntity)
							}

							setEnumValue("fetch", customAnnotation.getEnumValue("fetch"))

							if(customAnnotation.getEnumArrayValue("cascade").length > 0) {
								set("cascade", customAnnotation.getEnumArrayValue("cascade"))
							}
							// set("cascade", customAnnotation.getEnumValue("cascade"))
							if("XFWManyToMany".equals(customAnnotation.annotationTypeDeclaration.simpleName) ||
									"XFWOneToMany".equals(customAnnotation.annotationTypeDeclaration.simpleName)) {
								if("XFWOneToMany".equals(customAnnotation.annotationTypeDeclaration.simpleName)) {
									set("orphanRemoval", customAnnotation.getBooleanValue("orphanRemoval"))
								}
								logger.debug("mappedBy value: "+ customAnnotation.getValue("mappedBy"))
								logger.debug("mappedBy expression: "+ customAnnotation.getExpression("mappedBy"))
								val mappedBy = customAnnotation.getValue("mappedBy")
								if(mappedBy !== null && !"".equals(mappedBy)) {
									set("mappedBy", mappedBy)
								}
							} else if("XFWManyToOne".equals(customAnnotation.annotationTypeDeclaration.simpleName) ||
									"XFWOneToOne".equals(customAnnotation.annotationTypeDeclaration.simpleName)) {
								val optional = customAnnotation.getValue("optional")
								if(optional !== null) {
									setBooleanValue("optional", customAnnotation.getBooleanValue("optional"))
								}
							}

							if("XFWOneToOne".equals(customAnnotation.annotationTypeDeclaration.simpleName)) {
								val mappedBy = customAnnotation.getValue("mappedBy")
								if(mappedBy !== null && !"".equals(mappedBy)) {
									setStringValue("mappedBy", customAnnotation.getStringValue("mappedBy"))
								}
								set("orphanRemoval", customAnnotation.getBooleanValue("orphanRemoval"))
							}
						]
				)
			}
		]
	}

	def processJoinTable(MutableClassDeclaration clazz, extension PersistenceModuleJpa persistContext,
	extension TransformationContext transContext) {

		val fields = clazz.getDeclaredFields().filter [ f |
			f.annotations.exists[annotationTypeDeclaration.simpleName.equals("XFWJoinTable")]
		]
		addJPAJoinTableAnnotation(fields, transContext)
	}

	def addJPAJoinTableAnnotation(Iterable<? extends MutableFieldDeclaration> fields,
	extension TransformationContext transContext) {

		fields.forEach [
			val customAnnotation = annotations.filter [
				annotationTypeDeclaration.simpleName.equals("XFWJoinTable")
			].get(0)

			addAnnotation(
					JoinTable.newAnnotationReference [
						setStringValue("name", customAnnotation.getStringValue("name"))
						setStringValue("catalog", customAnnotation.getStringValue("catalog"))
						setStringValue("schema", customAnnotation.getStringValue("schema"))
						set("joinColumns", customAnnotation.getAnnotationArrayValue("joinColumns"))
						set("inverseJoinColumns", customAnnotation.getAnnotationArrayValue("inverseJoinColumns"))
						set("uniqueConstraints", customAnnotation.getAnnotationArrayValue("uniqueConstraints"))
						set("indexes", customAnnotation.getAnnotationArrayValue("indexes"))
						setAnnotationValue("foreignKey", customAnnotation.getAnnotationValue("foreignKey"))
						setAnnotationValue("inverseForeignKey", customAnnotation.getAnnotationValue("inverseForeignKey"))
					]
			)
		]
	}

}
