package ru.croc.ctp.jxfw.core.domain.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId;
import ru.croc.ctp.jxfw.core.domain.meta.XFWEnumerated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Класс для работы с Enum с специфичными для jXFW свойствами
 *
 * @author Nosov Alexander
 *         on 15.05.15.
 */
public final class EnumUtil {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(EnumUtil.class);

    /**
     * Преобразование из числового значения, заданного в аннотации к значению {@link Enum} в его значение.
     *
     * @param <T>       тип возвращаемого значения
     * @param enumClass класс {@link Enum}, значение которого возвращается
     * @param value     числовое значение, указанное в аннотации искомого значения
     * @return константа заданная в указанном классе, проаннатированная указанным значением
     * @throws IllegalArgumentException в случае, если значения константы с таким значением не задано
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumClass, int value) {
        for (final T item : enumClass.getEnumConstants()) {
            if (EnumUtil.findAnnotation(item)
                    .filter(xfwEnumId -> xfwEnumId.value() == value)
                    .isPresent()) {
                @SuppressWarnings("null")
                @Nonnull
                final T result = item;
                return result;
            }
        }

        throw new IllegalArgumentException(
                MessageFormat.format("No enum ({0}) constant found for value {1}", enumClass.getName(), value));
    }

    /**
     * Получить значение аннотации XFWEnumId
     *
     * @param enumeration - экземпляр типа Enum
     * @return значение value
     * @throws IllegalStateException - в случае если у enum нет аннотации XFWEnumId
     * @see ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId
     */
    public static int getEnumValue(Enum<?> enumeration) {
        final Optional<XFWEnumId> annotation = findAnnotation(enumeration);
        return annotation.orElseThrow(() ->
                new IllegalStateException("Enum " + enumeration + " not contains annotation XFWEnumId")).value();
    }

    /**
     * Поиск аннотации XFWEnumId в экземпляре энумератора
     *
     * @param enumeration - экземпляр типа Enum
     * @return - объект-аннотация для данного типа
     * @see ru.croc.ctp.jxfw.core.domain.meta.XFWEnumId
     */
    public static Optional<XFWEnumId> findAnnotation(Enum<?> enumeration) {
        String name = enumeration.name(); // Enum method to get name of presented enum constant
        Annotation[] annotations; // Classical reflection technique
        try {
            annotations = enumeration.getClass().getField(name).getAnnotations();
        } catch (NoSuchFieldException nsfe) {
            throw new RuntimeException(nsfe.getMessage(), nsfe);
        }
        return Arrays.stream(annotations)
                .filter(annotation -> annotation instanceof XFWEnumId)
                .map(annotation -> (XFWEnumId) annotation)
                .findFirst();
    }

    /**
     * Переводит значение флагов в коллекцию значений перечислимого типа.
     *
     * @param clazz      Класс, значение поля которого будет обрабатываться
     * @param fieldName  Поле, значение которого будет обрабатываться
     * @param flagsValue Значение флагов
     * @return Коллекция значений перечислимого типа, соответствующая переданным флагам
     */
    public static List<Enum<?>> parseFlags(final Class<?> clazz, String fieldName, final int flagsValue) {
        final XFWEnumerated annotation = getXfwEnumeratedAnnotationForField(clazz, fieldName);
        final Class<? extends Enum<?>> enumClass = annotation.value();
        final List<Enum<?>> enums = Arrays.asList(enumClass.getEnumConstants());
        final List<Enum<?>> result = new ArrayList<>();
        for (Enum<?> en : enums) {
            final int value = getEnumValue(en);
            if ((flagsValue & value) == value) {
                result.add(en);
            }
        }
        return result;
    }

    /**
     * Получение аннотации {@link XFWEnumerated} для поля класса.
     *
     * @param clazz     Класс, содержащий передаваемое поле
     * @param fieldName Поле, аннотацию которого нужно получить
     * @return Аннотация {@link XFWEnumerated}
     */
    public static XFWEnumerated getXfwEnumeratedAnnotationForField(final Class<?> clazz, String fieldName) {
        final Field field;
        final XFWEnumerated annotation;
        try {
            field = clazz.getDeclaredField(fieldName);
            annotation = field.getAnnotation(XFWEnumerated.class);
            return annotation;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (NullPointerException npe) {
            throw new RuntimeException("The field " + fieldName + " of class "
                    + clazz.getSimpleName() + " not annotated XFWEnumerated", npe);
        }
    }

    /**
     * Метод конструктор для преобразования Enum -> Integer.
     *
     * @param enumeration - экземпляр enum
     * @return - конструкотор DSL
     * @see EnumDslDelegate
     */
    public static EnumDslDelegate bitmaskOf(Enum<?> enumeration) {
        return EnumDslDelegate.bitmaskOf(enumeration);
    }

    /**
     * метод позволяет получить множество вариантов (комбинаций перечислений) которые могут содержать значение val.
     *
     * @param arr список значений перечислений
     * @param val значение
     * @return множество вариантов которые могут содержать значение val
     */
    public static Set<Integer> getPossibleValuesContainingCombinatios(List<Integer> arr, int val) {
        Set<Integer> result = new HashSet<>();
        while (arr.size() > 0) {
            for (Integer a : arr) {
                for (Integer b : arr) {
                    int actual = (a | b);
                    if (actual >= val) {
                        if (EnumUtil.isValueContained(actual, val)) {
                            result.add(actual);
                        }
                    }
                    a = actual;
                }
            }

            arr = arr.subList(1, arr.size());

        }
        return result;
    }

    /**
     * Получить булевый результат сравнения, содржится ли число в маске.
     *
     * @param actual Маска
     * @param val    Число
     * @return Результат сравнения
     */
    public static boolean isValueContained(int actual, int val) {
        return (actual & val) == val;
    }

    /**
     * Вспомогательный класс для преобразования Enum -> Integer.
     */
    @SuppressWarnings("WeakerAccess")
    public static final class EnumDslDelegate {
        private int result;

        private EnumDslDelegate(Enum<?> enumeration) {
            result = EnumUtil.getEnumValue(enumeration);
        }

        /**
         * Создает и возвращает экземпляр класса.
         *
         * @param enumeration Перечисление
         * @return Экземпляр {@link EnumDslDelegate}
         */
        static EnumDslDelegate bitmaskOf(Enum<?> enumeration) {
            return new EnumDslDelegate(enumeration);
        }

        /**
         * Устанавливает бит, соответсвующий переданному значению.
         *
         * @param enumeration Значение перечисления
         * @return Экзмепляр {@link EnumDslDelegate} с установленным битом
         */
        public EnumDslDelegate and(Enum<?> enumeration) {
            this.result |= EnumUtil.getEnumValue(enumeration);
            return this;
        }

        /**
         * Возвращает битовую маску.
         *
         * @return Битовая маска
         */
        public int toInt() {
            return result;
        }
    }
}
