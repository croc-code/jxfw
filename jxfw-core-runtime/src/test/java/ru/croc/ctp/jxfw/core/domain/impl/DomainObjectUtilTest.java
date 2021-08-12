package ru.croc.ctp.jxfw.core.domain.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.core.domain.TestDomainObject;

public class DomainObjectUtilTest {
	
	@SuppressWarnings("serial")
	static class LoadDataTestDomainObject implements TestDomainObject {
		
		private int obtainValueCallsCount = 0;
		
		@Override
		public List<? extends DomainObject<?>> obtainValueByPropertyName(String name) {
			obtainValueCallsCount++;
			return new ArrayList<>(Arrays.asList(null, this, null, otherTestObject, this, null, otherTestObject));
		}
	};
	
	private LoadDataTestDomainObject testObject = new LoadDataTestDomainObject();
	private static LoadDataTestDomainObject otherTestObject = new LoadDataTestDomainObject();
	
	@Test
	public void testLoadData() {		
		List<DomainObject<?>> objects = DomainObjectUtil.loadData("name", testObject);
		assertEquals(2, objects.size());
		assertEquals(1, testObject.obtainValueCallsCount);
		assertEquals(0, otherTestObject.obtainValueCallsCount);
	}
	
	@Test
	public void testLoadChainData() {	
		List<DomainObject<?>> objects = DomainObjectUtil.loadData("name.name", testObject);
		assertEquals(2, objects.size());
		assertEquals(3, testObject.obtainValueCallsCount);
		assertEquals(2, otherTestObject.obtainValueCallsCount);
	}		
}