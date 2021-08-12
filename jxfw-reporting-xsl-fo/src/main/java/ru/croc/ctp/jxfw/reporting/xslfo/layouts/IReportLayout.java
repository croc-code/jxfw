package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;

import javax.xml.stream.XMLStreamException;

/**
 * Интерфейс для описания внешнего вида отчета.
 * Created by vsavenkov on 15.03.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public interface IReportLayout {

    /**
     * Формирует визуальное представление отчета на основании описания.
     * @param layoutProfile профиль
     * @param layoutData параметры
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    void make(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) throws XMLStreamException;
}
