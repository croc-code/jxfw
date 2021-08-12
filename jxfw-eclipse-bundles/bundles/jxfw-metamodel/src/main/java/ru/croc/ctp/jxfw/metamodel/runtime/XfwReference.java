package ru.croc.ctp.jxfw.metamodel.runtime;

/**
 * Навигируемое или массивное навигируемое свойство в метамодели.
 */
public interface XfwReference extends XfwStructuralFeature {
    /* EReference methods*/

    // boolean isContainment();

    // boolean isContainer();

    // boolean isResolveProxies();


    /**
     * Ссылка на соответствующее свойство в другом классе в случае двухсторонней связи между полями
     * этих классов.
     * @return метаданные
     */
    XfwReference getEOpposite();


    /**
     * Метаданные типа данного свойства.
     * @return метаданные
     */
    XfwClass getEReferenceType();

    // EList<EAttribute> getEKeys();

}
