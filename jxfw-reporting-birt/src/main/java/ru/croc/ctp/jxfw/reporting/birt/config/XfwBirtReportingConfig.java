package ru.croc.ctp.jxfw.reporting.birt.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация отчетов на Birt.
 */
@Configuration
@ComponentScan(basePackages = {
        "ru.croc.ctp.jxfw.reporting.birt"})
public class XfwBirtReportingConfig {
}
