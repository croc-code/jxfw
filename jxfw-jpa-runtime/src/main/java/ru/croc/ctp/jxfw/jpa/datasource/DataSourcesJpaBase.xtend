package ru.croc.ctp.jxfw.jpa.datasource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaContext
import ru.croc.ctp.jxfw.core.domain.DomainObject
import ru.croc.ctp.jxfw.core.datasource.DataSourcesBase

/*
 * @deprecated since 1.6
 */
@Deprecated
abstract class DataSourcesJpaBase extends DataSourcesBase {
	
	@Autowired
	private JpaContext jpaContext;
	
	def getEntityManager(DomainObject<?> domainObject) {
		return getEntityManager(domainObject.class)
	}
	
	def getEntityManager(Class<?> domainObjectClass) {
		return jpaContext.getEntityManagerByManagedType(domainObjectClass)
	}
	
}