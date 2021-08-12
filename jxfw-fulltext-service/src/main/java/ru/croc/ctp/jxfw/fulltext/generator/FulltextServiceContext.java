package ru.croc.ctp.jxfw.fulltext.generator;

import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.ValidationContext;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * Контескт, содержит необходимые контексты для каждого этапа кодогенерации
 *
 * @author SMufazzalov
 * @since 1.4
 */
public class FulltextServiceContext {
    private Properties properties;

    private RegisterGlobalsContext registerGlobalsContext;

    private TransformationContext transformationContext;

    private ValidationContext validationContext;

    private CodeGenerationContext codeGenerationContext;

    /**
     * Проперти.
     * @return xtend.properties
     */
    public Properties getProperties() {
        if (properties == null) {
            throwNotSetException("Properties");
        }
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * RegisterGlobalsContext.
     * @return registerGlobalsContext
     */
    public RegisterGlobalsContext getRegisterGlobalsContext() {
        if (registerGlobalsContext == null) {
            throwNotSetException("RegisterGlobalsContext");
        }
        return registerGlobalsContext;
    }

    public void setRegisterGlobalsContext(RegisterGlobalsContext registerGlobalsContext) {
        this.registerGlobalsContext = registerGlobalsContext;
    }

    /**
     * TransformationContext.
     * @return transformationContext
     */
    public TransformationContext getTransformationContext() {
        if (transformationContext == null) {
            throwNotSetException("TransformationContext");
        }
        return transformationContext;
    }

    public void setTransformationContext(TransformationContext transformationContext) {
        this.transformationContext = transformationContext;
    }

    /**
     * ValidationContext.
     * @return validationContext
     */
    public ValidationContext getValidationContext() {
        if (validationContext == null) {
            throwNotSetException("ValidationContext");
        }
        return validationContext;
    }

    public void setValidationContext(ValidationContext validationContext) {
        this.validationContext = validationContext;
    }

    /**
     * CodeGenerationContext.
     * @return codeGenerationContext
     */
    public CodeGenerationContext getCodeGenerationContext() {
        if (codeGenerationContext == null) {
            throwNotSetException("CodeGenerationContext");
        }
        return codeGenerationContext;
    }

    public void setCodeGenerationContext(CodeGenerationContext codeGenerationContext) {
        this.codeGenerationContext = codeGenerationContext;
    }

    private void throwNotSetException(String type) {
        String msg = MessageFormat.format("{0} was not set", type);
        throw new IllegalStateException(msg);
    }
}
