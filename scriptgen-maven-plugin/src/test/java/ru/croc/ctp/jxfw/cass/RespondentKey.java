package ru.croc.ctp.jxfw.cass;

import org.eclipse.xtext.xbase.lib.Pure;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@PrimaryKeyClass
@SuppressWarnings("all")
public class RespondentKey implements Serializable {
  @PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED, name = "depId")
  private UUID departmentId;
  
  @PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED)
  private Integer periodYear;
  
  @PrimaryKeyColumn(ordinal = 2, type = PrimaryKeyType.PARTITIONED)
  private Integer periodCode;
  
  @PrimaryKeyColumn(ordinal = 3, type = PrimaryKeyType.CLUSTERED)
  private String OKPO;
  
  @Pure
  public UUID getDepartmentId() {
    return this.departmentId;
  }
  
  public void setDepartmentId(final UUID departmentId) {
    this.departmentId = departmentId;
  }
  
  @Pure
  public Integer getPeriodYear() {
    return this.periodYear;
  }
  
  public void setPeriodYear(final Integer periodYear) {
    this.periodYear = periodYear;
  }
  
  @Pure
  public Integer getPeriodCode() {
    return this.periodCode;
  }
  
  public void setPeriodCode(final Integer periodCode) {
    this.periodCode = periodCode;
  }
  
  @Pure
  public String getOKPO() {
    return this.OKPO;
  }
  
  public void setOKPO(final String OKPO) {
    this.OKPO = OKPO;
  }
}
