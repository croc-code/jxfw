package ru.croc.ctp.jxfw.facade;

import static com.squareup.javapoet.ClassName.get;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainObjectFactory;
import ru.croc.ctp.jxfw.core.facade.webclient.TransientDomainObjectFactory;
import ru.croc.ctp.jxfw.metamodel.XFWClass;

import java.util.Set;

/**
 * Генерация ТО сервисов для временных (на уровне сохранения) сущностей.
 *
 * @author Nosov Alexander
 */
public class TransientXfwObjectToGenerator<K extends TypeName> extends ToServiceGenerator<K> {

    /**
     * Конструктор генератора TO-сервисов.
     *
     * @param xfwClass    Класс доменного объекта из метамодели
     * @param xfwClasses  Все классы модели
     * @param storageType Тип хранилища
     * @param keyType     Тип идентификатора доменного объекта
     */
    public TransientXfwObjectToGenerator(XFWClass xfwClass, Set<XFWClass> xfwClasses, String storageType,
                                         K keyType) {
        super(xfwClass, xfwClasses, storageType, keyType);
    }

    @Override
    protected MethodSpec.Builder bodyGetByIdMethod(MethodSpec.Builder builder) {
        return unsupportedOperationBody(builder);
    }

    @Override
    protected MethodSpec.Builder bodyGetByIdWithPreloadsMethod(MethodSpec.Builder builder) {
        return unsupportedOperationBody(builder);
    }


    @Override
    protected MethodSpec.Builder bodyDeleteMethod(MethodSpec.Builder builder) {
        return unsupportedOperationBody(builder);
    }

    /*
     Новый объект создается без использования доменных сервисов,
     т.к. для временных объектов доменные сервисы не генерируются.
     */
    @Override
    protected MethodSpec.Builder bodyCreateNewDoMethod(MethodSpec.Builder builder) {
        return builder
                .addStatement("$1T o = new $1T()", jxfwDomainType)
                .addStatement("o.setId(key)")
                .addStatement("o.setNew(true)")
                .addStatement("return o");
    }

    @Override
    protected MethodSpec.Builder bodyGetDObyIdMethod(MethodSpec.Builder builder) {
        return unsupportedOperationBody(builder);
    }

    @Override
    protected MethodSpec.Builder bodyParsePropValueMethod(MethodSpec.Builder builder) {
        return unsupportedOperationBody(builder);
    }

    private MethodSpec.Builder unsupportedOperationBody(MethodSpec.Builder builder) {
        return builder.addStatement("throw new $T(\"This operation is not allowed for transient entity\")",
                get(UnsupportedOperationException.class));
    }

    @Override
    protected MethodSpec.Builder bodyFromToMethod(MethodSpec.Builder builder) {
        if (xfwClass.isAbstract()) {
            return unsupportedOperationBody(builder);
        }
        return builder.addStatement("$1T factory = new $1T(context, domainToServicesResolver)",
                ClassName.get(TransientDomainObjectFactory.class))
                .addStatement("return fromTo(factory.create(this, dto), dto, context)");
    }

    @Override
    protected MethodSpec.Builder bodyFromToWithDomainMethod(MethodSpec.Builder builder) {
        if (xfwClass.isAbstract()) {
            return unsupportedOperationBody(builder);
        }
        builder.addStatement("assert(dto.getType().equals(\"$T\"))", jxfwDomainType)
                .addStatement("$1T factory = new $1T(context, domainToServicesResolver)",
                        ClassName.get(DomainObjectFactory.class));

        fromToFieldsForFacade(builder);
        builder.addStatement("o.setPropChangedValues(dto.getOriginal())");
        specificFromToEnhancement(builder);
        builder.addStatement("return o");
        return builder;
    }

    /**
     * Временные объекты не сохраняются, следовательно не нужна синхронизация связей со стороны "не владельца".
     */
    protected boolean enableSynchronizeRelations() {
        return false;
    }

    @Override
    protected void autowireServiceBySetter() {

    }


}
