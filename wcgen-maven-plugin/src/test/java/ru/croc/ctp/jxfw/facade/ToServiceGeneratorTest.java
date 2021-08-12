package ru.croc.ctp.jxfw.facade;

import static org.junit.Assert.assertFalse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import org.junit.Test;
import ru.croc.ctp.jxfw.metamodel.XFWClass;
import ru.croc.ctp.jxfw.metamodel.XFWModel;
import ru.croc.ctp.jxfw.metamodel.impl.XFWModelImpl;

import java.nio.file.Paths;
import java.util.Collections;

public class ToServiceGeneratorTest {

    /**
     * XFW-1261 Убрать лишнюю проверку на тип в методе toTo генерируемого xxxToService
     */
    @Test
    public void toToMethodLacksRedundantAssertion() {
        XFWModel xfwModel = new XFWModelImpl(Paths.get("src/test/resources/models/XFWModelMasterDetail.ecore"));
        XFWClass abstractMaster = xfwModel.findBySimpleName("AbstractMaster", XFWClass.class);

        ToServiceGenerator toServiceGenerator = new ToServiceGenerator(abstractMaster,
                Collections.singleton(abstractMaster), null, ClassName.get(String.class));
        MethodSpec.Builder builder = toServiceGenerator.createToToMethod();
        //System.out.println(ReflectionToStringBuilder.toString(builder));

        boolean contains = builder.build().code.toString().contains("Parameter domainObject should be of type ru.croc.ctp.jxfw.domain.AbstractMaster or his child");
        assertFalse(contains);
    }
}
