/**
 *
 * $Id$
 */
package ru.croc.ctp.jxfw.metamodel.validation;

import org.eclipse.emf.common.util.EList;


/**
 * A sample validator interface for {@link ru.croc.ctp.jxfw.metamodel.XFWClass}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface XFWClassValidator {
    boolean validate();

    boolean validatePersistenceModule(EList<String> value);

    boolean validateComplexType(boolean value);

    boolean validateKeyTypeName(String value);

}
