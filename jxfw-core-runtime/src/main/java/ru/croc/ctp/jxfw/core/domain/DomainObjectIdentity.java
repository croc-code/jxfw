package ru.croc.ctp.jxfw.core.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Реализация {@link Identity} для доменных объектов.
 *  
 * @author akogun
 *
 * @param <ID> - тип идентификатора доменного объекта
 * 
 */
public class DomainObjectIdentity<ID extends Serializable> implements Identity<ID> {
    
    private final ID id;
    
    private final String typeName;
    
    public DomainObjectIdentity(ID id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }
    
    public DomainObjectIdentity(DomainObject<ID> domainObject) {
        this(domainObject.getId(), domainObject.getTypeName());
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    @JsonProperty("type")
    public String getTypeName() {
        return typeName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		DomainObjectIdentity other = (DomainObjectIdentity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DomainObjectIdentity [id=" + id + ", typeName=" + typeName + "]";
	}
	
}