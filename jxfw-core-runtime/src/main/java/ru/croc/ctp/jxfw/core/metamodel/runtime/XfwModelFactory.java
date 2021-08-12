package ru.croc.ctp.jxfw.core.metamodel.runtime;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import ru.croc.ctp.jxfw.metamodel.XFWMMPackage;
import ru.croc.ctp.jxfw.metamodel.runtime.XfwModel;
import ru.croc.ctp.jxfw.metamodel.runtime.impl.XfwModelImpl;

import java.io.IOException;
import java.net.URI;

/**
 * Фабрика метамодели в рантайм.
 */
public final class XfwModelFactory {

    private static class XFWModelHolder {

        static final XfwModel INSTANCE = loadRuntimeModel();


        private static XfwModel loadRuntimeModel() {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            try {
                Resource[] resources = resolver.getResources("classpath:/" + XFWMMPackage.eINSTANCE.getModelFolderPath()
                        + "/*." + XFWMMPackage.eINSTANCE.getModelFileExtension());
                URI[] uris = new URI[resources.length];
                for (int i = 0; i < resources.length; i++) {
                    uris[i] = resources[i].getURI();
                }
                return new XfwModelImpl(uris);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    /**
     * Возвращает синглтон модеди, доступный в рантайм.
     *
     * @return - модель.
     */
    public static XfwModel getInstance() {

        return XFWModelHolder.INSTANCE;


    }
}
