package ru.croc.ctp.jxfw.core.services;

import java.util.List;
import java.util.Map;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.Editable;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

public class TestObject implements DomainObject<String>, Editable {
	
    private static final long serialVersionUID = -1580881578355588156L;
    
    private String id;

    public TestObject(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getTypeName() {
        return "TestObject";
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Long getVersion() {
        return -1L;
    }

    @Override
    public void setVersion(Long ts) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getNameOfVersionField() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setNew(boolean isnew) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRemoved(Boolean removed) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Boolean isRemoved() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setPropChangedValues(Map<String, Object> original) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Map<String, Object> getPropChangedValues() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<? extends DomainObject<String>> obtainValueByPropertyName(
            String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XfwClass getMetadata() {
        return null;
    }

}
