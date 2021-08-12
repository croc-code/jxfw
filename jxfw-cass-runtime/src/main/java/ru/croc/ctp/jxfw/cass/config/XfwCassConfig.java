package ru.croc.ctp.jxfw.cass.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.croc.ctp.jxfw.core.config.XfwCoreConfig;

/**
 * Конфигурация Cass модуля.
 */
@Configuration
@Import(XfwCoreConfig.class)
@ComponentScan(
        basePackages = {
                "ru.croc.ctp.jxfw.cass.facade.webclient",
                "ru.croc.ctp.jxfw.cass.impl.services",
                "ru.croc.ctp.jxfw.cass.load.impl"
        }
)
public class XfwCassConfig {
}
