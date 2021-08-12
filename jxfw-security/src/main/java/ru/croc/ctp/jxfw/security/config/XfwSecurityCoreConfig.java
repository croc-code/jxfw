package ru.croc.ctp.jxfw.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import ru.croc.ctp.jxfw.security.impl.facade.webclient.file.LocalResourceStorePerUser;
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore;
import ru.croc.ctp.jxfw.wc.web.config.ConfigModuleProvider;

import java.io.File;
import javax.servlet.ServletContext;

/**
 * Базовые настройки приложения.
 *
 * @author Nosov Alexander
 * @since 1.3
 */
@Configuration
@Order(2)
@ComponentScan("ru.croc.ctp.jxfw.security")
public class XfwSecurityCoreConfig {


    @Autowired
    private ServletContext servletContext;


    @Autowired(required = false)
    private ConfigModuleProvider configModuleProvider;

    /**
     * Сервис временного хранения загружаемых пользователем на сервер файлов.
     *
     * @param rootDirAbsolutePath Путь папки для хранения контента
     * @return Сервис временного хранения
     */
    @Bean
    @ConditionalOnBean(ConfigModuleProvider.class)
    public ResourceStore resourceStore(@Value("${rootDirectory:}") String rootDirAbsolutePath) {
        if (rootDirAbsolutePath == null || rootDirAbsolutePath.isEmpty()) {
            rootDirAbsolutePath = ((File) servletContext
                    .getAttribute("javax.servlet.context.tempdir"))
                    .getAbsolutePath();
        }

        Long quotaPerUser = configModuleProvider.loadConfig().getFilesModuleConfig().getQuotaPerUser();

        return new LocalResourceStorePerUser(rootDirAbsolutePath, quotaPerUser);
    }

}

