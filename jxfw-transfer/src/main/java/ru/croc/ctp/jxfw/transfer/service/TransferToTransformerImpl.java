package ru.croc.ctp.jxfw.transfer.service;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainToProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import javax.annotation.Nonnull;

/**
 * Класс преобразования DomainTo в удобный для XFW2 формат.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class TransferToTransformerImpl implements TransferToTransformer {

    /**
     * @param dto ТО объект по котоорому необходимо собрать XML.
     * @return строка с XML представлением данного объекта.
     */
    @Override
    @Nonnull
    public String toXml(@Nonnull DomainTo dto) {
        final StringBuilder xmlBuilder
                = new StringBuilder("<" + dto.getType() + " oid=\"" + dto.getId() + "\" >");

        dto.forEachProperty((name, val) -> {
            if (val != null) {
                if (isDomainType(dto, name)) {
                    xmlBuilder.append("<").append(name).append(">");

                    if (isCollectionType(val)) {
                        Collection<Serializable> list = (Collection<Serializable>) val;
                        for (Serializable id : list) {
                            appendNavigableProperty(dto, xmlBuilder, name, id);
                        }
                    } else {
                        appendNavigableProperty(dto, xmlBuilder, name, (Serializable) val);
                    }

                    xmlBuilder.append("</").append(name).append(">");
                } else if (isSimpleType(val)) {
                    xmlBuilder.append("<").append(name)
                            .append(" dt:dt=\"").append(getTypeName(val)).append("\">");
                    xmlBuilder.append(formatValue(val));
                    xmlBuilder.append("</").append(name).append(">");
                } else {
                    xmlBuilder.append("<").append(name).append(">");
                    xmlBuilder.append(val);
                    xmlBuilder.append("</").append(name).append(">");
                }
            }
        });

        xmlBuilder.append("</").append(dto.getType()).append(">");
        return xmlBuilder.toString();
    }

    private boolean isDomainType(DomainTo dto, String name) {
        return dto.getPropertyMetadata().get(name).getType() == DomainToProperty.Type.DomainObject;
    }

    private boolean isCollectionType(Object obj) {
        return obj instanceof Collection;
    }

    /**
     * Добавить в XML описание навигируемого объекта.
     *
     * @param dto        ТО объект
     * @param xmlBuilder билдр XML
     * @param name       имя поля
     * @param id         идентификатор
     */
    protected void appendNavigableProperty(@Nonnull DomainTo dto, StringBuilder xmlBuilder, String name,
                                           Serializable id) {
        final String typeName = dto.getPropertyMetadata().get(name).getTypeName();
        xmlBuilder.append("<").append(typeName)
                .append(" oid=\"").append(id).append("\">");
        xmlBuilder.append("</").append(typeName).append(">");
    }

    private boolean isSimpleType(Object obj) {
        return !(obj instanceof Collection);
    }

    private String getTypeName(Object val) {
        switch (val.getClass().getSimpleName()) {
            case "Integer":
                return "i4";
            case "Long":
            case "BigInteger":
                return "i8";
            case "Boolean":
                return "boolean";
            case "LocalDateTime":
                return "dateTime";
            case "LocalDate":
                return "date";
            case "LocalTime":
                return "time";
            case "Double":
                return "double";
            case "BigDecimal":
                return "decimal";
            case "Byte":
                return "byte";
            case "Blob":
                return "binary";
            case "ZonedDateTime":
                return "dateTimeTz";
            case "Duration":
                return "timeSpan";
            case "UUID":
                return "uuid";
            default:
                return val.getClass().getSimpleName().toLowerCase();
        }
    }

    /**
     * Получить правильно отформатированное значение.
     * 
     * @param val значение
     * @return отформатированное значение.
     */
    protected Object formatValue(Object val) {
        switch (val.getClass().getSimpleName()) {
            case "LocalDateTime":
                return ((LocalDateTime) val).format(DateTimeFormatter.ISO_DATE_TIME);
            case "LocalDate":
                return ((LocalDate) val).format(DateTimeFormatter.ISO_DATE);
            case "LocalTime":
                return ((LocalTime) val).format(DateTimeFormatter.ISO_LOCAL_TIME);
            case "ZonedDateTime":
                return ((ZonedDateTime) val).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            default:
                return val;
        }

    }
}
