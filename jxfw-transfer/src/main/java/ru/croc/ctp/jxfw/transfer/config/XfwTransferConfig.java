package ru.croc.ctp.jxfw.transfer.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.task.TaskExecutor;
import ru.croc.ctp.jxfw.core.config.XfwCoreConfig;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.TransferService;
import ru.croc.ctp.jxfw.transfer.impl.TaskExecutorWithInterrupt;
import ru.croc.ctp.jxfw.transfer.impl.imp.xml.MapDeserializerModifier;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolver;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolverImpl;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformer;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformerImpl;

import java.util.List;



/**
 * Spring конфиг для модуля Transfer.
 *
 * @author Nosov Alexander
 * @since 1.4
 */
@Configuration
@EnableBatchProcessing
@Import({
        XfwCoreConfig.class,
        JsonXfwTransferConfig.class,
        XmlXfwTransferConfig.class})
@ComponentScan(basePackages = {"ru.croc.ctp.jxfw.transfer"})
public class XfwTransferConfig {

    /**
     * JobRegistryBeanPostProcessor.
     * 
     * @param jobRegistry - реестр задач.
     * @return jobRegistryBeanPostProcessor
     */
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    /**
     * @return объявление XML маппера.
     */
    @Bean
    protected XmlMapper xmlMapper() {
        final SimpleModule module = new SimpleModule("jxfwTransferModule", Version.unknownVersion());
        module.setDeserializerModifier(new MapDeserializerModifier());

        final XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(module);
        return xmlMapper;
    }

    /**
     * {@link TaskExecutor} для запуска задач Spring Batch с возможностью прерывания потока.
     *
     * @return исполнитель задач с возможностью прерывания потока.
     */
    @Bean
    @Autowired
    public TaskExecutorWithInterrupt taskExecutorWithInterrupt(
            TransferService transferService,
            @Value("${transfer.pollingInterval:30000}") int pollingInterval                                            ) {
        return new TaskExecutorWithInterrupt(transferService, pollingInterval);
    }

    /**
     * Заменяет {@link TaskExecutor}, на исполнитель задач с возможностью прерывания потока.
     *
     * @param jobRepository репозиторий meta-data spring batch.
     * @param taskExecutorWithInterrupt исполнитель задач с возможностью прерывания потока
     * @return {@link JobLauncher} с асинхронным {@link TaskExecutor}.
     */
    @Bean
    @Autowired
    public JobLauncher jobLauncher(JobRepository jobRepository, TaskExecutorWithInterrupt taskExecutorWithInterrupt) {
        final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setTaskExecutor(taskExecutorWithInterrupt);
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }

    /**
     * @return сервис для установки значений в ТО объекты.
     */
    @Bean
    public TransferPropertyResolver transferPropertyResolver() {
        return new TransferPropertyResolverImpl();
    }

    /**
     * @return сервис для перевода ТО объектов в XML представление.
     */
    @Bean
    public TransferToTransformer transferToTransformer() {
        return new TransferToTransformerImpl();
    }
}
