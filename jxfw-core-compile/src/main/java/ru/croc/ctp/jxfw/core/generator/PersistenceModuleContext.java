package ru.croc.ctp.jxfw.core.generator;

import org.eclipse.xtend.lib.macro.CodeGenerationContext;
import org.eclipse.xtend.lib.macro.RegisterGlobalsContext;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.ValidationContext;
import org.eclipse.xtend.lib.macro.declaration.CompilationUnit;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.services.TypeReferenceProvider;
import ru.croc.ctp.jxfw.core.generator.impl.XFWModelGenerator;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

/**
 * Контекст {@link PersistenceModuleContext},
 * который содержит информацию необходимую для {@link PersistenceModule}.
 */
public class PersistenceModuleContext {

    private final Properties properties;

    private RegisterGlobalsContext registerGlobalsContext;

    private TransformationContext transformationContext;

    private ValidationContext validationContext;

    private final TypeReferenceProvider typeReferenceProvider;

    private List<MutableClassDeclaration> mutableClasses;

    private CodeGenerationContext codeGenerationContext;

    /**
     * EMF-ресурс доменной модели jXFW.
     */
    private XFWModelGenerator modelEmfResource;


    /**
     * Конструктор.
     *
     * @param properties      свойства
     * @param compilationUnit представление текущего xtend файла
     */
    public PersistenceModuleContext(@Nonnull Properties properties,
                                    @Nonnull CompilationUnit compilationUnit) {
        this.properties = properties;

        // Хак для доступа к TypeReferenceProvider. Иначе он недоступен в
        // CodeGenerationContext
        try {
            Method getTypeReferenceProvider =
                    compilationUnit.getClass().getDeclaredMethod("getTypeReferenceProvider");
            this.typeReferenceProvider = (TypeReferenceProvider) getTypeReferenceProvider.invoke(compilationUnit);
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    public Properties getProperties() {
        return properties;
    }

    public void setTransformationContext(TransformationContext transformationContext) {
        this.transformationContext = transformationContext;
    }

    public TransformationContext getTransformationContext() {
        if (transformationContext == null) {
            throwNotSetException("TransformationContext");
        }
        return transformationContext;
    }

    /**
     * Возвращает {@link TypeReferenceProvider}.
     * <p/>
     *
     * @return {@link TypeReferenceProvider}
     */
    public TypeReferenceProvider getTypeReferenceProvider() {
        return typeReferenceProvider;
    }

    public void setMutableClasses(List<MutableClassDeclaration> mutableClasses) {
        if (mutableClasses.size() == 0) {
            throw new IllegalStateException("Parameter mutableClasses should not be empty list");
        }
        this.mutableClasses = mutableClasses;
    }

    public List<MutableClassDeclaration> getMutableClasses() {
        return mutableClasses;
    }

    public void setCodeGenerationContext(CodeGenerationContext codeGenerationContext) {
        this.codeGenerationContext = codeGenerationContext;
    }

    /**
     * Возвращает {@link CodeGenerationContext}.
     * <p/>
     *
     * @return {@link CodeGenerationContext}
     * @throws IllegalStateException если значение не было предварительно установлено
     */
    public CodeGenerationContext getCodeGenerationContext() {
        if (codeGenerationContext == null) {
            throwNotSetException("CodeGenerationContext");
        }
        return codeGenerationContext;
    }

    /**
     * Возвращает EMF-ресурс формируемой ecore-модели.
     * <p/>
     *
     * @throws IllegalStateException если значение не было предварительно установлено
     */
    public XFWModelGenerator getModelEmfResource() {
        if (modelEmfResource == null) {
            throwNotSetException("modelEmfResource");
        }
        return modelEmfResource;
    }

    public void setModelEmfResource(XFWModelGenerator modelEmfResource) {
        this.modelEmfResource = modelEmfResource;
    }

    public ValidationContext getValidationContext() {
        if (validationContext == null) {
            throwNotSetException("ValidationContext");
        }
        return validationContext;
    }

    public void setValidationContext(ValidationContext validationContext) {
        this.validationContext = validationContext;
    }

    public RegisterGlobalsContext getRegisterGlobalsContext() {
        if (registerGlobalsContext == null) {
            throwNotSetException("RegisterGlobalsContext");
        }
        return registerGlobalsContext;
    }

    public void setRegisterGlobalsContext(RegisterGlobalsContext registerGlobalsContext) {
        this.registerGlobalsContext = registerGlobalsContext;
    }

    private void throwNotSetException(String type) {
        String msg = MessageFormat.format("{0} was not set", type);
        throw new IllegalStateException(msg);
    }
}
