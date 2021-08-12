package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoPropertiesLoader;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolver;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Обеспичивает загрузку всех свойств доменного объекта по иформации о его рассположении в xml файле.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class XmlFileDtoPropertiesLoader implements FileDtoPropertiesLoader {
    /** Файл из которого происходит загрузка. */
    private File file;
    private TransferPropertyResolver transferPropertyResolver;
    private final String encoding;

    /** Создаёт загрузчик свойств доменных объектов из файла.
     * @param file файл из которого загружаются данные.
     * @param transferPropertyResolver ресолвер типов.
     */
    public XmlFileDtoPropertiesLoader(File file, TransferPropertyResolver transferPropertyResolver) {
        this(file, transferPropertyResolver, "UTF-8");
    }

    /** Создаёт загрузчик свойств доменных объектов из файла.
     * @param file файл из которого загружаются данные.
     * @param transferPropertyResolver ресолвер типов.
     * @param encoding кодировка файла.
     */
    public XmlFileDtoPropertiesLoader(File file, TransferPropertyResolver transferPropertyResolver,
                                      String encoding) {
        this.file = file;
        this.transferPropertyResolver = transferPropertyResolver;
        this.encoding = encoding;
    }

    @Override
    public void open() throws IOException {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean loadProperties(@NotNull ImportDtoInfo dtoInfo) throws ImportParseException, IOException {
        try (BufferedReader skipper = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
            skipper.skip(dtoInfo.getOffsetFirstByteInFile());

            final int length = new Long(dtoInfo.getOffsetLastByteInFile() - dtoInfo.getOffsetFirstByteInFile())
                    .intValue();
            final DomainTo dto = readObject(skipper, length);
            dtoInfo.setDomainTo(dto);
            return dto != null;
        }
    }

    @Override
    public boolean loadPropertiesForGroup(Collection<ImportDtoInfo> group) throws IOException, ImportParseException {
        final List<ImportDtoInfo> objects = new ArrayList<>(group);
        // сортируем объекты по смещению символа в файле
        objects.sort(new Comparator<ImportDtoInfo>() {
            @Override
            public int compare(ImportDtoInfo o1, ImportDtoInfo o2) {
                return Long.compare(o1.getOffsetFirstByteInFile(), o2.getOffsetFirstByteInFile());
            }
        });

        long offset = 0;
        try (BufferedReader skipper = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
            for (ImportDtoInfo dtoInfo : objects) {
                final int length = new Long(dtoInfo.getOffsetLastByteInFile() - dtoInfo.getOffsetFirstByteInFile())
                        .intValue();

                skipper.skip(dtoInfo.getOffsetFirstByteInFile() - offset);
                dtoInfo.setDomainTo(readObject(skipper, length));
                if (dtoInfo.getDomainTo() == null) {
                    return false;
                }
                offset = dtoInfo.getOffsetLastByteInFile();
            }
        }
        return true;
    }

    /** Вычитывает свойства бъекта из ридера.
     * @param skipper ридер.
     * @param length длина объекта в симовлах.
     */
    private DomainTo readObject(BufferedReader skipper, int length) throws IOException, ImportParseException {
        char[] buffer = new char[length];
        skipper.read(buffer);
        return parseObject(buffer);
    }

    /** Преобраразует xml в {@link DomainTo}.
     * @param buffer xml.
     * @return DTO объект.
     */
    private DomainTo parseObject(char[] buffer) throws ImportParseException {
        XMLStreamReader reader = null;
        try {
            reader = XMLInputFactory.newInstance().createXMLStreamReader(new CharArrayReader(buffer));
            reader.next();
            return DomainToXmlParser.parseDomainTo(reader, transferPropertyResolver);
        } catch (XMLStreamException e) {
            throw new ImportParseException();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    //ignore
                }
            }
        }
    }


}
