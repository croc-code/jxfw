package ru.croc.ctp.jxfw.cass;

import com.google.common.collect.Lists;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject;
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XFWObject
@Table(value = "comment_history")
@Accessors
@XFWToString
@SuppressWarnings("all")
public class CommentHistory implements DomainObject<CommentHistoryKey>, Editable {
    private final static long serialVersionUID = 1L;

    @PrimaryKey
    private CommentHistoryKey id;

    public CommentHistoryKey getId() {
        return id;
    }

    public void setId(final CommentHistoryKey id) {
        this.id = id;
    }

    @Transient
    private final static Logger logger = LoggerFactory.getLogger(CommentHistory.class);

    @Transient
    private boolean isNew = false;

    public boolean isNew() {
        return isNew;
    }

    public void setNew(final boolean isnew) {
        this.isNew = isnew;
    }

    @Transient
    private Boolean isRemoved = false;

    public Boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(final Boolean removed) {
        this.isRemoved = removed;
    }

    @Version
    @Transient
    private Long ts = -1L;

    public Long getVersion() {
        return ts;
    }

    public void setVersion(final Long ts) {
        this.ts = ts;
    }

    public String getNameOfVersionField() {
        return "ts";
    }

    @Transient
    private Map<String, Object> original = new HashMap<>();

    @Override
    public void setPropChangedValues(final Map<String, Object> values) {
        this.original = values;
    }

    @Override
    public Map<String, Object> getPropChangedValues() {
        return original;
    }

    public List<? extends DomainObject<CommentHistoryKey>> obtainValueByPropertyName(final String name) {
        return Lists.newArrayList();
    }

    @Override
    public XfwClass getMetadata() {
        return null;
    }

    public String getTypeName() {
        return "CommentHistory";
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CommentHistory");
        builder.append("(id = ").append(getId()).append(")");
        builder.append("[");
        builder.append("isNew=").append(isNew);
        builder.append(", ");
        builder.append("isRemoved=").append(isRemoved);
        builder.append(", ");
        builder.append("ts=").append(ts);
        builder.append("]");
        return builder.toString();
    }
}
