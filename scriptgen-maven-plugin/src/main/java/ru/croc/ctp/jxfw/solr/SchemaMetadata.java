package ru.croc.ctp.jxfw.solr;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey;
import ru.croc.ctp.jxfw.core.generator.Constants;
import ru.croc.ctp.jxfw.core.generator.impl.GeneratorHelper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//TODO типы
//TODO маппинги

/**
 * Контекст шаблона solr-schema.vm.
 *
 * @author SMufazzalov
 * @since 1.4
 */
public class SchemaMetadata {
    //только поля таблицы (не пропускаются системные)
    private Predicate<Field> fieldFilter = (field) ->
            !GeneratorHelper.systemFields.contains(field.getName()) && !field.getName().equals("id")
                    && !field.getName().equals(Constants.SERIAL_VERSION_UID_LABEL);
    private Class<?> clazz;


    /**
     * Коснтруктор.
     * @param solrDomain доменный объект с аннотацией {@link SolrDocument}.
     */
    public SchemaMetadata(Class<?> solrDomain) {
        this.clazz = solrDomain;
    }

    /**
     * Имя ядра.
     *
     * @return имя
     */
    public String name() {

        if (clazz.isAnnotationPresent(SolrDocument.class)) {
            SolrDocument annotation = clazz.getAnnotation(SolrDocument.class);
            String solrCoreName = annotation.solrCoreName();
            if (StringUtils.isNotEmpty(solrCoreName)) {
                return solrCoreName;
            }
        }

        return clazz.getSimpleName();
    }

    /**
     * Метадата полей.
     *
     * @return список
     */
    public List<FieldInfo> fields() {
        Field[] declaredFields = clazz.getDeclaredFields();

        return Arrays
                .stream(declaredFields)
                .filter(fieldFilter)
                .map(f -> new FieldInfo(f))
                .collect(Collectors.toList());
    }

    /**
     * Типы полей в solr.
     *
     * @return список
     */
    public List<TypeInfo> types() {
        Map<String, Field> uniqTypesMap = new HashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();

        List<Field> fields = Arrays
                .stream(declaredFields)
                .filter(fieldFilter)
                .collect(Collectors.toList());

        fields.stream().forEach(f -> {
            uniqTypesMap.put(f.getType().getSimpleName(), f);
        });

        return uniqTypesMap.values().stream().map(f -> new TypeInfo(f)).collect(Collectors.toList());
    }

    /**
     * Ключ.
     *
     * @return строка
     */
    public String uniqueKey() {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> list = Arrays
                .stream(declaredFields)
                .filter(f -> f.isAnnotationPresent(XFWPrimaryKey.class))
                .collect(Collectors.toList());

        String result = list.stream().sorted((f1, f2) ->
                Integer
                        .compare(f1.getAnnotation(XFWPrimaryKey.class).order(),
                                f2.getAnnotation(XFWPrimaryKey.class).order())
        ).map(f -> new FieldInfo(f).name())
                .collect(Collectors.joining(", ", "(", ")"));

        return result;
    }

    /**
     * Информация о типе.
     */
    public class TypeInfo {
        private Field field;

        /**
         * Конструктор.
         *
         * @param field поле
         */
        public TypeInfo(Field field) {
            this.field = field;
        }

        /**
         * Имя поля в solr schema.
         * @return имя поля
         */
        public String name() {
            String[] split = solrType().split("\\.");
            return split[split.length - 1];
        }

        /**
         * Тип в solr schema.
         * @return тип
         */
        public String solrType() {
            if (field.isAnnotationPresent(Indexed.class)) {
                Indexed ann = field.getAnnotation(Indexed.class);
                String type = ann.type();
                if (StringUtils.isNotEmpty(type)) {
                    return type;
                }
            }
            return MapFieldToSolr.toSolrType(field);
        }
    }

    /**
     * Метадата поля.
     */
    public class FieldInfo {
        private Field field;

        /**
         * Конструктор.
         *
         * @param field поле
         */
        public FieldInfo(Field field) {
            this.field = field;
        }

        /**
         * Имя филда схемы.
         *
         * @return имя
         */
        public String name() {
            if (field.isAnnotationPresent(Indexed.class)) {
                Indexed ann = field.getAnnotation(Indexed.class);
                String value = ann.name();
                if (StringUtils.isNotEmpty(value)) {
                    return value;
                }
            }

            return field.getName();
        }

        /**
         * Тип поля ядра.
         *
         * @return тип
         */
        public String type() {
            return new TypeInfo(field).name();
        }

        /**
         * Доступно для фильтрации. {@link Indexed#searchable()}
         *
         * @return да/нет
         */
        public String indexed() {
            if (field.isAnnotationPresent(Indexed.class)) {
                Indexed ann = field.getAnnotation(Indexed.class);
                return Boolean.valueOf(ann.searchable()).toString();
            }

            return Boolean.TRUE.toString();
        }

        /**
         * Доступно для чтения (noSql). {@link Indexed#stored()}
         *
         * @return да/нет
         */
        public String stored() {
            if (field.isAnnotationPresent(Indexed.class)) {
                Indexed ann = field.getAnnotation(Indexed.class);
                return Boolean.valueOf(ann.stored()).toString();
            }

            return Boolean.TRUE.toString();
        }
    }
}
