package ru.croc.ctp.jxfw.core.domain.impl;

import static java8.lang.Iterables.forEach;

import java8.util.stream.StreamSupport;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.ReflectionUtils;
import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.metamodel.runtime.XfwModelFactory;
import ru.croc.ctp.jxfw.core.store.SelfDirtinessTracker;
import ru.croc.ctp.jxfw.core.validation.meta.XFWReadOnly;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Набор вспомогательных функций для работы с DomainObject.
 *
 * @author Nosov Alexander
 * @since 1.1
 */
public class DomainObjectUtil {
    
    /**
     * 
     * @param type
     * @return
     */
    public static <T extends DomainObject<?>> String getDomainObjectTypeName(Class<T> type) {
        return (String) ReflectionUtils.getField(ReflectionUtils.findField(type, "TYPE_NAME"), null);
    }

    /**
     * 
     * @param typeName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends DomainObject<?>> Class<T> getDomainObjectType(String typeName) {
        XfwModel xfwModel = XfwModelFactory.getInstance();
        return (Class<T>) xfwModel.findThrowing(typeName, XfwClass.class).getInstanceClass();
    }

    /**
     * Проверка режима "Только для чтнеия" для объекта.
     *
     * @param object - проверяемый объект
     * @return класс сущности для которой выполняется проверка
     */
    public static boolean isReadOnlyEntity(DomainObject<?> object) {
        return getReadOnlyAnnotation(object) != null;
    }

    /**
     * Проверка режима "Только для чтнеия" для объекта.
     *
     * @param object - проверяемый объект
     * @return класс сущности для которой выполняется проверка
     */
    public static Annotation getReadOnlyAnnotation(DomainObject<?> object) {
        return getReadOnlyClassAnnotation(object.getClass());
    }

    /**
     * Проверка режима "Только для чтнеия" для объекта и его предков.
     *
     * @param object - проверяемый объект
     * @return являются ли объект или его предки read only
     */
    public static boolean isReadOnlyAnnotationIncludeSuperClasses(DomainObject<?> object) {
        return getReadOnlyAnnotationIncludeSuperClasses(object) != null;
    }

    /**
     * Проверка режима "Только для чтнеия" для объекта и его предков.
     *
     * @param object - проверяемый объект
     * @return класс сущности для которой выполняется проверка
     */
    public static Annotation getReadOnlyAnnotationIncludeSuperClasses(DomainObject<?> object) {
        Class<?> clazz = object.getClass();
        while (!clazz.equals(Object.class)) {
            final Annotation readOnlyAnnotation = getReadOnlyClassAnnotation(clazz);
            if (readOnlyAnnotation != null) {
                return readOnlyAnnotation;
            }
            clazz = clazz.getSuperclass();
        }

        return null;
    }

    /**
     * Проверка режима "Только для чтнеия" для класса.
     *
     * @param clazz - проверяемый класс
     * @return класс сущности для которой выполняется проверка
     */
    private static Annotation getReadOnlyClassAnnotation(Class<?> clazz) {
        final Annotation[] annotations = clazz.getAnnotations();
        // Новый механизм определения
        for (Annotation annotation : annotations) {
            final Class<? extends Annotation> typeDeclaration = annotation.annotationType();
            if (typeDeclaration != null && XFWReadOnly.class.getSimpleName().equals(typeDeclaration.getSimpleName())) {
                return annotation;
            }
        }
        return null;
    }

    /**
     * Загрузка доп данных для объекта. Навигируемое свойство может быть
     * цепочкой свойств, тогда загрузятся все промежуточные объекты в цепочке.
     * Поэтому возвращаемый список может быть разнородным.
     *
     * @param name   - Название навигируемого свойства
     * @param target - объект для которого получают данные
     * @return список доп данныхь
     */
    public static List<DomainObject<?>> loadData(String name, DomainObject<?> target) {
        List<DomainObject<?>> result = new ArrayList<>();
        loadDataRec(result, target, name.split("\\."));
        return distinct(result);
    }

    /**
     * Выбрать только уникальные записи списка.
     * Для сравнения используется equals.
     *
     * @param list - фильтруемый лист
     * @return список уникальных данных с сохранением порядка.
     */
    public static List<DomainObject<?>> distinct(List<DomainObject<?>> list) {
    	return list.stream().distinct().collect(Collectors.toList());
    }

    private static List<DomainObject<?>> loadDataRec(final List<DomainObject<?>> accum, DomainObject<?> target, String... props) {
        if (props.length == 0) {
            return accum;
        }
        final List<? extends DomainObject<?>> l =  target.obtainValueByPropertyName(props[0]);
        l.removeIf(Objects::isNull);
        accum.addAll(l); // merge with accum

        forEach(l, o -> loadDataRec(accum, o, Arrays.copyOfRange(props, 1, props.length)));
        return accum;
    }

    /**
     * Метод для получения списка навигируемых (те переменные которые ссылаются на объекты DomainObject) свойств.
     *
     * @param clazz - класс который необходимо проанализировать
     * @return Список имен свойств
     * @see DomainObject
     */
    public static <T extends DomainObject<?>> List<String> obtainNavigablePropertiesAsString(Class<T> clazz) {
        final List<String> result = new ArrayList<>();
        final Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            final Type type = field.getGenericType();
            final String name = field.getName();

            if (type instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) type;
                if (DomainObject.class.isAssignableFrom((Class<?>) parameterizedType.getActualTypeArguments()[0])) {
                    result.add(name);
                }
            } else {
                if (DomainObject.class.isAssignableFrom((Class<?>) type)) {
                    result.add(name);
                }
            }
        }
        return result;
    }
    
    /**
     * Копировать состояние доменного объекта для всех сериализуемых свойст, 
     * исключение состоявляют бинарные свойства.
     * @param domainObject - исходный доменный объект
     * @return - копия
     */
    public static <T extends SelfDirtinessTracker> T copyDomainObjectState(T domainObject) {
        //Map<DomainObject<?>, Map<String, Object>> blobValues = new HashMap<>();
        //collectAllBlobs(domainObject, blobValues);
        
        T result = SerializationUtils.clone(domainObject);
        
        //setAllBlobs(blobValues);
        return result;        
    }

    public static DomainObject<?> getDomainObjectStub() {
        return (DomainObject<?>) Proxy.newProxyInstance(DomainObjectUtil.class.getClassLoader(),
                new Class<?>[] { DomainObject.class }, (proxy, method, args) -> null);
    }

    @SuppressWarnings("unused")
	private static void collectAllBlobs(DomainObject<?> domainObject, Map<DomainObject<?>, Map<String, Object>> blobValues) {
        if (blobValues.get(domainObject) != null) {
            return;
        }

        blobValues.put(domainObject, new HashMap<>());
        StreamSupport.stream(domainObject.getMetadata().getEAllAttributes()).forEach(attribute -> {
            if (Blob.class.equals(attribute.getEAttributeType().getInstanceClass())) {
                String attr = attribute.getName();
                blobValues.get(domainObject).put(attr, getValueAndSetNull(domainObject, attr));
            }
        });
        
        
        List<String> references = domainObject.getMetadata().getEAllReferences().stream().map(ref -> {
            return ref.getName();
        }).collect(Collectors.toList());
        
        for (String ref : references) {//((SelfDirtinessTracker) domainObject).getDirtyAttributes()) {
            List<? extends Serializable> values = ((SelfDirtinessTracker) domainObject).getCurrentValue(ref);
            if (values.isEmpty()) {
                /*if ((Blob.class).equals(BeanUtils.getPropertyDescriptor(domainObject.getClass(), attr).getPropertyType())) {
                    blobValues.get(domainObject).put(attr, getValueAndSetNull(domainObject, attr));
                }*/
            } else {
                for (Serializable value : values) {
                    if (value instanceof DomainObject<?>) {
                        collectAllBlobs((DomainObject<?>) value, blobValues);
                    }
                }
            }
        }
    }
    
    private static Object getValueAndSetNull(Object obj, String attr) {
        Object result = null;
        try {
            Field f = ReflectionUtils.findField(obj.getClass(), attr);
            f.setAccessible(true);
            result = f.get(obj);
            f.set(obj, null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    
    @SuppressWarnings("unused")
	private static void setAllBlobs(Map<DomainObject<?>, Map<String, Object>> blobValues) {
        for(DomainObject<?> domainObject: blobValues.keySet()) {
            for (String attribute: blobValues.get(domainObject).keySet()) {
                setValue(domainObject, attribute, blobValues.get(domainObject).get(attribute));
            }
        }
      }

    private static void setValue(DomainObject<?> domainObject, String attr, Object object) {
        try {
            Field f = ReflectionUtils.findField(domainObject.getClass(), attr);
            f.setAccessible(true);
            f.set(domainObject, object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
