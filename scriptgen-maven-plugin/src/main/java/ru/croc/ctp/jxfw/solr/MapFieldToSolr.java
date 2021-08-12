package ru.croc.ctp.jxfw.solr;

import java.lang.reflect.Field;

/**
 * Информация о маппинге на типы Solr.
 *
 * @author SMufazzalov
 * @since 1.4
 */
public class MapFieldToSolr {
    /**
     * Дефолтовый маппинг.
     *
     * @param field филд доменного объекта
     * @return строка qualifiedName у типа Solr
     */
    public static String toSolrType(Field field) {
        String typeSimpleName = field.getType().getSimpleName();
        switch (typeSimpleName) {
            case "String":
                return "org.apache.solr.schema.TextField";

            case "Integer":
                return "org.apache.solr.schema.TrieIntField";
            case "int":
                return "org.apache.solr.schema.TrieIntField";

            case "Long":
                return "org.apache.solr.schema.TrieLongField";
            case "long":
                return "org.apache.solr.schema.TrieLongField";

            case "Double":
                return "org.apache.solr.schema.TrieFloatField";
            case "double":
                return "org.apache.solr.schema.TrieFloatField";

            case "Boolean":
                return "org.apache.solr.schema.BoolField";
            case "boolean":
                return "org.apache.solr.schema.BoolField";

            case "UUID":
                return "org.apache.solr.schema.UUIDField";
            case "Date":
                return "org.apache.solr.schema.TrieDateField";
            case "LocalDate":
                return "org.apache.solr.schema.TrieDateField";
            case "LocalDateTime":
                return "org.apache.solr.schema.TrieDateField";
            case "ZonedDateTime":
                return "org.apache.solr.schema.TrieDateField";

            default:
                throw new RuntimeException("Not mapped fieldType to Solr while schema generation : "
                        + typeSimpleName
                        + " "
                        + " try Specifying type in @XFWSearchField#type");
        }

    }
}
