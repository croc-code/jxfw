package ru.croc.ctp.jxfw.metamodel;


/**
 * Система типов структурных элементов Доменных объектов.
 *
 * @author OKrutova
 * @since 1.6
 */
public enum XfwValueType {
    UNDEFINED("???"),
    STRING("string"),
    TEXT("text"),
    OBJECT("object"),
    SHORT("i2"),
    INT("i4"),
    LONG("i8"),
    BOOLEAN("boolean"),
    DATE_TIME("dateTime"),
    DATE("date"),
    TIME("time"),
    DOUBLE("double"),
    BYTE("byte"),
    DECIMAL("decimal"),
    BLOB("binary"),
    DURATION("timeSpan"),
    ZONED_DATE_TIME("dateTimeTz"),
    ENUM("enum"),
    UUID("uuid"),
    COMPLEX("complex"),
    BINARY("binary");

    private final String wcCode;

    XfwValueType(String wcCode) {
        this.wcCode = wcCode;
    }

    public String getWcCode() {
        return wcCode;
    }

    public static XfwValueType from(String wcCode) {
        for (XfwValueType xfwValueType : XfwValueType.values()) {
            if (xfwValueType.wcCode.equalsIgnoreCase(wcCode)) {
                return xfwValueType;
            }
        }
        return UNDEFINED; // default;
    }


}

