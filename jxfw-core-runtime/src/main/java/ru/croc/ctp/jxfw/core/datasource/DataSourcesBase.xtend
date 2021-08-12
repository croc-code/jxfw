package ru.croc.ctp.jxfw.core.datasource

import ru.croc.ctp.jxfw.core.domain.DomainObject
import java.util.ArrayList
import org.springframework.util.StringUtils
import ru.croc.ctp.jxfw.core.datasource.impl.WebParamUtil
import org.springframework.beans.factory.annotation.Autowired
import ru.croc.ctp.jxfw.core.load.PreloadService

/*
 * @deprecated since 1.6
 */
@Deprecated
abstract class DataSourcesBase {
		
	@Autowired
	private PreloadService preloadService	

	def initMoreList(String expand, Iterable<? extends DomainObject<?>> objects) {
		val moreList = new ArrayList()
		if (!StringUtils.isEmpty(expand)) {
			val preLoadProps = WebParamUtil.parseExpandParam(expand)
			moreList.addAll(preloadService.loadMoreFor(objects, preLoadProps))
		}
		moreList
	}

}
