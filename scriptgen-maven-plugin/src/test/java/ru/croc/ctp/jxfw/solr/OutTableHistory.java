package ru.croc.ctp.jxfw.solr;

import com.google.common.collect.Lists;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.core.domain.meta.XFWPrimaryKey;
import ru.croc.ctp.jxfw.core.generator.meta.XFWToString;

import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SolrDocument(solrCoreName = "out_table_history")
@Accessors
@XFWToString
@SuppressWarnings("all")
public class OutTableHistory implements DomainObject<String>, Editable {
  @Indexed(searchable = true, stored = true, name = "department_id", type = "solr.UUIDField")
  @XFWPrimaryKey(order = 0)
  private UUID departmentId;
  
  @Indexed(searchable = true, stored = true, name = "template_id", type = "solr.UUIDField")
  @XFWPrimaryKey(order = 1)
  private UUID templateId;
  
  @Indexed(searchable = true, stored = true, name = "svod_type")
  @XFWPrimaryKey(order = 2)
  private String svodType;
  
  @Indexed(searchable = true, stored = true, name = "index_code")
  @XFWPrimaryKey(order = 3)
  private String indexCode;
  
  @Indexed(searchable = true, stored = true)
  @XFWPrimaryKey(order = 4)
  private String section;
  
  @Indexed(searchable = true, stored = true)
  private String comment;
  
  private final static long serialVersionUID = 1L;
  
  @Id
  private String _uniqueKey;
  
  public void setId(final String id) {
    _uniqueKey = id;
  }
  
  public String getId() {
    return _uniqueKey;
  }
  
  @Transient
  private final static Logger logger = LoggerFactory.getLogger(OutTableHistory.class);
  
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

  public List<? extends DomainObject<String>> obtainValueByPropertyName(final String name) {
                        return Lists.newArrayList();
  }

  @Override
  public XfwClass getMetadata() {
    return null;
  }

  public String getTypeName() {
    return "OutTableHistory";
  }
  
  @Pure
  public UUID getDepartmentId() {
    return this.departmentId;
  }
  
  public void setDepartmentId(final UUID departmentId) {
    this.departmentId = departmentId;
  }
  
  @Pure
  public UUID getTemplateId() {
    return this.templateId;
  }
  
  public void setTemplateId(final UUID templateId) {
    this.templateId = templateId;
  }
  
  @Pure
  public String getSvodType() {
    return this.svodType;
  }
  
  public void setSvodType(final String svodType) {
    this.svodType = svodType;
  }
  
  @Pure
  public String getIndexCode() {
    return this.indexCode;
  }
  
  public void setIndexCode(final String indexCode) {
    this.indexCode = indexCode;
  }
  
  @Pure
  public String getSection() {
    return this.section;
  }
  
  public void setSection(final String section) {
    this.section = section;
  }
  
  @Pure
  public String getComment() {
    return this.comment;
  }
  
  public void setComment(final String comment) {
    this.comment = comment;
  }

  @Pure
  public String get_uniqueKey() {
    return this._uniqueKey;
  }

  public void set_uniqueKey(final String _uniqueKey) {
    this._uniqueKey = _uniqueKey;
  }

  @Pure
  public static Logger getLogger() {
    return OutTableHistory.logger;
  }

  public void setIsNew(final boolean isNew) {
    this.isNew = isNew;
  }


  

}
