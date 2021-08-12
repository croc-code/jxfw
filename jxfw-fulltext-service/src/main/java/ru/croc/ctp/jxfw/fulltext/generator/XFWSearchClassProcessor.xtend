package ru.croc.ctp.jxfw.fulltext.generator

import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.RegisterGlobalsParticipant
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration
import org.eclipse.xtend.lib.macro.ValidationParticipant
import org.eclipse.xtend.lib.macro.CodeGenerationParticipant
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration
import java.util.List
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.ValidationContext
import org.eclipse.xtend.lib.macro.CodeGenerationContext
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory
import org.slf4j.Logger
import java.util.Properties
import ru.croc.ctp.jxfw.fulltext.generator.solr.FulltextServiceSolr

import static ru.croc.ctp.jxfw.core.generator.Constants.*

import static extension ru.croc.ctp.jxfw.core.generator.impl.DomainClassCompileUtil.*
import static extension ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper.*


/**
 * @author SMufazzalov
 * @since 1.4
 */
class XFWSearchClassProcessor implements TransformationParticipant<MutableClassDeclaration>,
RegisterGlobalsParticipant<ClassDeclaration>,
ValidationParticipant<ClassDeclaration>,
CodeGenerationParticipant<TypeDeclaration> {

    private static final Logger logger = LoggerFactory.getLogger(XFWSearchClassProcessor);

    static val FTS = "fulltext service"
    val ctx = new FulltextServiceContext

    //TBD: определять реализацию FulltextService (когда будет полнотекстовый поиск кроме solr)
    var service = new FulltextServiceSolr(ctx)

    override doRegisterGlobals(List<? extends ClassDeclaration> elements, extension RegisterGlobalsContext context) {
        logger.debug(FTS +" doRegisterGlobals started, number of ClassDeclarations: " + elements.size)

        ctx.registerGlobalsContext = context

        //из xtend.properties
        ctx.properties = loadProperties(elements.get(0).compilationUnit, context)

        //регистрация необходимых классов, для следующих этапов генерации (сервисы, репозитории..)
        service.register(elements)
    }

    override doTransform(List<? extends MutableClassDeclaration> elements, extension TransformationContext context) {
        logger.debug(FTS +" doTransform started")

        ctx.transformationContext = context
        //заполнение классов необходимыми полями и аннотациями
        service.transform(elements);
    }

    override doValidate(List<? extends ClassDeclaration> elements, extension ValidationContext context) {
        logger.debug(FTS +" doValidate started")

        ctx.validationContext = context

        elements.forEach[clazz |
            checkClassPackage(clazz,context)
            clazz.checkCoreFields(context)
            clazz.checkLabels(context)
            clazz.checkProtectedType(context)
            clazz.checkNullable(context)
        ]

    }

    override doGenerateCode(List<? extends TypeDeclaration> elements, extension CodeGenerationContext context) {
        logger.debug(FTS +" doGenerateCode started")

        ctx.codeGenerationContext = context

        if(elements == null || elements.size == 0) return;

        //ecore, скрипты
        service.generate(elements)
    }


}