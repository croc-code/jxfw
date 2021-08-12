package ru.croc.ctp.jxfw.core.generator.impl;

import static ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator.REENTRANT_LOCK;

import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.declaration.EnumerationTypeDeclaration;
import org.eclipse.xtend.lib.macro.file.Path;
import org.slf4j.Logger;
import ru.croc.ctp.jxfw.core.generator.AbstractEcoreGenerator;
import ru.croc.ctp.jxfw.core.xtend.logging.LoggerFactory;

import java.util.List;


/**
 * Генератор ecore для перечислений с аннотацией XFWEnum.
 * @since 1.6
 * @author OKrutova
 */
public class EnumEcoreGenerator extends AbstractEcoreGenerator {
    private static Logger logger = LoggerFactory.getLogger(DataSourceEcoreGenerator.class);

    private final List<? extends EnumerationTypeDeclaration> enums;

    /**
     * Конструктор для генератора.
     *
     * @param enums - перечисления
     * @param context - контест
     */
    public EnumEcoreGenerator(List<? extends EnumerationTypeDeclaration> enums, CodeGenerationContext context) {
        super(enums.get(0).getCompilationUnit(), context);
        this.enums = enums;
    }

    @Override
    public void generate() {
        REENTRANT_LOCK.lock();
        try {

            Path projectPath = context.getTargetFolder(compilationUnit.getFilePath());
            java.net.URI projectJavaUri = context.toURI(projectPath);
            logger.debug("CompilationUnit {}", compilationUnit.getFilePath());
            logger.debug("Loading models from " + DataSourceEcoreGenerator.class.getName());
            xfwModel = new XFWModelGenerator(projectJavaUri, logger);

            enums.forEach( enumeration -> findOrCreateEnum(enumeration));


            xfwModel.save();
        } finally {
            REENTRANT_LOCK.unlock();
        }
    }


}