package ru.croc.ctp.jxfw.core.domain;

import java.util.List;
import java.util.Map;

import ru.croc.ctp.jxfw.core.domain.DomainObject;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwClass;

/**
 * Адаптер {@link DomainObject} для реализации заглушек в тестах,
 * необходимые методы должны быть реализованы в конкретном классе.
 */
public interface TestDomainObject extends DomainObject<String> {
		
		@Override
		public default String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public default boolean isNew() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public default String getTypeName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public default void setId(String id) {
			// TODO Auto-generated method stub		
		}

		@Override
		public default void setNew(boolean isnew) {
			// TODO Auto-generated method stub
		}

		@Override
		public default void setRemoved(Boolean removed) {
			// TODO Auto-generated method stub
		}

		@Override
		public default Boolean isRemoved() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public default Map<String, Object> getPropChangedValues() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public default void setPropChangedValues(Map<String, Object> original) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public default XfwClass getMetadata() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public default List<? extends DomainObject<?>> obtainValueByPropertyName(String name) {
		    return null;
		}
}
