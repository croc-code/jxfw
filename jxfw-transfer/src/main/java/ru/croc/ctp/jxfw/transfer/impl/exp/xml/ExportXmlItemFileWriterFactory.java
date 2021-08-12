package ru.croc.ctp.jxfw.transfer.impl.exp.xml;

import static ru.croc.ctp.jxfw.transfer.impl.TransferContextService.DEFAULT_FILE_NAME;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.exp.ExportItemFileWriterFactory;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.ExportDynamicFileItemWriter;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformerImplWithXmlns;

import java.io.IOException;
import java.util.function.Function;


/**
 * Фабрика writer'ов для экспорта {@link DomainTo} в xml файл.
 *
 * @author Alexander Golovin
 * @since 1.6
 */
@Component
@Profile("exportXml")
@JobScope
public class ExportXmlItemFileWriterFactory implements ExportItemFileWriterFactory {
    private final TransferToTransformerImplWithXmlns transferToTransformer;
    private final TransferContextService transferContextService;
    private final String scenarioName;
    /** Один экземпляр уже объявлен. */
    private int count = 1;

    /**
     * Фабрика writer'ов для экспорта {@link DomainTo} в xml файл.
     *
     * @param transferContextService сервис для работы с контекстом
     * @param transferToTransformer трансформер xml
     * @param scenarioName имя задачи.
     */
    @Autowired
    public ExportXmlItemFileWriterFactory(TransferContextService transferContextService,
                                          TransferToTransformerImplWithXmlns transferToTransformer,
                                          @Value("#{jobParameters[scenarioName]}") String scenarioName) {
        this.transferContextService = transferContextService;
        this.transferToTransformer = transferToTransformer;
        this.scenarioName = scenarioName;
    }


    @Override
    public XmlFileItemWriter create() throws IOException {
        return create(DEFAULT_FILE_NAME + "-" + count++);
    }

    @Override
    public XmlFileItemWriter create(String fileName) throws IOException {
        return new XmlFileItemWriter(transferContextService, scenarioName, transferToTransformer, fileName);
    }

    @Override
    public ExportDynamicFileItemWriter create(Function<DomainTo, String> function) {
        return new ExportDynamicFileItemWriter(function, this, transferContextService);
    }
}
