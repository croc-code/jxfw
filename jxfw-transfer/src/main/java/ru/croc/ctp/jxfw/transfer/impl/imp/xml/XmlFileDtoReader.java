package ru.croc.ctp.jxfw.transfer.impl.imp.xml;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.component.imp.FileDtoReader;
import ru.croc.ctp.jxfw.transfer.component.imp.exception.ImportParseException;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportDtoInfo;
import ru.croc.ctp.jxfw.transfer.impl.imp.context.data.ImportFileDtoHead;
import ru.croc.ctp.jxfw.transfer.service.TransferPropertyResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.stream.XMLStreamException;


/**
 * Парсер для файлов импорта формата JXFW xml.
 *
 * @author Golovin Alexander
 * @since 1.5
 */
public class XmlFileDtoReader implements FileDtoReader {
    private final XMLStreamReader2 reader;
    private final ImportFileDtoHead header;
    private final TransferPropertyResolver transferPropertyResolver;
    private final String encoding;


    /** Создаёт парсер на ресурс и открывает его.
     * @param file файл содержащий доменные объекты в определенном формате.
     * @param transferPropertyResolver сопоставить типов из xml в доменную модель.
     * @throws IOException при возникновеннии проблем при чтении файла.
     * @throws ImportParseException при не совпадающем формате xml домента.
     */
    public XmlFileDtoReader(File file, TransferPropertyResolver transferPropertyResolver)
            throws IOException, ImportParseException {
        try {
            this.reader = (XMLStreamReader2) XMLInputFactory2.newInstance()
                    .createXMLStreamReader(new FileInputStream(file));
            this.transferPropertyResolver = transferPropertyResolver;
            this.header = readHeader();
            this.encoding = reader.getEncoding() != null ? reader.getEncoding() : StandardCharsets.UTF_8.displayName();
        } catch (XMLStreamException e) {
            throw new ImportParseException(e);
        }
    }

    /** Читает заголовок с метаданными и переносит курсор к началу чтения доменных объектов.
     *  @return заголовочные метаданные.
     */
    private ImportFileDtoHead readHeader() throws IOException, XMLStreamException {
        final ImportFileDtoHead head = new ImportFileDtoHead();
        // заголовки не где не используются
        reader.next();
        while (!reader.isStartElement() || !"objects".equalsIgnoreCase(reader.getLocalName())) {
            reader.next();
        }
        reader.next();
        return head;
    }

    @Override
    public ImportFileDtoHead getHeader() {
        return header;
    }

    @Override
    public ImportDtoInfo next() throws ImportParseException {
        try {
            while (!reader.isStartElement()) {
                if (reader.isEndElement() && "objects".equals(reader.getLocalName())) {
                    return null;
                }
                reader.next();
            }
            // запоминаем начало объекта
            final long firstOffset = reader.getLocation().getCharacterOffset();
            // читаем объект
            final DomainTo dto = DomainToXmlParser.parseDomainTo(reader, transferPropertyResolver);
            if (dto == null) {
                return null;
            }

            while (!reader.isStartElement() && !(reader.isEndElement() && "objects".equals(reader.getLocalName()))) {
                reader.next();
            }
            // запоминаем конец объекта
            final long lastOffset = reader.getLocation().getCharacterOffset();


            // собираем ImportDtoInfo
            return new ImportDtoInfo.Builder(dto.getType(), dto.getId())
                    .domainTo(dto)
                    .offsetFirstByteInFile(firstOffset)
                    .offsetLastByteInFile(lastOffset)
                    .build();
        } catch (XMLStreamException e) {
            throw new ImportParseException(e);
        }
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // ignore
            }
        }
    }

    @Override
    public String getEncoding() {
        return encoding;
    }
}
