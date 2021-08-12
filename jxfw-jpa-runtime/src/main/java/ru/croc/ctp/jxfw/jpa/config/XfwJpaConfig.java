package ru.croc.ctp.jxfw.jpa.config;

import org.h2.jdbc.JdbcSQLException;
import org.postgresql.util.PSQLException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.croc.ctp.jxfw.jpa.exception.DbExceptionParser;
import ru.croc.ctp.jxfw.jpa.exception.impl.H2DbExceptionParser;
import ru.croc.ctp.jxfw.jpa.exception.impl.PostgreDbExceptionParser;

/**
 * Конфигурация для слоя JPA.
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
        "ru.croc.ctp.jxfw.jpa.store",
        "ru.croc.ctp.jxfw.jpa.load",
        "ru.croc.ctp.jxfw.jpa.hibernate.metadata",
        "ru.croc.ctp.jxfw.jpa.exception",
        "ru.croc.ctp.jxfw.jpa.facade.webclient"
})
public class XfwJpaConfig {


    /**
     * Конфигурация, специфичная для СУБД PostgeSql.
     * Включается, если драйвера к этой СУБД есть в класспасе.
     */
    @ConditionalOnClass(PSQLException.class)
    public static class PostgreSqlConfiguration {

        /**
         * Сервис разбора исключений PostgeSql.
         *
         * @return Сервис разбора исключений PostgeSql
         */
        @Bean
        DbExceptionParser postgreDbExceptionParser() {
            return new PostgreDbExceptionParser();
        }
    }

    /**
     * Конфигурация, специфичная для СУБД H2.
     * Включается, если драйвера к этой СУБД есть в класспасе.
     */
    @ConditionalOnClass(JdbcSQLException.class)
    public static class H2Configuration {

        /**
         * Сервис разбора исключений H2.
         *
         * @return Сервис разбора исключений PostgeSql
         */
        @Bean
        DbExceptionParser h2DbExceptionParser() {
            return new H2DbExceptionParser();
        }
    }

}
