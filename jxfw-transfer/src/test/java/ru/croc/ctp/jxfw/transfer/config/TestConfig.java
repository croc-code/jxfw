package ru.croc.ctp.jxfw.transfer.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.scope.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import ru.croc.ctp.jxfw.core.facade.webclient.file.LocalResourceStore;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;

import java.io.File;
import javax.servlet.ServletContext;

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
@ComponentScan(basePackages = "ru.croc.ctp.jxfw")
@Import(XfwTransferConfig.class)
public class TestConfig {

    @Autowired
    ServletContext servletContext;

    @Bean
    public StepScope stepScope() {
        final StepScope stepScope = new StepScope();
        stepScope.setAutoProxy(true);
        return stepScope;
    }

    /**
     * Сервис временного хранения загружаемых пользователем на сервер файлов.
     ** @return Сервис временного хранения
     */
    @Bean
    ResourceStore resourceStore() {
        String rootDirAbsolutePath = "/";
        if (rootDirAbsolutePath == null || rootDirAbsolutePath.isEmpty()) {
            rootDirAbsolutePath = ((File) servletContext
                    .getAttribute("javax.servlet.context.tempdir"))
                    .getAbsolutePath();
        }

        return new LocalResourceStore(rootDirAbsolutePath, 10l);
    }


}
