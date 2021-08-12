package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReaderFactory;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolver;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolverImpl;

import java.io.File;
import java.io.IOException;

/**
 * Фабрика парсера для чтение доменных объектов формата xml.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
@Component
@Profile("importXml")
public class XmlFileDtoReaderFactory implements FileDtoReaderFactory {
    private final TransferPropertyResolver transferPropertyResolver;


    /** Создаёт фабрику для создания парсера файлов содержащий доменные объекты в определенном формате xml.
     */
    public XmlFileDtoReaderFactory() {
        this.transferPropertyResolver = new TransferPropertyResolverImpl();
    }

    @Override
    public XmlFileDtoReader createReader(File file) throws IOException, ImportParseException {
        return new XmlFileDtoReader(file, transferPropertyResolver);
    }

    @Override
    public XmlFileDtoPropertiesLoader createLoader(File file, String encoding) throws ImportParseException {
        return new XmlFileDtoPropertiesLoader(file, transferPropertyResolver, encoding);
    }
}
