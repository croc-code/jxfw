package ru.croc.ctp.jxfw.cass;

import org.eclipse.xtext.xbase.lib.Pure;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@PrimaryKeyClass
@SuppressWarnings("all")
public class CommentHistoryKey implements Serializable {
    @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED, name = "template_id")
    private String templateId;

    @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.CLUSTERED, name = "comment_date_zoned")
    private ZonedDateTime commentDateZoned;

    @PrimaryKeyColumn(ordinal = 3, type = PrimaryKeyType.CLUSTERED, name = "comment_date_local")
    private LocalDateTime commentDateLocal;

    public static CommentHistoryKey fromStringArray(final String[] arr) {
        CommentHistoryKey key = new CommentHistoryKey();
        key.templateId = arr[0];
        key.commentDateZoned = ZonedDateTime.parse(arr[1], DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        key.commentDateLocal = LocalDateTime.parse(arr[2], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return key;
    }

    public String[] toStringArray() {
        String[] strings = new String[3];
        strings[0] = "" + templateId;
        strings[1] = "" + commentDateZoned;
        strings[2] = "" + commentDateLocal;
        return strings;
    }

    @Pure
    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(final String templateId) {
        this.templateId = templateId;
    }

    @Pure
    public ZonedDateTime getCommentDateZoned() {
        return this.commentDateZoned;
    }

    public void setCommentDateZoned(final ZonedDateTime commentDateZoned) {
        this.commentDateZoned = commentDateZoned;
    }

    @Pure
    public LocalDateTime getCommentDateLocal() {
        return this.commentDateLocal;
    }

    public void setCommentDateLocal(final LocalDateTime commentDateLocal) {
        this.commentDateLocal = commentDateLocal;
    }
}
