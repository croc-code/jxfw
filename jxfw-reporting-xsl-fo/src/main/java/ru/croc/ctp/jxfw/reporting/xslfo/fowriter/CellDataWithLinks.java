package ru.croc.ctp.jxfw.reporting.xslfo.fowriter;

import ru.croc.ctp.jxfw.reporting.xslfo.exception.ReportException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Данные для отрисовке на печатной форме,
 * которые могут содержать ссылки.
 * @author PaNovikov
 * @since 06.07.2017.
 */
public class CellDataWithLinks {


    /**
     * Разделитель для ссылок.
     */
    public static final String LINK_SIGN = "@link";
    /**
     * Данные в ячейке.
     */
    protected String data;
    /**
     * Сыллки.
     */
    protected List<LinkBuilder> links = new ArrayList<>();

    private CellDataWithLinks(){}

    /**
     * Cоздать объект cellData.
     * @param data данные ячейки
     * @return созданный объект
     */
    public static CellDataWithLinks create(String data) {
        CellDataWithLinks cellData = new CellDataWithLinks();
        cellData.data = data;
        return cellData;
    }

    /**
     * Добавить ссылку.
     * @param linkBuilder объект {@link LinkBuilder}
     * @return this
     */
    public CellDataWithLinks addLink(LinkBuilder linkBuilder) {
        this.links.add(linkBuilder);
        return this;
    }

    /**
     * Добавить список ссылок.
     * @param linkBuilderList список ссылок
     * @return this
     */
    public CellDataWithLinks addLinkList(List<LinkBuilder> linkBuilderList) {
        this.links.addAll(linkBuilderList);
        return this;
    }

    /**
     * Отрисовка данных со ссылкой.
     * @param xmlStreamWriter отрисовщик;
     */
    protected void writeCellDataWithLinks(XMLStreamWriter xmlStreamWriter) {
        try {
            if (links.isEmpty() || !data.contains(LINK_SIGN)) {
                xmlStreamWriter.writeCharacters(data);
                return;
            }
            Iterator<LinkBuilder> iterator = links.iterator();
            String[] dataWithLinks = data.split(LINK_SIGN, -1);
            for (String val : dataWithLinks) {
                xmlStreamWriter.writeCharacters(val);
                if (iterator.hasNext()) {
                    LinkBuilder lb = iterator.next();
                    lb.writeLink(xmlStreamWriter);
                }
            }
        } catch (XMLStreamException e) {
            throw new ReportException("Error by writing cellData with links");
        }
    }
}
