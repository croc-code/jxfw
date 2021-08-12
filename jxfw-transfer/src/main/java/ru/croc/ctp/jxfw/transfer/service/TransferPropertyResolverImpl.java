package ru.croc.ctp.jxfw.transfer.service;

import static java.lang.Short.parseShort;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.impl.BinaryPropertyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Имплементация интерфейса {@link TransferPropertyResolver}.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
public class TransferPropertyResolverImpl implements TransferPropertyResolver {

    @Override
    public void addTypedValue(@Nonnull DomainTo dto, String key, String type, @Nullable Object value) {
        switch (type) {
            case "date":
                if (isEmpty(value)) {
                    dto.addProperty(key, null);
                } else if (!value.toString().contains("T")) {
                    dto.addProperty(key, value.toString() + "T00:00:00.000");
                } else {
                    dto.addProperty(key, value.toString());
                }
                break;
            case "i8":
                if (isEmpty(value)) {
                    dto.addProperty(key, 0L);
                } else {
                    final long longValue = Long.parseLong(value.toString());
                    dto.addProperty(key, longValue);
                }
                break;
            case "i4":
                if (isEmpty(value)) {
                    dto.addProperty(key, 0);
                } else {
                    final int intValue = Integer.parseInt(value.toString());
                    dto.addProperty(key, intValue);
                }
                break;
            case "i2":
                if (isEmpty(value)) {
                    dto.addProperty(key, 0);
                } else {
                    final short shortValue = parseShort(value.toString());
                    dto.addProperty(key, shortValue);
                }
                break;
            case "boolean":
                if (isEmpty(value)) {
                    dto.addProperty(key, null);
                } else {
                    dto.addProperty(key, Boolean.valueOf(value.toString()));
                }
                break;
            case "double":
                if (isEmpty(value)) {
                    dto.addProperty(key, 0.0d);
                } else {
                    dto.addProperty(key, Double.valueOf(value.toString()));
                }

                break;
            case "byte":
                if (isEmpty(value)) {
                    dto.addProperty(key, 0);
                } else {
                    dto.addProperty(key, Byte.valueOf(value.toString()));
                }
                break;
            case "bin.base64":
                final Map<String, Object> data = new HashMap<>();
                data.put(BinaryPropertyService.HeaderName.VALUE, value);
                dto.addProperty(key, data);
                break;
            default:
                dto.addProperty(key, value == null ? "" : value.toString());
                break;
        }
    }

    /** Проверяет является ли значение null или пустой строкой.
     * @param value проверяемое значение.
     * @return true, если null или пустая строка. Иначе false.
     */
    private static boolean isEmpty(Object value) {
        return value == null || (value instanceof String && ((String) value).isEmpty());
    }

    @Override
    public void addListValue(@Nonnull DomainTo dto, String key, @Nullable List<?> value) {
        if (value == null) {
            dto.addProperty(key, new ArrayList<>());
        } else {
            dto.addProperty(key, value);
        }
    }

}
