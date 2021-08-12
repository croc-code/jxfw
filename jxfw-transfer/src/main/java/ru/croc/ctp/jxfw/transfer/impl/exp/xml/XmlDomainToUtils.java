package ru.croc.ctp.jxfw.transfer.impl.exp.xml;

import ru.croc.ctp.jxfw.core.facade.webclient.DomainTo;
import ru.croc.ctp.jxfw.transfer.service.TransferToTransformer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Преобразует части DTO в xml строки.
 *
 * @author Alexander Golovin
 * @since 1.5
 */
public class XmlDomainToUtils {
    /** Разделитель междуу объектами. */
    public static final String SEPARATOR = "\n";
    /** Закрывающие теги файла импорта в xml строке. */
    public static final String TAGS_ARE_ENDED = "</objects>\n</transfer-data>\n";

    /** Создаёт шапку файла импорта в xml формате.
     * @param dateTime дата и время создания.
     * @return шапка документа в xml.
     */
    public static String createHeader(LocalDateTime dateTime) {
        return String.format(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                        + "<transfer-data n=\"Constants\" xmlns:dt=\"urn:schemas-microsoft-com:datatypes\">\n"
                        + "<head>\n"
                        + "<export-time dt:dt=\"dateTime.tz\">%s</export-time>\n"
                        + "</head>\n"
                        + "<objects>\n",
                dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    /** Преобразует {@link DomainTo} в строку xml.
     *  @param mapper маппер xml.
     *  @param domainTo трансформируемый доменный объект.
     *  @return доменный объект в строке xml.
     */
    public static String transformDomainTo(TransferToTransformer mapper, DomainTo domainTo) {
        return mapper.toXml(domainTo);
    }
}
