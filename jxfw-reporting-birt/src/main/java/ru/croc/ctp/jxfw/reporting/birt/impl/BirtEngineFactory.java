package ru.croc.ctp.jxfw.reporting.birt.impl;

import static org.eclipse.birt.report.engine.api.IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Spring {@link FactoryBean} for the shared, singleton instance of
 * the {@link IReportEngine export engine}. There should be one {@link IReportEngine} per JVM.
 *
 * @author ANosov
 */
@Component
public class BirtEngineFactory implements FactoryBean<IReportEngine>,
        ApplicationContextAware, DisposableBean, InitializingBean {

    /**
     * Имя ключа под которым контекст спринга в контекст движка отчета.
     */
    public static final String DEFAULT_SPRING_APPLICATION_CONTEXT_KEY = "spring";

    // guard the engine reference initialization
    private final Object monitor = new Object();

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Attribute under which the Spring {@link ApplicationContext application context} is exposed for BIRT reports.
     */
    private String exposedSpringApplicationContextKey = DEFAULT_SPRING_APPLICATION_CONTEXT_KEY;

    // we need this reference to expose it to BIRT
    private ApplicationContext context;

    // the reference
    private IReportEngine engine;

    public void setExposedSpringApplicationContextKey(String exposedSpringApplicationContextKey) {
        this.exposedSpringApplicationContextKey = exposedSpringApplicationContextKey;
    }

    @Override
    public void destroy() throws Exception {
        if (engine != null) {
            engine.destroy();
        }
        Platform.shutdown();
        RegistryProviderFactory.releaseDefault();
    }

    @Override
    public IReportEngine getObject() throws Exception {
        synchronized (this.monitor) {

            if (this.engine != null) {
                return this.engine;
            }

            EngineConfig config = getEngineConfig();
            config.setEngineHome("");
            config.getAppContext().put(this.exposedSpringApplicationContextKey, this.context);

            try {
                Platform.startup(config);
            } catch (BirtException e) {
                throw new RuntimeException("Could not start the BIRT engine.", e);
            }

            IReportEngineFactory factory =
                    (IReportEngineFactory) Platform.createFactoryObject(EXTENSION_REPORT_ENGINE_FACTORY);
            this.engine = factory.createReportEngine(config);
        }
        return this.engine;
    }

    /**
     * Set  engine configuration like logs and other things as per the need.
     *
     * @return - конфигурация для {@link IReportEngine}.
     * @throws IOException - исключение, если ошибка чтения файлов.
     */
    public EngineConfig getEngineConfig() throws IOException {
        EngineConfig config = new EngineConfig();
        config.setFontConfig(resourceLoader.getResource("classpath:/fontsConfig_pdf.xml").getURL());
        return config;
    }

    public Class<?> getObjectType() {
        return IReportEngine.class;
    }

    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // required properties
        Assert.notNull(exposedSpringApplicationContextKey,
                "you must provide a valid value for the 'exposedSpringApplicationContextKey' attribute");
    }
}
