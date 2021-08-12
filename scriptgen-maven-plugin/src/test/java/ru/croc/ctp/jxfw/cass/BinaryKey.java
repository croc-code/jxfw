package ru.croc.ctp.jxfw.cass;

import org.eclipse.xtext.xbase.lib.Pure;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@PrimaryKeyClass
@SuppressWarnings("all")
public class BinaryKey implements Serializable {
    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED, name = "object_id")
    private String objectId;

    public static BinaryKey fromStringArray(final String[] arr) {
        BinaryKey key = new BinaryKey();
        key.objectId = arr[0];
        return key;
    }

    public String[] toStringArray() {
        String[] strings = new String[1];
        strings[0] = "" + objectId;
        return strings;
    }

    @Pure
    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(final String objectId) {
        this.objectId = objectId;
    }
}
