package ru.croc.ctp.jxfw.core.generator.impl;

import static com.google.common.base.Verify.verify;

import com.google.common.base.Verify;
import com.google.common.base.VerifyException;
import java8.util.Optional;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.eclipse.xtend.lib.macro.declaration.AnnotationReference;
import org.eclipse.xtend.lib.macro.declaration.AnnotationTarget;
import org.eclipse.xtend.lib.macro.declaration.AnnotationTypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.ClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.FieldDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

/**
 * Утилитный класс для работы с классами.
 *
 * @author ANosov
 * @since 1.0
 */
public abstract class ClassUtil {

    /**
     * Получить все поля для данного класса и его родителей (если такие есть).
     *
     * @param cls - объект типа {@link Class} для запроса
     * @return Список всех полей {@link FieldDeclaration} (может быть пустым).
     * @throws VerifyException если класс {@code null}
     */
    public static List<FieldDeclaration> getAllFieldsList(ClassDeclaration cls) {
        verify(cls != null, "The class must not be null");

        final List<FieldDeclaration> fields = new ArrayList<>();
        ClassDeclaration currentClass = cls;
        while (currentClass != null) { // пока не дошли до верхушки иерархии
            final List<FieldDeclaration> localFields = getFieldsList(currentClass);
            fields.addAll(localFields);
            final TypeReference extendedClass = currentClass.getExtendedClass();
            currentClass = (ClassDeclaration) (extendedClass != null ? extendedClass.getType() : null);
        }
        return fields;
    }

    /**
     * Возвращает аннотацию из класса
     *
     * @param clazz           - класс в котором смотрим аннотацию
     * @param annotationClass - класс искомой аннотации
     * @return annotation or null
     */
    public static AnnotationReference getAnnotation(AnnotationTarget clazz, Class<?> annotationClass) {
        String annName = annotationClass.getName();
        for (AnnotationReference reference : clazz.getAnnotations()) {
            //полное имя одной из аннотации которыми проаннотирован элемент
            String refName = reference.getAnnotationTypeDeclaration().getQualifiedName();
            if (refName.equals(annName)) {
                return reference;
            }
        }

        return null;
    }

    /**
     * Получить только поля данного класса, не учитывая унаследованные.
     * 
     * @param clazz объект типа {@link Class} для запроса
     * @return список всех полей {@link FieldDeclaration} (может быть пустым)
     * @throws VerifyException если класс {@code null}
     */
    public static List<FieldDeclaration> getFieldsList(@Nonnull TypeDeclaration clazz) {
        Verify.verify(clazz != null, "expected not null reference");
        
        final List<FieldDeclaration> fields = new ArrayList<>();
        final Iterable<? extends FieldDeclaration> declaredFields = clazz.getDeclaredFields();
        for (FieldDeclaration field : declaredFields) {
            // В результат не должны попадать константы
            if (!field.isFinal() && !field.isStatic()) {
                fields.add(field);
            }
        }        
        return fields;
    }
    
    /**
     * Проверка наличия у типа {@link TypeDeclaration} аннотации по имени аннотации.
     *
     * @param type           - тип
     * @param annotationName - имя аннотации
     * @return результат проверки есть/отсуствует
     */
    private static boolean existAnnotation(@Nonnull final TypeDeclaration type, final String annotationName) {
        Verify.verify(type != null, "expected not null reference");

        final Optional<TypeDeclaration> optionalType = Optional.ofNullable(type);
        final List<? extends AnnotationReference> annotations =
                (List<? extends AnnotationReference>) optionalType
                        .map(TypeDeclaration::getAnnotations)
                        .orElseThrow(NullPointerException::new);

        final ArrayList<String> collect = StreamSupport.stream(annotations)
                .map(AnnotationReference::getAnnotationTypeDeclaration)
                .map(AnnotationTypeDeclaration::getSimpleName)
                .filter(annotationName::equals)
                .collect(Collectors.toCollection(ArrayList::new));
        return !collect.isEmpty();
    }
    
    /**
     * Проверка наличия у типа {@link TypeDeclaration} аннотации.
     *
     * @param type           - тип
     * @param annotationClass - искомая аннотация
     * @return результат проверки есть/отсуствует
     */
    public static boolean existAnnotation(@Nonnull final TypeDeclaration type, @Nonnull final Class<?> annotationClass) {
    	return existAnnotation(type, annotationClass.getSimpleName());
    }


    /**
     * Проверяет существование аннотации над классом, которая удовлетворяет условию.
     *
     * @param clazz           класс в котором ищем аннотацию
     * @param annotationClass искомая аннотация
     * @param condition       условие, которому должна удовлетворять найденная аннотация
     * @return true/false
     */
    public static boolean existAnnotation(ClassDeclaration clazz, @Nonnull Class<?> annotationClass, Predicate<AnnotationReference> condition) {
        return existAnnotation(clazz.getAnnotations(), annotationClass, condition);
    }

    /**
     * Поиск аннотации над классом, без ипользования вспомогательных xtend методов.
     *
     * @param clazz           класс в котором ищем аннотацию
     * @param annotationClass искомая аннотация
     * @return true/false
     */
    public static boolean existAnnotation(ClassDeclaration clazz, @Nonnull Class<?> annotationClass) {
        return existAnnotation(clazz.getAnnotations(), annotationClass);
    }

    /**
     * Поиск аннотации над полем, без ипользования вспомогательных xtend методов.
     *
     * @param field           field над которым ищем аннотацию
     * @param annotationClass искомая аннотация
     * @return true/false
     */
    public static boolean existAnnotation(FieldDeclaration field, @Nonnull Class<?> annotationClass) {
        return existAnnotation(field.getAnnotations(), annotationClass);
    }

    private static boolean existAnnotation(Iterable<? extends AnnotationReference> list, @Nonnull Class<?> annotationClass) {
        return existAnnotation(list, annotationClass, annotationReference -> true);
    }

    private static boolean existAnnotation(Iterable<? extends AnnotationReference> list, @Nonnull Class<?> annotationClass, Predicate<AnnotationReference> condition) {
        //полное имя искомой аннотации
        String annName = annotationClass.getName();
        for (AnnotationReference reference : list) {
            //полное имя одной из аннотации которыми проаннотирован элемент
            String refName = reference.getAnnotationTypeDeclaration() != null ? reference.getAnnotationTypeDeclaration().getQualifiedName() : null;
            if (annName.equals(refName)) {
                return condition.test(reference);
            }
        }
        return false;
    }

    /**
     * Поиск аннотации над полем, без ипользования вспомогательных xtend методов.
     *
     * @param field                  field над которым ищем аннотацию
     * @param simpleNameOfAnnotation искомая аннотация - имя
     * @return true/false
     */
    public static AnnotationReference annotationReferenceBySimpleName(
            FieldDeclaration field,
            String simpleNameOfAnnotation
    ) {
        for (AnnotationReference reference : field.getAnnotations()) {
            //полное имя одной из аннотации которыми проаннотирован элемент
            String refName = reference.getAnnotationTypeDeclaration().getSimpleName();
            if (refName.equals(simpleNameOfAnnotation)) {
                return reference;
            }
        }

        return null;
    }
}