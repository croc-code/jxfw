package ru.croc.ctp.jxfw.transfer.impl.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportDomainToProcessor;
import ru.croc.ctp.jxfw.transfer.component.imp.ImportGroupProcessor;
import ru.croc.ctp.jxfw.transfer.component.imp.context.data.ImportGroup;
import ru.croc.ctp.jxfw.transfer.impl.BinaryPropertyService;

import java.util.List;

/**
 * Обработчик импортируемой группы.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component("importGroupProcessor")
@JobScope
public class DefaultImportGroupProcessor implements ImportGroupProcessor {
    private static final Logger log = LoggerFactory.getLogger(DefaultImportGroupProcessor.class);

    @Autowired
    private ImportDomainToProcessor importDomainToProcessor;
    @Autowired
    private BinaryPropertyService binaryPropertyService;

    @Override
    public ImportGroup process(ImportGroup group) throws Exception {
        log.debug("Starting pre-process group. index: {} size: {}", group.getId(), group.getObjects().size());
        final List<DomainTo> processedObjects = importDomainToProcessor.process(group.getObjects());
        processedObjects.forEach(dto -> binaryPropertyService.replaceBinaryPropertyValuesToResources(dto));
        group.getObjects().clear();
        group.getObjects().addAll(processedObjects);
        log.debug("Pre-process group finished. index: {} size: {}", group.getId(), group.getObjects().size());
        return group;
    }
}
