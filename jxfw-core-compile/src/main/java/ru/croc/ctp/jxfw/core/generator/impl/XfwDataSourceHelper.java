package ru.croc.ctp.jxfw.core.generator.impl;

import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;

import java.beans.Introspector;
import java.util.List;
import javax.annotation.Nullable;

/**
 * @author Nosov Alexander
 *         on 12.05.15.
 */
public final class XfwDataSourceHelper {

    private static final String SUFFIX_TO_SERVICE = "ToService";

    /**
     * Метод который нужен для того чтобы попытаться получить нужный ТО сервис по возвращаемому типу исходного.
     *
     * @param methodDeclaration - метод на основе которого делается анализ
     * @param <T>               - тип класса описания метода
     * @return NULL если нам не удалось найти имя ТО сервиса
     */
    @Nullable
    public static <T extends MutableMethodDeclaration> String obtainToServiceName(T methodDeclaration) {
        final TypeReference returnType = methodDeclaration.getReturnType();
        final List<TypeReference> typeArguments = returnType.getActualTypeArguments(); // анализ generic типа
        if (typeArguments.size() != 0) {
            final TypeReference genericType = typeArguments.get(0);
            if (!genericType.getName().contains("QTuple")) {
                return Introspector.decapitalize(genericType.getSimpleName()) + SUFFIX_TO_SERVICE;
            }
        } else {
            if (!(returnType instanceof List) && !returnType.getName().contains("QTuple")) {
                return Introspector.decapitalize(returnType.getName() + SUFFIX_TO_SERVICE);
            }
        }
        return null;
    }
}
