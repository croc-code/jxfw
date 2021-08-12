package ru.croc.ctp.jxfw.reporting.xslfo.impl.util;

import org.apache.commons.lang3.StringUtils;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractComponentClass;
import ru.croc.ctp.jxfw.reporting.xslfo.types.VarTypesClass;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBElement;


/**
 * Вспомогатеьный статический класс, служит для преобразования типизированных
 * значений параметров из/в формат представления типизированных значений в XML.
 * Created by vsavenkov on 02.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public class Converter {

    /**
     * Представляет минимальное допустимое значение типа System.Decimal. Import from System.Decimal
     */
    public static final BigDecimal DECIMAL_MIN_VALUE = BigDecimal.valueOf(-79228162514264337593543950335D);

    /**
     * Представляет наибольшее возможное значение типа System.Decimal. Import from System.Decimal
     */
    public static final BigDecimal DECIMAL_MAX_VALUE = BigDecimal.valueOf(79228162514264337593543950335D);

    /**
     * Константа для форматирования DATE_PATTERN.
     * TODO: использовалась в toObject. Возможно придётся усложнять код трансформации
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Константа для форматирования time.tz.
     * TODO: использовалась в toObject. Возможно придётся усложнять код трансформации
     */
    public static final String TIME_TZ = "HH:mm:ss.fff";

    /**
     * Константа для форматирования dateTime.tz
     * TODO: использовалась в toObject. Возможно придётся усложнять код трансформации
     */
    public static final String DATE_TIME_TZ = "yyyy-MM-ddTHH:mm:ss.fff";

    /**
     * Преобразование строки в vartypes.
     * @param typeName тип
     * @return VarTypesClass соответсвующий строке
     */
    public static VarTypesClass vartypesFromString(String typeName) {
        return VarTypesClass.fromValue(typeName);
    }

    /**
     * Преобразует типизированное значение из строки, заданной в формате XML
     * к соотв. .NET-объекту.
     * @param from  - Строка со значением
     * @param type  - Имя XML-типа данных.
     *              Поддерживаются следующие типы:
     *                  DATE_PATTERN, dateTime.tz, time.tz, fixed14.4, i4, r8, string, boolean, uuid
     * @return Object - Соотв. типизированное значение
     */
    public static Object toObject(String from, String type) {
        return toObject(from, vartypesFromString(type));
    }

    /**
     * Преобразует типизированное значение из строки, заданной в формате XML
     * к соотв. .NET-объекту.
     * @param from  - Строка со значением
     * @param type  - Тип данных
     * @return Object   - Соотв. типизированное значение
     */
    public static Object toObject(String from, VarTypesClass type) {
        // Строки не преобразуются
        if (type == VarTypesClass.STRING) {
            return from;
        }

        // Если строка пустая то все, кроме строки, преобразуется в null
        if (from == null || from.length() == 0) {
            return null;
        }
        switch (type) {
            case I_4:
                return toInt(from);
            case FIXED_14_4:
                return toDecimal(from);
            case R_8:
                return toDouble(from);
            case DATE:
                return DatatypeConverter.parseDate(from);
            case DATE_TIME_TZ:
                return DatatypeConverter.parseDateTime(from);
            case TIME_TZ:
                return DatatypeConverter.parseTime(from);
            case BOOLEAN:
                return toBoolean(from);
            case UUID:
                return UUID.fromString(from);
            default:
                return from;
        }
    }

    /**
     * Преобразует .NET-объект к строке, заданной в формате XML.
     * @param from  - Значение
     * @param type  - Имя XML-типа данных.
     *              Поддерживаются следующие типы:
     *                  DATE_PATTERN, dateTime.tz, time.tz, fixed14.4, i4, r8, string, boolean, uuid
     * @return String   - Строка в соответствующем формате
     */
    public static String toString(Object from, String type) {
        return toString(from, vartypesFromString(type));
    }

    /**
     * Преобразует .NET-объект к строке, заданной в формате XML.
     * @param from  - Значение
     * @param type  - Тип данных
     * @return String - Строка в соответствующем формате
     */
    public static String toString(Object from, VarTypesClass type) {
        // Для null возвращаем пустую строку
        if (from == null /* TODO: сразу не нашёл, как это в Java перетащить || DBNull.Value == from*/) {
            return StringUtils.EMPTY;
        }

        // Иначе посмотрим что пришло...
        switch (type) {
            case I_4:
                return DatatypeConverter.printInt((int)from);
            case FIXED_14_4:
                return DatatypeConverter.printDecimal(new BigDecimal(from.toString()));
            case R_8:
                return DatatypeConverter.printDouble((double)from);
            case DATE:
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
                return dateFormat.format(from);
            case DATE_TIME_TZ:
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DATE_TIME_TZ);
                return dateTimeFormat.format(from);
            case TIME_TZ:
                SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_TZ);
                return timeFormat.format(from);
            case STRING:
                return (String)from;
            case BOOLEAN:
                return toBoolean(from.toString()) ? "1" : "0";
            case UUID:
                return ((UUID)from).toString();
            default:
                return from.toString();
        }
    }

    /**
     * Преобразует Oбъект к строке, заданной в формате XML.
     * @param from  - Значение, тип определяется как тип значения
     * @return String   - Строка в соответствующем формате
     */
    public static String toString(Object from) {
        if (from == null) {
            return StringUtils.EMPTY;
        }
        switch (from.getClass().getTypeName()) {
            case "Integer":
                return toString(from, VarTypesClass.I_4);
            case "BigDecimal":
                return toString(from, VarTypesClass.FIXED_14_4);
            case "Double":
                return toString(from, VarTypesClass.R_8);
            case "Calendar":
                return toString(from, VarTypesClass.DATE_TIME_TZ);
            case "Boolean":
                return (boolean)from ? "1" : "0";
            case "UUID":
                return toString(from, VarTypesClass.UUID);
            default:
                return from.toString();
        }
    }

    /**
     * Преобразование к int.
     * @param stringFrom строка на входе
     * @return целое преобразованное из строки
     */
    public static int toInt(String stringFrom) {
        return DatatypeConverter.parseInt(stringFrom);
    }

    /**
     * Преобразование к Boolean. Допустимые значения: 0, 1, false, true.
     * @param stringFrom строка на входе
     * @return булево значение
     */
    public static boolean toBoolean(String stringFrom) {
        if (stringFrom == null || stringFrom.length() == 0) {
            throw new NullPointerException("Приведение к Boolean пустой строки невыполнимо.");
        }
        return DatatypeConverter.parseBoolean(stringFrom.toLowerCase());
    }

    /**
     * Преобразование к Boolean. В случае ошибки возвращает значение по умолчанию.
     * @param stringFrom строка на входе
     * @param defaultValue значение по умолчанию
     * @return булево значение
     */
    public static boolean toBoolean(String stringFrom, boolean defaultValue) {
        if (stringFrom == null || stringFrom.length() == 0) {
            return defaultValue;
        }
        try {
            return toBoolean(stringFrom);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Преобразование к Double. Сперва в текущей культуре,
     * а в случае неудачи - с точкой в качестве разделителя.
     * @param stringFrom строка на входе
     * @return значение double
     */
    public static double toDouble(String stringFrom) {
        if (stringFrom == null || stringFrom.length() == 0) {
            throw new NullPointerException("Приведение к Double пустой строки невыполнимо.");
        }
        try {
            double result = Double.parseDouble(stringFrom);
            return result;
        } catch (NumberFormatException e) {
            return DatatypeConverter.parseDouble(stringFrom);
        }
    }

    /**
     * Преобразование к Double. В случае ошибки возвращает значение по умолчанию.
     * @param from          - Строка
     * @param defaultValue  - Значение по умолчанию
     * @return double   - значение double
     */
    public static double toDouble(String from, double defaultValue) {

        if (from == null || from.length() == 0) {
            return defaultValue;
        }
        try {
            return toDouble(from);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Преобразование к Decimal. Сперва в текущей культуре,
     * а в случае неудачи - с точкой в качестве разделителя
     * @param stringFrom строка на входе
     * @return значение BigDecimal
     */
    public static BigDecimal toDecimal(String stringFrom) {
        if (stringFrom == null || stringFrom.length() == 0) {
            throw new NullPointerException("Приведение к Decimal пустой строки невыполнимо.");
        }
        BigDecimal result = new BigDecimal(stringFrom);
        if (null == result) {
            return result;
        } else {
            return DatatypeConverter.parseDecimal(stringFrom);
        }
    }

    /**
     * Преобразование к Decimal. В случае ошибки возвращает значение по умолчанию.
     * @param from          - Строка
     * @param defaultValue  - Значение по умолчанию
     * @return BigDecimal   - возвращает преобразованное значение
     */
    public static BigDecimal toDecimal(String from, BigDecimal defaultValue)  {
        if (from == null || from.length() == 0) {
            return defaultValue;
        }
        try {
            return toDecimal(from);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Функция проверяет на то, что переданное значение не является NULL.
     * @param value - Значение
     * @return true если значение не null
     */
    public static boolean isNull(Object value) {
        return (null == value /* TODO: как бы это преобразовать || DBNull.Value == value */
                || StringUtils.EMPTY.equals(value));
    }

    /**
     * Пребразует список JAXBElement`ов в список типизированных объектов.
     * Используется для приведения списков, полученных из XML-файлов
     * @param list - список JAXBElement`ов
     * @param <T>  - тип элементов в списке
     * @return List&lt;T&gt;  - возвращает список типизированных объектов
     */
    public static <T extends AbstractComponentClass> List<T> jaxbListToTypedList(List<JAXBElement<? extends T>> list) {

        // вытаскиваю типизированные значения
        return list.stream().map(jaxbElement -> jaxbElement.getValue()).collect(Collectors.toList());
    }
}
