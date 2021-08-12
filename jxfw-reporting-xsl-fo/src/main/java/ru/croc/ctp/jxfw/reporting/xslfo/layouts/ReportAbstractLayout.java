package ru.croc.ctp.jxfw.reporting.xslfo.layouts;

import ru.croc.ctp.jxfw.reporting.xslfo.impl.util.MacroProcessor;
import ru.croc.ctp.jxfw.reporting.xslfo.types.AbstractLayoutClass;

import javax.xml.stream.XMLStreamException;

/**
 * Абстрактная реализация IReportLaypuut.
 * Created by vsavenkov on 24.04.2017. Import from Croc.XmlFramework.ReportService .Net.2.0
 */
public abstract class ReportAbstractLayout implements IReportLayout {

    /**
     * Реализация соотв. метода.
     * @param layoutProfile профиль
     * @param layoutData параметры
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    @Override
    public void make(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) throws XMLStreamException {
        doMake(layoutProfile, layoutData);
    }

    /**
     * Формирует визуальное представление отчета на основании описания.
     * @param layoutProfile - Профиль
     * @param layoutData    - Параметры
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    protected abstract void doMake(AbstractLayoutClass layoutProfile, ReportLayoutData layoutData) throws
            XMLStreamException;

    /**
     * Стандартный вывод сообщения об отсутствии данных (с подстановкой параметров, при необходимости).
     * @param layoutData    - Профиль
     * @param message       - Сообщение (null = заменяем на стандартное, "" - не выводим!)
     * @throws XMLStreamException   - выбрасывается из XMLStreamWriter
     */
    public static void writeNoDataMessage(ReportLayoutData layoutData, String message) throws XMLStreamException {

        // дефолтное сообщение об отсутствии данных
        final String noDataText = "Данные отсутствуют";
        // Сообщение об отсутствии данных. При необходимости
        // подставляем значения параметров в сообщение об отсутствии данных.

        String noDataMessage =
                message == null
                        ? noDataText
                        : message.length() == 0
                        ? null
                        : MacroProcessor.process(message, layoutData);

        // выводим сообщение об отсутствии данных
        layoutData.getRepGen().emptyBody(noDataMessage);
    }
}
