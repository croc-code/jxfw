package ru.croc.ctp.jxfw.cass;

import com.google.common.collect.Lists;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.generator.meta.XFWObject;
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XFWObject
@Table
@Accessors
@XFWToString
@SuppressWarnings("all")
public class Respondent implements DomainObject<RespondentKey>, Editable {
  private String caption;
  
  private final static long serialVersionUID = 1L;
  
  @PrimaryKey
  private RespondentKey id;
  
  public RespondentKey getId() {
    return id;
  }
  
  public void setId(final RespondentKey id) {
    this.id = id;
  }
  
  @Transient
  private final static Logger logger = LoggerFactory.getLogger(Respondent.class);
  
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
  
  @Transient
  private Long ts = 1L;
  
  public Long getVersion() {
    return ts;
  }
  
  public void setVersion(final Long ts) {
    this.ts = ts;
  }

  @Override
  public String getNameOfVersionField() {
    return "ts";
  }

  @Transient
  private Map<String, Object> original = new HashMap<>();;
  
  @Override
  public void setPropChangedValues(final Map<String, Object> values) {
    this.original = values;
  }
  
  @Override
  public Map<String, Object> getPropChangedValues() {
    return original;
  }
  
  public List<? extends DomainObject<RespondentKey>> obtainValueByPropertyName(final String name) {
                        return Lists.newArrayList();
  }

  @Override
  public XfwClass getMetadata() {
    return null;
  }

  public String getTypeName() {
    return "Respondent";
  }
  
  @Pure
  public String getCaption() {
    return this.caption;
  }
  
  public void setCaption(final String caption) {
    this.caption = caption;
  }
  
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Respondent");
    builder.append("(id = ").append(getId()).append(")");
    builder.append("[");
    builder.append("caption=").append(caption);
    builder.append(", ");
    builder.append("id=").append(id);
    builder.append(", ");
    builder.append("isNew=").append(isNew);
    builder.append(", ");
    builder.append("isRemoved=").append(isRemoved);
    builder.append(", ");
    builder.append("ts=").append(ts);
    builder.append(", ");
    builder.append("original=").append(original);
    builder.append("]");
    return builder.toString();
  }
}
