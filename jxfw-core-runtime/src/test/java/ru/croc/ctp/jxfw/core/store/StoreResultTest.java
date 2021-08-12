package ru.croc.ctp.jxfw.core.store;

import org.junit.Assert;
import org.junit.Test;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.core.services.TestObject;

public class StoreResultTest {

    @Test
    public void testMergeStoreResults() {
    	
    	StoreResult result1 = new StoreResult();   	
    	result1.getOriginalDomainObjects().add(new TestObject("1o"));
       	result1.getUpdatedDomainObjects().add(new TestObject("1u"));
    	
    	StoreResult result2 = new StoreResult();   	
    	result2.getOriginalDomainObjects().add(new TestObject("2o"));
    	
    	StoreResult result3 = new StoreResult();   	
    	result3.getOriginalDomainObjects().add(new TestObject("3o"));
    	result3.getUpdatedDomainObjects().add(new TestObject("3u"));
    	result3.getErrorObjects().add(new DomainTo());
    	
    	StoreResult result = new StoreResult(result1, result2, result3);

        Assert.assertEquals(3, result.getOriginalDomainObjects().size());
        Assert.assertEquals(0, result.getOriginalObjects().size());
        Assert.assertEquals(2, result.getUpdatedDomainObjects().size());
        Assert.assertEquals(0, result.getUpdatedObjects().size());
        Assert.assertEquals(1, result.getErrorObjects().size());
        Assert.assertEquals(0, result.getIdMapping().size());
    }
}
