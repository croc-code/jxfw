package ru.croc.ctp.jxfw.core.services

import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.IdentityMapping
import spock.lang.Specification

class IdentityMappingSpec extends Specification {
	
	DomainObject to = Stub()
	
	def objectUnderTest = new IdentityMapping(to, "origId_123")
	
	def setup() {
		to.getId() >> "newId_456"
		to.getTypeName() >> "TestObject"
	}
	
	def "getTypeName" () {
		expect:
		objectUnderTest.getTypeName() == "TestObject"
	}
	
	def "getOriginalId" () {
		expect:
		objectUnderTest.getOriginalId() == "origId_123"
	}
	
	def "getNewId" () {
		expect:
		objectUnderTest.getNewId() == "newId_456"
	}
	
}
