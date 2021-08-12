package ru.croc.ctp.jxfw.core.exception.exceptions;

import ru.croc.ctp.jxfw.core.domain.DomainObjectIdentity;

/**
 * Элемент коллекции {@link DomainViolation}.
 *
 * @author SMufazzalov
 * @since jXFW 1.5.0
 */
public class DomainViolationItem {
	
    /**
     * Объект, вызвавший ошибку.
     */
    private DomainObjectIdentity<?> identity;
    
    /**
     * Наименование свойства, вызвавшего ошибку.
     */
    private String propertyName;

    /**
     * Инициализация экземпляра ссылкой на identity, без указания свойства.
     *
     * @param identity Объект, вызвавший ошибку
     */
    public DomainViolationItem(DomainObjectIdentity<?> identity) {
        this.identity = identity;
    }

    /**
     * Инициализация экземпляра ссылкой на identity и свойство.
     *
     * @param identity     Объект, вызвавший ошибку
     * @param propertyName Наименование свойства, вызвавшего ошибку
     */
    public DomainViolationItem(DomainObjectIdentity<?> identity, String propertyName) {
        this.identity = identity;
        this.propertyName = propertyName;
    }

    public DomainObjectIdentity<?> getIdentity() {
        return identity;
    }

    public void setIdentity(DomainObjectIdentity<?> identity) {
        this.identity = identity;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DomainViolationItem that = (DomainViolationItem) obj;

        if (getIdentity() != null ? !getIdentity().equals(that.getIdentity()) : that.getIdentity() != null) {
            return false;
        }
        return getPropertyName() != null
                ? getPropertyName().equals(that.getPropertyName()) : that.getPropertyName() == null;
    }

    @Override
    public int hashCode() {
        int result = getIdentity() != null ? getIdentity().hashCode() : 0;
        result = 31 * result + (getPropertyName() != null ? getPropertyName().hashCode() : 0);
        return result;
    }

	@Override
	public String toString() {
		return "DomainViolationItem [identity=" + identity + ", propertyName=" + propertyName + "]";
	}
    
}
