package ru.croc.ctp.jxfw.transfer.impl.exp.xml;

import static ru.croc.ctp.jxfw.transfer.impl.TransferContextService.DEFAULT_FILE_NAME;

import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.impl.TransferContextService;
import ru.croc.ctp.jxfw.transfer.impl.exp.AbstractFileItemWriter;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformer;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformerImplWithXmlns;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Записывает данные со всех шагов в один xml файл.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("xmlFileItemWriter")
@Profile("exportXml")
@JobScope
public class XmlFileItemWriter extends AbstractFileItemWriter {
    /** Xml трансформер. */
    private TransferToTransformer transferToTransformer;


    /**
     * Записывает данные со всех шагов в один xml файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param scenarioName имя задачи.
     * @param transferToTransformer трансформер xml.
     * @throws IOException ошибка при работе с файловой системой.
     */
    @Autowired
    public XmlFileItemWriter(TransferContextService transferContextService,
                             @Value("#{jobParameters[scenarioName]}") String scenarioName,
                             TransferToTransformerImplWithXmlns transferToTransformer) throws IOException {
        this(transferContextService, scenarioName, transferToTransformer, DEFAULT_FILE_NAME);
    }

    /**
     * Записывает данные со всех шагов в один xml файл.
     *
     * @param transferContextService сервис для работы с контекстом.
     * @param scenarioName имя задачи.
     * @param transferToTransformer трансформер xml.
     * @param fileName имя файла.
     * @throws IOException ошибка при работе с файловой системой.
     */
    public XmlFileItemWriter(TransferContextService transferContextService,
                             String scenarioName,
                             TransferToTransformerImplWithXmlns transferToTransformer,
                             String fileName) throws IOException {
        super(transferContextService, scenarioName, fileName);
        this.transferToTransformer = transferToTransformer;
    }

    @Override
    protected String formatOfFile() {
        return "xml";
    }

    @Override
    protected String generateHeader() {
        return XmlDomainToUtils.createHeader(LocalDateTime.now());
    }

    @Override
    protected String getEndOfFile() {
        return XmlDomainToUtils.TAGS_ARE_ENDED;
    }

    @Override
    public void write(List<? extends DomainTo> items) throws Exception {
        final StringBuilder builder = new StringBuilder();
        for (DomainTo domainTo : items) {
            builder.append(XmlDomainToUtils.transformDomainTo(transferToTransformer, domainTo));
            builder.append(XmlDomainToUtils.SEPARATOR);
        }
        writeData(builder.toString());
    }
}
