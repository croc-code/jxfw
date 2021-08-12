package ru.croc.ctp.jxfw.facade;

import com.squareup.javapoet.MethodSpec;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ru.croc.ctp.jxfw.metamodel.XFWDataSource;
import ru.croc.ctp.jxfw.metamodel.XFWOperation;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebClientDataSourceCreatorTest {

    /**
     * Тест проверяет, проставляется ли дженерик информация для коллекции
     * DataSourceResult<NeededInstanceTypeName> list
     */
    @Test
    public void listContainsGenericInfo() {
        String neededInstanceTypeName = "NeededInstanceTypeName";

        WebClientDataSourceCreator creator = new WebClientDataSourceCreator();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("methodName");

        creator.addToListCode(methodBuilder, mock(XFWDataSource.class), getXfwOperation(neededInstanceTypeName));

        assertTrue(methodBuilder.build().toString().contains(neededInstanceTypeName));
    }

    @NotNull
    private XFWOperation getXfwOperation(String neededInstanceTypeName) {
        XFWOperation operation = mock(XFWOperation.class);
        when(operation.getEParameters()).thenReturn(new BasicEList<>());
        EGenericType eGenericType = mock(EGenericType.class);
        EClassifier eClassifier = mock(EClassifier.class);
        when(eClassifier.getInstanceTypeName()).thenReturn(neededInstanceTypeName);
        when(eGenericType.getEClassifier()).thenReturn(eClassifier);
        when(operation.getEGenericType()).thenReturn(eGenericType);
        return operation;
    }

}
