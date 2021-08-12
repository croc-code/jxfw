package ru.croc.ctp.jxfw.core.facade.webclient.containers

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo
import ru.croc.ctp.jxfw.core.facade.webclient.IdentityMapping
import ru.croc.ctp.jxfw.core.facade.webclient.StoreResultDto
import ru.croc.ctp.jxfw.core.facade.webclient.impl.StoreResultToServiceImpl
import ru.croc.ctp.jxfw.core.store.StoreResult
import spock.lang.Specification

class StoreResultTOServiceImplSpec extends Specification {

	def serviceUnderTest = new StoreResultToServiceImpl()
	def storeResult = new StoreResult()
	
	def "transform identity map" () {
		given:
		storeResult.idMapping.add(new IdentityMapping("TypeName", "orig_id", "newId"))
		
		when:
		def dto = serviceUnderTest.toTo(storeResult, Locale.getDefault())
		
		then:
		dto
		def newIds = dto[StoreResultDto.NEW_IDS_FIELD_NAME]
		newIds
		newIds.size == 1
		newIds[0].type == "TypeName"
		newIds[0].id == "orig_id"
		newIds[0].newId == "newId"
	}

	def "transform updated object" () {
		given:
		DomainTo domainTO = Stub(DomainTo)
		storeResult.updatedObjects.add(domainTO)

		when:
		def dto = serviceUnderTest.toTo(storeResult, Locale.getDefault())

		then:
		dto
		def updatedObjects = dto[StoreResultDto.UPDATED_OBJECTS_FIELD_NAME]
		updatedObjects
		updatedObjects.size == 1
		updatedObjects[0] instanceof DomainTo
	}
}
