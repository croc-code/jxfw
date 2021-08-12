package ru.croc.ctp.jxfw.config

import org.junit.rules.TemporaryFolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.*
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import ru.croc.ctp.jxfw.core.config.XfwCoreConfig
import ru.croc.ctp.jxfw.security.impl.facade.webclient.file.LocalResourceStorePerUser
import ru.croc.ctp.jxfw.core.facade.webclient.file.ResourceStore

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
@EnableAutoConfiguration(exclude = [CassandraDataAutoConfiguration])
@EntityScan(basePackages = ["org.eec.mscp.cc05","ru.croc.ctp.jxfw.domain"])
@EnableJpaRepositories(basePackages = ["ru.croc.ctp.jxfw.domain.repo", "ru.croc.ctp.jxfw.domain.complex.repo",
        "org.eec.mscp.cc05"])
@Import([XfwCoreConfig])
@ComponentScan(basePackages = ["ru.croc.ctp.jxfw.domain.facade",
        "ru.croc.ctp.jxfw.domain", "ru.croc.ctp.jxfw.domain.service",
        "ru.croc.ctp.jxfw.datasource", "ru.croc.ctp.jxfw.report", "org.eec.mscp.cc05"])
@PropertySource("classpath:application.properties")
class TestConfigFull extends WebMvcConfigurerAdapter {

    static final Logger logger = LoggerFactory.getLogger(TestConfigFull.class)

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(addLocalDateConverter())
    }

    /**
     * Бин для конвертирования строки с датой в формате "yyyy-MM-dd'T'HH:mm:ss.SSS"
     * принятой в WC в объект {@link java.time.LocalDate}.
     *
     * @return Бин типа {@code Converter < String , LocalDate >}
     * @see org.springframework.core.convert.converter.Converter
     */
    @Bean
    public Converter<String, LocalDate> addLocalDateConverter() {
        return new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String source) {
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            }
        };
    }

    /**
     * Временная папка для работы {@link ResourceStore} во время тестов.
     * Автоматически создается при инстанцировании бина {@link TestConfigFull}
     * и удаляется при остановке spring-контекста.
     */
    final static TemporaryFolder folder = new TemporaryFolder()

    @PostConstruct
    static void init() {
        folder.create()
        logger.debug("Temp folder created: " + folder.root.absolutePath)
    }

    @PreDestroy
    static void cleanUp() {
        logger.debug("Temp folder delete: " + folder.root.absolutePath)
        folder.delete();
    }

    /**
     * Сервис временного хранения загружаемых пользователем на сервер файлов.
     *
     * @return Сервис временного хранения
     */
    @Bean
    public ResourceStore resourceStore() {
        logger.debug("Creating bean ResourceStore")
        String rootDirAbsolutePath = folder.newFolder("jxfw-jpa-tests").absolutePath
        Long quotaPerUser = 100

        return new LocalResourceStorePerUser(rootDirAbsolutePath, quotaPerUser)
    }
}