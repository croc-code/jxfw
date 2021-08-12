package ru.croc.ctp.jxfw.core.services

import org.springframework.context.ApplicationEventPublisher

import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.domain.IdentityMapping
import ru.croc.ctp.jxfw.core.store.StoreResult
import ru.croc.ctp.jxfw.core.store.UnitOfWorkSingleStoreService
import ru.croc.ctp.jxfw.core.store.impl.UnitOfWorkMultiStoreServiceImpl

import spock.lang.Specification
import spock.lang.Ignore

//TODO: пересмотреть тесты всвязи с изменением API
@Ignore
class UnitOfWorkMultiStoreServiceImplSpec extends Specification {

	def serviceUnderTest = new UnitOfWorkMultiStoreServiceImpl()
	
	def setup() {
		serviceUnderTest.applicationEventPublisher = Mock(ApplicationEventPublisher)
        serviceUnderTest.validator = Mock(Validator)
	}
	
	def "store objects in two different storages" () {
		given:
		UnitOfWorkSingleStoreService persistSrv1 = Mock()
		UnitOfWorkSingleStoreService persistSrv2 = Mock()
		serviceUnderTest.services = [srv1 : persistSrv1, srv2 : persistSrv2]

		when:
		serviceUnderTest.store([Mock(DomainObject), Mock(DomainObject)])

		then:
		1 * persistSrv1.store({it.size == 2}) >> Stub(StoreResult)
		1 * persistSrv2.store({it.size == 2}) >> Stub(StoreResult)
	}

	def "return aggregated StoreResult from store" () {
		given:
		UnitOfWorkSingleStoreService persistSrv1 = Stub()
		persistSrv1.store(_) >> {return storeResultWithNewId} 
		UnitOfWorkSingleStoreService persistSrv2 = Stub()
		persistSrv2.store(_) >> {return storeResultWithNewId} 
		serviceUnderTest.services = [srv1 : persistSrv1, srv2 : persistSrv2]
		
		when:
		def storeResult = serviceUnderTest.store([])
		
		then:
		storeResult
		def idMapping = storeResult.idMapping
		idMapping.size() == 2
	}
	
	private def StoreResult getStoreResultWithNewId() {
		def result = new StoreResult()
		result.idMapping.add(new IdentityMapping(Mock(DomainObject), "dummyId"))
		return result
	}
}

