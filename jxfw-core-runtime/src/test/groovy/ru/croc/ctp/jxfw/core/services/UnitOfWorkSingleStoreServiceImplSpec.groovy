package ru.croc.ctp.jxfw.core.services

import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.DomainService
import ru.croc.ctp.jxfw.core.domain.DomainServicesResolver
import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkSingleStoreServiceImpl
import spock.lang.Specification

class UnitOfWorkSingleStoreServiceImplSpec extends Specification {

	DomainService entityService = Mock()
	UnitOfWorkSingleStoreServiceImpl serviceUnderTest = Spy()
	DomainServicesResolver serviceResolver = Stub()

	def setup() {
		serviceResolver.resolveService(_) >> entityService
		serviceUnderTest.serviceResolver = serviceResolver
	}

	def "store ignores not native objects" () {
		given:
		DomainObject o1 = Mock(DomainObject)
		DomainObject o2 = Mock(DomainObject)
		DomainObject o3 = Mock(DomainObject)
		o1.isNew() >> false
		o1.isRemoved() >> false
		o2.isNew() >> false
		o2.isRemoved() >> false
		o3.isNew() >> false
		o3.isRemoved() >> false

		serviceUnderTest.accepted(_, _) >> {DomainObject object, List uow ->	object != o2}

		when:
		serviceUnderTest.store([o1, o2, o3])

		then:
		1 * entityService.save(o1)
		0 * entityService.save(o2)
		1 * entityService.save(o3)
	}

	def "store returns changed ids" () {
		given:
		def o1 = new TestObject("orig_o1_id")
		def o2 = new TestObject("orig_o2_id")
		serviceUnderTest.accepted(_, _) >> true

		when:
		def storeResult = serviceUnderTest.store([o1, o2])

		then:
		1 * entityService.save(o1) >> {o1.setId("newId_456")}
		1 * entityService.save(o2)
		storeResult
		def idMapping = storeResult.idMapping
		idMapping.size() == 1
		idMapping[0].newId == "newId_456"
		idMapping[0].originalId == "orig_o1_id"
	}
}

