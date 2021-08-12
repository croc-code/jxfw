package ru.croc.ctp.jxfw.cass;

import com.google.common.collect.Lists;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.generator.meta.XFWBlobInfo;
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject;
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XFWObject
@Table(value = "binary")
@SuppressWarnings("all")
@Accessors
@XFWToString
public class Binary implements DomainObject<BinaryKey>, Editable {
    @XFWBlobInfo(value = true)
    private Blob content;

    private final static long serialVersionUID = 1L;

    @PrimaryKey
    private BinaryKey id;

    public BinaryKey getId() {
        return id;
    }

    public void setId(final BinaryKey id) {
        this.id = id;
    }

    @Transient
    private final static Logger logger = LoggerFactory.getLogger(Binary.class);

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

    public List<? extends DomainObject<BinaryKey>> obtainValueByPropertyName(final String name) {
        return Lists.newArrayList();
    }

    @Override
    public XfwClass getMetadata() {
        return null;
    }

    public String getTypeName() {
        return "Binary";
    }

    private String contentFileName;

    private String contentMimeContentType;

    private Long contentSize;

    @Pure
    public String getContentFileName() {
        return this.contentFileName;
    }

    public void setContentFileName(final String contentFileName) {
        this.contentFileName = contentFileName;
    }

    @Pure
    public String getContentMimeContentType() {
        return this.contentMimeContentType;
    }

    public void setContentMimeContentType(final String contentMimeContentType) {
        this.contentMimeContentType = contentMimeContentType;
    }

    @Pure
    public Long getContentSize() {
        return this.contentSize;
    }

    public void setContentSize(final Long contentSize) {
        this.contentSize = contentSize;
    }

    @Pure
    public Blob getContent() {
        return this.content;
    }

    public void setContent(final Blob content) {
        this.content = content;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Binary");
        builder.append("(id = ").append(getId()).append(")");
        builder.append("[");
        builder.append("content=").append(content);
        builder.append(", ");
        builder.append("isNew=").append(isNew);
        builder.append(", ");
        builder.append("isRemoved=").append(isRemoved);
        builder.append(", ");
        builder.append("ts=").append(ts);
        builder.append(", ");
        builder.append("contentFileName=").append(contentFileName);
        builder.append(", ");
        builder.append("contentMimeContentType=").append(contentMimeContentType);
        builder.append(", ");
        builder.append("contentSize=").append(contentSize);
        builder.append("]");
        return builder.toString();
    }
}
